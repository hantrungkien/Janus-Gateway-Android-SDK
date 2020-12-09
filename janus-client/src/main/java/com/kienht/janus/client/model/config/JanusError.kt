package com.kienht.janus.client.model.config

/**
 * @author kienht
 * @since 16/09/2020
 */
enum class JanusError {

    NO_SUCH_SESSION {
        override val code: Int
            get() = 458
    },

    NOT_REGISTERED_YET {
        override val code: Int
            get() = 473
    },

    USER_ALREADY_TAKEN {
        override val code: Int
            get() = 476
    },

    USER_NOT_EXIST {
        override val code: Int
            get() = 478
    };

    abstract val code: Int

    companion object {
        fun get(errorCode: Int) = values().find { it.code == errorCode }
    }

}