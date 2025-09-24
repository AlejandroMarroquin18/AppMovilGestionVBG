package com.example.appvbg.api
// ApiService.kt
import android.content.Context
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import androidx.core.content.edit
import com.example.appvbg.APIConstant
import org.json.JSONObject
import java.io.BufferedWriter
import java.io.InputStream
import java.io.OutputStreamWriter


data class GoogleLoginRequest(val id_token: String)
data class User(val email: String, val nombre: String, val rol:String)
data class LoginResponse(val token: String, val user: User, val is_new_user: Boolean)




interface ApiService {
    @POST("google_login/")  //  tu endpoint Django
    fun loginWithGoogle(@Body request: GoogleLoginRequest): Call<LoginResponse>
}

fun googleLogin(idToken: String): String? {
    val url = URL("${APIConstant.BACKEND_URL}api/auth/androidGoogleAuth/") // cambia por tu endpoint
    val jsonInput = """{"id_token":"$idToken"}"""
    var response: String? = null

    try {
        with(url.openConnection() as HttpURLConnection) {
            requestMethod = "POST"
            doInput = true
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("Accept", "application/json")

            // Escribir el JSON en el body
            outputStream.use { os: OutputStream ->
                val input = jsonInput.toByteArray(Charsets.UTF_8)
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

            response = responseBuilder.toString()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        response = null
    }

    return response

}


/**
 * Objeto para obtener todos los atributos globales de SharedPreferences
 * que se han guardado en el dispositivo.
 */


object PrefsHelper {
    private const val PREFS_NAME = "AppPrefs"
    private const val KEY_TOKEN = "authToken"
    private const val KEY_NOMBRE = "nombre"
    private const val KEY_EMAIL = "email"
    private const val KEY_ROL = "rol"

    // Guardar valores
    fun saveAuthToken(context: Context, token: String) {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPref.edit { putString(KEY_TOKEN, token) }
    }

    fun saveNombre(context: Context, nombre: String) {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPref.edit { putString(KEY_NOMBRE, nombre) }
    }

    fun saveEmail(context: Context, email: String) {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPref.edit { putString(KEY_EMAIL, email) }
    }

    fun saveRol(context: Context, rol: String) {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPref.edit { putString(KEY_ROL, rol) }
    }

    // Obtener valores
    fun getDRFToken(context: Context): String? {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPref.getString(KEY_TOKEN, null)
    }

    fun getNombre(context: Context): String? {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPref.getString(KEY_NOMBRE, null)
    }

    fun getEmail(context: Context): String? {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPref.getString(KEY_EMAIL, null)
    }

    fun getRol(context: Context): String? {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPref.getString(KEY_ROL, null)
    }

    // Eliminar all (para logout, por ejemplo)
    fun clearAll(context: Context) {
        val sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPref.edit { clear() }
    }
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

    if (method == "POST" || method == "PUT"||method=="PATCH") {
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
