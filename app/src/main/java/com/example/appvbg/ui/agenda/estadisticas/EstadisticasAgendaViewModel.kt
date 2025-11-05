package com.example.appvbg

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.appvbg.api.PrefsHelper
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieEntry
import org.json.JSONArray
import org.json.JSONObject
import com.example.appvbg.api.makeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EstadisticasAgendaViewModel : ViewModel() {

    // JSON completo
    private val _agenda = MutableLiveData<JSONObject>()
    val agenda: LiveData<JSONObject> = _agenda




    // Substats individuales como LiveData
    private val _totalEventosCreados = MutableLiveData<Int>()
    val totalEventosCreados: LiveData<Int> = _totalEventosCreados

    private val _totalEventosRealizados = MutableLiveData<Int>()
    val totalEventosRealizados: LiveData<Int> = _totalEventosRealizados

    private val _totalEstudiantes = MutableLiveData<Int>()
    val totalEstudiantes: LiveData<Int> = _totalEstudiantes

    private val _totalFuncionarios = MutableLiveData<Int>()
    val totalFuncionarios: LiveData<Int> = _totalFuncionarios

    private val _totalProfesores = MutableLiveData<Int>()
    val totalProfesores: LiveData<Int> = _totalProfesores

    private val _conteoFacultad = MutableLiveData<Pair<List<BarEntry>, List<String>>>()
    val conteoFacultad: LiveData<Pair<List<BarEntry>, List<String>>> = _conteoFacultad

    private val _conteoAnio = MutableLiveData<Pair<List<BarEntry>, List<String>>>()
    val conteoAnio: LiveData<Pair<List<BarEntry>, List<String>>> = _conteoAnio

    private val _conteoMes = MutableLiveData<Pair<List<BarEntry>, List<String>>>()
    val conteoMes: LiveData<Pair<List<BarEntry>, List<String>>> = _conteoMes

    private val _conteoTipo = MutableLiveData<List<PieEntry>>()
    val conteoTipo: LiveData<List<PieEntry>> = _conteoTipo

    private val _conteoGenero = MutableLiveData<List<PieEntry>>()
    val conteoGenero: LiveData<List<PieEntry>> = _conteoGenero



    /**
     * Llama al endpoint de estadisticas y actualiza los LiveData
     */
    fun fetchEstadisticasAgenda(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = makeRequest(
                    """${APIConstant.BACKEND_URL}api/events/stats/""",
                    "GET",
                    PrefsHelper.getDRFToken(context).toString()
                )

                val json = JSONObject(response)
                Log.d("EstadisticasQuejasViewModel", "JSON completo: $json")

                // actualizar los LiveData en hilo seguro
                _agenda.postValue(json)
                _totalEstudiantes.postValue(json.getInt("total_estudiantes"))
                _totalFuncionarios.postValue(json.getInt("total_funcionarios"))
                _totalProfesores.postValue(json.getInt("total_profesores"))
                _totalEventosCreados.postValue(json.getInt("total_eventos_creados"))
                _totalEventosRealizados.postValue(json.getInt("total_eventos_realizados"))



                _conteoFacultad.postValue(
                    transformarABarEntries(
                        json.getJSONArray("conteo_por_facultad_afectado"),
                        "case_id__persona_afectada__facultad",
                        "total_eventos"
                    )
                )
                _conteoAnio.postValue(
                    transformarABarEntries(
                        json.getJSONArray("conteo_por_anio"),
                        "year",
                        "total"
                    )
                )

                _conteoMes.postValue(
                    transformarABarEntries(
                        json.getJSONArray("conteo_por_mes"),
                        "month",
                        "total"
                    )
                )

                _conteoGenero.postValue(
                    transformarAPieEntries(
                        json.getJSONArray("conteo_por_genero_afectado"),
                        "persona_afectada__identidad_genero",
                        "total"
                    )
                )
                _conteoTipo.postValue(
                    transformarAPieEntries(
                        json.getJSONArray("conteo_por_tipo"),
                        "type",
                        "total"
                    )
                )

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    /**
     * Transforma un JSONArray en una lista de PieEntry
     */
    private fun transformarAPieEntries(
        jsonArray: JSONArray,
        countLabel: String,
        totalLabel: String
    ): List<PieEntry> {
        val entries = mutableListOf<PieEntry>()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            var facultad = obj.optString(countLabel, "No especificado")
            if (facultad == ""){
                facultad="No especificado"
            }

            val total = obj.optDouble(totalLabel, 0.0).toFloat()
            if (total > 0) { // opcional: no agregar vacíos
                entries.add(PieEntry(total, facultad.toString()))
            }
        }

        return entries
    }

    /**
     * Transforma un JSONObject en una lista de BarEntry
     */
    private fun transformarJSONAEntries(json: JSONObject): Pair<List<BarEntry>, List<String>> {
        val entries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()
        var index = 0f

        val keys = json.keys()
        while (keys.hasNext()) {
            val year = keys.next()
            val value = json.getInt(year)
            entries.add(BarEntry(index, value.toInt().toFloat()))
            labels.add(year)
            index++
        }
        return Pair(entries, labels)
    }

    /**
     * Transforma un JSONArray en una lista de BarEntry
     */
    private fun transformarABarEntries(
        jsonArray: JSONArray,
        countLabel: String,
        totalLabel: String
    ): Pair<List<BarEntry>, List<String>> {
        val entries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val facultad = obj.getString(countLabel)
            val total = obj.getInt(totalLabel)
            entries.add(BarEntry(i.toInt().toFloat(), total.toInt().toFloat()))
            labels.add(facultad)
        }
        return Pair(entries, labels)
    }



}
