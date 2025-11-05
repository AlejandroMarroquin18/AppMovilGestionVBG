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

class EstadisticasTalleresViewModel : ViewModel() {

    // JSON completo
    private val _talleres = MutableLiveData<JSONObject>()
    val talleres: LiveData<JSONObject> = _talleres

    // Substats individuales como LiveData
    private val _total_talleres = MutableLiveData<Int>()
    val totalTalleres: LiveData<Int> = _total_talleres
    //Pie
    private val _conteoModalidades = MutableLiveData<List<PieEntry>>()
    val conteoModalidades: LiveData<List<PieEntry>> = _conteoModalidades

    private val _conteoVirtuales = MutableLiveData<Int>()
    val totalVirtuales: LiveData<Int> = _conteoVirtuales

    private val _conteoPresenciales = MutableLiveData<Int>()
    val totalPresenciales: LiveData<Int> = _conteoPresenciales

    private val _total_participantes = MutableLiveData<Int>()
    val totalParticipantes: LiveData<Int> = _total_participantes

    private val _promedio_participantes = MutableLiveData<Double>()
    val promedioParticipantes: LiveData<Double> = _promedio_participantes
    ///Pie
    private val _conteoGenero = MutableLiveData<List<PieEntry>>()
    val conteoGenero: LiveData<List<PieEntry>> = _conteoGenero
    //Bar
    private val _conteoPrograma = MutableLiveData<Pair<List<BarEntry>, List<String>>>()
    val conteoPrograma: LiveData<Pair<List<BarEntry>, List<String>>> = _conteoPrograma

    //Bar
    private val _conteoEtnico = MutableLiveData<Pair<List<BarEntry>, List<String>>>()
    val conteoEtnico: LiveData<Pair<List<BarEntry>, List<String>>> = _conteoEtnico
    //Bar
    private val _conteoEdades = MutableLiveData<Pair<List<BarEntry>, List<String>>>()
    val conteoEdades: LiveData<Pair<List<BarEntry>, List<String>>> = _conteoEdades
    //Bar
    private val _conteoDiscapacidades = MutableLiveData<Pair<List<BarEntry>, List<String>>>()
    val conteoDiscapacidades: LiveData<Pair<List<BarEntry>, List<String>>> = _conteoDiscapacidades
    //Bar
    private val _conteoSedes = MutableLiveData<Pair<List<BarEntry>, List<String>>>()
    val conteoSedes: LiveData<Pair<List<BarEntry>, List<String>>> = _conteoSedes







    /**
     * Llama al endpoint de estadisticas y actualiza los LiveData
     */
    fun fetchEstadisticasTalleres(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = makeRequest(
                    """${APIConstant.BACKEND_URL}api/talleres/statistics/""",
                    "GET",
                    PrefsHelper.getDRFToken(context).toString()
                )

                val json = JSONObject(response)
                Log.d("EstadisticasQuejasViewModel", "JSON completo: $json")

                // actualizar los LiveData en hilo seguro
                _talleres.postValue(json)


                val modalidadVirtual = json.optInt("virtual_workshops", 0)
                val modalidadPresencial = json.optInt("in_person_workshops", 0)

                val modalidadesEntries = listOf(
                    PieEntry(modalidadVirtual.toFloat(), "Modalidad Virtual"),
                    PieEntry(modalidadPresencial.toFloat(), "Modalidad Presencial")
                )
                _conteoModalidades.postValue(modalidadesEntries)

                _conteoPrograma.postValue(
                    transformarABarEntries(json.getJSONArray("program_stats"), "program", "count")
                )

                _conteoGenero.postValue(
                    transformarAPieEntries(json.getJSONArray("gender_stats"), "gender_identity", "count")
                )

                _total_talleres.postValue(json.getInt("total_workshops"))
                _conteoVirtuales.postValue(json.getInt("virtual_workshops"))

                _conteoDiscapacidades.postValue(
                    transformarABarEntries(json.getJSONArray("disability_stats"), "disability", "count")
                )

                _conteoSedes.postValue(
                    transformarABarEntries(json.getJSONArray("sede_stats"), "sede", "count")
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
