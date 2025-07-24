package com.example.appvbg.ui.agenda.crear_cita
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.appvbg.R
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.appvbg.APIConstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.Calendar
import com.example.appvbg.databinding.FragmentCrearCitaBinding
import com.example.appvbg.ui.agenda.AgendaFragment
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class CrearCita : BottomSheetDialogFragment() {

    private var _binding: FragmentCrearCitaBinding? = null
    private val binding get() = _binding!!

    //lateinit var parentFragment: AgendaFragment;
    lateinit var tituloValue: String ;
    lateinit var fechaValue: String ;
    lateinit var horaInicioValue: String ;
    lateinit var horaFinalizacionValue: String ;
    lateinit var lugarValue: String;
    lateinit var idCasoValue: String;
    lateinit var detallesValue: String;
    //lateinit var invitadosValue: String;
    lateinit var colorValue: String;




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
                    fechaValue = fecha
                },
                year, month, day
            )
            datePicker.show()
        }

        binding.textViewHoraInicio.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePicker = TimePickerDialog(
                requireContext(),
                { _, selectedHour, selectedMinute ->
                    val hora = String.format("%02d:%02d", selectedHour, selectedMinute)
                    binding.textViewHoraInicio.text = hora
                    horaInicioValue = hora
                },
                hour, minute, true
            )
            timePicker.show()
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
                    horaFinalizacionValue = hora
                },
                hour, minute, true
            )
            timePicker.show()
        }

        binding.btnCrear.setOnClickListener {
            val jsonData = buildJSON()
            lifecycleScope.launch {
                val respuesta = withContext(Dispatchers.IO) {
                    enviarWorkshopJson(APIConstant.BACKEND_URL+ "http://192.168.0.32:8000/api/talleres/", jsonData)
                }
                Toast.makeText(requireContext(), "Cita creada", Toast.LENGTH_LONG).show()
                clearFields()
            }

            // Empaquetar los datos que quieres enviar
            val result = Bundle().apply {
                putParcelable("nuevo_evento", sendData())  // NewEvent debe implementar Parcelable
            }

            // Enviar el resultado al fragmento que lo pidió
            parentFragmentManager.setFragmentResult("crearCitaRequestKey", result)

            Toast.makeText(requireContext(), "Cita creada", Toast.LENGTH_LONG).show()
            clearFields()

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


    fun sendData():NewEvent{

        horaInicioValue=binding.textViewHoraInicio.text.toString();
        horaFinalizacionValue=binding.textViewHoraFinalizacion.text.toString();
        fechaValue=binding.textViewFecha.text.toString();
        tituloValue=binding.title.text.toString();
        lugarValue=binding.location.text.toString();
        detallesValue=binding.details.text.toString();
        idCasoValue=binding.IDCaso.text.toString();
        colorValue="Green";
        //colorValue=binding.editTextColor.text.toString();


        val localDate = LocalDate.parse(fechaValue) // parsea "YYYY-MM-DD"

        val hh= horaInicioValue.split(":")[0].toInt()
        val mm= horaInicioValue.split(":")[1].toInt()
        val localDateTime = LocalDateTime.of(localDate.year, localDate.month, localDate.dayOfMonth, hh, mm)
        val zona = ZoneId.systemDefault() // zona horaria del dispositivo, ej: -05:00
        val zonedDateTime = localDateTime.atZone(zona)
        val finalDate=zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)



        val newEvent= NewEvent(tituloValue,
            finalDate,
            horaInicioValue,
            horaFinalizacionValue,
            lugarValue,
            idCasoValue,
            detallesValue,
            colorValue);
        return newEvent;


    }


}

@Parcelize
data class NewEvent(val title: String,
                    val date: String,
                    val startHour: String,
                    val endHour: String,
                    val location:String?=null,
                    val IDCaso:String?=null,
                    val details: String?=null,
                    val emails:String?=null,
                    val color:String?=null):
    Parcelable;