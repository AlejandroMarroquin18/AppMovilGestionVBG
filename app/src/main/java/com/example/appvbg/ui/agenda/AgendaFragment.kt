package com.example.appvbg.ui.agenda

import AgendaViewModel
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.navigation.fragment.findNavController
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.appvbg.R
import com.example.appvbg.databinding.FragmentAgendaBinding
import com.example.appvbg.ui.agenda.crear_cita.NewEvent
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.example.appvbg.api.CalendarApi
import com.example.appvbg.ui.agenda.estadisticas.CustomGRID
import com.example.appvbg.ui.agenda.estadisticas.Event
import com.example.appvbg.ui.agenda.estadisticas.obtenerRangoSemanaActual
import com.google.android.gms.auth.api.identity.AuthorizationClient
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.kizitonwose.calendarview.model.CalendarDay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.YearMonth
import com.example.appvbg.api.updateGoogleAcces
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID


class AgendaFragment : Fragment() {

    private var _binding: FragmentAgendaBinding? = null
    private val binding get() = _binding!!

    private var selectedDate: LocalDate? = null
    private val eventMap = mutableMapOf<LocalDate, String>() // eventos simples
    private val calendarDayMap = mutableMapOf<LocalDate, CalendarDay>()
    private val agendaViewModel: AgendaViewModel by viewModels()
    private lateinit var startAuthorizationIntent: ActivityResultLauncher<IntentSenderRequest>

    override fun onCreate(savedInstanceState: Bundle?) {
        startAuthorizationIntent =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { activityResult ->
                try {
                    val authorizationResult = Identity.getAuthorizationClient(requireContext())
                        .getAuthorizationResultFromIntent(activityResult.data)
                    // short-lived access token
                    val accessToken = authorizationResult.accessToken
                    // store the authorization code used for getting a refresh token safely to your app's backend server
                    val authCode: String? = authorizationResult.serverAuthCode
                    sendAuthCodeToBackend(accessToken,authCode.toString())

                } catch (e: ApiException) {
                    // log exception
                    Log.e("App", "Error al obtener el resultado de la autorización", e)
                }
            }

        //askForGoogleScopes()
        requestCalendarAuthorization()
        agendaViewModel.fetchEvents(requireContext())
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAgendaBinding.inflate(inflater, container, false)
        val spinnerOptions= arrayOf("Semanal","Mensual")
        binding.selectView.adapter= ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, spinnerOptions)
        binding.selectView.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (spinnerOptions[position]) {
                    "Mensual" -> mostrarVistaMensual()
                    "Semanal" -> mostrarVistaSemanal()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val currentMonth = YearMonth.now()

        val firstDayOfWeek = java.time.DayOfWeek.MONDAY

        val startMonth = currentMonth.minusMonths(100) // Adjust as needed

        val endMonth = currentMonth.plusMonths(100) // Adjust as needed



        val daysOfWeek = arrayOf("Lunes", "Martes", "Miercoles" , "Jueves", "Viernes", "Sábado", "Domingo")
        val titlesContainer = view.findViewById<ViewGroup>(R.id.titlesContainer)

        titlesContainer.children
            .map { it as TextView }
            .forEachIndexed { index, textView ->
                val title = daysOfWeek[index]
                //val title = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
                textView.text = title
            }



        binding.nextButton.setOnClickListener{}
        binding.previousButton.setOnClickListener{}






        val customGRID = view.findViewById<CustomGRID>(R.id.calendarGrid)

        customGRID.setEventClickListener { json ->

            // Handle details button click, e.g., navigate to details fragment
            // You'll need to create a details fragment and implement navigation
            // Dentro de AgendaFragment, cuando hagas clic en un ítem o algo que navegue a DetallesAgendaFragment
            val action = AgendaFragmentDirections.actionAgendaFragmentToDetallesAgenda(json.toString())
            findNavController().navigate(action)


        }


        customGRID.addEvent(
            "10:00",
            "14:20",
            date = "2025-07-18T15:30:00-05:00",//LocalDate.of(2025, 7, 2),
            "Hola como estas? Yo bien y tu? Esta es una reunion de prueba"
        )
        customGRID.setEventsFromViewModel(agendaViewModel.getAllEvents())

        agendaViewModel.events.observe(viewLifecycleOwner) { allEvents ->
            //val currentRange = agendaViewModel.currentWeek.value ?: obtenerRangoSemanaActual(LocalDate.now())
            val newEventList = agendaViewModel.getAllEvents()
            customGRID.setEventsFromViewModel(allEvents)
            //customGRID.resetLayout()
        }
        customGRID.resetLayout()
    }

    fun recibirNuevoEvento(evento: NewEvent) {

        val newEvent=buildEventData(evento)
        CalendarApi.createEvent(requireContext(),newEvent){eventoDeBackend->
            try{
                val nuevoEvento = Event(
                    evento.title,
                    OffsetDateTime.parse(evento.date).toLocalDate(),//fecha
                    evento.startHour,
                    evento.endHour,
                    evento.location,
                    evento.IDCaso,
                    evento.details,
                    evento.emails,
                    evento.color
                )

                agendaViewModel.addEvent(nuevoEvento,requireContext())

                //Toast.makeText(requireContext(), evento.date, Toast.LENGTH_SHORT).show()

            }catch (e: Exception){
                Log.e("CalendarApi", "Error en createEvent. Respuesta: $eventoDeBackend", e)
                Log.e("CalendarApi", e.toString())
            }

            //Toast.makeText(requireContext(), evento, Toast.LENGTH_SHORT).show()

        }


        val grid = view?.findViewById<CustomGRID>(R.id.calendarGrid) ?: return
        //grid.resetLayout()
    }




    private fun mostrarVistaMensual() {

    }
    private fun mostrarVistaSemanal() {

    }





    private fun showAddEventDialog(date: LocalDate) {

    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }





    private fun requestCalendarAuthorization() {
        val requestedScopes = listOf(
            Scope("https://www.googleapis.com/auth/calendar"),
            Scope("https://www.googleapis.com/auth/calendar.events")
        )

        val authorizationRequest = AuthorizationRequest.builder()
            .setRequestedScopes(requestedScopes)
            .requestOfflineAccess(getString(R.string.server_client_id)) // necesario si quieres refresh_token en backend
            .build()

        Identity.getAuthorizationClient(requireActivity())
            .authorize(authorizationRequest)
            .addOnSuccessListener { authorizationResult ->
                if (authorizationResult.hasResolution()) {
                    // Mostrar el diálogo de consentimiento de Google
                    val pendingIntent = authorizationResult.pendingIntent
                    startAuthorizationIntent.launch(
                        IntentSenderRequest.Builder(pendingIntent!!.intentSender).build()
                    )
                } else {
                    // Ya tenía permisos previamente → tokens listos
                    val accessToken = authorizationResult.accessToken
                    val serverAuthCode = authorizationResult.serverAuthCode
                    
                    Log.d("CalendarAuth", "Token directo: $accessToken")
                    sendAuthCodeToBackend(accessToken,serverAuthCode.toString())
                }
            }
            .addOnFailureListener { e ->
                Log.e("CalendarAuth", "Error solicitando scopes", e)
            }
    }

    // Define el ActivityResultLauncher como una propiedad de la clase

    private fun sendAuthCodeToBackend(accessToken: String?,authCode: String) {
        Log.d("CalendarAuth", "Token directo: $accessToken")
        Log.d("CalendarAuth", "Token directo: $authCode")
        lifecycleScope.launch {
            val result = updateGoogleAcces(
                accessToken = accessToken, // o tu access_token
                serverAuthCode = authCode,
                context = requireContext() // tu DRF token real
            )

            if (result != null) {
                println("Respuesta del backend: $result")
            } else {
                println("Error en la llamada al backend")
            }
        }



    }


    fun buildEventData(newEvent: NewEvent): JSONObject {
        val eventData = JSONObject()

        // Campos básicos
        eventData.put("summary", newEvent.title)
        eventData.put("location", newEvent.location)
        eventData.put(
            "description",
            "ID caso: ${newEvent.IDCaso}\n" +
                    "Organizador: ${newEvent.organizer}\n" +
                    "Tipo: ${newEvent.type}\n" +
                    "description: ${newEvent.details}\n"
        )

        // Attendees
        val attendeesArray = newEvent.emails
        //val attendeesArray = JSONArray()
        /*
        newEvent.emails?.forEach { email ->
            val attendee = JSONObject().put("email", email)
            attendeesArray.put(attendee)
        }
        */
        eventData.put("attendees", attendeesArray)

        // Horarios
        val start = JSONObject()
            .put("dateTime", newEvent.start.toString()) // Si es un Instant, usar toString() (ISO-8601)
            .put("timeZone", "America/Bogota")

        val end = JSONObject()
            .put("dateTime", newEvent.end.toString())
            .put("timeZone", "America/Bogota")

        eventData.put("start", start)
        eventData.put("end", end)
        Log.d("hola", newEvent.color.toString())
        // Color
        eventData.put("colorId", newEvent.color)

        // Condicional: crear Meet
        if (newEvent.createMeet == true) {
            val conferenceData = JSONObject()
            val createRequest = JSONObject()
                .put("requestId", UUID.randomUUID().toString()) // ID único
                .put("conferenceSolutionKey", JSONObject().put("type", "hangoutsMeet"))
            conferenceData.put("createRequest", createRequest)
            eventData.put("conferenceData", conferenceData)
        }

        return eventData
    }






}
