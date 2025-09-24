package com.example.appvbg.ui.agenda.detalles

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.example.appvbg.R
import com.example.appvbg.api.CalendarApi
import com.example.appvbg.databinding.FragmentDetallesAgendaBinding
import org.json.JSONArray
import androidx.navigation.fragment.findNavController
import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.UUID


class DetallesAgenda : Fragment() {
    private val args: DetallesAgendaArgs by navArgs()
    private var _binding: FragmentDetallesAgendaBinding? = null
    private val binding get() = _binding!!
    private var editMode = false
    private var eventGoogleData= JSONObject();


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetallesAgendaBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val agendaJsonString = args.agendaJSON
        val agendaJson = JSONObject(agendaJsonString)
        eventGoogleData=agendaJson.getJSONObject("json")
        setFieldsFromJSON(agendaJson)
        
        
        
        binding.cancelButton.setOnClickListener {
            editMode = false
            setEditMode(editMode)
            binding.cancelButton.visibility = View.GONE
        }
        binding.editButton.setOnClickListener {
            if (editMode) {
                saveChangesToJSON()
            }
            editMode = !editMode
            binding.editButton.text = if (editMode) "Guardar" else "Editar"
            setEditMode(editMode)
        }

        binding.deleteButton.setOnClickListener {
            CalendarApi.deleteEvent(requireContext(), eventGoogleData.getString("id")) {resp->
                requireActivity().runOnUiThread {
                    if (resp == "error") {
                        Toast.makeText(requireContext(), "Error al eliminar el evento", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Evento eliminado", Toast.LENGTH_SHORT).show()
                        val action = DetallesAgendaDirections.actionDetallesAgendaToAgendaFragment()
                        findNavController().navigate(action)
                    }
                }
            }
        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null


    }

    private fun setEditMode(enabled: Boolean) {
        val isEnabledToHide= if (enabled) View.GONE else View.VISIBLE
        val isEnabledToShow= if (enabled) View.VISIBLE else View.GONE
        
        binding.tituloLabel.setVisibility(isEnabledToHide)
        binding.fechaLabel.setVisibility(isEnabledToHide)
        binding.horaInicioLabel.setVisibility(isEnabledToHide)
        binding.horaFinalizacionLabel.setVisibility(isEnabledToHide)
        binding.lugarLabel.setVisibility(isEnabledToHide)
        binding.IDCasoLabel.setVisibility(isEnabledToHide)
        binding.detallesLabel.setVisibility(isEnabledToHide)
        binding.emailsLabel.setVisibility(isEnabledToHide)
        binding.colorLabel.setVisibility(isEnabledToHide)


        binding.tituloEdit.setVisibility(isEnabledToShow)
        binding.fechaEdit.setVisibility(isEnabledToShow)
        binding.horaInicioEdit.setVisibility(isEnabledToShow)
        binding.horaFinalizacionEdit.setVisibility(isEnabledToShow)
        binding.lugarEdit.setVisibility(isEnabledToShow)
        binding.IDCasoEdit.setVisibility(isEnabledToShow)
        binding.detallesEdit.setVisibility(isEnabledToShow)
        binding.emailsEdit.setVisibility(isEnabledToShow)
        binding.colorEdit.setVisibility(isEnabledToShow)
    }

    private fun setFieldsFromJSON(json: JSONObject) {
        binding.tituloLabel.setText(json.optString("title"))
        binding.fechaLabel.setText(json.optString("date"))
        binding.horaInicioLabel.setText(json.optString("startHour"))
        binding.horaFinalizacionLabel.setText(json.optString("endHour"))
        binding.lugarLabel.setText(json.optString("location"))
        binding.IDCasoLabel.setText(json.optString("IDCaso"))
        binding.detallesLabel.setText(json.optString("details"))
        binding.emailsLabel.setText(json.optString("emails"))
        binding.colorLabel.setText(json.optString("color"))
        
        
        binding.tituloEdit.setText(json.optString("title"))
        binding.fechaEdit.setText(json.optString("date"))
        binding.horaInicioEdit.setText(json.optString("startHour"))
        binding.horaFinalizacionEdit.setText(json.optString("endHour"))
        binding.lugarEdit.setText(json.optString("location"))
        binding.IDCasoEdit.setText(json.optString("IDCaso"))
        binding.detallesEdit.setText(json.optString("details"))
        binding.emailsEdit.setText(json.optString("emails"))
        binding.colorEdit.setText(json.optString("color"))
    }


    private fun saveChangesToJSON() {
        val horaInicioValue = binding.horaInicioEdit.text.toString()
        val fechaValue = binding.fechaEdit.text.toString()
        val horaFinalizacionValue = binding.horaFinalizacionEdit.text.toString()

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



        val json = JSONObject();

        json.put(
            "description",
            "ID caso: ${binding.IDCasoEdit.text.toString()}\n" +
                    "Organizador: \n" +
                    "Tipo: \n" +
                    "description: ${binding.detallesEdit.text.toString()}\n"
        )

        // Attendees
        val attendeesArray = JSONArray()
        binding.emailsEdit.text.toString()
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .forEach { email ->
                attendeesArray.put(JSONObject().put("email", email))
            }
        json.put("attendees", attendeesArray)

        //val attendeesArray = binding.emailsEdit.text.toString()

        //val attendeesArray = JSONArray()
        /*
        newEvent.emails?.forEach { email ->
            val attendee = JSONObject().put("email", email)
            attendeesArray.put(attendee)
        }
        */
        json.put("attendees", attendeesArray)

        // Horarios
        val start = JSONObject()
            .put("dateTime", finalDate) // Si es un Instant, usar toString() (ISO-8601)
            .put("timeZone", "America/Bogota")

        val end = JSONObject()
            .put("dateTime", endDate)
            .put("timeZone", "America/Bogota")

        json.put("start", start)
        json.put("end", end)
        Log.d("hola", json.optString("color").toString())
        // Color
        val eventColors: Map<String, String> = mapOf(
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

        json.put("colorId", eventColors.entries.find { it.value == binding.colorEdit.text.toString() }?.key)
        /*
        // Condicional: crear Meet
        if (newEvent.createMeet == true) {
            val conferenceData = JSONObject()
            val createRequest = JSONObject()
                .put("requestId", UUID.randomUUID().toString()) // ID único
                .put("conferenceSolutionKey", JSONObject().put("type", "hangoutsMeet"))
            conferenceData.put("createRequest", createRequest)
            eventData.put("conferenceData", conferenceData)
        }*/


        CalendarApi.updateEvent(requireContext(), eventGoogleData.getString("id"), json) {


        }


    }

    



}