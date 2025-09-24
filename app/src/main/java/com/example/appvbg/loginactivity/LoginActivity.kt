package com.example.appvbg.loginactivity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.CustomCredential
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.example.appvbg.MainActivity
import com.example.appvbg.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.io.InputStreamReader
import java.net.URL
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.appvbg.APIConstant
import com.example.appvbg.api.makeRequest
import kotlinx.coroutines.CoroutineScope


class LoginActivity : AppCompatActivity() {

    private val serverClientId by lazy { getString(R.string.server_client_id) }

    private lateinit var btnGoogle: Button
    private lateinit var loginbutton: Button
    private lateinit var forgotpassword: TextView
    //private lateinit var progress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnGoogle = findViewById(R.id.googleSignInButton)
        btnGoogle.setOnClickListener { signInWithGoogle() }

        loginbutton = findViewById(R.id.loginButton)
        loginbutton.setOnClickListener { signInWithEmail() }

        forgotpassword = findViewById(R.id.forgottenPassword)
        forgotpassword.setOnClickListener { forgotPassword() }

    }


    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    // -------------------
    // Sign in with Google
    // -------------------
    private fun signInWithGoogle() {
        val credentialManager = CredentialManager.create(this)
        val googleIdOption = GetGoogleIdOption.Builder().setServerClientId(serverClientId) // Web client ID (del servidor)
        .setFilterByAuthorizedAccounts(false) // true: sólo cuentas ya usadas con tu app
        .build()
        val request = GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()
        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(this@LoginActivity, request)
                val credential = result.credential
                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleCred = GoogleIdTokenCredential.createFrom(credential.data)
                    val idToken = googleCred.idToken
                    sendTokensToBackend(idToken,null)
                }
                else {
                    Toast.makeText(this@LoginActivity, "No se obtuvo credencial válida", Toast.LENGTH_SHORT).show()
                }
            } catch (e: GetCredentialException) {
                when (e) {
                    is GetCredentialCancellationException -> {
                        Log.d("Login", "Usuario canceló el login")
                    }
                    else -> {
                        Log.e("Login", "Error en Credential Manager", e)
                    }
                }
            }
            catch (e: Exception) {
                Log.e("Login", "Unexpected error: ${e.message}", e)
                Toast.makeText(this@LoginActivity, "Error inesperado", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // -------------------
    // Backend exchange
    // -------------------
    fun sendTokensToBackend(idToken: String, serverAuthCode: String?) {
        val url = URL("""${APIConstant.BACKEND_URL}api/auth/androidGoogleAuth/""")
        val jsonInput = buildString {
            append("""{"id_token":"$idToken"""")
            if (!serverAuthCode.isNullOrEmpty()) {
                append(""", "server_auth_code":"$serverAuthCode"""")
            }
            append("}")
        }

        lifecycleScope.launch(Dispatchers.IO) {
            var response: String? = null
            try {
                with(url.openConnection() as HttpURLConnection) {
                    requestMethod = "POST"
                    doInput = true
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json")
                    setRequestProperty("Accept", "application/json")

                    outputStream.use { os ->
                        val input = jsonInput.toByteArray(Charsets.UTF_8)
                        os.write(input, 0, input.size)
                    }

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
                    Log.e("Login", "Response: $response")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            withContext(Dispatchers.Main) {
                if (response.isNullOrEmpty()) {
                    Toast.makeText(this@LoginActivity, "No se pudo conectar con el servidor", Toast.LENGTH_LONG).show()
                    return@withContext
                }

                try {
                    val json = JSONObject(response)

                    if (json.has("error")) {
                        val errMsg = json.optString("error", "Error de autenticación")
                        Toast.makeText(this@LoginActivity, errMsg, Toast.LENGTH_LONG).show()
                        return@withContext
                    }

                    val token = json.optString("token", "")
                    val userjson= json.optJSONObject("user")
                    val email = userjson.optString("email", "")
                    val rol = userjson.optString("rol", "")
                    val nombre = userjson.optString("nombre", "")
                    if (token.isBlank()) {
                        Toast.makeText(this@LoginActivity, "Respuesta inválida del servidor", Toast.LENGTH_LONG).show()
                        return@withContext
                    }

                    // Guardar token de forma segura
                    saveTokenSecure(token,nombre,email,rol)

                    // Mostrar mensaje y cambiar de actividad
                    Toast.makeText(this@LoginActivity, "Autenticado", Toast.LENGTH_SHORT).show()
                    goToMain()

                } catch (je: JSONException) {
                    Log.e("Login", "JSON parse error: ${je.message}", je)
                    Toast.makeText(this@LoginActivity, "Respuesta no válida", Toast.LENGTH_LONG).show()
                }
            }
        }
    }



    // -------------------
    // Secure storage, guarda la info del usuario en la memoria local
    // -------------------
    private fun saveTokenSecure(token: String, nombre:String,email: String,rol:String) {
        // Intenta EncryptedSharedPreferences; si falla, cae a SharedPreferences normal


        val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        sharedPref.edit().putString("authToken", token).apply()
        sharedPref.edit().putString("nombre", nombre).apply()
        sharedPref.edit().putString("email", email).apply()
        sharedPref.edit().putString("rol", rol).apply()

    }


    /**
     * Funcion para autenticar al usuario vía correo electronico
     */
    private fun signInWithEmail() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val email = findViewById<EditText>(R.id.emailEditText).text.toString()
                val password = findViewById<EditText>(R.id.passwordEditText).text.toString()
                val json = JSONObject()
                json.put("email", email)
                json.put("password", password)

                val response = makeRequest(
                    """${APIConstant.BACKEND_URL}api/login/""",
                    "POST",
                    "",
                    json
                )

                if (response != "error") {
                    val jsonresponse = JSONObject(response)
                    val userjson = jsonresponse.optJSONObject("user")

                    saveTokenSecure(
                        jsonresponse.getString("token"),
                        userjson.getString("nombre"),
                        userjson.getString("email"),
                        userjson.getString("rol")
                    )
                    Toast.makeText(this@LoginActivity, "Autenticado", Toast.LENGTH_SHORT).show()
                    goToMain()

                }
            }catch (e: Exception) {
                Log.e("Login", "Error en el inicio de sesión: ${e.message}", e)
                Toast.makeText(this@LoginActivity, "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
            }

        }

    }
    private fun forgotPassword() {
        //cambia a un fragment donde se envía el correo, luego donde se introduce el codigo
        //para finalmente cambiar la contraseña

    }
    companion object {
        private const val RC_SIGN_IN = 9001
    }


}
