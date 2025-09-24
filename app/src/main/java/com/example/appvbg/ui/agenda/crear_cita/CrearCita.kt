package com.example.appvbg.ui.agenda.crear_cita
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.drawable.GradientDrawable
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
import android.graphics.Color
import android.widget.Spinner


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
    private val eventColors: Map<String, String> = mapOf(
        "1" to "#a4bdfc",
        "2" to "#7ae7bf",
        "3" to "#dbadff",
        "4" to "#ff887c",
        "5" to "#fbd75b",
        "6" to "#ffb878",
        "7" to "#46d6db",
        "8" to "#e1e1e1",
        "9" to "#5484ed",
        "10" to "#51b749",
        "11" to "#dc2127"
    )




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



        val spinner: Spinner = view.findViewById(R.id.colorSpinner)
        val adapter = ColorSpinnerAdapter(requireContext(), eventColors.values.toList())
        spinner.adapter = adapter

        binding.btnCrear.setOnClickListener {

            val colorList = eventColors.toList()
            val position = spinner.selectedItemPosition
            val (colorId, colorHex) = colorList[position]
            val jsonData = buildJSON()





            // Empaquetar los datos que quieres enviar
            val result = Bundle().apply {
                putParcelable("nuevo_evento", sendData(colorId))  // NewEvent debe implementar Parcelable
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



    private fun clearFields() {
        // Implementación real aquí
    }


    fun sendData(color: String):NewEvent{

        horaInicioValue=binding.textViewHoraInicio.text.toString();
        horaFinalizacionValue=binding.textViewHoraFinalizacion.text.toString();
        fechaValue=binding.textViewFecha.text.toString();
        tituloValue=binding.title.text.toString();
        lugarValue=binding.location.text.toString();
        detallesValue=binding.details.text.toString();
        idCasoValue=binding.IDCaso.text.toString();
        colorValue=color;
        //colorValue=binding.editTextColor.text.toString();




        val localDate = LocalDate.parse(fechaValue) // parsea "YYYY-MM-DD"

        val hh= horaInicioValue.split(":")[0].toInt()
        val mm= horaInicioValue.split(":")[1].toInt()
        val localDateTime = LocalDateTime.of(localDate.year, localDate.month, localDate.dayOfMonth, hh, mm)
        val zona = ZoneId.systemDefault() // zona horaria del dispositivo, ej: -05:00
        val zonedDateTime = localDateTime.atZone(zona)
        val finalDate=zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

        val hhf= horaFinalizacionValue.split(":")[0].toInt()
        val mmf= horaInicioValue.split(":")[1].toInt()
        val localDateTimef = LocalDateTime.of(localDate.year, localDate.month, localDate.dayOfMonth, hhf, mmf)
        val zonaf = ZoneId.systemDefault() // zona horaria del dispositivo, ej: -05:00
        val zonedDateTimef = localDateTimef.atZone(zonaf)
        val endDate=zonedDateTimef.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)




        val newEvent= NewEvent(
            title=tituloValue,
            date=finalDate,
            startHour=horaInicioValue,
            endHour=horaFinalizacionValue,
            location=lugarValue,
            IDCaso=idCasoValue,
            details=detallesValue,
            emails="",
            color=colorValue,
            start=finalDate,
            end=endDate
            );
        return newEvent;


    }


}
class ColorSpinnerAdapter(
    context: Context,
    private val colors: List<String>
) : ArrayAdapter<String>(context, 0, colors) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createColorView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createColorView(position, convertView, parent)
    }

    private fun createColorView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(R.layout.color_spinner, parent, false)
        val colorCircle = view.findViewById<View>(R.id.colorCircle)

        val drawable = colorCircle.background.mutate() as GradientDrawable
        drawable.setColor(Color.parseColor(colors[position]))

        return view
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
                    val color:String?=null,
                    val organizer: String? = null,
                    val createMeet: Boolean? = null,
                    val type: String? = null,
                    val start: String? = null,
                    val end: String?=null,):
    Parcelable;