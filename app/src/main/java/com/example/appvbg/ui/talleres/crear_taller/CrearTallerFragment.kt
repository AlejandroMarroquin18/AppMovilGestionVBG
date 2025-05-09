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


import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext




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
    ): View? {
        val view = inflater.inflate(R.layout.fragment_crear_taller, container, false)

        _binding = FragmentCrearTallerBinding.inflate(inflater, container, false)
        val modalidades = listOf("Presencial", "Virtual")

        editModalidad = view.findViewById(R.id.editModalidad)

        editModalidad.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, modalidades)


        return binding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val textViewFecha = view.findViewById<TextView>(R.id.textViewFecha)
        val textViewHoraInicio = view.findViewById<TextView>(R.id.horaInicioTallerTextView)
        val textViewHoraFinalizacion = view.findViewById<TextView>(R.id.textViewHoraFinalizacion)
        val btnCrear = view.findViewById<TextView>(R.id.btnCrear)
        val btnReiniciar = view.findViewById<TextView>(R.id.btnReiniciar)

        textViewFecha.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->

                    val fecha = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                    textViewFecha.text = fecha
                },
                year, month, day
            )
            datePicker.show()
        }

        textViewHoraInicio.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePicker = TimePickerDialog(
                requireContext(),
                { _, selectedHour, selectedMinute ->
                    val hora = String.format("%02d:%02d", selectedHour, selectedMinute)
                    textViewHoraInicio.text = hora
                },
                hour, minute, true
            )
            timePicker.show()
        }

        textViewHoraFinalizacion.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePicker = TimePickerDialog(
                requireContext(),
                { _, selectedHour, selectedMinute ->
                    val hora = String.format("%02d:%02d", selectedHour, selectedMinute)
                    textViewHoraFinalizacion.text = hora
                },
                hour, minute, true
            )
            timePicker.show()
        }
        btnCrear.setOnClickListener {
            val jsonData = buildJSON()
            //val response = enviarWorkshopJson("http://127.0.0.1:8000/api/talleres/", json)
            lifecycleScope.launch {
                val respuesta = withContext(Dispatchers.IO) {
                    enviarWorkshopJson("http://127.0.0.1:8000/api/talleres/", jsonData)
                }

                Toast.makeText(requireContext(), respuesta, Toast.LENGTH_LONG).show()
                clearFields()
            }
        }

        btnReiniciar.setOnClickListener {
            clearFields()
        }


    }
    fun clearFields() {
        binding.editNombre.text.clear()
        binding.textViewFecha.text = ""
        binding.textViewHoraInicio.text = ""
        binding.textViewHoraFinalizacion.text = ""
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
        json.put("modality", editModalidad.selectedItem as String)
        json.put("slots", binding.editCupos.text.toString())
        json.put("facilitators", binding.editTallerista.text.toString())
        json.put("details", binding.editDetalles.text.toString())


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
            conn.disconnect()

            return response
        } catch (e: Exception) {
            e.printStackTrace()
            return "Error: ${e.message}"
        }
    }
}