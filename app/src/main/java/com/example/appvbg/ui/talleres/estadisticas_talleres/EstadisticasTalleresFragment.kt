package com.example.appvbg.ui.talleres.estadisticas_talleres

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
import androidx.fragment.app.Fragment
import com.example.appvbg.R

class EstadisticasTalleresFragment: Fragment(R.layout.fragment_estadisticas_talleres) {

    private lateinit var modalidadesChart:BarChart
    private lateinit var generoChart: PieChart
    private lateinit var departamentoChart: BarChart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_estadisticas_talleres, container, false)

        modalidadesChart = view.findViewById(R.id.graficoTalleresModalidades)
        generoChart = view.findViewById(R.id.graficoTalleresGenero)
        departamentoChart = view.findViewById(R.id.graficoTalleresDepartamento)

        setUpModalidades()
        setUpGenero()
        setUpDepartamento()

        return view
    }

    private fun setUpModalidades(){
        val entries = arrayListOf(
            BarEntry(0f, 20f),
            BarEntry(1f, 10f)
        )

        val labels = listOf("Talleres Virtuales", "Talleres Presenciales")

        val dataSet = BarDataSet(entries, "Distribución de talleres virtuales vs presenciales")
        dataSet.color = resources.getColor(R.color.red, null)

        val barData = BarData(dataSet)
        barData.barWidth = 0.9f

        modalidadesChart.data = barData
        modalidadesChart.setFitBars(true)
        modalidadesChart.description.isEnabled = false
        modalidadesChart.animateY(1000)

        val xAxis = modalidadesChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.labelCount = labels.size

        modalidadesChart.axisRight.isEnabled = false // desactiva eje derecho si no lo usas

        modalidadesChart.invalidate()

    }
    private fun setUpGenero(){
        val entries = listOf(
            PieEntry(70f, "Masculino"),
            PieEntry(300f, "Femenino")
        )

        val dataSet = PieDataSet(entries, "Distribución de género")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f

        val pieData = PieData(dataSet)
        pieData.setValueTextSize(12f)
        pieData.setValueTextColor(Color.WHITE)

        generoChart.data = pieData
        generoChart.setUsePercentValues(true)
        generoChart.description.isEnabled = false
        generoChart.centerText = "Distribución de género"
        generoChart.setCenterTextSize(18f)
        generoChart.animateY(1000)

        generoChart.invalidate() // refrescar gráfico

    }
    private fun setUpDepartamento(){
        val entries = arrayListOf(
            BarEntry(0f, 20f),
            BarEntry(1f, 10f)
        )

        val labels = listOf("Talleres Virtuales", "Talleres Presenciales")

        val dataSet = BarDataSet(entries, "Distribución de talleres virtuales vs presenciales")
        dataSet.color = resources.getColor(R.color.red, null)

        val barData = BarData(dataSet)
        barData.barWidth = 0.9f

        departamentoChart.data = barData
        departamentoChart.setFitBars(true)
        departamentoChart.description.isEnabled = false
        departamentoChart.animateY(1000)

        val xAxis = departamentoChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.labelCount = labels.size

        departamentoChart.axisRight.isEnabled = false // desactiva eje derecho si no lo usas

        departamentoChart.invalidate()
    }

}