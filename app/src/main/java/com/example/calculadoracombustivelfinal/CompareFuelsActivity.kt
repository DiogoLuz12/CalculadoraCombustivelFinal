package com.example.calculadoracombustivelfinal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.calculadoracarroseletricos.EletricoActivity
import com.example.calculadoracombustivelfinal.ui.theme.CalculadoraCombustivelFinalTheme

class CompareFuelsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent{
            CalculadoraCombustivelFinalTheme {
                Scaffold(modifier = Modifier.fillMaxSize()){innerPadding ->
                    CompareLayout(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }

            }
        }
    }
}

@Composable
fun CompareLayout(name: String, modifier: Modifier = Modifier) {

    val context = LocalContext.current
    var gasolinePrice by remember { mutableStateOf("") }
    var gasoleoPrice by remember { mutableStateOf("") }
    var gasolineConsumption by remember { mutableStateOf("") }
    var gasoleoConsumption by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }

    IconButton(
        onClick = {
            navigateToActivity(context, MainActivity::class.java)
        }
    ) {
        Icon(Icons.Default.ArrowBack, contentDescription = "back")
    }
    Spacer(modifier = Modifier
        .padding(horizontal = 90.dp))
    Column(
        modifier = Modifier
            .statusBarsPadding()
            .padding(horizontal = 40.dp)
            .verticalScroll(rememberScrollState())
            .safeDrawingPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {


        Text(text = "Comparar Combustíveis", style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .padding(bottom = 15.dp, top = 35.dp)
                .align(alignment = Alignment.Start),)

        Spacer(modifier = Modifier.height(16.dp))

        EditNumberField(
            label = R.string.preco_gasolina,
            value = gasolinePrice,
            onValueChanged = { gasolinePrice = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        EditNumberField(
            label = R.string.preco_gasoleo,
            value = gasoleoPrice,
            onValueChanged = { gasoleoPrice = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        EditNumberField(
            label = R.string.consumo_gasolina,
            value = gasolineConsumption,
            onValueChanged = { gasolineConsumption = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        EditNumberField(
            label = R.string.consumo_gasoleo,
            value = gasoleoConsumption,
            onValueChanged = { gasoleoConsumption = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val gasolinePriceValue = gasolinePrice.toDoubleOrNull() ?: 0.0
            val ethanolPriceValue = gasoleoPrice.toDoubleOrNull() ?: 0.0
            val gasolineConsumptionValue = gasolineConsumption.toDoubleOrNull() ?: 0.0
            val ethanolConsumptionValue = gasoleoConsumption.toDoubleOrNull() ?: 0.0

            if (gasolinePriceValue > 0 && ethanolPriceValue > 0 && gasolineConsumptionValue > 0 && ethanolConsumptionValue > 0) {
                val costPerKmGasoline = gasolinePriceValue / gasolineConsumptionValue
                val costPerKmEthanol = ethanolPriceValue / ethanolConsumptionValue

                result = if (costPerKmGasoline < costPerKmEthanol) {
                    "Gasolina é mais econômica"
                } else {
                    "Etanol é mais econômico"
                }
            } else {
                result = "Por favor, insira valores válidos."
            }
        }) {
            Text(text = "Comparar")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = result)

    }
}

@Preview(showBackground = true)
@Composable
fun CompararPreview(){
    CalculadoraCombustivelFinalTheme {
        CompareLayout("Android")
    }
}



