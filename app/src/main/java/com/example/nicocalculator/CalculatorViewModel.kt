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
}

class CalculatorViewModel : ViewModel() {
    var displayState by mutableStateOf("0")
        private set

    var isScientificExpanded by mutableStateOf(false)
        private set

    var isDegreeMode by mutableStateOf(true)
        private set

    private var currentInput = ""

    // Custom Trig Functions to handle Deg/Rad
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
    
    // Custom Inverse Trig
    private val asinFunc = object : Function("asin", 1) {
        override fun apply(vararg args: Double): Double = 
            if (isDegreeMode) Math.toDegrees(asin(args[0])) else asin(args[0])
    }
    private val acosFunc = object : Function("acos", 1) {
        override fun apply(vararg args: Double): Double = 
            if (isDegreeMode) Math.toDegrees(acos(args[0])) else acos(args[0])
    }
    private val atanFunc = object : Function("atan", 1) {
        override fun apply(vararg args: Double): Double = 
            if (isDegreeMode) Math.toDegrees(atan(args[0])) else atan(args[0])
    }

    // Custom Factorial Function
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
            }
            is CalculatorAction.Operation -> enterOperation(action.operation)
            is CalculatorAction.Calculate -> performCalculation()
            is CalculatorAction.Scientific -> enterScientific(action.function)
            is CalculatorAction.Delete -> delete()
            is CalculatorAction.Constant -> enterConstant(action.constant)
            is CalculatorAction.Parentheses -> enterParentheses(action.bracket)
            is CalculatorAction.ToggleScientific -> isScientificExpanded = !isScientificExpanded
            is CalculatorAction.ToggleDegRad -> isDegreeMode = !isDegreeMode
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
            val functions = listOf("asin(", "acos(", "atan(", "sin(", "cos(", "tan(", "log10(", "log(", "sqrt(", "exp(", "fact(", "1/", "10^(")
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
            "sin" -> "sin("
            "cos" -> "cos("
            "tan" -> "tan("
            "asin" -> "asin("
            "acos" -> "acos("
            "atan" -> "atan("
            "log" -> "log10("
            "ln" -> "log("
            "sqrt" -> "sqrt("
            "pow2" -> "^2"
            "pow3" -> "^3"
            "fact" -> "fact("
            "inv" -> "1/("
            "10x" -> "10^("
            "ex" -> "exp("
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
                .functions(sinFunc, cosFunc, tanFunc, asinFunc, acosFunc, atanFunc, factorial)
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
        
        // Handle floating point precision errors for trig functions (e.g. sin(180) should be 0)
        val roundedResult = if (abs(result) < 1e-10) 0.0 else result

        return if (roundedResult == roundedResult.toLong().toDouble() && roundedResult < Long.MAX_VALUE && roundedResult > Long.MIN_VALUE) {
            roundedResult.toLong().toString()
        } else {
            "%.10f".format(Locale.US, roundedResult)
                .replace(Regex("0*$"), "")
                .replace(Regex("\\.$"), "")
        }
    }
}
