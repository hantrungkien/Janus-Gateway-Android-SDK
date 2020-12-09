package com.kienht.janus.client.model.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * @author kienht
 * @since 09/09/2020
 */
@JsonClass(generateAdapter = true)
data class JanusJsepRequest(
    @Json(name = "type") val type: String,
    @Json(name = "sdp") val sdp: String
)