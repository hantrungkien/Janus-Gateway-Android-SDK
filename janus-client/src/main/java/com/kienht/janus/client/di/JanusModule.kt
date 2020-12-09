package com.kienht.janus.client.di

import android.content.Context
import com.kienht.janus.client.BuildConfig
import com.kienht.janus.client.di.qualifier.JanusEchoPluginQualifier
import com.kienht.janus.client.di.qualifier.JanusVideoCallPluginQualifier
import com.kienht.janus.client.di.qualifier.JanusVideoRoomPluginQualifier
import com.kienht.janus.client.plugin.JanusPlugin
import com.kienht.janus.client.plugin.echo.JanusEchoPlugin
import com.kienht.janus.client.plugin.videocall.JanusVideoCallPlugin
import com.kienht.janus.client.plugin.videoroom.JanusVideoRoomPlugin
import com.kienht.janus.client.utils.JanusMoshiAdapters
import com.kienht.janus.client.websocket.JanusWSClient
import com.kienht.janus.client.websocket.JanusWSClientImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 * @author kienht
 * @since 09/12/2020
 */
@Module
internal class JanusModule(private val context: Context) {

    @Provides
    @JanusEchoPluginQualifier
    fun provideJanusEchoPlugin(janusWSClient: JanusWSClient): JanusPlugin =
        JanusEchoPlugin(janusWSClient)

    @Provides
    @JanusVideoCallPluginQualifier
    fun provideJanusVideoCallPlugin(janusWSClient: JanusWSClient, moshi: Moshi): JanusPlugin =
        JanusVideoCallPlugin(janusWSClient, moshi)

    @Provides
    @JanusVideoRoomPluginQualifier
    fun provideJanusVideoRoomPlugin(janusWSClient: JanusWSClient, moshi: Moshi): JanusPlugin =
        JanusVideoRoomPlugin(janusWSClient, moshi)

    @Provides
    fun provideMoshi() = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .add(JanusMoshiAdapters)
        .build()

    @Provides
    fun provideJanusWSClient(
        okHttpClient: OkHttpClient,
        moshi: Moshi
    ): JanusWSClient = JanusWSClientImpl(context, okHttpClient, moshi)

    @Provides
    fun provideJanusWSOkHttp(
        logging: HttpLoggingInterceptor
    ) = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(logging)
        .addInterceptor { chain ->
            val originalResponse = chain.proceed(chain.request())
            originalResponse.newBuilder()
                .addHeader("Content-type", "application/json")
                .build()
        }
        .retryOnConnectionFailure(true)
        .pingInterval(5000, TimeUnit.SECONDS)
        .build()

    @Provides
    fun provideJanusWSOkHttpLogging() = HttpLoggingInterceptor()
        .apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
}