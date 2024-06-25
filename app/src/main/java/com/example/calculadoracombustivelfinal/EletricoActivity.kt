package com.example.calculadoracombustivelfinal

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.unit.dp
import com.example.calculadoracombustivelfinal.ui.theme.CalculadoraCombustivelFinalTheme



class EletricoActivity : ComponentActivity() {
    private val viewModel: CalculadoraViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculadoraCombustivelFinalTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CalculadoraEletricaLayout(
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
fun CalculadoraEletricaLayout(
    name: String,
    modifier: Modifier = Modifier,
    viewModel: CalculadoraViewModel) {

    val context = LocalContext.current
    val enableCalculate = viewModel.nomeViagem.value.isNotEmpty() &&
            viewModel.distanciaTotal.value.isNotEmpty() &&
            viewModel.consumoMedio.value.isNotEmpty() &&
            viewModel.precoEnergia.value.isNotEmpty()

    Column(
        modifier = Modifier
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
            text = stringResource(R.string.app_name2), style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .padding(bottom = 15.dp, top = 35.dp)
                .align(alignment = Alignment.Start),
        )
        TextField(
            value = viewModel.nomeViagem.value,
            onValueChange = { viewModel.nomeViagem.value = it },
            label = { Text("Nome da Viagem") },
            modifier = Modifier
                .padding(bottom = 30.dp)
                .fillMaxWidth()
        )
        EditNumberField(
            label = R.string.distanciaAserPercorrida,
            value = viewModel.distanciaTotal.value,
            onValueChanged = { viewModel.distanciaTotal.value = it },
            modifier = Modifier
                .padding(bottom = 30.dp)
                .fillMaxWidth(),
        )
        EditNumberField(
            label = R.string.consumo_Medio2,
            value = viewModel.consumoMedio.value,
            onValueChanged = { viewModel.consumoMedio.value = it },
            modifier = Modifier
                .padding(bottom = 30.dp)
                .fillMaxWidth()
        )
        EditNumberField(
            label = R.string.precoEnergia,
            value = viewModel.precoEnergia.value,
            onValueChanged = { viewModel.precoEnergia.value = it },
            modifier = Modifier
                .padding(bottom = 30.dp)
                .fillMaxWidth()
        )
        Button(
            onClick = {

                val distancia = viewModel.distanciaTotal.value.toDoubleOrNull() ?: 0.0
                val consumo = viewModel.consumoMedio.value.toDoubleOrNull() ?: 0.0
                val preco = viewModel.precoEnergia.value.toDoubleOrNull() ?: 0.0
                if (viewModel.nomeViagem.value.isEmpty() || distancia <= 0.0 || consumo <= 0.0 || preco <= 0.0
                ) {
                    viewModel.showError.value = true
                } else {
                    val custo = calculateTotalCostEletrico(distancia, consumo, preco)
                    viewModel.custoTotal.doubleValue = custo
                    viewModel.historicoEnergia.add(CalculoEnergia(distancia, consumo, preco, custo, viewModel.nomeViagem.value))
                }
            },
            enabled = enableCalculate,
            modifier = Modifier.padding(bottom = 32.dp),
        ) {
            Text(text = "Calcular")
        }
        Text(
            text = stringResource(R.string.custo_total, viewModel.custoTotal.doubleValue),
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.height(20.dp))
        HistoricoListEletrico(viewModel.historicoEnergia)
        Spacer(modifier = Modifier.height(150.dp))

        if (viewModel.showError.value) {
            // Utilizado para mensagens temporárias
            Snackbar(
                modifier = Modifier.padding(16.dp),
                action = {
                    Button(onClick = { viewModel.showError.value = false }) {
                        Text(text = "OK")
                    }
                }
            ) {
                Text("Por favor, insira valores válidos.")
            }
        }

    }
}






data class CalculoEnergia(
    val distancia: Double,
    val consumoMedio: Double,
    val precoEnergia: Double,
    val custoTotal: Double,
    val nomeViagem: String
)

@Composable
fun HistoricoListEletrico(historico: List<CalculoEnergia>) {
    Column {
        Text(
            text = stringResource(R.string.historico_de_calculos),
            style = MaterialTheme.typography.headlineSmall
        )
        for (index in historico.indices) {
            val calculo = historico[index]
            Row {
                Text(
                    text = "Nome da viagem: ${calculo.nomeViagem}, Distância: ${calculo.distancia} km, Consumo: ${calculo.consumoMedio} kWh/100 km, Preço: €${calculo.precoEnergia}/kWh, Custo: €${String.format("%.2f", calculo.custoTotal)}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            if (index < historico.size - 1) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = Color.Gray, thickness = 1.dp)
            }
        }
    }
}

fun calculateTotalCostEletrico(distancia: Double, consumoMedio: Double, precoEnergia: Double): Double {
    val quantidadeEnergia = (distancia / 100.0) * consumoMedio
    return quantidadeEnergia * precoEnergia
}

