package com.example.byteflowsih.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.byteflowsih.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val loginBtn = findViewById<Button>(R.id.loginButton)

        loginBtn.setOnClickListener {
            val intent = Intent(this, InputActivity::class.java)
            startActivity(intent)
        }

    }
}