package com.example.appvbg.ui.talleres.ver_talleres

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appvbg.ui.talleres.ver_talleres.VerTalleresFragment.ItemTalleres
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class VerTalleresViewModel: ViewModel()  {
    private val _filtros = MutableLiveData<FiltroData>()

    val filtros: LiveData<FiltroData> = _filtros

    private var originalItems: List<ItemTalleres> = emptyList()


    // LiveData para la lista de quejas (items)
    private val _items = MutableLiveData<List<ItemTalleres>>()
    val items: LiveData<List<ItemTalleres>> = _items

    private val JSONDataTalleres = JSONArray()

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error



    // Puedes inicializar la lista si es necesario
    init {
        cargarItems()
        //_items.value = cargarItems()
    }

    fun setItems(newItems: List<ItemTalleres>) {
        _items.value = newItems
    }

    fun removeItem(item: ItemTalleres) {
        _items.value = _items.value?.filter { it != item }
    }

    private fun generateDummyItems(): List<ItemTalleres> {
        // Replace this with your actual data loading logic
        return (1..10).map {
            ItemTalleres(
                id = it,
                nombre = "$it",
                fechaInicio = "$it",
                horaInicio = "$it",
                horaFin = "$it",
                ubicacion = "$it",
                modalidad = "$it",
                beneficiarios = "$it",
                talleristas = "$it",
                descripcion = "$it",
                estado = "$it",
                json = null
            )
        }
    }


    fun actualizarFiltros(nuevosFiltros: FiltroData) {
        _filtros.value = nuevosFiltros
        filtrarItems()
    }

    private fun filtrarItems() {
        val filtros = _filtros.value ?: return
        val filtered = originalItems.filter { item ->
            // Filtrar código (contiene)
            val nombreOk = filtros.nombre.isBlank() || item.nombre.contains(filtros.nombre, true)
            // Filtrar sede ("Todos" ignora)
            val fechaOk = filtros.fechaInicio == "Todas" || item.fechaInicio == filtros.fechaInicio
            // Filtrar tipo
            val modalidadOk = filtros.modalidad == "Todas" || item.modalidad == filtros.modalidad.toLowerCase()
            // Filtrar facultad
            val estadoOk = filtros.estado == "Todos" || item.estado == filtros.estado
            nombreOk && fechaOk && modalidadOk && estadoOk
        }
        _items.postValue(filtered)
    }

    fun cargarItems() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = URL("http://192.168.0.30:8000/api/talleres/")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    val reader = BufferedReader(InputStreamReader(inputStream))
                    val response = reader.readText()
                    reader.close()

                    val jsonArray = JSONArray(response)
                    val lista = mutableListOf<ItemTalleres>()

                    //Inserta todos los arrays en la variable
                    JSONDataTalleres.put(jsonArray)


                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        val item = VerTalleresFragment.ItemTalleres(
                            id=obj.getInt("id"),
                            nombre = obj.getString("name"),
                            fechaInicio = obj.getString("date"),
                            horaInicio = obj.getString("start_time"),
                            horaFin = obj.getString("end_time"),
                            ubicacion = obj.getString("location"),
                            modalidad = obj.getString("modality"),
                            beneficiarios = obj.getString("slots"),
                            talleristas = obj.getString("facilitators"),
                            descripcion = obj.getString("details"),
                            estado = "????",
                            json=obj

                        )
                        lista.add(item)
                    }
                    originalItems = lista
                    _items.postValue(lista)
                } else {
                    _error.postValue("Error de conexión: $responseCode")
                }

                connection.disconnect()
            } catch (e: Exception) {
                _error.postValue("Error: ${e.localizedMessage}")
            }
        }
    }






}


data class FiltroData(
    val nombre: String,
    val fechaInicio: String,
    val modalidad: String,
    val estado: String
)