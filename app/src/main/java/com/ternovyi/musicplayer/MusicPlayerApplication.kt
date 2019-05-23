package com.ternovyi.musicplayer

import android.app.Application
import android.databinding.DataBindingUtil
import com.ternovyi.musicplayer.util.databinding.DefaultDataBindingComponent

class MusicPlayerApplication : Application() {

    companion object {
        lateinit var instance: MusicPlayerApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        DataBindingUtil.setDefaultComponent(DefaultDataBindingComponent())
    }
}