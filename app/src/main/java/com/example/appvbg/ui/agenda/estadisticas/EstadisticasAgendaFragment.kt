package com.example.appvbg.ui.agenda.estadisticas

import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com. github. mikephil. charting. utils. ColorTemplate
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.example.appvbg.R

class EstadisticasAgendaFragment: Fragment(R.layout.fragment_estadisticas_agenda) {
    private lateinit var citasAnio: BarChart
    private lateinit var citasCumplimiento: BarChart
    private lateinit var citasFacultad: BarChart
    private lateinit var departamentoChart: PieChart
    private lateinit var razonesChart: PieChart
    private lateinit var frecuenciaChart: BarChart
    private lateinit var generoChart: PieChart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_estadisticas_agenda, container, false)
        
        citasAnio = view.findViewById(R.id.graficoCitasAnio)
        citasCumplimiento = view.findViewById(R.id.graficoCitasCumplimiento)
        citasFacultad = view.findViewById(R.id.graficoCitasFacultad)
        departamentoChart = view.findViewById(R.id.graficoCitasDepartamento)
        razonesChart = view.findViewById(R.id.graficoCitasRazones)
        frecuenciaChart = view.findViewById(R.id.graficoCitasFrecuencia)
        generoChart = view.findViewById(R.id.graficoCitasGenero)
        
        
        setUpCitasAnio()
        setUpCitasCumplimiento()
        setUpCitasFacultad()
        setUpDepartamento()
        setUpRazones()
        setUpFrecuencia()
        setUpGenero()
        return view
    }
    
    private fun setUpCitasAnio() {
        val entries = arrayListOf(
            BarEntry(0f, 10f),
            BarEntry(1f, 20f),
            BarEntry(2f, 15f)
        )

        val labels = listOf("2023", "2024", "2025")

        val dataSet = BarDataSet(entries, "Evolución anual de citas")
        dataSet.color = resources.getColor(R.color.red, null)

        val barData = BarData(dataSet)
        barData.barWidth = 0.9f

        citasAnio.data = barData
        citasAnio.setFitBars(true)
        citasAnio.description.isEnabled = false
        citasAnio.animateY(1000)

        val xAxis = citasAnio.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.labelCount = labels.size

        citasAnio.axisRight.isEnabled = false // desactiva eje derecho si no lo usas

        citasAnio.invalidate()
        
    }
    private fun setUpCitasCumplimiento() {
        val entries = arrayListOf(
            BarEntry(0f, 80f),
            BarEntry(1f, 70f),
            BarEntry(2f, 90f)
        )

        val labels = listOf("2023", "2024", "2025")

        val dataSet = BarDataSet(entries, "Tasa de cumplimiento de citas por año")
        dataSet.color = resources.getColor(R.color.purple_500, null)

        val barData = BarData(dataSet)
        barData.barWidth = 0.9f

        citasCumplimiento.data = barData
        citasCumplimiento.setFitBars(true)
        citasCumplimiento.description.isEnabled = false
        citasCumplimiento.animateY(1000)

        val xAxis = citasCumplimiento.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.labelCount = labels.size

        citasCumplimiento.axisRight.isEnabled = false // desactiva eje derecho si no lo usas

        citasCumplimiento.invalidate()
    }
    private fun setUpCitasFacultad() {
        val entries = arrayListOf(
            BarEntry(0f, 100f),
            BarEntry(1f, 130f),
            BarEntry(2f, 127f),
            BarEntry(3f, 165f),
            BarEntry(4f, 200f)
        )

        val labels = listOf("Artes", "Ciencias", "Medicina", "Ingeniería","Derecho")

        val dataSet = BarDataSet(entries, "Facultad")
        dataSet.color = resources.getColor(R.color.purple_500, null)

        val barData = BarData(dataSet)
        barData.barWidth = 0.9f

        citasFacultad.data = barData
        citasFacultad.setFitBars(true)
        citasFacultad.description.isEnabled = false
        citasFacultad.animateY(1000)

        val xAxis = citasFacultad.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.labelCount = labels.size

        citasFacultad.axisRight.isEnabled = false // desactiva eje derecho si no lo usas

        citasFacultad.invalidate()
    }
    private fun setUpDepartamento() {
        val entries = listOf(
            PieEntry(300f, "Acedémico"),
            PieEntry(500f, "Investigación"),
            PieEntry(400f, "Bienestar")
        )

        val dataSet = PieDataSet(entries, "Citas de funcionarios por departamento")
        dataSet.colors = com.github.mikephil.charting.utils.ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f

        val pieData = PieData(dataSet)
        pieData.setValueTextSize(12f)
        pieData.setValueTextColor(Color.WHITE)

        departamentoChart.data = pieData
        departamentoChart.setUsePercentValues(true)
        departamentoChart.description.isEnabled = false
        departamentoChart.centerText = "Citas de funcionarios por departamento"
        departamentoChart.setCenterTextSize(18f)
        departamentoChart.animateY(1000)

        departamentoChart.invalidate() // refrescar gráfico



    }
    private fun setUpRazones() {
        val entries = listOf(
            PieEntry(300f, "Orientación psicológica"),
            PieEntry(500f, "Atención integral")
        )

        val dataSet = PieDataSet(entries, "Razones más comunes para agendar cita")
        dataSet.colors = com.github.mikephil.charting.utils.ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f

        val pieData = PieData(dataSet)
        pieData.setValueTextSize(12f)
        pieData.setValueTextColor(Color.WHITE)

        razonesChart.data = pieData
        razonesChart.setUsePercentValues(true)
        razonesChart.description.isEnabled = false
        razonesChart.centerText = "Razones más comunes para agendar cita"
        razonesChart.setCenterTextSize(18f)
        razonesChart.animateY(1000)

        razonesChart.invalidate() // refrescar gráfico


    }
    private fun setUpFrecuencia() {
        val entries = arrayListOf(
            BarEntry(0f, 100f),
            BarEntry(1f, 130f),
            BarEntry(2f, 127f),
            BarEntry(3f, 165f),
            BarEntry(4f, 200f),
            BarEntry(5f, 150f),
            BarEntry(6f, 180f),
            BarEntry(7f, 120f),
            BarEntry(8f, 160f),
            BarEntry(9f, 190f),
            BarEntry(10f, 140f),
            BarEntry(11f, 170f)

        )

        val labels = listOf("Enero", "Febrero", "Marzo", "Abril", "Mayo",
            "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre",
            "Diciembre" )

        val dataSet = BarDataSet(entries, "Frecuencia de citas por mes")
        dataSet.color = resources.getColor(R.color.purple_500, null)

        val barData = BarData(dataSet)
        barData.barWidth = 0.9f

        frecuenciaChart.data = barData
        frecuenciaChart.setFitBars(true)
        frecuenciaChart.description.isEnabled = false
        frecuenciaChart.animateY(1000)

        val xAxis = citasFacultad.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.labelCount = labels.size

        frecuenciaChart.axisRight.isEnabled = false // desactiva eje derecho si no lo usas

        frecuenciaChart.invalidate()
    }
    private fun setUpGenero() {
        val entries = listOf(
            PieEntry(70f, "Masculino"),
            PieEntry(300f, "Femenino")
        )

        val dataSet = PieDataSet(entries, "Razones más comunes para agendar cita")
        dataSet.colors = com.github.mikephil.charting.utils.ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f

        val pieData = PieData(dataSet)
        pieData.setValueTextSize(12f)
        pieData.setValueTextColor(Color.WHITE)

        generoChart.data = pieData
        generoChart.setUsePercentValues(true)
        generoChart.description.isEnabled = false
        generoChart.centerText = "Razones más comunes para agendar cita"
        generoChart.setCenterTextSize(18f)
        generoChart.animateY(1000)

        generoChart.invalidate() // refrescar gráfico

    }

}