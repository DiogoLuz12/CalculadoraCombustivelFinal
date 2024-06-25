package com.example.calculadoracombustivelfinal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.calculadoracombustivelfinal.ui.theme.CalculadoraCombustivelFinalTheme

class CompareFuelsActivity : ComponentActivity() {
    private val viewModel: CalculadoraViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent{
            CalculadoraCombustivelFinalTheme {
                Scaffold(modifier = Modifier.fillMaxSize()){innerPadding ->
                    CompareLayout(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding),
                        viewModel = viewModel
                    )
                }

            }
        }
    }
}

@Composable
fun CompareLayout(
    name: String,
    modifier: Modifier = Modifier,
    viewModel: CalculadoraViewModel
) {
    val context = LocalContext.current
    val enableCalculate = viewModel.gasolinePrice.value.isNotEmpty() &&
            viewModel.gasoleoPrice.value.isNotEmpty() &&
            viewModel.gasolineConsumption.value.isNotEmpty() &&
            viewModel.gasoleoConsumption.value.isNotEmpty()


    Column(
        modifier = modifier
            .statusBarsPadding()
            .padding(horizontal = 40.dp)
            .verticalScroll(rememberScrollState())
            .safeDrawingPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {


        IconButton(
            onClick = {
                navigateToActivity(context, MainActivity::class.java)
            }
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "back")
        }
        Text(
            text = "Comparar Combustíveis",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 15.dp, top = 35.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        EditNumberField(
            label = R.string.preco_gasolina,
            value = viewModel.gasolinePrice.value,
            onValueChanged = { viewModel.gasolinePrice.value = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        EditNumberField(
            label = R.string.preco_gasoleo,
            value = viewModel.gasoleoPrice.value,
            onValueChanged = { viewModel.gasoleoPrice.value = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        EditNumberField(
            label = R.string.consumo_gasolina,
            value = viewModel.gasolineConsumption.value,
            onValueChanged = { viewModel.gasolineConsumption.value = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        EditNumberField(
            label = R.string.consumo_gasoleo,
            value = viewModel.gasoleoConsumption.value,
            onValueChanged = { viewModel.gasoleoConsumption.value = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val gasolinePriceValue = viewModel.gasolinePrice.value.toDoubleOrNull() ?: 0.0
            val gasoleoPriceValue = viewModel.gasoleoPrice.value.toDoubleOrNull() ?: 0.0
            val gasolineConsumptionValue = viewModel.gasolineConsumption.value.toDoubleOrNull() ?: 0.0
            val gasoleoConsumptionValue = viewModel.gasoleoConsumption.value.toDoubleOrNull() ?: 0.0

            if (gasolinePriceValue > 0 && gasoleoPriceValue > 0 &&
                gasolineConsumptionValue > 0 && gasoleoConsumptionValue > 0) {
                val costPerKmGasoline = gasolinePriceValue / gasolineConsumptionValue
                val costPerKmGasoleo = gasoleoPriceValue / gasoleoConsumptionValue

                viewModel.result.value = if (costPerKmGasoline < costPerKmGasoleo) {
                    "Gasolina é mais econômica"
                } else {
                    "Gasóleo é mais econômico"
                }
            } else {
                viewModel.result.value = "Por favor, insira valores válidos."
            }
        },
            enabled = enableCalculate
        ){
            Text(text = "Comparar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = viewModel.result.value)
    }
}

@Preview(showBackground = true)
@Composable
fun CompararPreview(){
    CalculadoraCombustivelFinalTheme {
        CompareLayout("Android",  viewModel = CalculadoraViewModel())
    }
}



