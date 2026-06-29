package com.example.nicocalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
    onAction: (CalculatorAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Calculator "Screen" Display Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF2D2D2D))
                .padding(20.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            val scrollState = rememberScrollState()
            LaunchedEffect(state) {
                scrollState.animateScrollTo(scrollState.maxValue)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                // Mode Indicator (DEG/RAD)
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
                    fontSize = 48.sp,
                    color = Color.White,
                    maxLines = 1,
                    softWrap = false
                )
            }
        }

        // Action Row: Toggle Scientific & DEG/RAD
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isScientificExpanded) "HIDE SCIENTIFIC" else "SHOW SCIENTIFIC",
                modifier = Modifier
                    .clickable { onAction(CalculatorAction.ToggleScientific) }
                    .padding(8.dp),
                color = Color.LightGray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = if (isDegreeMode) "SWITCH TO RAD" else "SWITCH TO DEG",
                modifier = Modifier
                    .clickable { onAction(CalculatorAction.ToggleDegRad) }
                    .padding(8.dp),
                color = if (isDegreeMode) Color(0xFFFF9F0A) else Color(0xFF50D2C2),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Scientific Buttons Panel
        AnimatedVisibility(
            visible = isScientificExpanded,
            enter = expandVertically(animationSpec = tween(300)),
            exit = shrinkVertically(animationSpec = tween(300))
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Row 1: sin, cos, tan, asin, acos
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ScientificButton(symbol = "sin", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Scientific("sin")) }
                    ScientificButton(symbol = "cos", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Scientific("cos")) }
                    ScientificButton(symbol = "tan", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Scientific("tan")) }
                    ScientificButton(symbol = "sin⁻¹", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Scientific("asin")) }
                    ScientificButton(symbol = "cos⁻¹", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Scientific("acos")) }
                }
                
                // Row 2: atan, log, ln, √, x²
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ScientificButton(symbol = "tan⁻¹", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Scientific("atan")) }
                    ScientificButton(symbol = "log", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Scientific("log")) }
                    ScientificButton(symbol = "ln", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Scientific("ln")) }
                    ScientificButton(symbol = "√", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Scientific("sqrt")) }
                    ScientificButton(symbol = "x²", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Scientific("pow2")) }
                }

                // Row 3: xʸ, π, e, x!, (, )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ScientificButton(symbol = "xʸ", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Operation("^")) }
                    ScientificButton(symbol = "π", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Constant("PI")) }
                    ScientificButton(symbol = "e", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Constant("E")) }
                    ScientificButton(symbol = "x!", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Scientific("fact")) }
                    ScientificButton(symbol = "(", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Parentheses("(")) }
                    ScientificButton(symbol = ")", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Parentheses(")")) }
                }
            }
        }

        // Main Calculator Buttons
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
}

@Composable
fun CalculatorButton(
    symbol: String,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF333333),
    textColor: Color = Color.White,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .aspectRatio(if (symbol == "0") 2.1f else 1f)
            .clip(CircleShape)
            .background(color)
            .clickable { onClick() }
    ) {
        Text(
            text = symbol,
            fontSize = 28.sp,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ScientificButton(
    symbol: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .aspectRatio(1.5f)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF212121))
            .clickable { onClick() }
    ) {
        Text(
            text = symbol,
            fontSize = 14.sp,
            color = Color.LightGray,
            fontWeight = FontWeight.Normal
        )
    }
}
