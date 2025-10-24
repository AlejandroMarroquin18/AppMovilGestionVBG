package com.example.appvbg.loginactivity

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.appvbg.APIConstant
import com.example.appvbg.MainActivity
import com.example.appvbg.R
import com.example.appvbg.api.makeRequest
import com.example.appvbg.databinding.FragmentLoginBinding
import com.example.appvbg.loginactivity.LoginActivity
import com.example.appvbg.loginactivity.forgottenPassword.RecuperarContrasenaFragment
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL



class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val serverClientId by lazy { getString(R.string.server_client_id) }

    private lateinit var btnGoogle: Button
    private lateinit var loginbutton: Button
    private lateinit var forgotpassword: TextView
    //private lateinit var progress: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_login)


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnGoogle = binding.googleSignInButton
        btnGoogle.setOnClickListener { signInWithGoogle() }

        loginbutton = binding.loginButton
        loginbutton.setOnClickListener { signInWithEmail() }

        forgotpassword = binding.forgottenPassword
        forgotpassword.setOnClickListener { forgotPassword() }
    }


    private fun goToMain() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish() // cierra la LoginActivity
    }

    // -------------------
    // Sign in with Google
    // -------------------
    private fun signInWithGoogle() {
        val credentialManager = CredentialManager.create(requireContext())
        val googleIdOption = GetGoogleIdOption.Builder().setServerClientId(serverClientId) // Web client ID (del servidor)
            .setFilterByAuthorizedAccounts(false) // true: sólo cuentas ya usadas con tu app
            .build()
        val request = GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()
        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(requireActivity(), request)
                val credential = result.credential
                if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleCred = GoogleIdTokenCredential.createFrom(credential.data)
                    val idToken = googleCred.idToken
                    sendTokensToBackend(idToken,null)
                }
                else {
                    Toast.makeText(requireContext(), "No se obtuvo credencial válida", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(requireContext(), "Error inesperado", Toast.LENGTH_SHORT).show()
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
                    Toast.makeText(requireContext(), "No se pudo conectar con el servidor", Toast.LENGTH_LONG).show()
                    return@withContext
                }

                try {
                    val json = JSONObject(response)

                    if (json.has("error")) {
                        val errMsg = json.optString("error", "Error de autenticación")
                        Toast.makeText(requireContext(), errMsg, Toast.LENGTH_LONG).show()
                        return@withContext
                    }

                    val token = json.optString("token", "")
                    val userjson= json.optJSONObject("user")
                    val email = userjson.optString("email", "")
                    val rol = userjson.optString("rol", "")
                    val nombre = userjson.optString("nombre", "")
                    if (token.isBlank()) {
                        Toast.makeText(requireContext(), "Respuesta inválida del servidor", Toast.LENGTH_LONG).show()
                        return@withContext
                    }

                    // Guardar token de forma segura
                    saveTokenSecure(token,nombre,email,rol)

                    // Mostrar mensaje y cambiar de actividad
                    Toast.makeText(requireContext(), "Autenticado", Toast.LENGTH_SHORT).show()
                    goToMain()

                } catch (je: JSONException) {
                    Log.e("Login", "JSON parse error: ${je.message}", je)
                    Toast.makeText(requireContext(), "Respuesta no válida", Toast.LENGTH_LONG).show()
                }
            }
        }
    }



    // -------------------
    // Secure storage, guarda la info del usuario en la memoria local
    // -------------------
    private fun saveTokenSecure(token: String, nombre: String, email: String, rol: String) {
        val sharedPref = requireActivity().getSharedPreferences("AppPrefs", AppCompatActivity.MODE_PRIVATE)
        sharedPref.edit().apply {
            putString("authToken", token)
            putString("nombre", nombre)
            putString("email", email)
            putString("rol", rol)
            apply()
        }
    }



    /**
     * Funcion para autenticar al usuario vía correo electronico
     */
    private fun signInWithEmail() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val email = binding.emailEditText.text.toString()
                val password = binding.passwordEditText.text.toString()
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
                    Toast.makeText(requireContext(), "Autenticado", Toast.LENGTH_SHORT).show()
                    goToMain()

                }
            }catch (e: Exception) {
                Log.e("Login", "Error en el inicio de sesión: ${e.message}", e)
                Toast.makeText(requireContext(), "Error al iniciar sesión", Toast.LENGTH_SHORT).show()
            }

        }

    }
    private fun forgotPassword() {
        //cambia a un fragment donde se envía el correo, luego donde se introduce el codigo
        //para finalmente cambiar la contraseña

        binding.forgottenPassword.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, RecuperarContrasenaFragment())
                .addToBackStack(null) // permite volver atrás con el botón de retroceso
                .commit()
        }

    }
    companion object {
        private const val RC_SIGN_IN = 9001
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }





}