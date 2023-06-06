package com.zzx.hooktest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class PluginTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plugin_test)
        val stringExtra = intent.getStringExtra("test")
        if (stringExtra != null) {
            Toast.makeText(this, stringExtra, Toast.LENGTH_SHORT).show()
        }
    }
}