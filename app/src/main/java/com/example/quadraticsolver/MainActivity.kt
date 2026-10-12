package com.example.quadraticsolver

import android.os.Bundle
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quadraticsolver.ui.theme.QuadraticSolverTheme
import kotlin.math.sqrt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuadraticSolverTheme {
                QuadraticSolverApp()
            }
        }
    }
}

@Composable
fun QuadraticSolverApp(modifier: Modifier = Modifier) {
    var aText by remember {
        mutableStateOf("")
    }
    var bText by remember {
        mutableStateOf("")
    }
    var cText by remember {
        mutableStateOf("")
    }
    var result by remember {
        mutableStateOf("")
    }
    var firstRoot by remember {
        mutableStateOf("")
    }
    var secondRoot by remember {
        mutableStateOf("")
    }
    val context = LocalContext.current
    val uniqueRootMessage = stringResource(R.string.unique_root)
    val doubleRootMessage = stringResource(R.string.double_root)
    val twoRootsMessage = stringResource(R.string.two_distinct_roots)
    val inputRequiredMessage = stringResource(R.string.input_required)
    val invalidNumber = stringResource(R.string.invalid_number)
    Surface(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            EditNumberField(
                label = R.string.coefficient_a_label,
                value = aText,
                onValueChange = { aText = it },
                modifier = Modifier
                    .padding(bottom = 4.dp)
            )
            EditNumberField(
                label = R.string.coefficient_b_label,
                value = bText,
                onValueChange = { bText = it },
                modifier = Modifier
                    .padding(vertical = 4.dp)
            )
            EditNumberField(
                label = R.string.coefficient_c_label,
                value = cText,
                onValueChange = { cText = it },
                modifier = Modifier
                    .padding(top = 4.dp)
            )
            ButtonSolved(
                onClick = {
                    val a = aText.toDoubleOrNull()
                    val b = bText.toDoubleOrNull()
                    val c = cText.toDoubleOrNull()
                    fun normalizeZero(value: Double): Double =
                        if (value == 0.0) 0.0 else value
                    if (a != null && b != null && c != null) {
                        result = solveEquation(context, a, b, c)
                        firstRoot = ""
                        secondRoot = ""
                        when (result) {
                            uniqueRootMessage -> {
                                val x = -c / b
                                firstRoot = "x = ${normalizeZero(x)}"
                                secondRoot = ""
                            }
                            doubleRootMessage -> {
                                val x = -b / (2*a)
                                firstRoot = "x1 = x2 = ${normalizeZero(x)}"
                                secondRoot = ""
                            }
                            twoRootsMessage -> {
                                val delta = b * b - 4 * a * c
                                val x1 = (-b + sqrt(delta)) / (2*a)
                                val x2 = (-b - sqrt(delta)) / (2*a)
                                firstRoot = "x1 = ${normalizeZero(x1)}"
                                secondRoot = "x2 = ${normalizeZero(x2)}"
                            }
                        }
                    } else {
                        result = invalidNumber
                        firstRoot = ""
                        secondRoot = ""
                    }
                    if (aText.isBlank() || bText.isBlank() || cText.isBlank()) {
                        result = inputRequiredMessage
                        firstRoot = ""
                        secondRoot = ""
                    }
                },
                modifier = Modifier.padding(top = 24.dp)
            )
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = result,
                fontSize = 27.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            when (result) {
                uniqueRootMessage -> {
                    Text(
                        text = firstRoot,
                        fontSize = 27.sp
                    )
                }
                doubleRootMessage -> {
                    Text(
                        text = firstRoot,
                        fontSize = 27.sp
                    )
                }
                twoRootsMessage -> {
                    Text(
                        text = firstRoot,
                        fontSize = 25.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = secondRoot,
                        fontSize = 25.sp
                    )
                }
            }
        }
    }
}

@Composable
fun EditNumberField(
    @StringRes label: Int,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        label = {
            Text(
                text = stringResource(label),
                fontSize = 15.sp
            )
        },
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        textStyle = TextStyle(
            fontSize = 30.sp,
            textAlign = TextAlign.Justify
        ),
        singleLine = true
    )
}

@Composable
fun ButtonSolved(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.width(250.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = stringResource(R.string.solve_equation),
                fontSize = 21.sp
            )
        }
    }
}

fun solveEquation(
    context: Context,
    a: Double,
    b: Double,
    c: Double
): String {
    if (a == 0.0) {
        return if (b == 0.0) {
            if (c == 0.0) {
                context.getString(R.string.infinite_roots)
            } else {
                context.getString(R.string.no_roots)
            }
        } else {
            context.getString(R.string.unique_root)
        }
    } else {
        val delta = b * b - 4 * a * c
        return if (delta < 0.0) {
            context.getString(R.string.no_roots)
        } else if (delta == 0.0) {
            context.getString(R.string.double_root)
        } else {
            context.getString(R.string.two_distinct_roots)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    QuadraticSolverTheme {
        QuadraticSolverApp()
    }
}