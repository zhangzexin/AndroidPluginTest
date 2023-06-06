package com.zzx.hooktest

import android.app.Application
import android.content.Context
import android.os.Build
import com.zzx.pluglib.enums.HookActivityMode
import com.zzx.pluglib.PlugImpl
import org.lsposed.hiddenapibypass.HiddenApiBypass

class MyAppAplication: Application() {
    override fun attachBaseContext(context: Context) {
        super.attachBaseContext(context)
        if (Build.VERSION.SDK_INT >= 28) HiddenApiBypass.addHiddenApiExemptions("")
    }
    override fun onCreate() {
        super.onCreate()
        PlugImpl.init(baseContext, HookActivityMode.InstrumentationMode)
    }
}