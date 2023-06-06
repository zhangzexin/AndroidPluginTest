package com.zzx.plugin

import android.os.Bundle
import android.view.LayoutInflater
import com.zzx.plugextend.PlugBaseActivity

class ScrollingActivity : PlugBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(pluginInHostRunning) {
            setContentView(LayoutInflater.from(mContext).inflate(R.layout.plugin_activity, null))
        } else {
            setContentView(LayoutInflater.from(this).inflate(R.layout.plugin_activity, null))
        }

    }

}