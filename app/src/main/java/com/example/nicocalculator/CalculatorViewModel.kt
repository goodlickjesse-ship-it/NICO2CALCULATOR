package com.example.nicocalculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.math.*

class CalculatorViewModel : ViewModel() {
    var displayState by mutableStateOf("0")
        private set

    private var currentInput = ""

    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.Number -> enterNumber(action.number)
            is CalculatorAction.Decimal -> enterDecimal()
            is CalculatorAction.Clear -> {
                currentInput = ""
                displayState = "0"
            }
            is CalculatorAction.Operation -> enterOperation(action.operation)
            is CalculatorAction.Calculate -> performCalculation()
            is CalculatorAction.Scientific -> performScientific(action.function)
            is CalculatorAction.Delete -> delete()
            is CalculatorAction.Constant -> enterConstant(action.constant)
        }
    }

    private fun enterNumber(number: Int) {
        if (currentInput == "0") currentInput = ""
        currentInput += number.toString()
        displayState = currentInput
    }

    fun enterConstant(constant: String) {
        val value = if (constant == "PI") PI else E
        if (currentInput == "0") currentInput = ""
        currentInput += value.toString()
        displayState = currentInput
    }

    private fun enterDecimal() {
        if (!currentInput.contains(".")) {
            if (currentInput.isEmpty()) currentInput = "0"
            currentInput += "."
            displayState = currentInput
        }
    }

    private fun enterOperation(operation: String) {
        if (currentInput.isNotEmpty()) {
            currentInput += " $operation "
            displayState = currentInput
        }
    }

    private fun delete() {
        if (currentInput.isNotEmpty()) {
            currentInput = currentInput.dropLast(1).trim()
            displayState = currentInput.ifEmpty { "0" }
        }
    }

    private fun performScientific(function: String) {
        try {
            val value = currentInput.toDoubleOrNull() ?: displayState.toDoubleOrNull() ?: 0.0
            val result = when (function) {
                "sin" -> sin(Math.toRadians(value))
                "cos" -> cos(Math.toRadians(value))
                "tan" -> tan(Math.toRadians(value))
                "exp" -> exp(value)
                "log" -> ln(value)
                "sqrt" -> sqrt(value)
                else -> 0.0
            }
            currentInput = formatResult(result)
            displayState = currentInput
        } catch (_: Exception) {
            displayState = "Error"
        }
    }

    private fun performCalculation() {
        try {
            val result = evaluateExpression(currentInput)
            currentInput = formatResult(result)
            displayState = currentInput
        } catch (_: Exception) {
            displayState = "Error"
        }
    }

    private fun formatResult(result: Double): String {
        if (result.isInfinite()) return "Error"
        if (result.isNaN()) return "Error"
        
        return if (result == result.toLong().toDouble()) {
            result.toLong().toString()
        } else {
            // Limits to 10 decimal places and removes trailing zeros
            "%.10f".format(java.util.Locale.US, result)
                .replace(Regex("0*$"), "")
                .replace(Regex("\\.$"), "")
        }
    }

    // Simple evaluation logic for basic expressions
    private fun evaluateExpression(expression: String): Double {
        // This is a placeholder for a more complex parser. 
        // For now, let's handle simple operations: +, -, *, /
        val tokens = expression.split(" ").filter { it.isNotEmpty() }
        if (tokens.isEmpty()) return 0.0
        
        var result = tokens[0].toDouble()
        var i = 1
        while (i < tokens.size) {
            val op = tokens[i]
            val nextVal = tokens[i + 1].toDouble()
            result = when (op) {
                "+" -> result + nextVal
                "-" -> result - nextVal
                "*" -> result * nextVal
                "/" -> result / nextVal
                "^" -> result.pow(nextVal)
                else -> result
            }
            i += 2
        }
        return result
    }
}

sealed class CalculatorAction {
    data class Number(val number: Int) : CalculatorAction()
    object Clear : CalculatorAction()
    object Delete : CalculatorAction()
    object Decimal : CalculatorAction()
    object Calculate : CalculatorAction()
    data class Operation(val operation: String) : CalculatorAction()
    data class Scientific(val function: String) : CalculatorAction()
    data class Constant(val constant: String) : CalculatorAction()
}
