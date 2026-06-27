package com.example.nicocalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
                
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CalculatorScreen(
                        state = state,
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
    onAction: (CalculatorAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.DarkGray)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.BottomEnd
        ) {
            val scrollState = rememberScrollState()
            
            // Automatically scroll to the end when the state (text) changes
            LaunchedEffect(state) {
                scrollState.animateScrollTo(scrollState.maxValue)
            }

            Text(
                text = state,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
                    .padding(vertical = 16.dp),
                fontWeight = FontWeight.Light,
                fontSize = 48.sp,
                color = Color.White,
                maxLines = 1,
                softWrap = false
            )
        }
        // Scientific Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CalculatorButton(symbol = "sin", color = Color.Gray, fontSize = 16.sp, modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Scientific("sin")) }
            CalculatorButton(symbol = "cos", color = Color.Gray, fontSize = 16.sp, modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Scientific("cos")) }
            CalculatorButton(symbol = "exp", color = Color.Gray, fontSize = 16.sp, modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Scientific("exp")) }
            CalculatorButton(symbol = "PI", color = Color.Gray, fontSize = 16.sp, modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Constant("PI")) }
            CalculatorButton(symbol = "^", color = Color.Gray, fontSize = 16.sp, modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Operation("^")) }
        }
        // Grid of buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CalculatorButton(symbol = "AC", color = Color.LightGray, fontSize = 18.sp, modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Clear) }
            CalculatorButton(symbol = "sqrt", color = Color.LightGray, fontSize = 16.sp, modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Scientific("sqrt")) }
            CalculatorButton(symbol = "log", color = Color.LightGray, fontSize = 16.sp, modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Scientific("log")) }
            CalculatorButton(symbol = "/", color = Color(0xFFFF9800), fontSize = 20.sp, modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Operation("/")) }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CalculatorButton(symbol = "7", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Number(7)) }
            CalculatorButton(symbol = "8", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Number(8)) }
            CalculatorButton(symbol = "9", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Number(9)) }
            CalculatorButton(symbol = "*", color = Color(0xFFFF9800), modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Operation("*")) }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CalculatorButton(symbol = "4", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Number(4)) }
            CalculatorButton(symbol = "5", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Number(5)) }
            CalculatorButton(symbol = "6", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Number(6)) }
            CalculatorButton(symbol = "-", color = Color(0xFFFF9800), modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Operation("-")) }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CalculatorButton(symbol = "1", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Number(1)) }
            CalculatorButton(symbol = "2", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Number(2)) }
            CalculatorButton(symbol = "3", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Number(3)) }
            CalculatorButton(symbol = "+", color = Color(0xFFFF9800), modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Operation("+")) }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            CalculatorButton(symbol = "0", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Number(0)) }
            CalculatorButton(symbol = ".", modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Decimal) }
            CalculatorButton(symbol = "DEL", color = Color.DarkGray, fontSize = 16.sp, modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Delete) }
            CalculatorButton(symbol = "=", color = Color(0xFFFF9800), modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Calculate) }
        }
    }
}

@Composable
fun CalculatorButton(
    symbol: String,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFF333333),
    fontSize: androidx.compose.ui.unit.TextUnit = 20.sp,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(CircleShape)
            .background(color)
            .clickable { onClick() }
            .aspectRatio(1f)
    ) {
        Text(
            text = symbol,
            fontSize = fontSize,
            color = Color.White
        )
    }
}
