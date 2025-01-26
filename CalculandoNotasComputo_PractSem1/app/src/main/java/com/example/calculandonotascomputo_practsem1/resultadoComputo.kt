package com.example.calculandonotascomputo_practsem1

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView

class resultadoComputo : AppCompatActivity() {

    private lateinit var resultTextView: TextView
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resultado_computo)

        resultTextView = findViewById(R.id.resultTextView)
        backButton = findViewById(R.id.backButton)

        val studentName = intent.getStringExtra("studentName") ?: "Desconocido"
        val minGrade = intent.getDoubleExtra("minGrade", 0.0)
        val computationAverages = intent.getDoubleArrayExtra("computationAverages") ?: doubleArrayOf()

        // Promedio final
        val finalAverage = computationAverages.average()

        // Construcción del mensaje para los cómputos
        val computationsMessage = computationAverages.mapIndexed { index, average ->
            "Cómputo ${index + 1}: Promedio = %.2f".format(average)
        }.joinToString("\n")

        // Evaluación de aprobación
        val approvalMessage: String
        val approvalColor: Int
        if (finalAverage >= minGrade) {
            approvalMessage = "¡Aprobado!"
            approvalColor = resources.getColor(android.R.color.holo_green_dark, null)
        } else {
            val pointsMissing = minGrade - finalAverage //puntos faltantes
            approvalMessage = "No Aprobado.\nLe faltan %.2f puntos.".format(pointsMissing)
            approvalColor = resources.getColor(android.R.color.holo_red_dark, null)
        }

        // Crear un SpannableString(aplicar diferentes estilos de texto) para los resultados
        val baseMessage = buildString {
            append("Estudiante: $studentName\n\n")
            append("$computationsMessage\n\n")
            append("Promedio final: %.2f\n\n".format(finalAverage))
            append("$approvalMessage")
        }

        val spannableMessage = SpannableString(baseMessage)

        // Resaltar el promedio final
        val startAverage = baseMessage.indexOf("%.2f".format(finalAverage))
        val endAverage = startAverage + "%.2f".format(finalAverage).length
        spannableMessage.setSpan(
            ForegroundColorSpan(approvalColor),
            startAverage,
            endAverage,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Resaltar el mensaje de aprobación/reprobación
        val startApproval = baseMessage.indexOf(approvalMessage)
        spannableMessage.setSpan(
            ForegroundColorSpan(approvalColor), //Aplica el color al txt

            startApproval,
            baseMessage.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Configurar el TextView con los saltos de línea correctos
        resultTextView.text = spannableMessage

        backButton.setOnClickListener { finish() }
    }
}
