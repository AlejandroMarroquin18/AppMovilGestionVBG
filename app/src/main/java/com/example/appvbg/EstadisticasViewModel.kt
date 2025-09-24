package com.example.appvbg

import android.content.Context
import androidx.constraintlayout.utils.widget.MotionLabel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appvbg.api.PrefsHelper
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieEntry
import org.json.JSONArray
import org.json.JSONObject
import com.example.appvbg.api.makeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.appvbg.APIConstant

class EstadisticasViewModelTemp: ViewModel() {
    private val _quejas = MutableLiveData<JSONObject>()
    ///Substats de quejas
    val quejas: LiveData<JSONObject> = _quejas

    var _conteoQuejaEstudiantes = 0
    var _conteoQuejaProfesores = 0
    var _conteoQuejaFuncionarios = 0

    lateinit var _conteoQuejaAnio: Pair<List<BarEntry>, List<String>>
    lateinit var _conteoQuejaMes : Pair<List<BarEntry>, List<String>>
    lateinit var _conteoQuejasFacultad : Pair<List<BarEntry>, List<String>>
    lateinit var _conteoQuejasSede : Pair<List<BarEntry>, List<String>>
    lateinit var _conteoVice : List<PieEntry>
    lateinit var _conteoGenero : List<PieEntry>



    fun fetchEstadisticasQuejas(context: Context) {

        viewModelScope.launch(Dispatchers.IO) {
            try{
            //fetch
                val response=makeRequest(
                    """${APIConstant.BACKEND_URL}/api/estadisticas/""",
                    "GET",
                    PrefsHelper.getDRFToken(context).toString()
                )


                //transformar a json
                val json=JSONObject(response)

                withContext(Dispatchers.Main) {
                    //getting the values
                    _conteoQuejaEstudiantes = json.getInt("conteo_quejas_estudiantes")
                    _conteoQuejaProfesores = json.getInt("conteo_quejas_profesores")
                    _conteoQuejaFuncionarios = json.getInt("conteo_quejas_funcionarios")
                    ///iterando
                    _conteoQuejaAnio =transformarJSONAEntries(json.getJSONObject("conteo_por_anio"))//BarEntry
                    _conteoQuejaMes =transformarJSONAEntries(json.getJSONObject("conteo_por_mes"))//BarEntry
                    _conteoQuejasFacultad =transformarABarEntries(json.getJSONArray("conteo_por_facultad_afectado"),"afectado_facultad","total")//BarEntry
                    _conteoQuejasSede =transformarABarEntries(json.getJSONArray("conteo_por_sede_afectado"),"afectado_sede","total")//BarEntry
                    _conteoVice =transformarAPieEntries(json.getJSONArray("conteo_por_vice"),"vice","total")
                    _conteoGenero =transformarAPieEntries(json.getJSONArray("conteo_por_genero"),"genero","total")


                }
                } catch (e: Exception) {
                e.printStackTrace()

            }
        }

    }






    /**
     * Transforma un JSONArray en una lista de PieEntry
     * @param jsonArray JSONArray con los datos a transformar
     * @param countLabel Nombre del campo que contiene el conteo
     * @param totalLabel Nombre del campo que contiene el total
     * @return Lista de PieEntry con los datos transformados
     */
    private fun transformarAPieEntries(jsonArray: JSONArray,countLabel:String,totalLabel: String): List<PieEntry> {
        val entries = mutableListOf<PieEntry>()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            //val facultad = obj.getString("afectado_facultad")
            val facultad = obj.getString(countLabel)
            val total = obj.getInt(totalLabel)
            entries.add(PieEntry(total.toFloat(), facultad))
        }
        return entries
    }

    /**
     * Transforma un JSON en una lista de BarEntry
     * @param jsonArray JSONArray con los datos a transformar
     *
     */
    private fun transformarJSONAEntries(json: JSONObject): Pair<List<BarEntry>, List<String>> {
        val entries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()
        var index = 0f

        val keys = json.keys()
        while (keys.hasNext()) {
            val year = keys.next()
            val value = json.getInt(year)

            entries.add(BarEntry(index, value.toFloat()))
            labels.add(year)

            index++
        }

        return Pair(entries, labels)
    }



    /**
     * Transforma un JSONArray en una lista de BarEntry
     * @param jsonArray JSONArray con los datos a transformar
     * @param countLabel Nombre del campo que contiene el conteo
     * @param totalLabel Nombre del campo que contiene el total
     */
    private fun transformarABarEntries(jsonArray: JSONArray,countLabel:String,totalLabel: String): Pair<List<BarEntry>, List<String>> {
        val entries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            val facultad = obj.getString(countLabel)
            val total = obj.getInt(totalLabel)

            // usamos el índice i como posición en X
            entries.add(BarEntry(i.toFloat(), total.toFloat()))
            labels.add(facultad)
        }

        return Pair(entries, labels)
    }
}