package com.example.byteflowsih.activities

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.byteflowsih.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class InputActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)

        database = FirebaseDatabase.getInstance().reference

        setupSpinner()

        val submitButton = findViewById<Button>(R.id.submitButton)
        submitButton.setOnClickListener {
            submitData()
        }
    }

    private fun submitData() {

        // Collecting data from user
        val excavationInput =
            findViewById<EditText>(R.id.inputExcavation).text.toString().toDoubleOrNull()
        val transportationInput =
            findViewById<EditText>(R.id.inputTransportation).text.toString().toDoubleOrNull()
        val equipmentInput =
            findViewById<EditText>(R.id.inputEquipment).text.toString().toDoubleOrNull()
        val methaneInput =
            findViewById<EditText>(R.id.inputMethane).text.toString().toDoubleOrNull()
        val selectedState = findViewById<Spinner>(R.id.spinner).selectedItem.toString()

        // Validating inputs
        if (excavationInput == null || transportationInput == null || equipmentInput == null || methaneInput == null) {
            Toast.makeText(this, "Please enter valid values for all fields", Toast.LENGTH_SHORT)
                .show()
            return
        }

        // Calculate total carbon emissions
        val totalCarbonEmissions = excavationInput + transportationInput + equipmentInput

        // Data to be sent to Firebase
        val data = mapOf(
            "carbonEmissions" to totalCarbonEmissions,
            "methaneEmissions" to methaneInput
        )

        // Update Firebase with the data for the selected state
        database.child("states").child(selectedState).updateChildren(data)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Data submitted successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to submit data.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun setupSpinner() {
        val spinner = findViewById<Spinner>(R.id.spinner)
        val states = arrayOf(
            "Odisha", "Jharkhand", "Chhattisgarh", "West Bengal", "Madhya Pradesh",
            "Telangana", "Maharashtra", "Meghalaya", "UttarPradesh"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, states)
        spinner.adapter = adapter
    }
}