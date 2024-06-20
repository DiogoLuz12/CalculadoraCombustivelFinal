package com.example.calculadoracombustivelfinal

import androidx.compose.runtime.mutableDoubleStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf

class CalculadoraViewModel : ViewModel() {
    var nomeViagem = mutableStateOf("")
    var distanciaTotal = mutableStateOf("")
    var consumoMedio = mutableStateOf("")
    var precoCombustivel = mutableStateOf("")

    val custoTotal = mutableDoubleStateOf(0.0)
    val historico = mutableStateListOf<CalculoCombustivel>()
    var showError = mutableStateOf(false)
}