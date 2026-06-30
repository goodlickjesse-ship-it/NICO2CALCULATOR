package com.example.nicocalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nicocalculator.ui.theme.NICOCALCULATORTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NICOCALCULATORTheme {
                val viewModel = viewModel<CalculatorViewModel>()
                val state = viewModel.displayState
                val isScientificExpanded = viewModel.isScientificExpanded
                val isDegreeMode = viewModel.isDegreeMode
                
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color(0xFF1C1C1C)
                ) { innerPadding ->
                    CalculatorScreen(
                        state = state,
                        isScientificExpanded = isScientificExpanded,
                        isDegreeMode = isDegreeMode,
                        viewModel = viewModel,
                        onAction = viewModel::onAction,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun CalculatorScreen(
    state: String,
    isScientificExpanded: Boolean,
    isDegreeMode: Boolean,
    viewModel: CalculatorViewModel,
    onAction: (CalculatorAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Calculator "Screen"
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF2D2D2D))
                .padding(20.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            LaunchedEffect(state) {
                scrollState.animateScrollTo(scrollState.maxValue)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (isDegreeMode) "DEG" else "RAD",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = state,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState),
                    fontWeight = FontWeight.Medium,
                    fontSize = 40.sp,
                    color = Color.White,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }

        // Scientific Functions Bar (Horizontally Scrollable)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val functions = listOf("sin", "cos", "tan", "sinh", "cosh", "tanh", "log", "ln", "√", "π", "e", "(", ")")
            functions.forEach { func ->
                ScientificButton(symbol = func, modifier = Modifier.width(70.dp)) {
                    if (func == "π" || func == "e") {
                        onAction(CalculatorAction.Constant(if (func == "π") "PI" else "E"))
                    } else if (func == "(" || func == ")") {
                        onAction(CalculatorAction.Parentheses(func))
                    } else {
                        onAction(CalculatorAction.Scientific(if (func == "√") "sqrt" else func))
                    }
                }
            }
        }

        // Toggle Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (isScientificExpanded) "HIDE MATRIX" else "SHOW MATRIX",
                modifier = Modifier.clickable { onAction(CalculatorAction.ToggleScientific) }.padding(8.dp),
                color = Color.LightGray, fontSize = 12.sp, fontWeight = FontWeight.Bold
            )
            Text(
                text = if (isDegreeMode) "DEG" else "RAD",
                modifier = Modifier.clickable { onAction(CalculatorAction.ToggleDegRad) }.padding(8.dp),
                color = if (isDegreeMode) Color(0xFFFF9F0A) else Color(0xFF50D2C2),
                fontSize = 12.sp, fontWeight = FontWeight.Bold
            )
        }
        
        // Advanced Matrix Panel
        AnimatedVisibility(
            visible = isScientificExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Matrix Section Title
                Text("Matrix Operation (2x2)", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                
                // Matrix Grid
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    for (i in 0 until 2) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            for (j in 0 until 2) {
                                MatrixCell(
                                    value = viewModel.matrixData.value[i][j],
                                    onValueChange = { onAction(CalculatorAction.MatrixInput(i, j, it)) }
                                )
                            }
                        }
                    }
                }
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ScientificButton(symbol = "Det", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.CalculateMatrixDet) }
                    Text(viewModel.matrixResult, color = Color.White, modifier = Modifier.weight(2f), fontSize = 14.sp)
                }
            }
        }

        // Standard Buttons (Numbers etc.)
        StandardButtonGrid(onAction)
    }
}

@Composable
fun MatrixCell(value: String, onValueChange: (String) -> Unit) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .width(60.dp)
            .padding(2.dp)
            .background(Color(0xFF444444), RoundedCornerShape(4.dp))
            .padding(4.dp),
        textStyle = TextStyle(color = Color.White, textAlign = TextAlign.Center, fontSize = 14.sp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        cursorBrush = SolidColor(Color.White)
    )
}

@Composable
fun StandardButtonGrid(onAction: (CalculatorAction) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CalculatorButton(symbol = "AC", color = Color(0xFFA5A5A5), textColor = Color.Black, modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Clear) }
            CalculatorButton(symbol = "DEL", color = Color(0xFFA5A5A5), textColor = Color.Black, modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Delete) }
            CalculatorButton(symbol = "%", color = Color(0xFFA5A5A5), textColor = Color.Black, modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Operation("%")) }
            CalculatorButton(symbol = "÷", color = Color(0xFFFF9F0A), modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Operation("/")) }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CalculatorButton(symbol = "7", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Number(7)) }
            CalculatorButton(symbol = "8", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Number(8)) }
            CalculatorButton(symbol = "9", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Number(9)) }
            CalculatorButton(symbol = "×", color = Color(0xFFFF9F0A), modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Operation("*")) }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CalculatorButton(symbol = "4", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Number(4)) }
            CalculatorButton(symbol = "5", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Number(5)) }
            CalculatorButton(symbol = "6", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Number(6)) }
            CalculatorButton(symbol = "-", color = Color(0xFFFF9F0A), modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Operation("-")) }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CalculatorButton(symbol = "1", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Number(1)) }
            CalculatorButton(symbol = "2", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Number(2)) }
            CalculatorButton(symbol = "3", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Number(3)) }
            CalculatorButton(symbol = "+", color = Color(0xFFFF9F0A), modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Operation("+")) }
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CalculatorButton(symbol = "0", modifier = Modifier.weight(2.1f)) { onAction(CalculatorAction.Number(0)) }
            CalculatorButton(symbol = ".", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Decimal) }
            CalculatorButton(symbol = "=", color = Color(0xFFFF9F0A), modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Calculate) }
        }
    }
}

@Composable
fun CalculatorButton(symbol: String, modifier: Modifier = Modifier, color: Color = Color(0xFF333333), textColor: Color = Color.White, onClick: () -> Unit) {
    Box(contentAlignment = Alignment.Center, modifier = modifier.aspectRatio(if (symbol == "0") 2.1f else 1f).clip(CircleShape).background(color).clickable { onClick() }) {
        Text(text = symbol, fontSize = 24.sp, color = textColor, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun ScientificButton(symbol: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(contentAlignment = Alignment.Center, modifier = modifier.aspectRatio(1.8f).clip(RoundedCornerShape(8.dp)).background(Color(0xFF212121)).clickable { onClick() }) {
        Text(text = symbol, fontSize = 13.sp, color = Color.LightGray)
    }
}
