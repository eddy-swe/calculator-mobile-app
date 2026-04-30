package com.example.calculator  // ← keep your package name

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var tvDisplay: TextView
    private lateinit var tvExpression: TextView

    private var currentInput = ""
    private var firstOperand = 0.0
    private var pendingOperator = ""
    private var freshResult = false
    private var expression = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvDisplay = findViewById(R.id.tvDisplay)
        tvExpression = findViewById(R.id.tvExpression)

        val numberButtons = mapOf(
            R.id.btn0 to "0", R.id.btn1 to "1", R.id.btn2 to "2",
            R.id.btn3 to "3", R.id.btn4 to "4", R.id.btn5 to "5",
            R.id.btn6 to "6", R.id.btn7 to "7", R.id.btn8 to "8",
            R.id.btn9 to "9"
        )

        for ((id, digit) in numberButtons) {
            findViewById<Button>(id).setOnClickListener { appendDigit(digit) }
        }

        findViewById<Button>(R.id.btnDot).setOnClickListener { appendDot() }
        findViewById<Button>(R.id.btnAdd).setOnClickListener { setOperator("+") }
        findViewById<Button>(R.id.btnSubtract).setOnClickListener { setOperator("−") }
        findViewById<Button>(R.id.btnMultiply).setOnClickListener { setOperator("×") }
        findViewById<Button>(R.id.btnDivide).setOnClickListener { setOperator("÷") }
        findViewById<Button>(R.id.btnEquals).setOnClickListener { calculate() }
        findViewById<Button>(R.id.btnClear).setOnClickListener { clear() }
        findViewById<Button>(R.id.btnNegate).setOnClickListener { negate() }
        findViewById<Button>(R.id.btnPercent).setOnClickListener { percent() }
    }

    private fun appendDigit(digit: String) {
        if (freshResult) {
            currentInput = ""
            expression = ""
            freshResult = false
        }
        if (currentInput == "0" && digit != ".") currentInput = ""
        currentInput += digit
        tvDisplay.text = currentInput
    }

    private fun appendDot() {
        if (freshResult) { currentInput = "0"; expression = ""; freshResult = false }
        if (!currentInput.contains(".")) {
            if (currentInput.isEmpty()) currentInput = "0"
            currentInput += "."
            tvDisplay.text = currentInput
        }
    }

    private fun setOperator(op: String) {
        if (currentInput.isNotEmpty()) {
            firstOperand = currentInput.toDouble()
            expression = currentInput + " " + op
            currentInput = ""
        } else if (expression.isNotEmpty()) {
            // Allow changing operator
            expression = expression.dropLast(1) + op
        }
        pendingOperator = op
        tvExpression.text = expression
        freshResult = false
    }

    private fun calculate() {
        if (pendingOperator.isEmpty() || currentInput.isEmpty()) return

        val secondOperand = currentInput.toDouble()
        val fullExpression = expression + " " + currentInput + " ="

        val result = when (pendingOperator) {
            "+"  -> firstOperand + secondOperand
            "−"  -> firstOperand - secondOperand
            "×"  -> firstOperand * secondOperand
            "÷"  -> if (secondOperand != 0.0) firstOperand / secondOperand else Double.NaN
            else -> secondOperand
        }

        pendingOperator = ""
        currentInput = formatResult(result)
        freshResult = true

        tvExpression.text = fullExpression
        tvDisplay.text = currentInput
    }

    private fun clear() {
        currentInput = ""
        firstOperand = 0.0
        pendingOperator = ""
        freshResult = false
        expression = ""
        tvDisplay.text = "0"
        tvExpression.text = ""
    }

    private fun negate() {
        if (currentInput.isNotEmpty() && currentInput != "0") {
            val value = currentInput.toDouble() * -1
            currentInput = formatResult(value)
            tvDisplay.text = currentInput
        }
    }

    private fun percent() {
        if (currentInput.isNotEmpty()) {
            val value = currentInput.toDouble() / 100
            currentInput = formatResult(value)
            tvDisplay.text = currentInput
        }
    }

    private fun formatResult(value: Double): String {
        if (value.isNaN()) return "Error"
        return if (value == kotlin.math.floor(value) && !value.isInfinite())
            value.toLong().toString()
        else
            value.toString()
    }
}