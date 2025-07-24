package com.example.appvbg.ui.agenda

import AgendaViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.appvbg.R
import com.example.appvbg.databinding.FragmentAgendaBinding
import com.example.appvbg.ui.agenda.crear_cita.NewEvent

import com.example.appvbg.ui.agenda.estadisticas.CustomGRID
import com.example.appvbg.ui.agenda.estadisticas.Event
import com.example.appvbg.ui.agenda.estadisticas.obtenerRangoSemanaActual
import com.kizitonwose.calendarview.model.CalendarDay
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.YearMonth


class AgendaFragment : Fragment() {

    private var _binding: FragmentAgendaBinding? = null
    private val binding get() = _binding!!

    private var selectedDate: LocalDate? = null
    private val eventMap = mutableMapOf<LocalDate, String>() // eventos simples
    private val calendarDayMap = mutableMapOf<LocalDate, CalendarDay>()
    private val agendaViewModel: AgendaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
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


        binding.btnAddEvent.setOnClickListener {
            selectedDate?.let { date ->
                showAddEventDialog(date)
            } ?: Toast.makeText(requireContext(), "Selecciona una fecha", Toast.LENGTH_SHORT).show()
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

        agendaViewModel.addEvent(nuevoEvento)

        //Toast.makeText(requireContext(), evento.date, Toast.LENGTH_SHORT).show()
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




}
