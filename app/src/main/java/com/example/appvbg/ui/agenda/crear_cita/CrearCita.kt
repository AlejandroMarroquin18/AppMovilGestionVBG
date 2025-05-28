package com.example.appvbg.ui.agenda.crear_cita
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.appvbg.R
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.Calendar
import com.example.appvbg.databinding.FragmentCrearCitaBinding




class CrearCita : BottomSheetDialogFragment() {

    private var _binding: FragmentCrearCitaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCrearCitaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewFecha.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    val fecha = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
                    binding.textViewFecha.text = fecha
                },
                year, month, day
            )
            datePicker.show()
        }

        binding.textViewHoraFinalizacion.setOnClickListener {
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
                Toast.makeText(requireContext(), "Cita creada", Toast.LENGTH_LONG).show()
                clearFields()
            }
            dismiss()
        }

        binding.btnReiniciar.setOnClickListener {
            clearFields()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun buildJSON(): JSONObject {
        return JSONObject()
    }

    private fun enviarWorkshopJson(url: String, jsonObject: JSONObject) {
        // Implementación real aquí
    }

    private fun clearFields() {
        // Implementación real aquí
    }
}
