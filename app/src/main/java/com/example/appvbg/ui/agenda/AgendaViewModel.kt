import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.appvbg.ui.agenda.crear_cita.NewEvent
import com.example.appvbg.ui.agenda.estadisticas.DateRange
import java.time.LocalDate
import com.example.appvbg.ui.agenda.estadisticas.Event
import com.example.appvbg.ui.agenda.estadisticas.obtenerRangoSemanaActual
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class AgendaViewModel : ViewModel() {

    private val _events = MutableLiveData<MutableList<Event>>(mutableListOf())
    val events: LiveData<MutableList<Event>> get() = _events

    private val _currentWeek = MutableLiveData<DateRange>()
    val currentWeek: LiveData<DateRange> get() = _currentWeek

    init {
        val today = LocalDate.now()
        _currentWeek.value = obtenerRangoSemanaActual(today)
    }

    fun setEvents(newEvents: MutableList<Event>) {
        _events.value = newEvents.toMutableList()
    }

    fun addEvent(event: Event) {
        /**_events.value = _events.value?.apply {
            add(event)
        }
        */
        val currentList = _events.value ?: mutableListOf()
        val updatedList = currentList.toMutableList() //  importante: NUEVA instancia
        updatedList.add(event)
        _events.value = updatedList
    }



    fun updateWeek(newRange: DateRange) {
        _currentWeek.value = newRange
    }

    fun removeEvent(event: Event) {
        _events.value = _events.value?.apply {
            remove(event)
        }
    }

    fun getEventsForRange(range: DateRange): List<Event> {
        return _events.value?.filter {
            !it.date.isBefore(range.start) && !it.date.isAfter(range.end)
        } ?: emptyList()
    }

    fun getAllEvents(): MutableList<Event> {
        return events.value ?: mutableListOf()
    }

    fun fetchEvents(){
        //calculos q se pueden hacer solo una vez
        val zona = ZoneId.systemDefault() // zona horaria del dispositivo, ej: -05:00

        /**

        //calculos por separado
        val localDate = LocalDate.parse(fechaValue) // parsea "YYYY-MM-DD"

        val hh= horaInicioValue.split(":")[0].toInt()
        val mm= horaInicioValue.split(":")[1].toInt()
        val localDateTime = LocalDateTime.of(localDate.year, localDate.month, localDate.dayOfMonth, hh, mm)
        val zonedDateTime = localDateTime.atZone(zona)


        val finalDate=zonedDateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

        */




    }
}
