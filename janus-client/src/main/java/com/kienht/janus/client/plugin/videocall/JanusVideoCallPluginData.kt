package com.kienht.janus.client.plugin.videocall

import com.kienht.janus.client.model.config.JanusPluginDataEvent
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * @author kienht
 * @since 09/09/2020
 */
@JsonClass(generateAdapter = true)
internal data class JanusVideoCallPluginData(
    @Json(name = "videocall") val videoCall: String?,
    @Json(name = "result") val result: Result?,
    @Json(name = "error_code") val errorCode: Int?,
    @Json(name = "error") val error: String?
) {

    @JsonClass(generateAdapter = true)
    data class Result(
        @Json(name = "event") val event: JanusPluginDataEvent?,
        @Json(name = "username") val userId: String?,
        @Json(name = "media") val media: String?,
        @Json(name = "reason") val reason: String?
    )
}