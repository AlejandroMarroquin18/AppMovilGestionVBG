package com.example.appvbg.ui.agenda

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.TextView
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class GridTouchView(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {

    private val numRows = 24 * 4
    private val numCols = 7

    private val resizeThreshold = 40
    private var resizingTop = false
    private var resizingBottom = false
    private var isCreatingEvent = false
    private var createEventRectangleHeight = 0
    private val events = mutableListOf<EventCardView>()

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

    private var cellWidth = 0f
    private var cellHeight = 0f

    private var startRow = -1
    private var startCol = -1
    private var endRow = -1
    private var endCol = -1

    private var isDragging = false

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(
            MeasureSpec.getSize(widthMeasureSpec),
            MeasureSpec.getSize(heightMeasureSpec)
        )
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        cellWidth = width / numCols.toFloat()
        cellHeight = height / numRows.toFloat()

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            val tag = child.tag as? EventCardView ?: continue

            val left = (tag.column * cellWidth).toInt()
            val top = (tag.startRow * cellHeight).toInt()
            val right = ((tag.column + 1) * cellWidth).toInt()
            val bottom = ((tag.endRow + 1) * cellHeight).toInt()

            child.layout(left, top, right, bottom)
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)

        // Dibuja la cuadrícula
        for (i in 1 until numCols) {
            canvas.drawLine(i * cellWidth, 0f, i * cellWidth, height.toFloat(), paint)
        }

        for (j in 1 until numRows) {
            if (j % 4 == 0) {
                canvas.drawLine(0f, j * cellHeight, width.toFloat(), j * cellHeight, paint)
            }
        }

        // Dibuja el recuadro temporal
        if (startRow >= 0 && startCol >= 0 && endRow >= 0 && endCol >= 0 && isDragging) {
            val left = min(startCol, endCol) * cellWidth
            val top = min(startRow, endRow) * cellHeight
            val right = (max(startCol, endCol) + 1) * cellWidth
            val bottom = (max(startRow, endRow) + 1) * cellHeight

            canvas.drawRect(left, top, right, bottom, rectPaint)
            canvas.drawRect(left, top, right, bottom, borderPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
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
                        invalidate()
                        createEventRectangleHeight = abs(endRow - startRow)
                    }
                    isCreatingEvent = true
                }
            }
        } else {
            startRow = row
            startCol = col
            endRow = createEventRectangleHeight + row
            endCol = col
            addEvent(startRow, endRow, startCol, "Nuevo evento")
            isCreatingEvent = false
        }

        return true
    }

    fun addEvent(startRow: Int, endRow: Int, column: Int, title: String) {
        val event = EventCardView(startRow, endRow, column, title)
        events.add(event)

        val card = TextView(context).apply {
            text = title
            setPadding(8, 8, 8, 8)
            setBackgroundColor(Color.parseColor("#FFEB3B")) // amarillo
            setTextColor(Color.BLACK)
            textSize = 14f
            tag = event
            setBackgroundResource(android.R.drawable.dialog_holo_light_frame)
        }

        addView(card)
        requestLayout()
    }

    fun changeCreatingEvent(inicio: Int, fin: Int) {
        createEventRectangleHeight = abs(fin - inicio)
        invalidate()
    }

    fun cancelCreatingEvent() {
        isCreatingEvent = false
        startRow = -1
        startCol = -1
        endRow = -1
        endCol = -1
        invalidate()
    }
}

data class EventCardView(
    val startRow: Int,
    val endRow: Int,
    val column: Int,
    val title: String
)
