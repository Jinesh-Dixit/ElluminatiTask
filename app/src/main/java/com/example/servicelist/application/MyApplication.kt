package com.example.servicelist.application

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class MyApplication : Application() {


    companion object {
        @SuppressLint("StaticFieldLeak")
        var ctx: Context? = null
    }

    override fun onCreate() {
        super.onCreate()
        ctx = applicationContext
    }


}