package com.kienht.janus.client.model.config

/**
 * @author kienht
 * @since 08/09/2020
 */
enum class JanusEventName {
    SUCCESS {
        override val value: String
            get() = "success"
    },
    ACK {
        override val value: String
            get() = "ack"
    },
    EVENT {
        override val value: String
            get() = "event"
    },
    DETACHED {
        override val value: String
            get() = "detached"
    },
    ERROR {
        override val value: String
            get() = "error"
    };

    abstract val value: String
}