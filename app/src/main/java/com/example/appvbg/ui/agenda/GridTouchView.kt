package com.example.appvbg.ui.agenda

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.OverScroller
import androidx.core.view.ViewCompat
import com.example.appvbg.R
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

import android.widget.TextView

class GridTouchView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private val numRows = 24 * 4
    private val numColsPerWeek = 7

    private var currentWeekOffset = 0

    private var cellWidth = 0f
    private var cellHeight = 0f
    private var weekWidth = 0

    private val scroller = OverScroller(context)
    private val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {

        private var scrollStartX = 0f

        override fun onDown(e: MotionEvent): Boolean {
            scrollStartX = e.x
            return true
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            val dx = (e2?.x ?: 0f) - scrollStartX
            if (abs(dx) > cellWidth * 2) {
                if (dx < 0) {
                    currentWeekOffset += 1
                } else {
                    currentWeekOffset -= 1
                }

                scrollToSemana(currentWeekOffset)
                scrollStartX = e2?.x ?: 0f // reinicia para evitar múltiples saltos
            }
            return true
        }

    })



    private val paint = Paint().apply {
        color = Color.LTGRAY
        strokeWidth = 1f
    }

    private val rectPaint = Paint().apply {
        color = Color.TRANSPARENT
        style = Paint.Style.FILL
    }

    private val borderPaint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }

    private var isDragging = false
    private var isCreatingEvent = false
    private var createEventRectangleHeight = 0

    private var startRow = -1
    private var startCol = -1
    private var endRow = -1
    private var endCol = -1

    private val events = mutableListOf<EventCardView>()

    init {
        isHorizontalScrollBarEnabled = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        weekWidth = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)

        cellWidth = weekWidth / numColsPerWeek.toFloat()
        cellHeight = height / numRows.toFloat()

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val eventData = child.tag as? EventCardView
            if (eventData != null) {
                val widthSpec = MeasureSpec.makeMeasureSpec(cellWidth.toInt(), MeasureSpec.EXACTLY)
                val heightSpec = MeasureSpec.makeMeasureSpec(
                    ((eventData.endRow - eventData.startRow + 1) * cellHeight).toInt(),
                    MeasureSpec.EXACTLY
                )
                child.measure(widthSpec, heightSpec)
            } else {
                child.measure(0, 0)
            }
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val eventData = child.tag as? EventCardView ?: continue

            val weekOffset = eventData.weekOffset
            val left = ((eventData.column + weekOffset * numColsPerWeek) * cellWidth).toInt()
            val top = (eventData.startRow * cellHeight).toInt()
            val right = ((eventData.column + 1 + weekOffset * numColsPerWeek) * cellWidth).toInt()
            val bottom = ((eventData.endRow + 1) * cellHeight).toInt()

            child.layout(left, top, right, bottom)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)

        val x = event.x + scrollX
        val y = event.y
        val col = (x / cellWidth).toInt()
        val row = (y / cellHeight).toInt()

        if (!isCreatingEvent) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startRow = row
                    startCol = col
                    endRow = row
                    endCol = col
                    isDragging = true
                    invalidate()
                }

                MotionEvent.ACTION_MOVE -> {
                    if (isDragging) {
                        endRow = row
                        invalidate()
                    }
                }

                MotionEvent.ACTION_UP -> {
                    if (isDragging) {
                        endRow = row
                        isDragging = false
                        createEventRectangleHeight = abs(endRow - startRow)
                        invalidate()
                    }
                    isCreatingEvent = true
                }
            }
            invalidate()
        } else {
            startRow = row
            startCol = col
            endRow = createEventRectangleHeight + row
            endCol = col
            invalidate()
        }

        return true
    }

    fun addEvent(startRow: Int, endRow: Int, column: Int, title: String, weekOffset: Int = currentWeekOffset) {
        val data = EventCardView(startRow, endRow, column, title, weekOffset)
        events.add(data)

        val view = LayoutInflater.from(context).inflate(R.layout.event_item_layout, this, false)
        view.findViewById<TextView>(R.id.cardTitle).text = title
        view.tag = data
        addView(view)
        requestLayout()
    }

    override fun dispatchDraw(canvas: Canvas) {
        val startCol = (scrollX / cellWidth).toInt()
        val endCol = ((scrollX + width) / cellWidth).toInt() + 1

        for (i in startCol..endCol) {
            canvas.drawLine(i * cellWidth - scrollX, 0f, i * cellWidth - scrollX, height.toFloat(), paint)
        }

        for (j in 1 until numRows) {
            if (j % 4 == 0) {
                canvas.drawLine(0f, j * cellHeight, width.toFloat(), j * cellHeight, paint)
            }
        }

        super.dispatchDraw(canvas)

        if (startRow >= 0 && startCol >= 0 && endRow >= 0 && endCol >= 0) {
            val left = min(startCol, endCol) * cellWidth - scrollX
            val top = min(startRow, endRow) * cellHeight
            val right = (max(startCol, endCol) + 1) * cellWidth - scrollX
            val bottom = (max(startRow, endRow) + 1) * cellHeight

            canvas.drawRect(left, top, right, bottom, rectPaint)
            canvas.drawRect(left, top, right, bottom, borderPaint)
        }
    }

    override fun computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.currX, scroller.currY)
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }
    private fun scrollToSemana(weekOffset: Int) {
        val targetScrollX = (weekOffset * weekWidth)
        scroller.startScroll(scrollX, 0, targetScrollX - scrollX, 0, 300)

        // 🔧 Fuerza reposicionar y redibujar
        requestLayout()
        invalidate()

        ViewCompat.postInvalidateOnAnimation(this)
    }




}

data class EventCardView(
    val startRow: Int,
    val endRow: Int,
    val column: Int,
    val title: String,
    val weekOffset: Int = 0
)

/**
override fun onTouchEvent(event: MotionEvent): Boolean {
    val x = event.x
    val y = event.y

    val col = (x / cellWidth).toInt()
    val row = (y / cellHeight).toInt()

    if (!isCreatingEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Inicia recuadro
                startRow = row
                startCol = col
                endRow = row
                endCol = col
                isDragging = true
                invalidate()
                println("Click en celda: fila=$row, columna=$col")
            }

            MotionEvent.ACTION_MOVE -> {
                // Redimensiona recuadro
                if (isDragging) {
                    endRow = row
                    //endCol = col
                    invalidate()
                }
            }

            MotionEvent.ACTION_UP -> {
                // Finaliza arrastre
                if (isDragging) {
                    endRow = row
                    //endCol = cols
                    isDragging = false
                    invalidate()
                    createEventRectangleHeight = abs(endRow - startRow)
                }
                isCreatingEvent = true
            }
        }
        invalidate()

    } else{
        startRow = row
        startCol = col
        endRow = createEventRectangleHeight + row
        endCol = col
        invalidate()
    }



    return true
}
*/