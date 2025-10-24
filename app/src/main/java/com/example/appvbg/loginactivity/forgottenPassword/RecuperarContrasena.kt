package com.example.appvbg.loginactivity.forgottenPassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.appvbg.APIConstant
import com.example.appvbg.R
import com.example.appvbg.api.PrefsHelper
import com.example.appvbg.api.makeRequest
import com.example.appvbg.loginactivity.LoginFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class RecuperarContrasenaFragment : Fragment() {

    private lateinit var contenedor: FrameLayout
    private var correoUsuario: String? = null
    private var codigoUsuario: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_recuperar_contrasena, container, false)
        contenedor = root.findViewById(R.id.contenedor_recuperar)
        mostrarPasoCorreo()
        return root
    }

    private fun mostrarPasoCorreo() {
        contenedor.removeAllViews()
        val vista = layoutInflater.inflate(R.layout.layout_paso_email, contenedor, false)
        contenedor.addView(vista)

        val inputCorreo = vista.findViewById<EditText>(R.id.inputCorreo)
        val btnEnviar = vista.findViewById<Button>(R.id.btnEnviarCorreo)

        btnEnviar.setOnClickListener {
            val correo = inputCorreo.text.toString().trim()
            if (correo.isEmpty()) {
                Toast.makeText(requireContext(), "Ingresa un correo", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Llamar al endpoint de backend para enviar código
            lifecycleScope.launch(Dispatchers.IO) {
                try{
                    val body = JSONObject()
                    body.put("email", correo)
                    val response = makeRequest(
                        """${APIConstant.BACKEND_URL}api/forgottenPassword/""",
                        "POST",
                        PrefsHelper.getDRFToken(requireContext()).toString(),
                        body
                    )
                    withContext(Dispatchers.Main) {
                        if (response!= "error"){
                            correoUsuario = correo
                            Toast.makeText(requireContext(), "Código enviado al correo", Toast.LENGTH_SHORT).show()
                            mostrarPasoCodigo()
                        }else {
                            Toast.makeText(requireContext(), "Error al enviar el correo", Toast.LENGTH_SHORT).show()
                        }

                    }

                }catch (e:Exception){
                    Toast.makeText(requireContext(), "Error al enviar el correo", Toast.LENGTH_SHORT).show()
                }
            }



        }
    }

    private fun mostrarPasoCodigo() {
        contenedor.removeAllViews()
        val vista = layoutInflater.inflate(R.layout.layout_paso_codigo, contenedor, false)
        contenedor.addView(vista)

        val inputCodigo = vista.findViewById<EditText>(R.id.inputCodigo)
        val btnVerificar = vista.findViewById<Button>(R.id.btnVerificarCodigo)

        btnVerificar.setOnClickListener {
            val codigo = inputCodigo.text.toString().trim()
            if (codigo.isEmpty()) {
                Toast.makeText(requireContext(), "Ingresa el código", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Verificar código con el backend
            lifecycleScope.launch(Dispatchers.IO) {
                try{
                    val body = JSONObject()
                    body.put("codigo", codigo)
                    body.put("email", correoUsuario)
                    val response = makeRequest(
                        """${APIConstant.BACKEND_URL}api/validateForgottenPasswordCode/""",
                        "POST",
                        PrefsHelper.getDRFToken(requireContext()).toString(),
                        body
                    )
                    withContext(Dispatchers.Main) {

                        if (response!= "error"){
                            codigoUsuario = codigo
                            Toast.makeText(requireContext(), "Código válido", Toast.LENGTH_SHORT).show()
                            mostrarPasoNuevaContrasena()
                        }else {
                            Toast.makeText(requireContext(), "Error al enviar el correo", Toast.LENGTH_SHORT).show()
                        }
                    }

                }catch (e:Exception){
                    Toast.makeText(requireContext(), "Error al enviar el correo", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun mostrarPasoNuevaContrasena() {
        contenedor.removeAllViews()
        val vista = layoutInflater.inflate(R.layout.layout_paso_nueva_contrasena, contenedor, false)
        contenedor.addView(vista)

        val inputContrasena = vista.findViewById<EditText>(R.id.inputNuevaContrasena)
        val confirmarContrasena = vista.findViewById<EditText>(R.id.inputConfirmarNuevaContrasena)
        val btnCambiar = vista.findViewById<Button>(R.id.btnCambiarContrasena)

        btnCambiar.setOnClickListener {
            val nuevaContrasena = inputContrasena.text.toString().trim()
            val confirmarContrasena = confirmarContrasena.text.toString().trim()
            if (nuevaContrasena != confirmarContrasena) {
                Toast.makeText(requireContext(), "Las contraseñas deben coincidir", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //  Enviar nueva contraseña al backend
            lifecycleScope.launch(Dispatchers.IO) {
                try{
                    val body = JSONObject()
                    body.put("email", correoUsuario)
                    body.put("codigo", codigoUsuario)
                    body.put("password", nuevaContrasena)
                    val response = makeRequest(
                        """${APIConstant.BACKEND_URL}api/changeForgottenPassword/""",
                        "POST",
                        PrefsHelper.getDRFToken(requireContext()).toString(),
                        body
                    )
                    withContext(Dispatchers.Main) {
                        if (response!= "error"){

                            Toast.makeText(requireContext(), "Contraseña cambiada correctamente", Toast.LENGTH_SHORT).show()
                            pasoaLogin()
                        }else {
                            Toast.makeText(requireContext(), "Error al enviar el correo", Toast.LENGTH_SHORT).show()
                        }
                    }

                }catch (e:Exception){
                    Toast.makeText(requireContext(), "Error al enviar el correo", Toast.LENGTH_SHORT).show()
                }
            }





            // Opcional: volver al login
        }
    }

    private fun pasoaLogin(){
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, LoginFragment())
            .commit()
    }
    override fun onDestroyView() {
        super.onDestroyView()
    }
}
