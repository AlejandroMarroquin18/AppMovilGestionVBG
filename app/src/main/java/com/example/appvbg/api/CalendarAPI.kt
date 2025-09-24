package com.example.appvbg.api

import android.content.Context
import com.example.appvbg.APIConstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

//llamar en el contexto como requireContext()




suspend fun updateGoogleAcces(accessToken: String?, serverAuthCode: String, context: Context ): String? = withContext(Dispatchers.IO){
    //llama a la clase que obtiene las prefs
    val drfToken = PrefsHelper.getDRFToken(context)


    val url = URL("${APIConstant.BACKEND_URL}api/auth/authorizeGoogleAccesss/") // cambia por tu endpoint
    val jsonBody = JSONObject()
    jsonBody.put("server_auth_code", serverAuthCode)
    accessToken?.let { jsonBody.put("access_token", it) }
    var response: String? = null


    try {
        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "POST"
            doInput = true
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("Accept", "application/json")
            setRequestProperty("Authorization", "Token $drfToken")

            // Escribir el JSON en el body
            outputStream.use { os ->
                val input = jsonBody.toString().toByteArray(Charsets.UTF_8)
                os.write(input, 0, input.size)
            }

            // Leer respuesta
            val reader = if (responseCode in 200..299) {
                BufferedReader(InputStreamReader(inputStream, Charsets.UTF_8))
            } else {
                BufferedReader(InputStreamReader(errorStream, Charsets.UTF_8))
            }

            val responseBuilder = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                responseBuilder.append(line)
            }
            reader.close()

            responseBuilder.toString()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * CalendarApi.fetchEvents(requireContext(), 2025) { events ->
 *     requireActivity().runOnUiThread {
 *         for (event in events) {
 *             val title = event.optString("summary", "Sin título")
 *             println("Evento: $title")
 *         }
 *     }
 * }
 */
object CalendarApi {

    private const val BASE_URL = APIConstant.BACKEND_URL

    private fun getAuthToken(context: Context): String {
        val sharedPref = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        return sharedPref.getString("authToken", "") ?: ""
    }

    fun makeRequest(
        urlString: String,
        method: String,
        token: String,
        body: JSONObject? = null
    ): String {
        val url = URL(urlString)
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = method
        conn.setRequestProperty("Authorization", "Token $token")
        conn.setRequestProperty("Content-Type", "application/json")

        if (method == "POST" || method == "PUT") {
            conn.doOutput = true
            BufferedWriter(OutputStreamWriter(conn.outputStream, "UTF-8")).use { writer ->
                writer.write(body.toString())
            }
        }

        val responseCode = conn.responseCode
        val inputStream: InputStream =
            if (responseCode in 200..299) conn.inputStream else conn.errorStream

        val response = inputStream.bufferedReader().use { it.readText() }
        conn.disconnect()

        return if (responseCode in 200..299) response else "error"
    }


    fun fetchEvents(context: Context, year: Int, callback: (JSONArray) -> Unit) {
        Thread {
            val token = getAuthToken(context)

            val url = "${BASE_URL}api/calendar/fetchEvents/${year}"

            try {
                val rawResponse = makeRequest(url, "GET", token)
                val eventsList = JSONArray(rawResponse)
                callback(eventsList)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }.start()
    }

    fun createEvent(context: Context, jsonObject: JSONObject?, callback: (String) -> Unit) {
        if(jsonObject==null ) return callback("Error: jsonObject is null")
        Thread {
            val token = getAuthToken(context)
            val url = "${BASE_URL}api/calendar/create"
            val result = makeRequest(url, "POST", token, jsonObject)
            callback(result)
        }.start()
    }

    fun updateEvent(context: Context, eventId: String?, jsonObject: JSONObject?, callback: (String) -> Unit) {
        if(eventId==null || jsonObject==null ) return callback("Error: eventId is null")
        Thread {
            val token = getAuthToken(context)
            val url = "${BASE_URL}api/calendar/update/${eventId}"
            val result = makeRequest(url, "PUT", token, jsonObject)
            callback(result)
        }.start()
    }

    fun deleteEvent(context: Context, eventId: String?, callback: (String) -> Unit) {
        Thread {
            val token = getAuthToken(context)
            val url = "${BASE_URL}api/calendar/delete/${eventId}"
            val result = makeRequest(url, "DELETE", token)
            callback(result)
        }.start()
    }

    fun fetchEventById(context: Context, eventId: String?, callback: (String) -> Unit) {
        Thread {
            val token = getAuthToken(context)
            val url = "${BASE_URL}api/calendar/fetchById/${eventId}"
            val result = makeRequest(url, "GET", token)
            callback(result)
        }.start()
    }
}


