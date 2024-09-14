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
        val equipmentEmissionsInput = findViewById<EditText>(R.id.inputEquipmentEmissions).text.toString().toDoubleOrNull()
        val electricalEmissionsInput = findViewById<EditText>(R.id.inputElectricalEmissions).text.toString().toDoubleOrNull()
        val methaneInput = findViewById<EditText>(R.id.inputMethane).text.toString().toDoubleOrNull()
        val scope3EmissionsInput = findViewById<EditText>(R.id.inputScope3).text.toString().toDoubleOrNull()
        val energyConsumptionInput = findViewById<EditText>(R.id.inputEnergyConsumption).text.toString().toDoubleOrNull()
        val fuelConsumptionInput = findViewById<EditText>(R.id.inputFuelConsumption).text.toString().toDoubleOrNull()
        val selectedState = findViewById<Spinner>(R.id.spinner).selectedItem.toString()

        // Performing null check on the inputs
        if (equipmentEmissionsInput == null || electricalEmissionsInput == null || methaneInput == null ||
            scope3EmissionsInput == null || energyConsumptionInput == null || fuelConsumptionInput == null) {
            Toast.makeText(this, "Please enter valid values for all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Fetch the existing data for the selected state
        val stateRef = database.child("states").child(selectedState)
        Log.d("Firebase", "Selected State: $selectedState")

        stateRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val snapshot = task.result
                if (snapshot.exists()) {
                    // If the state exists, get the current values
                    val currentEquipmentEmissions = snapshot.child("Equipment Emissions").getValue(Double::class.java) ?: 0.0
                    val currentElectricalEmissions = snapshot.child("Electrical Emissions").getValue(Double::class.java) ?: 0.0
                    val currentMethaneEmissions = snapshot.child("Methane Emissions").getValue(Double::class.java) ?: 0.0
                    val currentScope3Emissions = snapshot.child("Scope 3 Emissions").getValue(Double::class.java) ?: 0.0
                    val currentEnergyConsumption = snapshot.child("Energy Consumption").getValue(Double::class.java) ?: 0.0
                    val currentFuelConsumption = snapshot.child("Fuel Consumption").getValue(Double::class.java) ?: 0.0

                    Log.d("Firebase", "Current Methane Emissions: $currentMethaneEmissions")
                    Log.d("Firebase", "Current Equipment Emissions: $currentEquipmentEmissions")
                    Log.d("Firebase", "Current Electrical Emissions: $currentElectricalEmissions")
                    Log.d("Firebase", "Current Scope 3 Emissions: $currentScope3Emissions")
                    Log.d("Firebase", "Current Energy Consumption: $currentEnergyConsumption")
                    Log.d("Firebase", "Current Fuel Consumption: $currentFuelConsumption")

                    // Add the new values to the current values
                    val totalEquipmentEmissions = currentEquipmentEmissions + equipmentEmissionsInput
                    val totalElectricalEmissions = currentElectricalEmissions + electricalEmissionsInput
                    val totalMethaneEmissions = currentMethaneEmissions + methaneInput
                    val totalScope3Emissions = currentScope3Emissions + scope3EmissionsInput
                    val totalEnergyConsumption = currentEnergyConsumption + energyConsumptionInput
                    val totalFuelConsumption = currentFuelConsumption + fuelConsumptionInput

                    // Update the data in Firebase
                    val data = mapOf(
                        "Equipment Emissions" to totalEquipmentEmissions,
                        "Electrical Emissions" to totalElectricalEmissions,
                        "Methane Emissions" to totalMethaneEmissions,
                        "Scope 3 Emissions" to totalScope3Emissions,
                        "Energy Consumption" to totalEnergyConsumption,
                        "Fuel Consumption" to totalFuelConsumption
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
                        "Equipment Emissions" to equipmentEmissionsInput,
                        "Electrical Emissions" to electricalEmissionsInput,
                        "Methane Emissions" to methaneInput,
                        "Scope 3 Emissions" to scope3EmissionsInput,
                        "Energy Consumption" to energyConsumptionInput,
                        "Fuel Consumption" to fuelConsumptionInput
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