package com.example.calculadoracombustivelfinal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.calculadoracarroseletricos.EletricoActivity
import com.example.calculadoracombustivelfinal.ui.theme.CalculadoraCombustivelFinalTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculadoraCombustivelFinalTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CalculadoraLayout(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculadoraLayout(name: String, modifier: Modifier = Modifier) {

    val context = LocalContext.current
    var nomeViagem by remember { mutableStateOf("") }
    var distanciaTotal by remember { mutableStateOf("") }
    var consumoMedio by remember { mutableStateOf("") }
    var precoCombustivel by remember { mutableStateOf("") }

    val custoTotal = remember { mutableDoubleStateOf(0.0) }
    val historico = remember { mutableStateListOf<CalculoCombustivel>() }
    var showError by remember { mutableStateOf(false) }



    Column (
        modifier = Modifier
            .statusBarsPadding()
            .padding(horizontal = 40.dp)
            .verticalScroll(rememberScrollState())
            .safeDrawingPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top

    ) {
        TopAppBar(
            title = {},
            actions = { Button(onClick = { navigateToActivity(context, EletricoActivity::class.java) }) {
                Text(text = "Calculadora Eletrico")
            }}
        )
        Text(
            text = stringResource(R.string.app_name),style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .padding(bottom = 15.dp, top = 35.dp)
                .align(alignment = Alignment.Start),
        )
        TextField(
            value = nomeViagem,
            onValueChange = { nomeViagem = it },
            label = { Text("Nome da Viagem") }, // Label para a nova TextField
            modifier = Modifier
                .padding(bottom = 30.dp)
                .fillMaxWidth()
        )
        EditNumberField(
            label = R.string.distanciaAserPercorrida,
            value = distanciaTotal,
            onValueChanged = { distanciaTotal = it },
            modifier = Modifier
                .padding(bottom = 30.dp)
                .fillMaxWidth(),
        )
        EditNumberField(
            label = R.string.consumo_Medio,
            value = consumoMedio,
            onValueChanged = { consumoMedio = it },
            modifier = Modifier
                .padding(bottom = 30.dp)
                .fillMaxWidth()
        )
        EditNumberField(
            label = R.string.precoCombus,
            value = precoCombustivel,
            onValueChanged = { precoCombustivel = it },
            modifier = Modifier
                .padding(bottom = 30.dp)
                .fillMaxWidth()
        )
        Button(
            onClick = {

                val distancia = distanciaTotal.toDoubleOrNull() ?: 0.0
                val consumo = consumoMedio.toDoubleOrNull() ?: 0.0
                val preco = precoCombustivel.toDoubleOrNull() ?: 0.0
                if (distancia >= 0 && consumo >= 0 && preco >= 0) {
                    val custo = calculateTotalCost(distancia, consumo, preco)
                    custoTotal.value = custo
                    historico.add(CalculoCombustivel(distancia, consumo, preco, custo, nomeViagem))
                } else {
                    showError = true
                }
            },
            modifier = Modifier.padding(bottom = 32.dp),
        ) {
            Text(text = "Calcular")
        }
        Text(
            text = stringResource(R.string.custo_total, custoTotal.doubleValue),
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.height(20.dp))
        HistoricoList(historico)
        Spacer(modifier = Modifier.height(150.dp))

        if (showError) {
            //Utilizado para mensagens temporárias
            Snackbar(
                modifier = Modifier.padding(16.dp),
                action = {
                    Button(onClick = { showError = false }) {
                        Text(text = "OK")
                    }
                }
            ) {
                Text("Por favor, insira valores válidos.")
            }


        }

    }
}

fun navigateToActivity(context: Context, activityClass: Class<*>) {
    context.startActivity(Intent(context, activityClass))
}


@Composable
fun EditNumberField(
    @StringRes label: Int,
    value: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier
){
    TextField(
        value = value,
        singleLine = true,
        modifier = modifier,
        onValueChange = onValueChanged,
        label = { Text(stringResource(label)) },
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
    )

}


data class CalculoCombustivel(
    val distancia: Double,
    val consumoMedio: Double,
    val precoCombustivel: Double,
    val custoTotal: Double,
    val nomeViagem: String
)


@Composable
fun HistoricoList(historico: List<CalculoCombustivel>) {
    Column {
        Text(text = stringResource(R.string.historico_de_calculos), style = MaterialTheme.typography.headlineSmall)
        for (calculo in historico) {
            Row{
                Text(
                    text = "Nome da viagem: ${calculo.nomeViagem}, Distância: ${calculo.distancia} km, Consumo: ${calculo.consumoMedio} L/100 km, Preço: €${calculo.precoCombustivel}/L, Custo: €${String.format("%.2f", calculo.custoTotal)}",
                    style = MaterialTheme.typography.bodyLarge
                )

            }

        }
        Divider(color = Color.Gray, thickness = 1.dp)

    }

}

fun calculateTotalCost(distancia: Double, consumoMedio: Double, precoCombustivel: Double): Double {

    val quantidadeCombustivel = (distancia / 100.0) * consumoMedio
    return quantidadeCombustivel * precoCombustivel
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CalculadoraCombustivelFinalTheme {
        CalculadoraLayout("Android")
    }
}