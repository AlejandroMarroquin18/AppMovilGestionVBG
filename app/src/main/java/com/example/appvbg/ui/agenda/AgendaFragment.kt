package com.example.appvbg.ui.agenda

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.appvbg.R
import com.example.appvbg.databinding.FragmentAgendaBinding
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
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


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAgendaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val calendarView = binding.calendarView
        val currentMonth = YearMonth.now()
        val firstDayOfWeek = java.time.DayOfWeek.MONDAY

        calendarView.setup(currentMonth.minusMonths(1), currentMonth.plusMonths(1), firstDayOfWeek)
        calendarView.scrollToMonth(currentMonth)

        class DayViewContainer(view: View) : ViewContainer(view) {
            val textView: TextView = view.findViewById(R.id.calendarDayText)
            lateinit var day: CalendarDay

            init {
                view.setOnClickListener {
                    if (day.owner == DayOwner.THIS_MONTH) {
                        selectedDate = day.date
                        binding.calendarView.notifyDayChanged(day)
                        handleDayClick(day.date)
                    }
                }
            }
        }

        calendarView.dayBinder = object : DayBinder<DayViewContainer> {
            override fun create(view: View): DayViewContainer = DayViewContainer(view)

            override fun bind(container: DayViewContainer, day: CalendarDay) {
                container.day = day
                container.textView.text = day.date.dayOfMonth.toString()

                calendarDayMap[day.date] = day

                container.textView.setBackgroundColor(
                    if (day.date == selectedDate) 0xFFE0E0E0.toInt() else 0x00000000
                )
                container.textView.setTextColor(
                    if (eventMap.containsKey(day.date)) 0xFF388E3C.toInt() else 0xFF000000.toInt()
                )
            }
        }

        binding.btnAddEvent.setOnClickListener {
            selectedDate?.let { date ->
                showAddEventDialog(date)
            } ?: Toast.makeText(requireContext(), "Selecciona una fecha", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleDayClick(date: LocalDate) {
        if (eventMap.containsKey(date)) {
            showEditDeleteDialog(date)
        } else {
            Toast.makeText(requireContext(), "Sin evento en esta fecha", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showAddEventDialog(date: LocalDate) {
        val input = EditText(requireContext())
        AlertDialog.Builder(requireContext())
            .setTitle("Agregar evento")
            .setView(input)
            .setPositiveButton("Guardar") { _, _ ->
                eventMap[date] = input.text.toString()
                //binding.calendarView.notifyDayChanged(CalendarDay(date, DayOwner.THIS_MONTH))
                calendarDayMap[date]?.let { day ->
                    binding.calendarView.notifyDayChanged(day)
                }

            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showEditDeleteDialog(date: LocalDate) {
        val input = EditText(requireContext())
        input.setText(eventMap[date])

        AlertDialog.Builder(requireContext())
            .setTitle("Editar o eliminar evento")
            .setView(input)
            .setPositiveButton("Guardar") { _, _ ->
                eventMap[date] = input.text.toString()
                //binding.calendarView.notifyDayChanged(CalendarDay(date, DayOwner.THIS_MONTH))
                calendarDayMap[date]?.let { day ->
                    binding.calendarView.notifyDayChanged(day)
                }
            }
            .setNegativeButton("Eliminar") { _, _ ->
                eventMap.remove(date)
                //binding.calendarView.notifyDayChanged(CalendarDay(date, DayOwner.THIS_MONTH))
                calendarDayMap[date]?.let { day ->
                    binding.calendarView.notifyDayChanged(day)
                }
            }
            .setNeutralButton("Cancelar", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
