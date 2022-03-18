package com.toxicflame427.simpletimer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Thread{
            Thread.sleep(2500)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }.start()
    }
}