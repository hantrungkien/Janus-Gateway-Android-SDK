package com.kienht.janus.client

import android.app.Application
import android.content.Context
import com.kienht.janus.client.di.DaggerJanusComponent
import com.kienht.janus.client.di.JanusModule
import com.kienht.janus.client.di.qualifier.JanusEchoPluginQualifier
import com.kienht.janus.client.di.qualifier.JanusVideoCallPluginQualifier
import com.kienht.janus.client.di.qualifier.JanusVideoRoomPluginQualifier
import com.kienht.janus.client.plugin.JanusPlugin
import javax.inject.Inject

/**
 * @author kienht
 * @since 09/12/2020
 */
class JanusManager {

    val echoPlugin: JanusPlugin
        get() = _echoPlugin

    val videoCallPlugin: JanusPlugin
        get() = _videoCallPlugin

    val videoRoomPlugin: JanusPlugin
        get() = _videoRoomPlugin

    @Inject
    @JanusEchoPluginQualifier
    internal lateinit var _echoPlugin: JanusPlugin

    @Inject
    @JanusVideoCallPluginQualifier
    internal lateinit var _videoCallPlugin: JanusPlugin

    @Inject
    @JanusVideoRoomPluginQualifier
    internal lateinit var _videoRoomPlugin: JanusPlugin

    fun init(applicationContext: Context) {
        if (applicationContext !is Application) throw IllegalArgumentException()
        DaggerJanusComponent.factory()
            .janusComponent(JanusModule(applicationContext))
            .inject(this)
    }

    companion object {
        @Volatile
        private var instance: JanusManager? = null

        fun getInstance(): JanusManager {
            return instance ?: synchronized(this) {
                instance ?: JanusManager()
                    .also {
                        instance = it
                    }
            }
        }
    }
}