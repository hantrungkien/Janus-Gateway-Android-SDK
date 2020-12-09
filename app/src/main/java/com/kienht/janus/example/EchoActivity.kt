package com.kienht.janus.example

import android.Manifest
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.kienht.janus.client.JanusManager
import com.kienht.janus.client.listener.JanusCallingEventListener
import com.kienht.janus.client.listener.OnJanusListener
import com.kienht.janus.client.model.config.JanusCommand
import com.kienht.janus.client.model.config.JanusError
import com.kienht.janus.client.model.config.JanusState
import com.kienht.janus.client.plugin.JanusPluginName
import com.kienht.janus.example.databinding.EchoActivityBinding
import org.webrtc.*
import org.webrtc.audio.JavaAudioDeviceModule
import org.webrtc.voiceengine.WebRtcAudioUtils
import java.util.concurrent.Executors

class EchoActivity : AppCompatActivity(), OnJanusListener, JanusCallingEventListener {

    private val executor = Executors.newSingleThreadExecutor()

    private val eglBase by lazy {
        EglBase.create()
    }

    private val echoPlugin = JanusManager.getInstance().echoPlugin
    private var janusWSConnected: Boolean? = null

    private lateinit var peerConnectionFactory: PeerConnectionFactory
    private var peerConnection: PeerConnection? = null

    private var localMediaStream: MediaStream? = null
    private var cameraVideoCapturer: CameraVideoCapturer? = null

    private val localMediaStreamLiveData = MutableLiveData<MediaStream>()
    private val remoteMediaStreamLiveData = MutableLiveData<MediaStream>()

    private val peerConnectionObserver = object : RTCPeerConnectionObserver() {

        override fun onIceGatheringChange(iceGatheringState: PeerConnection.IceGatheringState?) {
            super.onIceGatheringChange(iceGatheringState)
            if (iceGatheringState == PeerConnection.IceGatheringState.COMPLETE) {
                echoPlugin.execute(JanusCommand.TrickleComplete)
            }
        }

        @SuppressLint("NullSafeMutableLiveData")
        override fun onAddStream(mediaStream: MediaStream?) {
            super.onAddStream(mediaStream)
            if (mediaStream != null) {
                remoteMediaStreamLiveData.postValue(mediaStream)
            }
        }

        override fun onIceCandidate(iceCandidate: IceCandidate?) {
            super.onIceCandidate(iceCandidate)
            if (iceCandidate == null) {
                echoPlugin.execute(JanusCommand.TrickleComplete)
            } else {
                echoPlugin.execute(JanusCommand.Trickle(iceCandidate))
            }
        }
    }

    private val callPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            if (result.isNotEmpty() && result.values.all { it }) {
                if (localMediaStream == null) {
                    createLocalMediaStream()
                    createPeerConnection()
                } else {
                    createOffer()
                }
            }
        }

    private lateinit var binding: EchoActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EchoActivityBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        binding.localViewRender.init(eglBase.eglBaseContext, null)
        binding.localViewRender.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL)

        binding.remoteViewRender.init(eglBase.eglBaseContext, null)
        binding.remoteViewRender.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL)

        initPeerConnectionFactory()
        createPeerConnectionFactory()

        localMediaStreamLiveData
            .observe(this, Observer { track ->
                track.videoTracks.firstOrNull()?.addSink(binding.localViewRender)
            })

        remoteMediaStreamLiveData
            .observe(this, Observer { track ->
                track.videoTracks.firstOrNull()?.addSink(binding.remoteViewRender)
            })

        echoPlugin.onJanusListener = this
        echoPlugin.janusCallingEventListener = this

        echoPlugin.initWS(WSS)
        echoPlugin.observeWebSocket()

        binding.buttonEcho.setOnClickListener {
            callPermissions.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
                )
            )
        }

        binding.buttonEcho.performClick()
    }

    override fun onDestroy() {
        super.onDestroy()
        echoPlugin.close()
        echoPlugin.janusCallingEventListener = null
        echoPlugin.onJanusListener = null
    }

    override fun onJanusStateChanged(plugin: JanusPluginName, state: JanusState) {
        when (state) {
            JanusState.READY -> {
                echoPlugin.execute(JanusCommand.Register(JANUS_TOKEN, USER_ID))
            }
            JanusState.REGISTERED -> {

            }
        }
    }

    override fun onJanusConnectionChanged(isConnected: Boolean) {
        if (janusWSConnected == false && isConnected) {
            echoPlugin.execute(JanusCommand.Claim)
        }
        janusWSConnected = isConnected
    }

    override fun onJanusIncoming(userId: String, remoteSdp: SessionDescription) {
        setRemoteDescription(remoteSdp) {

        }
    }

    override fun onJanusAccepted(userId: String, remoteSdp: SessionDescription) {

    }

    override fun onJanusHangup() {

    }

    override fun onJanusError(error: JanusError) {

    }

    private fun initPeerConnectionFactory() {
        executor.execute {
            PeerConnectionFactory.initialize(
                PeerConnectionFactory.InitializationOptions.builder(applicationContext)
                    .setFieldTrials("WebRTC-IntelVP8/Enabled/WebRTC-H264HighProfile/Enabled/WebRTC-MediaTekH264/Enabled/")
                    .setEnableInternalTracer(true)
                    .createInitializationOptions()
            )
        }
    }

    private fun createPeerConnectionFactory() {
        executor.execute {
            val decoderFactory = DefaultVideoDecoderFactory(eglBase.eglBaseContext)
            val encoderFactory = DefaultVideoEncoderFactory(eglBase.eglBaseContext, true, true)

            val audioDeviceModule = JavaAudioDeviceModule
                .builder(applicationContext)
                .setUseHardwareAcousticEchoCanceler(true)
                .setUseHardwareNoiseSuppressor(true)
                .createAudioDeviceModule()
            val options = PeerConnectionFactory.Options()
            peerConnectionFactory = PeerConnectionFactory.builder()
                .setOptions(options)
                .setAudioDeviceModule(audioDeviceModule)
                .setVideoDecoderFactory(decoderFactory)
                .setVideoEncoderFactory(encoderFactory)
                .createPeerConnectionFactory()
            audioDeviceModule.release()
        }
    }

    private fun createPeerConnection() {
        executor.execute {
            val googleStun = PeerConnection.IceServer.builder(defaultICEServers).createIceServer()
            val iceServers = listOf(googleStun)
            val configuration = PeerConnection.RTCConfiguration(iceServers)
                .apply {
                    tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.ENABLED
                    sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
                    bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE
                    continualGatheringPolicy =
                        PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
                    enableRtpDataChannel = false
                    enableDtlsSrtp = true
                    enableCpuOveruseDetection = true
                }

            peerConnection =
                peerConnectionFactory.createPeerConnection(configuration, peerConnectionObserver)

            localMediaStream?.let { localMediaStream ->
                localMediaStream.audioTracks.firstOrNull()?.let { peerConnection?.addTrack(it) }
                localMediaStream.videoTracks.firstOrNull()?.let { peerConnection?.addTrack(it) }
            }
        }
    }

    private fun createLocalMediaStream() {
        executor.execute {
            val localMediaStream = peerConnectionFactory.createLocalMediaStream(LOCAL_MEDIA_IDS)
                .also {
                    this.localMediaStream = it
                }
            localMediaStream.addTrack(createLocalAudioSource())
            localMediaStream.addTrack(createLocalVideoSource())

            localMediaStreamLiveData.postValue(localMediaStream)
        }
    }

    private fun createLocalAudioSource(): AudioTrack {
        WebRtcAudioUtils.setWebRtcBasedAcousticEchoCanceler(true)
        WebRtcAudioUtils.setWebRtcBasedNoiseSuppressor(true)
        WebRtcAudioUtils.setWebRtcBasedAutomaticGainControl(true)

        val audioConstraints = MediaConstraints()
            .apply {
                mandatory.add(MediaConstraints.KeyValuePair("googEchoCancellation", "true"))
                mandatory.add(MediaConstraints.KeyValuePair("googEchoCancellation2", "true"))
                mandatory.add(MediaConstraints.KeyValuePair("googDAEchoCancellation", "true"))
                mandatory.add(MediaConstraints.KeyValuePair("googAutoGainControl", "true"))
                mandatory.add(MediaConstraints.KeyValuePair("googAutoGainControl2", "true"))
                mandatory.add(MediaConstraints.KeyValuePair("googNoiseSuppression", "true"))
                mandatory.add(MediaConstraints.KeyValuePair("googNoiseSuppression2", "true"))
                mandatory.add(MediaConstraints.KeyValuePair("googTypingNoiseDetection", "true"))
                mandatory.add(MediaConstraints.KeyValuePair("googHighpassFilter", "true"))
                mandatory.add(MediaConstraints.KeyValuePair("googAudioMirroring", "false"))
            }

        val audioSource = peerConnectionFactory.createAudioSource(audioConstraints)
        return peerConnectionFactory.createAudioTrack(AUDIO_TRACK_ID, audioSource)
    }

    private fun createLocalVideoSource(): VideoTrack {
        val cameraEnumerator = if (Camera2Enumerator.isSupported(applicationContext)) {
            Camera2Enumerator(applicationContext)
        } else {
            Camera1Enumerator(true)
        }

        fun findDeviceCamera(
            cameraEnumerator: CameraEnumerator,
            frontFacing: Boolean
        ) =
            cameraEnumerator.deviceNames.firstOrNull { cameraEnumerator.isFrontFacing(it) == frontFacing }

        var deviceName = findDeviceCamera(cameraEnumerator, true)
        if (deviceName == null) {
            deviceName = findDeviceCamera(cameraEnumerator, false)
        }
        cameraVideoCapturer = cameraEnumerator.createCapturer(deviceName, null)

        val surfaceTextureHelper =
            SurfaceTextureHelper.create("CaptureThread", eglBase.eglBaseContext)

        val videoSource =
            peerConnectionFactory.createVideoSource(cameraVideoCapturer?.isScreencast ?: false)

        cameraVideoCapturer?.initialize(
            surfaceTextureHelper,
            applicationContext,
            videoSource?.capturerObserver
        )
        cameraVideoCapturer?.startCapture(1280, 720, 30)

        return peerConnectionFactory.createVideoTrack(VIDEO_TRACK_ID, videoSource)
    }

    private fun createOffer() {
        executor.execute {
            val mediaConstraints = MediaConstraints()
                .apply {
                    mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
                    mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
                }
            peerConnection?.createOffer(object : RTCSdpObserver("Create Offer") {
                override fun onCreateSuccess(sessionDescription: SessionDescription?) {
                    if (sessionDescription != null) {
                        super.onCreateSuccess(sessionDescription)
                        setLocalDescription(sessionDescription)
                        echoPlugin.execute(JanusCommand.Call(sessionDescription))
                    }

                }
            }, mediaConstraints)
        }
    }

    private fun createAnswer() {
        executor.execute {
            peerConnection?.createAnswer(object : RTCSdpObserver("Create Answer") {
                override fun onCreateSuccess(sessionDescription: SessionDescription?) {
                    if (sessionDescription != null) {
                        super.onCreateSuccess(sessionDescription)
                        setLocalDescription(sessionDescription)
                        echoPlugin.execute(JanusCommand.Answer(sessionDescription))
                    }
                }
            }, MediaConstraints())
        }
    }

    private fun setRemoteDescription(sdp: SessionDescription, onSetSuccess: () -> Unit) {
        executor.execute {
            peerConnection?.setRemoteDescription(
                object : RTCSdpObserver("Set Remote SDP") {
                    override fun onSetSuccess() {
                        super.onSetSuccess()
                        onSetSuccess()
                    }
                },
                sdp
            )
        }
    }

    private fun setLocalDescription(sdp: SessionDescription) {
        peerConnection?.setLocalDescription(object : RTCSdpObserver("Set Local SDP") {
            override fun onSetSuccess() {
                Log.e(TAG, "setLocalDescription onSetSuccess: ")
            }
        }, sdp)
    }

    companion object {
        private val TAG: String = EchoActivity::class.java.simpleName

        private const val WSS = ""
        private const val USER_ID = ""
        private const val JANUS_TOKEN = ""

        private val defaultICEServers = listOf(
            "stun:stun.l.google.com:19302",
            "stun:stun1.l.google.com:19302",
            "stun:stun2.l.google.com:19302",
            "stun:stun3.l.google.com:19302",
            "stun:stun4.l.google.com:19302"
        )
        private const val LOCAL_MEDIA_IDS = "ARDAMS"
        private const val VIDEO_TRACK_ID = "ARDAMSv0"
        private const val AUDIO_TRACK_ID = "ARDAMSa0"
    }
}