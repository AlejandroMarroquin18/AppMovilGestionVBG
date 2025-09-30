package com.example.appvbg.ui.quejas


import com.example.appvbg.ui.quejas.QuejasFragment.Item
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import androidx.lifecycle.*
import com.example.appvbg.APIConstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class QuejaViewModel: ViewModel() {
    private val _filtros = MutableLiveData<FiltroData>()

    val filtros: LiveData<FiltroData> = _filtros

    private var originalItems: List<Item> = emptyList()


    // LiveData para la lista de quejas (items)
    private val _items = MutableLiveData<List<Item>>()
    val items: LiveData<List<Item>> = _items

    private val JSONDataQuejas = JSONArray()

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error



    // Puedes inicializar la lista si es necesario
    init {
        cargarItems()
        //_items.value = cargarItems()
    }

    fun setItems(newItems: List<Item>) {
        _items.value = newItems
    }

    fun removeItem(item: Item) {
        _items.value = _items.value?.filter { it != item }
    }

    private fun generateDummyItems(): List<Item> {
        return (1..10).map {
            Item(
                id = it,
                nombre = "Item $it",
                codigo = "$it",
                facultad = " $it",
                sede = "$it",
                tipo_de_acompanamiento = "",
                fecha = "TODO()",
                estado = "TODO()",
                detalles = "TODO()",
                unidad = "TODO()",
                json = null
            )
        }
    }

    fun actualizarFiltros(nuevosFiltros: FiltroData) {
        _filtros.value = nuevosFiltros
        filtrarItems()
    }
    fun filtrarItems() {
        val filtros = _filtros.value ?: return
        val filtered = originalItems.filter { item ->
            // Filtrar código (contiene)
            val codigoOk = filtros.codigo.isBlank() || item.codigo.contains(filtros.codigo, true)
            // Filtrar sede ("Todos" ignora)
            val sedeOk = filtros.sede == "Todos" || item.sede == filtros.sede
            // Filtrar tipo
            val tipoOk = filtros.tipo == "Todos" || item.tipo_de_acompanamiento == filtros.tipo
            // Filtrar facultad
            val facOk = filtros.facultad == "Todos" || item.facultad == filtros.facultad
            codigoOk && sedeOk && tipoOk && facOk
        }
        _items.postValue(filtered)
    }
    fun cargarItems() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val url = URL(APIConstant.BACKEND_URL + "api/quejas/")
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
                    val lista = mutableListOf<Item>()

                    //Inserta todos los arrays en la variable
                    JSONDataQuejas.put(jsonArray)


                    for (i in 0 until jsonArray.length()) {
                        val obj = jsonArray.getJSONObject(i)
                        val item = Item(
                            id = obj.getInt("id"),
                            nombre = obj.getString("afectado_nombre"),
                            sede = obj.getString("afectado_sede"),
                            codigo = obj.getString("afectado_codigo"),
                            tipo_de_acompanamiento = obj.getString("tipo_de_acompanamiento"),
                            fecha = obj.getString("fecha_recepcion"),
                            estado = obj.getString("estado"),
                            detalles = obj.getString("observaciones"),
                            facultad = if (obj.isNull("afectado_facultad")) "null" else obj.getString("afectado_facultad"),
                            unidad = if (obj.isNull("unidad")) "null" else obj.getString("unidad"),
                            facultad = if (obj.isNull("afectado_facultad")) null else obj.getString("afectado_facultad"),
                            unidad = if (obj.isNull("unidad")) null else obj.getString("unidad"),
                            json = obj
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
    val id: Int,
    val codigo: String,
    val sede: String,
    val tipo: String,
    val facultad: String
)


