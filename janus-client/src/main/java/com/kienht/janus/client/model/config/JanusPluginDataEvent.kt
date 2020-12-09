package com.kienht.janus.client.model.config

/**
 * @author kienht
 * @since 23/09/2020
 */
enum class JanusPluginDataEvent {
    EVENT {
        override val value: String
            get() = "event"
    },

    REGISTERED {
        override val value: String
            get() = "registered"
    },

    ACCEPTED {
        override val value: String
            get() = "accepted"
    },

    HANGUP {
        override val value: String
            get() = "hangup"
    },

    INCOMING_CALL {
        override val value: String
            get() = "incomingcall"
    },

    JOINED {
        override val value: String
            get() = "joined"
    },

    LEAVING {
        override val value: String
            get() = "leaving"
    };

    abstract val value: String
}