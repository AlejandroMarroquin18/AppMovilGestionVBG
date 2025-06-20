package com.example.appvbg.ui.agenda

import android.app.AlertDialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.compose.ui.text.TextStyle
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appvbg.R
import com.example.appvbg.databinding.FragmentAgendaBinding
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.*

class AgendaFragment : Fragment() {

    private var _binding: FragmentAgendaBinding? = null
    private val binding get() = _binding!!

    private var selectedDate: LocalDate? = null
    private val eventMap = mutableMapOf<LocalDate, String>() // eventos simples
    private val calendarDayMap = mutableMapOf<LocalDate, CalendarDay>()


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




        //creacion del calendario
        val gridTouchView = view.findViewById<GridTouchView>(R.id.gridTouchView)

        // Agrega un evento en la columna 3, entre las filas 20 y 28
        gridTouchView.addEvent(
            startRow = 20,
            endRow = 28,
            column = 3,
            title = "Consulta médica"
        )






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
