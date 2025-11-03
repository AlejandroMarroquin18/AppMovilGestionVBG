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

class EstadisticasQuejasViewModel : ViewModel() {

    // JSON completo
    private val _quejas = MutableLiveData<JSONObject>()
    val quejas: LiveData<JSONObject> = _quejas

    // Substats individuales como LiveData
    private val _conteoQuejaEstudiantes = MutableLiveData<Int>()
    val conteoQuejaEstudiantes: LiveData<Int> = _conteoQuejaEstudiantes

    private val _conteoQuejaProfesores = MutableLiveData<Int>()
    val conteoQuejaProfesores: LiveData<Int> = _conteoQuejaProfesores

    private val _conteoQuejaFuncionarios = MutableLiveData<Int>()
    val conteoQuejaFuncionarios: LiveData<Int> = _conteoQuejaFuncionarios

    private val _conteoQuejaAnio = MutableLiveData<Pair<List<BarEntry>, List<String>>>()
    val conteoQuejaAnio: LiveData<Pair<List<BarEntry>, List<String>>> = _conteoQuejaAnio

    private val _conteoQuejaMes = MutableLiveData<Pair<List<BarEntry>, List<String>>>()
    val conteoQuejaMes: LiveData<Pair<List<BarEntry>, List<String>>> = _conteoQuejaMes

    private val _conteoQuejasFacultad = MutableLiveData<Pair<List<BarEntry>, List<String>>>()
    val conteoQuejasFacultad: LiveData<Pair<List<BarEntry>, List<String>>> = _conteoQuejasFacultad

    private val _conteoQuejasSede = MutableLiveData<Pair<List<BarEntry>, List<String>>>()
    val conteoQuejasSede: LiveData<Pair<List<BarEntry>, List<String>>> = _conteoQuejasSede

    private val _conteoVice = MutableLiveData<List<PieEntry>>()
    val conteoVice: LiveData<List<PieEntry>> = _conteoVice

    private val _conteoGenero = MutableLiveData<List<PieEntry>>()
    val conteoGenero: LiveData<List<PieEntry>> = _conteoGenero
    /////news
    private val _ConteoEdades = MutableLiveData<Pair<List<BarEntry>, List<String>>>()
    val ConteoEdades: LiveData<Pair<List<BarEntry>, List<String>>> = _ConteoEdades

    private val _conteoComunas = MutableLiveData<Pair<List<BarEntry>, List<String>>>()
    val conteoComunas: LiveData<Pair<List<BarEntry>, List<String>>> = _conteoComunas

    private val _conteoTipoVBG = MutableLiveData<Pair<List<BarEntry>, List<String>>>()
    val conteoTipoVBG: LiveData<Pair<List<BarEntry>, List<String>>> = _conteoTipoVBG

    private val _conteoFactores = MutableLiveData<Pair<List<BarEntry>, List<String>>>()
    val conteoFactores: LiveData<Pair<List<BarEntry>, List<String>>> = _conteoFactores


    /**
     * Llama al endpoint de estadisticas y actualiza los LiveData
     */
    fun fetchEstadisticasQuejas(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = makeRequest(
                    """${APIConstant.BACKEND_URL}api/quejas/statistics/""",
                    "GET",
                    PrefsHelper.getDRFToken(context).toString()
                )

                val json = JSONObject(response)
                Log.d("EstadisticasQuejasViewModel", "JSON completo: $json")

                // actualizar los LiveData en hilo seguro
                _quejas.postValue(json)
                _conteoQuejaEstudiantes.postValue(json.getInt("afectado_estudiantes"))
                _conteoQuejaProfesores.postValue(json.getInt("afectado_profesores"))
                _conteoQuejaFuncionarios.postValue(json.getInt("afectado_funcionarios"))
                _conteoQuejaAnio.postValue(transformarJSONAEntries(json.getJSONObject("conteo_por_anio")))
                //_conteoQuejaMes.postValue(transformarJSONAEntries(json.getJSONObject("conteo_por_mes")))
                _conteoQuejasFacultad.postValue(
                    transformarABarEntries(
                        json.getJSONArray("conteo_por_facultad_afectado"),
                        "persona_afectada__facultad",
                        "total"
                    )
                )
                _conteoQuejasSede.postValue(
                    transformarABarEntries(
                        json.getJSONArray("conteo_por_sede_afectado"),
                        "persona_afectada__sede",
                        "total"
                    )
                )

                _conteoVice.postValue(
                    transformarAPieEntries(
                        json.getJSONArray("conteo_por_vicerrectoria_adscrita_afectado"),
                        "persona_afectada__vicerrectoria_adscrito",
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
                _ConteoEdades.postValue(
                    transformarABarEntries(json.getJSONArray("edades"),"persona_afectada__edad", "total")
                )
                _conteoComunas.postValue(
                    transformarABarEntries(
                        json.getJSONArray("comunas"),
                        "persona_afectada__comuna",
                        "total"
                    )
                )
                _conteoTipoVBG.postValue(
                    transformarJSONAEntries(json.getJSONObject("tipo_vbg"))
                )
                _conteoFactores.postValue(
                    transformarJSONAEntries(json.getJSONObject("factores_riesgo"))
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
