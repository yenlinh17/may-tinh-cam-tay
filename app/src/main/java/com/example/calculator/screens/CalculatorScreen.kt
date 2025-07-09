package com.example.calculator.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.components.CalculatorButton
import com.example.calculator.model.HistoryItem
import com.example.calculator.utils.DataStoreHelper
import com.example.calculator.utils.evaluateExpression
import com.example.calculator.utils.handleInput
import com.example.calculator.utils.isValidExpression
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CalculatorScreen() {
    val context = LocalContext.current
    var expression by remember { mutableStateOf("") }
    var history by remember { mutableStateOf(listOf<HistoryItem>()) }
    var searchQuery by remember { mutableStateOf("") }
    var showSearch by remember { mutableStateOf(false) }
    var showAdvancedButtons by remember { mutableStateOf(false) }
    var showKeypad by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        history = DataStoreHelper.loadHistory(context)
    }

    val basicButtons = listOf(
        listOf("C", "⌫", "±", "/"),
        listOf("7", "8", "9", "*"),
        listOf("4", "5", "6", "-"),
        listOf("1", "2", "3", "+"),
        listOf("0", ".", "(", ")"),
        listOf("√", "x²", "%", "=")
    )

    val advancedButtons = listOf(
        listOf("sin", "cos", "tan", "π"),
        listOf("log", "ln", "!", "e")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                showKeypad = false
            }
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .background(Color.Black),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { showSearch = !showSearch }) {
                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
            }

            IconButton(onClick = { showAdvancedButtons = !showAdvancedButtons }) {
                Icon(Icons.Default.Settings, contentDescription = "Cài đặt", tint = Color.White)
            }
        }

        if (showSearch) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = {
                    Text(
                        "🔍 Tìm trong lịch sử",
                        fontSize = 18.sp,
                        color = Color.White
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 22.sp, color = Color.White),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = Color.White,
                    focusedLabelColor = Color.LightGray,
                    unfocusedLabelColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true
            )
        }

        Text(
            text = expression,
            fontSize = 32.sp,
            color = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            maxLines = 2
        )

        val filteredHistory = history.filter {
            it.expression.contains(searchQuery, ignoreCase = true) ||
                    it.result.contains(searchQuery, ignoreCase = true)
        }.reversed()

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(bottom = 4.dp)
        ) {
            itemsIndexed(filteredHistory) { _, item ->
                val formattedTime = SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault())
                    .format(Date(item.timestamp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .border(1.dp, Color.White, shape = RoundedCornerShape(6.dp))
                        .padding(8.dp)
                        .clickable { expression = item.expression },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "${item.expression} = ${item.result}",
                            fontSize = 24.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "🕒 $formattedTime",
                            fontSize = 18.sp,
                            color = Color.LightGray
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "❌",
                        fontSize = 26.sp,
                        color = Color.Red,
                        modifier = Modifier
                            .clickable {
                                history = history.filterNot { it == item }
                                CoroutineScope(Dispatchers.IO).launch {
                                    DataStoreHelper.saveHistory(context, history)
                                }
                            }
                    )
                }
            }
        }

        if (history.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp, horizontal = 4.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "🗑️ Xoá toàn bộ lịch sử",
                    fontSize = 16.sp,
                    color = Color.Red,
                    modifier = Modifier
                        .clickable {
                            history = emptyList()
                            CoroutineScope(Dispatchers.IO).launch {
                                DataStoreHelper.saveHistory(context, emptyList())
                            }
                        }
                        .padding(4.dp)
                )
            }
        }

        if (showKeypad) {
            basicButtons.forEach { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    row.forEach { symbol ->
                        CalculatorButton(
                            symbol = symbol,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1.5f)
                                .padding(horizontal = 1.dp),
                            backgroundColor = when (symbol) {
                                "=", "/", "*", "-", "+", "x²", "√", "%" -> Color(0xFFFFA726)
                                "C", "⌫", "±" -> Color(0xFF616161)
                                else -> Color(0xFF424242)
                            },
                            onClick = {
                                showKeypad = true
                                if (symbol == "=") {
                                    if (!isValidExpression(expression)) {
                                        expression = "Lỗi cú pháp"
                                        return@CalculatorButton
                                    }
                                    try {
                                        val result = evaluateExpression(expression)
                                        val displayResult = if (result % 1 == 0.0)
                                            result.toInt().toString()
                                        else result.toString()

                                        val item = HistoryItem(
                                            expression = expression,
                                            result = displayResult,
                                            timestamp = System.currentTimeMillis()
                                        )
                                        history = history + item
                                        expression = displayResult

                                        CoroutineScope(Dispatchers.IO).launch {
                                            DataStoreHelper.saveHistory(context, history)
                                        }
                                    } catch (e: Exception) {
                                        expression = "Lỗi"
                                    }
                                } else {
                                    expression = handleInput(expression, symbol)
                                }
                            }
                        )
                    }
                }
            }

            if (showAdvancedButtons) {
                Spacer(modifier = Modifier.height(4.dp))
                advancedButtons.forEach { row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        row.forEach { symbol ->
                            CalculatorButton(
                                symbol = symbol,
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1.5f)
                                    .padding(horizontal = 1.dp),
                                backgroundColor = Color(0xFF616161),
                                onClick = {
                                    showKeypad = true
                                    expression = handleInput(expression, symbol)
                                }
                            )
                        }
                    }
                }
            }
        }

        // Nút hiện bàn phím nếu đang bị ẩn
        if (!showKeypad) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(
                    onClick = { showKeypad = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray)
                ) {
                    Text(
                        text = "📥 Hiện bàn phím",
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}



