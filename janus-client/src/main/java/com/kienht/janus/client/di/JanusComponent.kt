package com.kienht.janus.client.di

import com.kienht.janus.client.JanusManager
import com.kienht.janus.client.di.qualifier.JanusEchoPluginQualifier
import com.kienht.janus.client.di.qualifier.JanusVideoCallPluginQualifier
import com.kienht.janus.client.di.qualifier.JanusVideoRoomPluginQualifier
import com.kienht.janus.client.plugin.JanusPlugin
import dagger.Component
import javax.inject.Singleton

/**
 * @author kienht
 * @since 09/12/2020
 */
@Singleton
@Component(modules = [JanusModule::class])
internal interface JanusComponent {

    fun inject(janusManager: JanusManager)

    @JanusEchoPluginQualifier
    fun provideJanusEchoPlugin(): JanusPlugin

    @JanusVideoCallPluginQualifier
    fun provideJanusVideoCallPlugin(): JanusPlugin

    @JanusVideoRoomPluginQualifier
    fun provideJanusVideoRoomPlugin(): JanusPlugin

    @Component.Factory
    interface Factory {

        fun janusComponent(janusModule: JanusModule): JanusComponent
    }
}