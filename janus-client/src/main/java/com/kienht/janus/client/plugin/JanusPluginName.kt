package com.kienht.janus.client.plugin

/**
 * @author kienht
 * @since 08/09/2020
 */
enum class JanusPluginName {

    ECHO {
        override val plugin: String
            get() = "janus.plugin.echotest"
    },

    VIDEO_CALL {
        override val plugin: String
            get() = "janus.plugin.videocall"
    },

    VIDEO_ZOOM {
        override val plugin: String
            get() = "janus.plugin.videoroom"
    };

    abstract val plugin: String
}