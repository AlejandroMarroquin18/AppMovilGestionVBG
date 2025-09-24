import android.content.Context
import android.util.Log

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.appvbg.api.CalendarApi
import com.example.appvbg.api.PrefsHelper
import com.example.appvbg.ui.agenda.crear_cita.NewEvent
import com.example.appvbg.ui.agenda.estadisticas.DateRange
import java.time.LocalDate
import com.example.appvbg.ui.agenda.estadisticas.Event
import com.example.appvbg.ui.agenda.estadisticas.obtenerRangoSemanaActual
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.text.get


class AgendaViewModel : ViewModel() {
    private var _rawEvents: MutableList<JSONObject> = mutableListOf()
    private val _events = MutableLiveData<MutableList<Event>>(mutableListOf())
    val events: LiveData<MutableList<Event>> get() = _events

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

    private val _currentWeek = MutableLiveData<DateRange>()
    val currentWeek: LiveData<DateRange> get() = _currentWeek

    init {
        val today = LocalDate.now()
        _currentWeek.value = obtenerRangoSemanaActual(today)
    }

    fun setEvents(newEvents: MutableList<Event>) {
        _events.value = newEvents.toMutableList()
    }

    private fun getCurrentEvents(): MutableList<Event> =
        _events.value?.toMutableList() ?: mutableListOf()

    fun addEvent(event: Event, context: Context) {
        CalendarApi.createEvent(context, event.json) { it ->
            try {
                val newEvent = transformJSONtoEvent(JSONObject(it))
                val updatedList = getCurrentEvents().apply { add(newEvent) }
                _events.postValue(updatedList)
            } catch (e: Exception) {
                Log.e("CalendarApi", "Error en createEvent. Respuesta: $it", e)
            }
        }
    }

    fun removeEvent(context: Context, event: Event) {
        CalendarApi.deleteEvent(context, event.id) { it ->
            try {

                // Opcional: validar respuesta del backend
                val updatedList = getCurrentEvents()
                    .filter { ev -> ev.id != event.id }
                    .toMutableList()
                _events.postValue(updatedList)
            } catch (e: Exception) {
                Log.e("CalendarApi", "Error en deleteEvent. Respuesta: $it", e)
            }
        }
    }

    fun updateEvent(context: Context, updatedEvent: Event) {
        CalendarApi.fetchEventById(context, updatedEvent.id) { it ->
            try {
                val eventEquivalent = JSONObject(it)
                val newEvent = mergeJSONS(eventEquivalent, updatedEvent.json)

                CalendarApi.updateEvent(context, updatedEvent.id, newEvent) { result ->
                    try {
                        val currentList = getCurrentEvents()
                        val index = currentList.indexOfFirst { ev -> ev.id == updatedEvent.id }
                        if (index != -1) {
                            val updatedList = currentList.toMutableList()
                            updatedList[index] = transformJSONtoEvent(JSONObject(result))
                            _events.postValue(updatedList)
                        } else {
                            Log.w("CalendarApi", "updateEvent: No se encontró el evento con id=${updatedEvent.id}")
                        }
                    } catch (e: Exception) {
                        Log.e("CalendarApi", "Error procesando respuesta de updateEvent. Respuesta: $result", e)
                    }
                }
            } catch (e: Exception) {
                Log.e("CalendarApi", "Error en fetchEventById. Respuesta: $it", e)
            }
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

    fun fetchEvents(context: Context){
        //calculos q se pueden hacer solo una vez

        val year = currentWeek.value?.start?.year ?: LocalDate.now().year

        CalendarApi.fetchEvents(context, year) {it->
            val result = mutableListOf<JSONObject>()
            for (i in 0 until it.length()) {
                val obj = it.getJSONObject(i)
                result.add(obj)
            }

            _rawEvents=result

            val mappedEvents = _rawEvents.map { json ->

                //se convierten a json

                transformJSONtoEvent(json,eventColors[json.optString("color", "9")])

            }.toMutableList()

            _events.postValue(mappedEvents)



        }

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




    fun mergeJSONS(json1: JSONObject?, json2: JSONObject?): JSONObject {
        val result = JSONObject(json1.toString()) // copia del base
        json2!!.keys().forEach { key ->
            result.put(key, json2.get(key)) // sobrescribe si existe, agrega si no
        }
        return result
    }


    fun replaceEventById(
        newEvent: JSONObject
    ) {

        val id=newEvent.optString("id", "" )
        _rawEvents = _rawEvents.map { ev ->
            if (ev.getString("id") == id) newEvent else ev
        }.toMutableList()

        val formattedEvent=transformJSONtoEvent(newEvent)
        _events.value = _events.value?.apply {
            removeIf { it.id == id }
            add(formattedEvent)
        }

    }

    private fun transformJSONtoEvent(json: JSONObject, color: String? = "#5484ed"): Event {
        /**
         * formateo de horas
         */
        Log.d("CalendarApi", "transformJSONtoEvent: $json")

        if (json.optString("eventType", "") == "birthday") {
            return Event(
                id = json.optString("id", ""),
                title = "",   // vacío
                date = LocalDate.parse(json.optJSONObject("start")?.optString("date") ?: LocalDate.now().toString()),
                startHour = "",
                endHour = "",
                location = "",
                IDCaso = "",
                details = "",
                emails = "",
                link = "",
                color = color,
                json = json
            )
        }

        val start = json.optJSONObject("start") ?: JSONObject()
        val end = json.optJSONObject("end") ?: JSONObject()

        //se saca el string de la hora con fechaa
        val startHourString = start.optString("dateTime", "")


        val endHourString = end.optString("dateTime", "")
        //se especifica que sacar
        val formatter = DateTimeFormatter.ofPattern("HH:mm") // 24h
        ///se formatea creando un ZonedDateTime y luego se aplica el formater de la linea anterior
        //////Hora de inicio
        val startzonedDateTime = ZonedDateTime.parse(startHourString)
        val formattedStartHour = startzonedDateTime.format(formatter)
        //////Hora fin
        val endzonedDateTime = ZonedDateTime.parse(endHourString)
        val formattedEndHour = endzonedDateTime.format(formatter)

        //fecha
        val date = startzonedDateTime.toLocalDate()

        val formattedEvent = Event(
            id = json.optString("id", ""),
            title = json.optString("summary", ""),
            date = date,
            startHour = formattedStartHour,
            endHour = formattedEndHour,
            location = json.optString("location", "Sin ubicación"),
            IDCaso = json.optString("description", ""),
            details = json.optString("description", ""),
            emails = json.optString("attendees", ""),
            link = json.optString("hangoutLink", ""),
            color = color,
            json = json
        )

        return formattedEvent
    }
}
