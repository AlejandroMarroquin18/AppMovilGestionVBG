package com.example.appvbg.ui.talleres.crear_taller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.appvbg.R
import com.example.appvbg.databinding.FragmentCrearTallerBinding
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL


import java.util.Calendar
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android. util. Log

import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray


class CrearTallerFragment: Fragment(R.layout.fragment_crear_taller) {
    private var _binding: FragmentCrearTallerBinding? = null
    private val binding get() = _binding!!
    private lateinit var editModalidad: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCrearTallerBinding.inflate(inflater, container, false)

        val modalidades = listOf("presencial", "virtual")
        editModalidad = binding.editModalidad
        editModalidad.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, modalidades)

        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.textViewHoraInicio.setOnClickListener {
            // ...
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePicker = TimePickerDialog(
                requireContext(),
                { _, selectedHour, selectedMinute ->
                    val hora = String.format("%02d:%02d", selectedHour, selectedMinute)
                    binding.textViewHoraInicio.text = hora
                },
                hour, minute, true
            )
            timePicker.show()
        }

        binding.textViewHoraFinalizacion.setOnClickListener {
            // ...
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePicker = TimePickerDialog(
                requireContext(),
                { _, selectedHour, selectedMinute ->
                    val hora = String.format("%02d:%02d", selectedHour, selectedMinute)
                    binding.textViewHoraFinalizacion.text = hora
                },
                hour, minute, true
            )
            timePicker.show()
        }

        binding.btnCrear.setOnClickListener {
            val jsonData = buildJSON()
            lifecycleScope.launch {
                val respuesta = withContext(Dispatchers.IO) {
                    enviarWorkshopJson("http://192.168.0.32:8000/api/talleres/", jsonData)
                }
                Toast.makeText(requireContext(), respuesta, Toast.LENGTH_LONG).show()
                clearFields()
            }
        }

        binding.btnReiniciar.setOnClickListener {
            clearFields()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun clearFields() {
        binding.editNombre.text.clear()
        binding.textViewFecha.text = "seleccionar Fecha"
        binding.textViewHoraInicio.text = "Seleccionar hora"
        binding.textViewHoraFinalizacion.text = "seleccionar hora"
        binding.editUbicacion.text.clear()
        editModalidad.setSelection(0)
        binding.editCupos.text.clear()
        binding.editTallerista.text.clear()
        binding.editDetalles.text.clear()
    }

    fun buildJSON():JSONObject {
        val json = JSONObject()
        json.put("name", binding.editNombre.text.toString())
        json.put("date", binding.textViewFecha.text.toString())
        json.put("start_time", binding.textViewHoraInicio.text.toString())
        json.put("end_time", binding.textViewHoraFinalizacion.text.toString())
        json.put("location", binding.editUbicacion.text.toString())




        //json.put("modality", editModalidad.selectedItem as String)
        json.put("slots", binding.editCupos.text.toString())
        //val facilitatorsArray = JSONArray().put(1)  // idDelTallerista debe ser un Int
        //json.put("facilitators", facilitatorsArray)
        json.put("details", binding.editDetalles.text.toString())
        json.put("participants", JSONArray())


// Facilitators (debe ser un ID válido)
        val facilitatorsArray = JSONArray().put(1)
        json.put("facilitators", facilitatorsArray)

        return json;

    }

    fun enviarWorkshopJson(apiUrl: String, jsonData: JSONObject): String {
        try {
            val url = URL(apiUrl)
            val conn = url.openConnection() as HttpURLConnection

            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; utf-8")
            conn.setRequestProperty("Accept", "application/json")
            conn.doOutput = true
            conn.doInput = true

            // Escribir el JSON en el cuerpo de la solicitud
            val outputWriter = OutputStreamWriter(conn.outputStream, "UTF-8")
            outputWriter.write(jsonData.toString())
            outputWriter.flush()
            outputWriter.close()

            // Leer la respuesta
            val responseCode = conn.responseCode
            val inputStream = if (responseCode in 200..299) {
                conn.inputStream
            } else {
                conn.errorStream
            }

            val response = inputStream.bufferedReader().use { it.readText() }
            Log.e("WorkshopError", response)
            conn.disconnect()

            return response
        } catch (e: Exception) {
            e.printStackTrace()
            return "Error: ${e.message}"
        }
    }
}