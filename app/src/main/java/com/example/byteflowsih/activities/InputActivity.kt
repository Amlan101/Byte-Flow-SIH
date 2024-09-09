package com.example.byteflowsih.activities

import android.os.Bundle
import android.util.Log
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

        // Calculate the new carbon emissions
        val newCarbonEmissions = excavationInput + transportationInput + equipmentInput

        // Fetch the existing data for the selected state
        val stateRef = database.child("states").child(selectedState)
        Log.d("Firebase", "Selected State: $selectedState")

        stateRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                if (snapshot.exists()) {
                    // If the state exists, get the current values
                    val currentCarbonEmissions = snapshot.child("carbonEmissions").getValue(Double::class.java) ?: 0.0
                    val currentMethaneEmissions = snapshot.child("methaneEmissions").getValue(Double::class.java) ?: 0.0
                    Log.d("Firebase", "Current Carbon Emissions: $currentCarbonEmissions")
                    Log.d("Firebase", "Current Methane Emissions: $currentMethaneEmissions")

                    // Add new values to the current values
                    val totalCarbonEmissions = currentCarbonEmissions + newCarbonEmissions
                    val totalMethaneEmissions = currentMethaneEmissions + methaneInput

                    // Update the data in Firebase
                    val data = mapOf(
                        "carbonEmissions" to totalCarbonEmissions,
                        "methaneEmissions" to totalMethaneEmissions
                    )

                    stateRef.updateChildren(data).addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            Toast.makeText(this, "Data added successfully!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Failed to add data.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    // If the state does not exist, create a new entry
                    val data = mapOf(
                        "carbonEmissions" to newCarbonEmissions,
                        "methaneEmissions" to methaneInput
                    )

                    stateRef.setValue(data).addOnCompleteListener { newTask ->
                        if (newTask.isSuccessful) {
                            Toast.makeText(this, "New state data added successfully!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Failed to add new state data.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Error retrieving state data.", Toast.LENGTH_SHORT).show()
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