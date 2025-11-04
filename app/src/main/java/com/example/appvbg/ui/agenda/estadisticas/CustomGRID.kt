package com.example.appvbg.ui.agenda.estadisticas

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.TextView
import com.example.appvbg.R
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.util.Date
import android.animation.ValueAnimator
import android.graphics.RectF
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.appvbg.ui.quejas.QuejasFragmentDirections
import org.json.JSONObject
import java.time.OffsetDateTime


import java.util.*
import kotlin.math.abs

class CustomGRID (context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs), GestureDetector.OnGestureListener {

    private val gestureDetector = GestureDetector(context, this)
    private val eventRects = mutableListOf<Pair<RectF, Event>>()  // Asocia cada rectángulo a su evento
    private var directions = null;
    private var eventClickListener: ((JSONObject) -> Unit)? = null


    var isCreatingEvent = false


    private val dateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())


    private var animatedOffsetX = 0f
    private var animating = false
    private val animationDuration = 300L // milisegundos
    private val topHeaderHeight = 90f // Ajusta según tu diseño


    var currentWeek: DateRange // Corrected declaration
    var nextWeek: DateRange    // Corrected declaration (assuming DateRange type)
    var previousWeek: DateRange // Corrected declaration (assuming DateRange type)

    //var events = mutableListOf<Event>()
    var events: MutableList<Event> = mutableListOf()
    var filteredevents: MutableList<Event> = mutableListOf()


    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = Color.BLACK // Fondo negro
    }

    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 3f
        color = Color.BLUE // Borde azul
    }

    private val textPaint = Paint().apply {
        color = Color.WHITE
        textSize = 12f // Ajusta el tamaño según necesites
        isAntiAlias = true
        textAlign = Paint.Align.LEFT // O CENTER, según lo que prefieras
    }

    private val cornerRadius = 15f


    init {
        currentWeek = obtenerRangoSemanaActual(LocalDate.now())

        nextWeek = obtenerRangoSemanaSiguiente(LocalDate.now())
        previousWeek = obtenerRangoSemanaAnterior(LocalDate.now())

        filterEvents(previousWeek, nextWeek)
        setBackgroundColor(Color.WHITE)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        setMeasuredDimension(width, height)


    }

    /**
     * Esta funcion ubica las vistas, en nuestro caso las tarjetas customizadas,
     * dentro de la malla cuadriculada o grid o layout, como quieras llamarlo,
     * con el bucle for llama a cada hijo view creado con el addEvent() usando view.addView(child)
     *
     */
    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        /**
        for (i in 0 until childCount) {
        val child = getChildAt(i)
        val event = child.tag as? Event ?: continue

        val width = r - l
        val height = b - t
        val dayWidth = width / 7
        val hourHeight = height / 24

        val dayIndex = event.date.dayOfWeek.value - 1
        val left = (dayIndex * dayWidth - animatedOffsetX).toInt()
        val top = (event.startHour) * hourHeight
        val right = left + dayWidth
        val bottom = (event.endHour) * hourHeight

        child.layout(left, top, right, bottom)
        }*/
    }

    /**
     * Esto dibuja las lineas y all el grid
     *
     */

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawColor(Color.WHITE)

        val viewWidth = width
        val viewHeight = height
        val boxWidth = viewWidth / 7f
        val hourHeight = (viewHeight - topHeaderHeight) / 24f

        val zoneId = java.time.ZoneId.systemDefault()

        paint.strokeWidth = 2f

        //  Dibuja los 7 días de la semana actual
        for (i in 0 until 7) {
            val x = i * boxWidth - animatedOffsetX

            val localDate = currentWeek.start.plusDays(i.toLong())
            val date = Date.from(localDate.atStartOfDay(zoneId).toInstant())
            val label = dateFormat.format(date)

            // Línea vertical
            paint.color = Color.LTGRAY
            canvas.drawLine(x, 0f, x, viewHeight.toFloat(), paint)

            // Texto de la fecha
            paint.color = Color.BLACK
            paint.textSize = 26f
            // Texto de la fecha
            canvas.drawText(label, x + 10f, topHeaderHeight / 2f, paint)

        }

        // Línea vertical al final de la semana
        paint.color = Color.LTGRAY

        canvas.drawLine(
            7 * boxWidth - animatedOffsetX,
            0f,
            7 * boxWidth - animatedOffsetX,
            viewHeight.toFloat(),
            paint
        )


        //  Dibuja las líneas horizontales de horas
        for (h in 0..24) {
            val y = topHeaderHeight + h * hourHeight

            // Línea de hora
            paint.color = Color.LTGRAY
            canvas.drawLine(0f, y, viewWidth.toFloat(), y, paint)

            // Texto "hh:00"
            paint.color = Color.DKGRAY
            paint.textSize = 22f
            canvas.drawText("${h}:00", 10f, y - 5f, paint)
        }
        //dibuja los eventos
        eventRects.clear()
        for (event in filteredevents) {

            val partesStartHour = event.startHour.split(":")
            val horasStartHour = partesStartHour[0].toInt()
            val minutosStartHour = ((partesStartHour[1].toInt()) / 60)
            val startY = topHeaderHeight + (horasStartHour + minutosStartHour) * hourHeight

            val partesEndHour = event.endHour.split(":")
            val horasEndHour = partesEndHour[0].toInt()
            val minutosEndHour = ((partesEndHour[1].toInt()) / 60)
            val endY = topHeaderHeight + (horasEndHour + minutosEndHour) * hourHeight

            val dayIndex = event.date.dayOfWeek.value - 1


            val left = dayIndex * boxWidth - animatedOffsetX
            val right = (dayIndex + 1) * boxWidth - animatedOffsetX
            val rect = RectF(left, startY, right, endY)
            eventRects.add(rect to event)  // Guardamos el área clickeable y el evento asociado

            //val rect = RectF((dayIndex)-animatedOffsetX, startHourHeigth, (dayIndex+1)-animatedOffsetX, endHourHeigth)

            // Dibujar fondo negro con esquinas redondeadas
            // Si event.color es un hex (#FF5733), lo parseamos:
            event.color?.let { colorHex ->
                try {
                    paint.color = Color.parseColor(colorHex)
                } catch (e: Exception) {
                    paint.color = Color.BLACK // Default si hay error
                }
            } ?: run {
                paint.color = Color.BLACK // Default si es null
            }

            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
            // Dibujar borde azul con las mismas esquinas redondeadas
            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, borderPaint)


            val text = event.title

            // Calcular coordenadas del texto
            val textX = rect.left + 10  // margen izquierdo
            val textY = rect.top + textPaint.textSize + 10  // margen superior + altura de texto

            canvas.drawText(text, textX, textY, textPaint)


        }


    }


    /**
     * detecta los toques tap tap
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    //ni idea
    override fun onDown(e: MotionEvent): Boolean = true

    /**
     * detectaa el scroll y lo redibuja mediante animateWeekChange()
     *
     */
    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        // Ignorar scroll si ya hay una animación en curso
        if (animating) return true

        val dx = e2.x - (e1?.x ?: e2.x)

        if (dx > 100) {
            animateWeekChange(-1) // Semana anterior
        } else if (dx < -100) {
            animateWeekChange(1) // Semana siguiente
        }

        return true
    }


    // Otros métodos del GestureDetector (no usados pero necesarios)
    override fun onShowPress(e: MotionEvent) {}
    override fun onSingleTapUp(e: MotionEvent): Boolean {
        val x = e.x
        val y = e.y

        for ((rect, event) in eventRects) {
            if (rect.contains(x, y)) {
                Toast.makeText(context, "Evento: ${event.title}", Toast.LENGTH_SHORT).show()

                // Aquí puedes abrir un diálogo, lanzar una actividad, etc.
                // Por ejemplo:
                val json = JSONObject().apply {
                    put("title", event.title)
                    put("date", event.date.toString())
                    put("startHour", event.startHour)
                    put("endHour", event.endHour)
                    put("location", event.location)
                    put("IDCaso", event.IDCaso)
                    put("details", event.details)
                    put("emails", event.emails)
                    put("color", event.color)
                    put("id", event.id)
                    put("link", event.link)
                    put("json", event.json)


                }


                // Handle details button click, e.g., navigate to details fragment
                // You'll need to create a details fragment and implement navigation
                eventClickListener?.invoke(json)




                return true
            }
        }

        return false
    }

    override fun onLongPress(e: MotionEvent) {}
    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent,
        velocityX: Float,
        velocityY: Float
    ): Boolean = false


    private fun animateWeekChange(direction: Int) {
        if (animating) return

        val viewWidth = width
        val targetOffset = direction * viewWidth.toFloat() // desplazamiento de una semana entera

        val animator = ValueAnimator.ofFloat(0f, targetOffset)
        animator.duration = animationDuration
        animator.addUpdateListener { animation ->
            animatedOffsetX = animation.animatedValue as Float
            invalidate()
        }



        animator.addListener(object : android.animation.Animator.AnimatorListener {
            override fun onAnimationStart(animation: android.animation.Animator) {
                animating = true
            }

            override fun onAnimationEnd(animation: android.animation.Animator) {

                animatedOffsetX = 0f

                // Cambiar semana real
                if (direction == 1) {
                    nextWeek()
                    //renderEvents()
                } else if (direction == -1) {
                    previousWeek()
                    //renderEvents()
                }

                invalidate()
                animating = false
            }

            override fun onAnimationCancel(animation: android.animation.Animator) {
                animating = false
            }

            override fun onAnimationRepeat(animation: android.animation.Animator) {}
        })


        animator.addUpdateListener { animation ->
            animatedOffsetX = animation.animatedValue as Float
            invalidate()
            requestLayout()  //  Necesario para mover las vistas también
        }

        animator.start()
    }



    private fun nextWeek() {
        currentWeek = nextWeek
        nextWeek = obtenerRangoSemanaSiguiente(currentWeek.start)
        previousWeek = obtenerRangoSemanaAnterior(currentWeek.start)

        filterEvents(currentWeek, currentWeek)

    }

    private fun previousWeek() {
        currentWeek = previousWeek
        nextWeek = obtenerRangoSemanaSiguiente(currentWeek.start)
        previousWeek = obtenerRangoSemanaAnterior(currentWeek.start)
        filterEvents(currentWeek, currentWeek)
    }


    fun filterEvents(previousDateRange: DateRange, nextDateRange: DateRange) {

        filteredevents.clear()
        filteredevents.addAll(
            events.filter {
                !it.date.isBefore(previousDateRange.start) &&
                        !it.date.isAfter(nextDateRange.end)
            }
        )


    }

    /**
     * Crea un evento y lo agrega a la lista de eventos para que sse renderice
     * en el calendario
     * startHour: "HH:mm"
     * endHour: "HH:mm"
     * date: "YYYY-MM-DDTHH:mm:00-HH:mm" LocalDate en String
     */
    fun addEvent(startHour: String, endHour: String, date: String, title: String) {
        val formattedDate=OffsetDateTime.parse(date).toLocalDate()
        val newEvent = Event( title, formattedDate, startHour, endHour)
        events.add(newEvent)
        filterEvents(currentWeek, currentWeek)
        //renderEvents()

    }


    fun resetLayout() {
        //removeAllViews()
        requestLayout()
        invalidate()
    }
    fun setEventClickListener(listener: (json: JSONObject) -> Unit) {

        eventClickListener = listener
    }

    fun setEventsFromViewModel(events: MutableList<Event>) {
        this.events.clear()
        this.events.addAll(events)
        filterEvents(currentWeek, currentWeek)

        requestLayout()
        invalidate()
    }





}

fun obtenerRangoSemanaActual(localDate: LocalDate): DateRange{
    //val hoy = LocalDate.now()

    val lunes = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val domingo = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

    return DateRange(lunes, domingo)
}
fun obtenerRangoSemanaSiguiente(localDate: LocalDate): DateRange {
    //val hoy = LocalDate.now()

    val lunesSiguiente = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).plusWeeks(1)
    val domingoSiguiente = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).plusWeeks(1)

    return DateRange(lunesSiguiente, domingoSiguiente)
}

fun obtenerRangoSemanaAnterior(localDate: LocalDate): DateRange {
    //val hoy = LocalDate.now()

    val lunesAnterior = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).minusWeeks(1)
    val domingoAnterior = localDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).minusWeeks(1)

    return DateRange(lunesAnterior, domingoAnterior)
}

data class DateRange(val start: LocalDate, val end: LocalDate)

//data class Event(val id: Int, val title: String, val date: LocalDate, val startHour: Int, val endHour: Int)
data class Event( val title: String,
                  val date: LocalDate,
                  val startHour: String,
                  val endHour: String,
                  val location:String?=null,
                  val IDCaso:String?=null,
                  val details: String?=null,
                  val emails:String?=null,
                  val color:String?=null,
                  val id:String?=null,
                  val link:String?=null,
                  val json:JSONObject? = null)