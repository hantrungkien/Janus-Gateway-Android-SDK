package com.kienht.janus.client.model.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * @author kienht
 * @since 09/09/2020
 */
@JsonClass(generateAdapter = true)
data class JanusBodyRequest(
    @Json(name = "request") val request: Request,
    @Json(name = "username") val userId: String? = null,
    @Json(name = "token") val token: String? = null,
    @Json(name = "sid") val sid: String? = null,
    @Json(name = "video") val video: Boolean? = false,
    @Json(name = "audio") val audio: Boolean? = false
) {
    enum class Request {
        CALL {
            override val value: String
                get() = "call"
        },

        ACCEPT {
            override val value: String
                get() = "accept"
        },

        HANGUP {
            override val value: String
                get() = "hangup"
        },

        REGISTER {
            override val value: String
                get() = "register"
        };

        abstract val value: String
    }
}