package com.kienht.janus.client.model.request

import com.kienht.janus.client.model.config.JanusCommandName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * @author kienht
 * @since 07/09/2020
 */
@JsonClass(generateAdapter = true)
data class JanusCreateSessionRequest(
    @Json(name = "janus") val name: JanusCommandName,
    @Json(name = "transaction") val transactionId: String
)