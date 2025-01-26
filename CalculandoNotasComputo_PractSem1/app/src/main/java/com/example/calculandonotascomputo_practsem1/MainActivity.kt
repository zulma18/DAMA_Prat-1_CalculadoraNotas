package com.example.calculandonotascomputo_practsem1

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    // Variables de los elementos de la interfaz
    private lateinit var studentNameInput: EditText
    private lateinit var miniNote: EditText
    private lateinit var InputNote1: EditText
    private lateinit var InputNote2: EditText
    private lateinit var InputNote3: EditText
    private lateinit var computationSelector: RadioGroup
    private lateinit var calculateButton: Button
    private lateinit var viewResultsButton: Button

    private var computationAverages = DoubleArray(3)
    private var studentName = ""
    private var minGrade = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Referencias a los elementos de la interfaz
        studentNameInput = findViewById(R.id.studentNameInput)
        miniNote = findViewById(R.id.minNote)
        InputNote1 = findViewById(R.id.InputNote1)
        InputNote2 = findViewById(R.id.InputNote2)
        InputNote3 = findViewById(R.id.InputNote3)
        computationSelector = findViewById(R.id.computationSelector)
        calculateButton = findViewById(R.id.calculateButton)
        viewResultsButton = findViewById(R.id.viewResultsButton)

        // Inicialmente deshabilitar el botón de "Ver resultados"
        viewResultsButton.isEnabled = false

        // Aplicar validación a los campos de notas
        validarNota(InputNote1)
        validarNota(InputNote2)
        validarNota(InputNote3)

        // Escuchar cambios en los campos de notas para habilitar/deshabilitar el botón "Calcular"
        val textWatcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                calculateButton.isEnabled = (
                        InputNote1.text.isNotEmpty() ||
                                InputNote2.text.isNotEmpty() ||
                                InputNote3.text.isNotEmpty()
                        )
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }
        InputNote1.addTextChangedListener(textWatcher)
        InputNote2.addTextChangedListener(textWatcher)
        InputNote3.addTextChangedListener(textWatcher)

        calculateButton.setOnClickListener {
            calculateAverage()
            clearNoteFields() // Limpiar los campos de notas después del cálculo del promedio
        }

        viewResultsButton.setOnClickListener { navigateToResults() }
    }

    // Función para validar y corregir la entrada de notas
    private fun validarNota(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val text = s.toString()

                if (text.isNotEmpty()) {
                    val input = text.toDoubleOrNull()
                    if (input != null) {
                        if (input < 0) {
                            editText.setText("0")
                            editText.setSelection(editText.text.length)
                        } else if (input > 10) {
                            editText.setText("10")
                            editText.setSelection(editText.text.length)
                        }
                    } else {
                        editText.setText("") // Si no es un número válido, limpiar el campo
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    // Función para calcular el promedio
    private fun calculateAverage() {
        studentName = studentNameInput.text.toString()
        val minNoteText = miniNote.text.toString()
        val note1Text = InputNote1.text.toString()
        val note2Text = InputNote2.text.toString()
        val note3Text = InputNote3.text.toString()

        // Validar si todos los campos de entrada están llenos
        if (studentName.isBlank() || minNoteText.isBlank() || note1Text.isBlank() ||
            note2Text.isBlank() || note3Text.isBlank()
        ) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Validación para cuando no se ha seleccionado un cómputo
        if (computationSelector.checkedRadioButtonId == -1) {
            Toast.makeText(this, "Por favor, selecciona un cómputo", Toast.LENGTH_SHORT).show()
            return
        }

        minGrade = minNoteText.toDouble()
        val Note1 = note1Text.toDouble()
        val Note2 = note2Text.toDouble()
        val Note3 = note3Text.toDouble()
        val average = (Note1 + Note2 + Note3) / 3

        val selectedComputation = when (computationSelector.checkedRadioButtonId) {
            R.id.computation1 -> {
                computationAverages[0] = average
                "Cómputo 1"
            }
            R.id.computation2 -> {
                computationAverages[1] = average
                "Cómputo 2"
            }
            R.id.computation3 -> {
                computationAverages[2] = average
                "Cómputo 3"
            }
            else -> null
        }

        val resultMessage = if (average >= minGrade) {
            "$selectedComputation: ¡Aprobado! \nPromedio: $average"
        } else {
            "$selectedComputation: No Aprobado :( \nPromedio: $average"
        }

        // Mostrar un Toast con el resultado
        Toast.makeText(this, resultMessage, Toast.LENGTH_LONG).show()

        // Activar el botón de "Ver resultados" después de calcular
        viewResultsButton.isEnabled = true
    }

    // Función para limpiar los campos de notas
    private fun clearNoteFields() {
        InputNote1.text.clear()
        InputNote2.text.clear()
        InputNote3.text.clear()
    }

    // Función para navegar a la actividad de resultados
    private fun navigateToResults() {
        // Crear el Intent para pasar los datos a la actividad de resultado
        val intent = Intent(this, resultadoComputo::class.java).apply {
            putExtra("studentName", studentName)
            putExtra("minGrade", minGrade)
            putExtra("computationAverages", computationAverages)
        }
        startActivity(intent)
    }
}
