package com.example.calculadoracombustivelfinal


import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.calculadoracombustivelfinal.ui.theme.CalculadoraCombustivelFinalTheme


class MainActivity : ComponentActivity() {
    private val viewModel: CalculadoraViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculadoraCombustivelFinalTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CalculadoraLayout(
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
fun CalculadoraLayout(
    name: String,
    modifier: Modifier = Modifier,
    viewModel: CalculadoraViewModel
) {
    val context = LocalContext.current

    val canCalculate =
                viewModel.nomeViagem.value.isNotEmpty() &&
                viewModel.distanciaTotal.value.isNotEmpty() &&
                viewModel.consumoMedio.value.isNotEmpty() &&
                viewModel.precoCombustivel.value.isNotEmpty()


    Column(
        modifier = Modifier
            .statusBarsPadding()
            .padding(horizontal = 40.dp)
            .verticalScroll(rememberScrollState())
            .safeDrawingPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Button(onClick = { navigateToActivity(context, CompareFuelsActivity::class.java) }) {
            Text(text = stringResource(R.string.app_name3))
        }
        Button(onClick = { navigateToActivity(context, EletricoActivity::class.java) }) {
            Text(text = stringResource(R.string.app_name2))
        }
        Text(
            text = stringResource(R.string.app_name), style = MaterialTheme.typography.headlineSmall,
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
            label = R.string.consumo_Medio,
            value = viewModel.consumoMedio.value,
            onValueChanged = { viewModel.consumoMedio.value = it },
            modifier = Modifier
                .padding(bottom = 30.dp)
                .fillMaxWidth()
        )
        EditNumberField(
            label = R.string.precoCombus,
            value = viewModel.precoCombustivel.value,
            onValueChanged = { viewModel.precoCombustivel.value = it },
            modifier = Modifier
                .padding(bottom = 30.dp)
                .fillMaxWidth()
        )
        Button(
            onClick = {
                val distancia = viewModel.distanciaTotal.value.toDoubleOrNull()
                val consumo = viewModel.consumoMedio.value.toDoubleOrNull()
                val preco = viewModel.precoCombustivel.value.toDoubleOrNull()

                if (viewModel.nomeViagem.value.isEmpty() ||
                    distancia == null || distancia < 0.0 ||
                    consumo == null || consumo < 0.0 ||
                    preco == null || preco < 0.0) {

                    viewModel.showError.value = true
                } else {

                    val custo = calculateTotalCost(distancia, consumo, preco)
                    viewModel.custoTotal.doubleValue = custo
                    viewModel.historico.add(CalculoCombustivel(distancia, consumo, preco, custo, viewModel.nomeViagem.value))
                }
            },
            enabled = canCalculate,
            modifier = Modifier.padding(bottom = 32.dp),
        ) {
            Text(text = "Calcular")
        }
        Text(
            text = stringResource(R.string.custo_total, viewModel.custoTotal.value),
            style = MaterialTheme.typography.bodyLarge,
        )
        Spacer(modifier = Modifier.height(20.dp))
        HistoricoList(viewModel.historico, viewModel)
        Spacer(modifier = Modifier.height(150.dp))

        if (viewModel.showError.value) {
            Snackbar(
                modifier = Modifier.padding(16.dp),
                action = {
                    Button(onClick = { viewModel.showError.value = false }) {
                        Text(text = "OK")
                    }
                }
            ) {
                Text("Por favor, preencha todos os campos corretamente.")
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
fun HistoricoList(historico: List<CalculoCombustivel>, viewModel: CalculadoraViewModel) {
    Column {
        Text(
            text = stringResource(R.string.historico_de_calculos),
            style = MaterialTheme.typography.headlineSmall
        )
        for (index in historico.indices) {
            val calculo = historico[index]
            Row {
                Text(
                    text = "Nome da viagem: ${calculo.nomeViagem}, Distância: ${calculo.distancia} km, Consumo: ${calculo.consumoMedio} L/100 km, Preço: €${calculo.precoCombustivel}/L, Custo: €${String.format("%.2f", calculo.custoTotal)}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            if (index < historico.size - 1) {
                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = Color.Gray, thickness = 1.dp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewModel.limparHistoricoNormal() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Limpar Histórico")
        }
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
        CalculadoraLayout("Android", viewModel = CalculadoraViewModel())
    }
}