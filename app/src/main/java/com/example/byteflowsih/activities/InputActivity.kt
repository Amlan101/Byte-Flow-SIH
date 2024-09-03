package com.example.byteflowsih.activities

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.byteflowsih.R

class InputActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)
        val submitButton = findViewById<Button>(R.id.submitButton)
        submitButton.setOnClickListener {
            Toast.makeText(this, "Submitted Data", Toast.LENGTH_SHORT).show()
        }
    }
}