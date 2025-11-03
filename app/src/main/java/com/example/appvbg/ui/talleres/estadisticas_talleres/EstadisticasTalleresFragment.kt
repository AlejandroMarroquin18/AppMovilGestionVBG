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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.appvbg.EstadisticasTalleresViewModel
import com.example.appvbg.R

class EstadisticasTalleresFragment: Fragment(R.layout.fragment_estadisticas_talleres) {

    private lateinit var modalidadesChart:PieChart
    private lateinit var generoChart: PieChart
    private lateinit var departamentoChart: BarChart
    private lateinit var sedeChart: BarChart
    private lateinit var discapacidadChart: BarChart

    private val viewModel: EstadisticasTalleresViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_estadisticas_talleres, container, false)

        modalidadesChart = view.findViewById(R.id.graficoTalleresModalidades)
        generoChart = view.findViewById(R.id.graficoTalleresGenero)
        departamentoChart = view.findViewById(R.id.graficoTalleresDepartamento)



        sedeChart = view.findViewById(R.id.graficoTalleresSede)
        discapacidadChart = view.findViewById(R.id.graficoTalleresDiscapacidad)

        viewModel.fetchEstadisticasTalleres(requireContext())

        viewModel.conteoSedes.observe(viewLifecycleOwner){ (entries, labels) ->
            setUpBarChart(entries, labels, "Talleres por sedes", sedeChart)
        }

        viewModel.conteoDiscapacidades.observe(viewLifecycleOwner){ (entries, labels)->
            setUpBarChart(entries, labels, "Participantes por tipo de discapacidad", discapacidadChart)
        }

        viewModel.conteoGenero.observe (viewLifecycleOwner){ entries ->
            setUpPieChart(entries, "Participantes por género", generoChart)

        }

        viewModel.conteoModalidades.observe (viewLifecycleOwner){ entries ->
            setUpPieChart(entries, "Talleres por modalidad", modalidadesChart)
        }

        return view
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


    /**
    private fun setUpModalidades(valores: List<Float>) {
        val entries = valores.mapIndexed { index, value -> BarEntry(index.toFloat(), value) }
        val labels = listOf("Talleres Virtuales", "Talleres Presenciales")

        val dataSet = BarDataSet(entries, "Distribución de talleres virtuales vs presenciales")
        dataSet.color = resources.getColor(R.color.red, null)

        val barData = BarData(dataSet)
        barData.barWidth = 0.9f

        modalidadesChart.apply {
            data = barData
            setFitBars(true)
            description.isEnabled = false
            animateY(1000)
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(labels)
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                labelCount = labels.size
            }
            axisRight.isEnabled = false
            invalidate()
        }
    }

    private fun setUpGenero(valores: List<Float>) {
        val entries = listOf(
            PieEntry(valores[0], "Masculino"),
            PieEntry(valores[1], "Femenino")
        )

        val dataSet = PieDataSet(entries, "Distribución de género")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()

        val pieData = PieData(dataSet).apply {
            setValueTextSize(12f)
            setValueTextColor(Color.WHITE)
        }

        generoChart.apply {
            data = pieData
            setUsePercentValues(true)
            description.isEnabled = false
            centerText = "Distribución de género"
            setCenterTextSize(18f)
            animateY(1000)
            invalidate()
        }
    }

    private fun setUpDepartamento(valores: List<Float>) {
        val entries = valores.mapIndexed { index, value -> BarEntry(index.toFloat(), value) }
        val labels = listOf("Talleres Virtuales", "Talleres Presenciales")

        val dataSet = BarDataSet(entries, "Distribución por departamento")
        dataSet.color = resources.getColor(R.color.red, null)

        val barData = BarData(dataSet)
        barData.barWidth = 0.9f

        departamentoChart.apply {
            data = barData
            setFitBars(true)
            description.isEnabled = false
            animateY(1000)
            xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(labels)
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                labelCount = labels.size
            }
            axisRight.isEnabled = false
            invalidate()
        }
    }*/


    private fun setUpBarChart(entries: List<BarEntry>, labels: List<String>, title: String, chart: BarChart){

        val dataSet = BarDataSet(entries, "Quejas por año")
        dataSet.color = resources.getColor(R.color.purple_500, null)

        val barData = BarData(dataSet)
        barData.barWidth = 0.9f

        chart.data = barData
        chart.setFitBars(true)
        chart.description.isEnabled = false
        chart.animateY(1000)

        val xAxis = chart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.labelCount = labels.size

        chart.axisRight.isEnabled = false // desactiva eje derecho si no lo usas

        chart.invalidate()
    }
    private fun setUpPieChart(entries: List<PieEntry>, title: String, chart: PieChart){


        val dataSet = PieDataSet(entries, title)
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f

        val pieData = PieData(dataSet)
        pieData.setValueTextSize(12f)
        pieData.setValueTextColor(Color.WHITE)

        chart.data = pieData
        chart.setUsePercentValues(true)
        chart.description.isEnabled = false
        chart.centerText = title
        chart.setCenterTextSize(18f)
        chart.animateY(1000)

        chart.invalidate() // refrescar gráfico

    }
}