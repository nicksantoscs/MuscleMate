package com.lh1110642.gymgenie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import com.lh1110642.gymgenie.databinding.ActivityBodyCalculationBinding
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import org.checkerframework.checker.units.qual.m

class BodyCalculationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBodyCalculationBinding
    private lateinit var bodyStats: BodyStats
    var auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBodyCalculationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bodyFatTextView.visibility = View.INVISIBLE
        binding.healthTextView.visibility = View.INVISIBLE
        binding.normRangeTextView.visibility = View.INVISIBLE


        binding.bodyFatResultTextView.visibility = View.INVISIBLE
        binding.healthResultTextView.visibility = View.INVISIBLE
        binding.normRangeResultTextView.visibility = View.INVISIBLE

        val weightText = findViewById<EditText>(R.id.wtText)
        val heightText = findViewById<EditText>(R.id.htText)
        val calcButton = findViewById<Button>(R.id.calcBtn)

        calcButton.setOnClickListener {
            val weight = weightText.text.toString()
            val height = heightText.text.toString()
            if(validateInput(weight,height)) {
                val bmi = (0.45*weight.toFloat())/((height.toFloat()/100)*(height.toFloat()/100))
                val bmiFinal = String.format("%.2f", bmi).toFloat()
                displayResult(bmiFinal)
             }
        }

        binding.saveBtn.setOnClickListener {
            database(bodyStats)
        }

        /*height = cmToM(height)
        weight = kgToLb(weight)
        bmi = calculateBMI(weight,height)

        println("The height is $height m and the weight is $weight pound. The BMI is $bmi%.")

    }

    fun cmToM(cm: Double): Double {
        return cm / 100.0
    }

    fun kgToLb(kg: Double): Double {
        return kg * 2.20462
    }

    fun calculateBMI(weight: Double, height: Double): Double {
        return weight / (height * height)
    }*/

        //test


}

    private fun validateInput(weight: String?, height:String?):Boolean {
        return when {
            weight.isNullOrEmpty() -> {
                Toast.makeText(this,  "Weight is empty", Toast.LENGTH_LONG).show()
                return false
            }
            height.isNullOrEmpty() -> {
                Toast.makeText(this,  "Height is empty", Toast.LENGTH_LONG).show()
                return false
            }
            else -> {
                return true
            }
        }
    }

    private fun displayResult(bmi: Float) {
        binding.bodyFatResultTextView.text = bmi.toString()
        binding.normRangeResultTextView.text= "18.5-24.9"

        var resultText = ""

        when {
            bmi < 18.50 -> {
                resultText = "Under Weight"
            }
            bmi in 18.50..24.99-> {
                resultText= "Normal"
            }
            bmi in 25.00..29.99 -> {
                resultText = "Overweight"
            }
            bmi > 29.99 -> {
                resultText = "Obese"
            }
        }
        binding.healthResultTextView.text = resultText

        binding.bodyFatTextView.visibility = View.VISIBLE
        binding.healthTextView.visibility = View.VISIBLE
        binding.normRangeTextView.visibility = View.VISIBLE


        binding.bodyFatResultTextView.visibility = View.VISIBLE
        binding.healthResultTextView.visibility = View.VISIBLE
        binding.normRangeResultTextView.visibility = View.VISIBLE
    }


    fun database(stats: BodyStats){
        val db = FirebaseFirestore.getInstance().collection("bodystats")

        val id = bodyStats.height+Firebase.auth.currentUser?.uid

        Log.w("DB_Parth", auth.currentUser!!.uid)
        if (stats != null) { //Gets the users ID
            stats.setId(auth.currentUser!!.uid)
        }
        //Writes to the database
        if (stats != null) {
            bodyStats.setHeight(bodyStats.height)
            bodyStats.setWeight(bodyStats.weight)
            bodyStats.setBmi(bodyStats.bmi)
            db.document(id).set(stats)
                .addOnSuccessListener {  Log.w("DB_Parth", "ADDED") }
                .addOnFailureListener{ Log.w("DB_Fail", it.localizedMessage)}
        }


    }

}