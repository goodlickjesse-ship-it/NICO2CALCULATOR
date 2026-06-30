package com.example.nicocalculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import net.objecthunter.exp4j.ExpressionBuilder
import net.objecthunter.exp4j.function.Function
import java.util.Locale
import kotlin.math.*

sealed class CalculatorAction {
    data class Number(val number: Int) : CalculatorAction()
    object Clear : CalculatorAction()
    object Delete : CalculatorAction()
    object Decimal : CalculatorAction()
    object Calculate : CalculatorAction()
    data class Operation(val operation: String) : CalculatorAction()
    data class Scientific(val function: String) : CalculatorAction()
    data class Constant(val constant: String) : CalculatorAction()
    data class Parentheses(val bracket: String) : CalculatorAction()
    object ToggleScientific : CalculatorAction()
    object ToggleDegRad : CalculatorAction()
    
    // Matrix Actions
    data class MatrixInput(val row: Int, val col: Int, val value: String) : CalculatorAction()
    object CalculateMatrixInverse : CalculatorAction()
    object CalculateMatrixDet : CalculatorAction()
    data class SetMatrixSize(val size: Int) : CalculatorAction()
}

class CalculatorViewModel : ViewModel() {
    var displayState by mutableStateOf("0")
        private set

    var isScientificExpanded by mutableStateOf(false)
        private set

    var isDegreeMode by mutableStateOf(true)
        private set

    // Matrix State
    var matrixSize by mutableStateOf(2)
        private set
    var matrixData = mutableStateOf(Array(4) { Array(4) { "" } })
        private set
    var matrixResult by mutableStateOf("")
        private set

    private var currentInput = ""

    // Custom Trig Functions
    private val sinFunc = object : Function("sin", 1) {
        override fun apply(vararg args: Double): Double = 
            if (isDegreeMode) sin(Math.toRadians(args[0])) else sin(args[0])
    }
    private val cosFunc = object : Function("cos", 1) {
        override fun apply(vararg args: Double): Double = 
            if (isDegreeMode) cos(Math.toRadians(args[0])) else cos(args[0])
    }
    private val tanFunc = object : Function("tan", 1) {
        override fun apply(vararg args: Double): Double = 
            if (isDegreeMode) tan(Math.toRadians(args[0])) else tan(args[0])
    }
    
    // Hyperbolic Functions
    private val sinhFunc = object : Function("sinh", 1) {
        override fun apply(vararg args: Double): Double = sinh(args[0])
    }
    private val coshFunc = object : Function("cosh", 1) {
        override fun apply(vararg args: Double): Double = cosh(args[0])
    }
    private val tanhFunc = object : Function("tanh", 1) {
        override fun apply(vararg args: Double): Double = tanh(args[0])
    }

    private val factorial = object : Function("fact", 1) {
        override fun apply(vararg args: Double): Double {
            val arg = args[0]
            if (arg < 0 || arg > 170 || arg != floor(arg)) return Double.NaN
            var result = 1.0
            for (i in 1..arg.toInt()) result *= i
            return result
        }
    }

    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.Number -> enterNumber(action.number)
            is CalculatorAction.Decimal -> enterDecimal()
            is CalculatorAction.Clear -> {
                currentInput = ""
                displayState = "0"
                matrixResult = ""
            }
            is CalculatorAction.Operation -> enterOperation(action.operation)
            is CalculatorAction.Calculate -> performCalculation()
            is CalculatorAction.Scientific -> enterScientific(action.function)
            is CalculatorAction.Delete -> delete()
            is CalculatorAction.Constant -> enterConstant(action.constant)
            is CalculatorAction.Parentheses -> enterParentheses(action.bracket)
            is CalculatorAction.ToggleScientific -> isScientificExpanded = !isScientificExpanded
            is CalculatorAction.ToggleDegRad -> isDegreeMode = !isDegreeMode
            
            // Matrix Handlers
            is CalculatorAction.SetMatrixSize -> matrixSize = action.size
            is CalculatorAction.MatrixInput -> {
                val newData = matrixData.value.map { it.copyOf() }.toTypedArray()
                newData[action.row][action.col] = action.value
                matrixData.value = newData
            }
            is CalculatorAction.CalculateMatrixDet -> calculateDeterminant()
            is CalculatorAction.CalculateMatrixInverse -> matrixResult = "Inverse not implemented yet"
        }
    }

    private fun enterNumber(number: Int) {
        if (currentInput == "0") currentInput = ""
        currentInput += number.toString()
        displayState = currentInput
    }

    private fun enterConstant(constant: String) {
        if (currentInput == "0") currentInput = ""
        currentInput += when(constant) {
            "PI" -> "π"
            "E" -> "e"
            else -> constant
        }
        displayState = currentInput
    }

    private fun enterDecimal() {
        if (currentInput.isEmpty() || currentInput.endsWith(" ") || currentInput.endsWith("(")) {
            currentInput += "0."
        } else {
            val lastPart = currentInput.split(Regex("[\\+\\-\\*/\\^\\(\\)]")).last()
            if (!lastPart.contains(".")) currentInput += "."
        }
        displayState = currentInput
    }

    private fun enterOperation(operation: String) {
        if (currentInput.isNotEmpty() || operation == "-") {
            val op = when(operation) {
                "*" -> "×"
                "/" -> "÷"
                else -> operation
            }
            currentInput += op
            displayState = currentInput
        }
    }

    private fun enterParentheses(bracket: String) {
        if (currentInput == "0") currentInput = ""
        currentInput += bracket
        displayState = currentInput
    }

    private fun delete() {
        if (currentInput.isNotEmpty()) {
            val functions = listOf("sinh(", "cosh(", "tanh(", "sin(", "cos(", "tan(", "log10(", "log(", "sqrt(", "exp(", "fact(", "1/", "10^(")
            val matchedFunc = functions.find { currentInput.endsWith(it) }
            
            if (matchedFunc != null) {
                currentInput = currentInput.dropLast(matchedFunc.length)
            } else {
                currentInput = currentInput.dropLast(1)
            }
            displayState = if (currentInput.isEmpty()) "0" else currentInput
        }
    }

    private fun enterScientific(function: String) {
        if (currentInput == "0") currentInput = ""
        currentInput += when (function) {
            "sin", "cos", "tan", "sinh", "cosh", "tanh", "sqrt", "log", "ln", "exp", "fact" -> {
                if (function == "log") "log10(" else if (function == "ln") "log(" else "$function("
            }
            "pow2" -> "^2"
            "inv" -> "1/("
            "10x" -> "10^("
            else -> "$function("
        }
        displayState = currentInput
    }

    private fun performCalculation() {
        if (currentInput.isEmpty()) return
        try {
            var expressionStr = currentInput
                .replace("π", "pi")
                .replace("e", "e")
                .replace("×", "*")
                .replace("÷", "/")
                .replace("%", "*0.01")

            val openBrackets = expressionStr.count { it == '(' }
            val closeBrackets = expressionStr.count { it == ')' }
            if (openBrackets > closeBrackets) expressionStr += ")".repeat(openBrackets - closeBrackets)

            val expression = ExpressionBuilder(expressionStr)
                .functions(sinFunc, cosFunc, tanFunc, sinhFunc, coshFunc, tanhFunc, factorial)
                .build()
                
            val result = expression.evaluate()
            currentInput = formatResult(result)
            displayState = currentInput
        } catch (e: Exception) {
            displayState = "Error"
        }
    }

    private fun formatResult(result: Double): String {
        if (result.isInfinite() || result.isNaN()) return "Error"
        val roundedResult = if (abs(result) < 1e-10) 0.0 else result
        return if (roundedResult == roundedResult.toLong().toDouble()) {
            roundedResult.toLong().toString()
        } else {
            "%.10f".format(Locale.US, roundedResult).replace(Regex("0*$"), "").replace(Regex("\\.$"), "")
        }
    }

    // Basic 2x2 Determinant as starting point
    private fun calculateDeterminant() {
        try {
            val data = matrixData.value
            val det = if (matrixSize == 2) {
                val a = data[0][0].toDoubleOrNull() ?: 0.0
                val b = data[0][1].toDoubleOrNull() ?: 0.0
                val c = data[1][0].toDoubleOrNull() ?: 0.0
                val d = data[1][1].toDoubleOrNull() ?: 0.0
                (a * d) - (b * c)
            } else {
                0.0 // Placeholder for higher order
            }
            matrixResult = "Det: ${formatResult(det)}"
        } catch (e: Exception) {
            matrixResult = "Error"
        }
    }
}
