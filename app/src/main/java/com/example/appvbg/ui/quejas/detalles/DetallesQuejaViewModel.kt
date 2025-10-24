package com.example.appvbg.ui.agenda.detalles

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.appvbg.APIConstant
import com.example.appvbg.api.PrefsHelper
import com.example.appvbg.api.makeRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class DetallesAgendaViewModel : ViewModel() {

    private val gson = Gson()

    private val _historial = MutableLiveData<List<HistorialQueja>>()
    val historial: LiveData<List<HistorialQueja>> get() = _historial

    private val _mensaje = MutableLiveData<String>()
    val mensaje: LiveData<String> get() = _mensaje

    /**
     * Obtener historial de quejas
     */
    fun fetchHistorial(context: Context, id: Int ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {

                val response = makeRequest(
                    """${APIConstant.BACKEND_URL}api/quejas/historial-quejas/${id}/""",
                    "GET",
                    PrefsHelper.getDRFToken(context).toString()
                )
                if (response != "error") {
                    val listType = object : TypeToken<List<HistorialQueja>>() {}.type
                    val lista: List<HistorialQueja> = gson.fromJson(response, listType)
                    _historial.postValue(lista)
                } else {
                    _mensaje.postValue("Error al obtener historial")
                    Log.d("fetchHistorial", "Error al obtener historial")
                }
                Log.d("fetchHistorial", "Respuesta: $response")
            } catch (e: Exception) {
                _mensaje.postValue("Excepción en fetch: ${e.message}")
            }
        }
    }

    /**
     * Crear nuevo historial
     */
    fun crearHistorial( context: Context, nuevo: HistorialQueja) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val body = JSONObject(gson.toJson(nuevo))
                val response = makeRequest(
                    """${APIConstant.BACKEND_URL}api/quejas/historial-quejas/""",
                    "POST",
                    PrefsHelper.getDRFToken(context).toString(),
                    body
                )
                if (response != "error") {
                    _mensaje.postValue("Historial creado con éxito")
                    fetchHistorial( context, nuevo.queja_id!!) // refrescar lista
                } else {
                    _mensaje.postValue("Error al crear historial")
                }
            } catch (e: Exception) {
                _mensaje.postValue("Excepción en crear: ${e.message}")
            }
        }
    }

    /**
     * Editar historial existente
     */
    fun editarHistorial(context: Context, id: Int, actualizado: HistorialQueja) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val body = JSONObject(gson.toJson(actualizado))
                val response = makeRequest(
                    """${APIConstant.BACKEND_URL}api/quejas/historial-queja/${id}/""",
                    "PUT",
                    PrefsHelper.getDRFToken(context).toString(),
                    body
                )
                if (response != "error") {
                    _mensaje.postValue("Historial actualizado con éxito")
                    fetchHistorial(context, id)
                } else {
                    _mensaje.postValue("Error al actualizar historial")
                }
            } catch (e: Exception) {
                _mensaje.postValue("Excepción en editar: ${e.message}")
            }
        }
    }

    /**
     * Eliminar historial
     */
    fun eliminarHistorial(context: Context, id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = makeRequest(
                    """${APIConstant.BACKEND_URL}api/quejas/historial-queja/${id}/""",
                    "DELETE",
                    PrefsHelper.getDRFToken(context).toString()
                )
                if (response != "error") {
                    _mensaje.postValue("Historial eliminado con éxito")
                    fetchHistorial(context, id)
                } else {
                    _mensaje.postValue("Error al eliminar historial")
                }
            } catch (e: Exception) {
                _mensaje.postValue("Excepción en eliminar: ${e.message}")
            }
        }
    }
}


data class HistorialQueja(
    val id: Int,
    val fecha: String,
    val queja_id: Int,
    val descripcion: String,
    val tipo: String,
    val numero: Int?
)
