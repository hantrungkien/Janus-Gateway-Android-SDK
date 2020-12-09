package com.kienht.janus.example

import android.app.Application
import com.kienht.janus.client.JanusManager

/**
 * @author kienht
 * @since 09/12/2020
 */
class CallApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        JanusManager.getInstance().init(this)
    }
}