package com.example.calculator.utils

import net.objecthunter.exp4j.ExpressionBuilder

fun handleInput(expression: String, input: String): String {
    return when (input) {
        "=" -> {
            try {
                val result = evaluateExpression(expression)
                result.toString()
            } catch (e: Exception) {
                "Lỗi"
            }
        }

        "C" -> ""

        "⌫" -> if (expression.isNotEmpty()) expression.dropLast(1) else ""

        "±" -> {
            if (expression.isEmpty()) return "-"
            val lastNumber = Regex("""(\d+\.?\d*)$""").find(expression)?.value
            return if (lastNumber != null) {
                val updated = expression.removeSuffix(lastNumber)
                if (lastNumber.startsWith("-")) updated + lastNumber.drop(1)
                else updated + "-" + lastNumber
            } else expression
        }

        "√" -> "sqrt($expression)"
        "x²" -> "$expression^2"
        "%" -> "$expression/100"

        else -> expression + input
    }
}

fun evaluateExpression(expr: String): Double {
    val expression = ExpressionBuilder(expr).build()
    return expression.evaluate()
}

fun isValidExpression(expression: String): Boolean {
    return try {
        val exp = ExpressionBuilder(expression).build()
        val result = exp.evaluate()
        !result.isNaN() && !result.isInfinite()
    } catch (e: Exception) {
        false
    }
}
