package com.kienht.janus.client.di.qualifier

import javax.inject.Qualifier

/**
 * @author kienht
 * @since 11/09/2020
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
internal annotation class JanusVideoRoomPluginQualifier