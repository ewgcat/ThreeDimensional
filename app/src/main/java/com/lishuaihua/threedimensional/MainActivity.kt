package com.lishuaihua.threedimensional

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val iv_background = findViewById<ImageView>(R.id.iv_background)
        val iv_mid = findViewById<ImageView>(R.id.iv_mid)
        val iv_foreground = findViewById<ImageView>(R.id.iv_foreground)
        iv_background.setImageResource(R.mipmap.background1)
        iv_mid.setImageResource(R.mipmap.mid1)
        iv_foreground.setImageResource(R.mipmap.foreground1)
    }
}