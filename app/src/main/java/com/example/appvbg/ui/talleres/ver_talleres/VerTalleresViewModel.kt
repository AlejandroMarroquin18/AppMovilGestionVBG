package com.example.appvbg.ui.talleres.ver_talleres

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class VerTalleresViewModel: ViewModel()  {
    val filtros = MutableLiveData<FiltroData>()
}


data class FiltroData(
    val nombre: String,
    val fecha: String,
    val modalidad: String,
    val estado: String
)