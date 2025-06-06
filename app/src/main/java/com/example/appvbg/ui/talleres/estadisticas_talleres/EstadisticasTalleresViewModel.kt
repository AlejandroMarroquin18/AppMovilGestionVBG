package com.example.appvbg.ui.talleres.estadisticas_talleres
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import android.os.StrictMode
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope


class EstadisticasTalleresViewModel : ViewModel() {

    //private val repository = EstadisticasRepository()

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _estadisticas = MutableLiveData<EstadisticasTalleres>()
    val estadisticas: LiveData<EstadisticasTalleres> = _estadisticas

    init {
        fetchEstadisticas("https://tudominio.com/api")
    }



    private fun fetchEstadisticas(baseURL: String) {
        _loading.postValue(true)

        viewModelScope.launch {
            try {
                val url = URL("$baseURL/talleres/statistics/")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 5000
                connection.readTimeout = 5000

                val responseCode = connection.responseCode
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    _error.postValue("Error al cargar estadísticas: Código $responseCode")
                    _loading.postValue(false)
                    return@launch
                }

                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String?

                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                reader.close()
                connection.disconnect()

                val json = JSONObject(response.toString())

                val stats = EstadisticasTalleres(
                    totalWorkshops = json.getInt("total_workshops"),
                    virtualWorkshops = json.getInt("virtual_workshops"),
                    inPersonWorkshops = json.getInt("in_person_workshops"),
                    totalParticipants = json.getInt("total_participants"),
                    averageParticipants = json.getDouble("average_participants"),
                    genderStats = json.getJSONObject("gender_stats"),
                    programStats = json.getJSONObject("program_stats")
                )

                _estadisticas.postValue(stats)

            } catch (e: Exception) {
                Log.e("StatsViewModel", "Error: ${e.message}", e)
                _error.postValue("Error: ${e.message}")
            } finally {
                _loading.postValue(false)
            }
        }

    }
}

data class EstadisticasTalleres(
    val totalWorkshops: Int,
    val virtualWorkshops: Int,
    val inPersonWorkshops: Int,
    val totalParticipants: Int,
    val averageParticipants: Double,
    val genderStats: JSONObject,
    val programStats: JSONObject
)
