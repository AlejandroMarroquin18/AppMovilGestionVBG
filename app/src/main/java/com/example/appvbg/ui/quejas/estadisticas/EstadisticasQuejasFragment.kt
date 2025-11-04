package com.example.appvbg.ui.quejas.estadisticas

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.os.Bundle
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
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.appvbg.EstadisticasQuejasViewModel
import androidx.fragment.app.viewModels

import com.example.appvbg.R
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.formatter.ValueFormatter

class EstadisticasQuejasFragment: Fragment(R.layout.fragment_estadisticas_quejas) {
    private lateinit var barChart: BarChart
    private lateinit var sedeChart: BarChart
    private lateinit var anioChart: BarChart
    private lateinit var departamentoChart: PieChart
    private lateinit var generoChart: PieChart
    private lateinit var edadesChart: BarChart
    private lateinit var comunaChart: BarChart
    private lateinit var tipoVBGChart: BarChart
    private lateinit var factoresChart: BarChart
    private val viewModel: EstadisticasQuejasViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {



        val view = inflater.inflate(R.layout.fragment_estadisticas_quejas, container, false)


        barChart = view.findViewById(R.id.graficoQuejaGenero)
        sedeChart = view.findViewById(R.id.graficoQuejaSede)
        anioChart = view.findViewById(R.id.graficoQuejaAnio)
        departamentoChart = view.findViewById(R.id.graficoQuejaDepartamento)
        generoChart = view.findViewById(R.id.graficoQuejaXGenero)

        edadesChart = view.findViewById(R.id.graficoQuejaEdad)
        comunaChart = view.findViewById(R.id.graficoQuejaComuna)
        tipoVBGChart = view.findViewById(R.id.graficoQuejaTipoVBG)
        factoresChart = view.findViewById(R.id.graficoQuejaFactores)

        viewModel.fetchEstadisticasQuejas(requireContext())

        viewModel.conteoQuejaEstudiantes.observe(viewLifecycleOwner) { value ->
            setUpNumbers(view.findViewById(R.id.quejasEstudiantes), value)
        }
        viewModel.conteoQuejaProfesores.observe(viewLifecycleOwner) { value ->
            setUpNumbers(view.findViewById(R.id.quejasProfesores), value)
        }
        viewModel.conteoQuejaFuncionarios.observe(viewLifecycleOwner) { value ->
            setUpNumbers(view.findViewById(R.id.quejasFuncionarios), value)
        }

        viewModel.conteoQuejasFacultad.observe(viewLifecycleOwner) { (entries, labels) ->
            setupBarChart(entries, labels)
        }

        viewModel.conteoQuejasSede.observe(viewLifecycleOwner) { (entries, labels) ->
            setUpSede(entries, labels)
        }

        viewModel.conteoQuejaAnio.observe(viewLifecycleOwner) { (entries, labels) ->
            setUpAnio(entries, labels)
        }

        viewModel.conteoVice.observe(viewLifecycleOwner) { entries ->
            setUpDepartamento(entries)
        }

        viewModel.conteoGenero.observe(viewLifecycleOwner) { entries ->
            setUpGenero(entries)
        }

        viewModel.ConteoEdades.observe(viewLifecycleOwner) { (entries, labels) ->
            setUpBarChart(entries, labels, "Atenciones por edad", edadesChart)
        }
        viewModel.conteoComunas.observe(viewLifecycleOwner){ (entries, labels) ->
            setUpBarChart(entries, labels, "Atenciones por comuna",comunaChart)
        }
        viewModel.conteoTipoVBG.observe(viewLifecycleOwner){ (entries, labels) ->
            setUpBarChart(entries, labels, "Atenciones por tipo de VBG", tipoVBGChart)
        }
        viewModel.conteoFactores.observe(viewLifecycleOwner){(entries, labels) ->
            setUpBarChart(entries, labels, "Atenciones por factores de riesgo",factoresChart)
        }


        return view
    }

    private fun setupBarChart(entries: List<BarEntry>, labels: List<String>) {

        /**
        val entries = arrayListOf(
            BarEntry(0f, 120f), // Facultad A
            BarEntry(1f, 95f),  // Facultad B
            BarEntry(2f, 150f), // Facultad C
            BarEntry(3f, 80f)   // Facultad D
        )

        // Nombres de facultades en el eje X
        val labels = listOf("Artes", "Ciencias", "Medicina", "Ingeniería")
        */
        val dataSet = BarDataSet(entries, "Facultades")
        dataSet.color = resources.getColor(R.color.purple_500, null)

        val barData = BarData(dataSet)
        barData.barWidth = 0.9f

        barChart.data = barData
        barChart.setFitBars(true)
        barChart.description.isEnabled = false
        barChart.animateY(1000)

        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.labelCount = labels.size
        xAxis.labelRotationAngle = -30f // opcional, si los textos son largos

        barChart.axisRight.isEnabled = false
        barChart.invalidate()
    }




    private fun setUpSede(entries: List<BarEntry>, labels: List<String>){
        /**
        val entries = arrayListOf(
            BarEntry(0f, 10f),
            BarEntry(1f, 20f),
            BarEntry(2f, 15f),
            BarEntry(3f, 30f),
            BarEntry(4f, 25f)
        )

        val labels = listOf("Melendez", "San Fernando", "Buga", "Palmira", "Zarzal")
        */
        val dataSet = BarDataSet(entries, "Sedes")
        dataSet.color = resources.getColor(R.color.red, null)

        val barData = BarData(dataSet)
        barData.barWidth = 0.9f

        sedeChart.data = barData
        sedeChart.setFitBars(true)
        sedeChart.description.isEnabled = false
        sedeChart.animateY(1000)

        val xAxis = sedeChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.labelCount = labels.size

        sedeChart.axisRight.isEnabled = false // desactiva eje derecho si no lo usas

        sedeChart.invalidate()
    }
    private fun setUpAnio(entries: List<BarEntry>, labels: List<String>){

        val dataSet = BarDataSet(entries, "Atenciones por año")
        dataSet.color = resources.getColor(R.color.purple_500, null)

        val barData = BarData(dataSet)
        barData.barWidth = 0.9f

        anioChart.data = barData
        anioChart.setFitBars(true)
        anioChart.description.isEnabled = false
        anioChart.animateY(1000)

        val xAxis = anioChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.labelCount = labels.size

        anioChart.axisRight.isEnabled = false // desactiva eje derecho si no lo usas

        anioChart.invalidate()
    }
    private fun setUpDepartamento(entries: List<PieEntry>){
        Log.d("EstadisticasQuejasFragment", "DepartamentoEntries: $entries")
        /**
        val entries = listOf(
            PieEntry(30f, "Bienestar"),
            PieEntry(60f, "Académica"),
            PieEntry(10f, "Investigaciones")
        )*/

        val dataSet = PieDataSet(entries, "Atenciones por departamentos")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f


        val pieData = PieData(dataSet)
        pieData.setValueTextSize(12f)
        pieData.setValueTextColor(Color.WHITE)


        departamentoChart.data = pieData
        departamentoChart.setUsePercentValues(true)
        departamentoChart.description.isEnabled = false
        departamentoChart.centerText = "Atenciones por departamento"
        departamentoChart.setCenterTextSize(18f)
        departamentoChart.animateY(1000)

        departamentoChart.invalidate() // refrescar gráfico

    }
    private fun setUpGenero(entries: List<PieEntry>){
        Log.d("EstadisticasQuejasFragment", "EntriesGenero: $entries")
        /**
        val entries = listOf(
            PieEntry(10f, "Masculino"),
            PieEntry(50f, "Femenino"),
            PieEntry(40f, "No binario")
        )*/

        val dataSet = PieDataSet(entries, "Distribución de atenciones por género")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        dataSet.sliceSpace = 3f
        dataSet.selectionShift = 5f

        val pieData = PieData(dataSet)
        pieData.setValueTextSize(12f)
        pieData.setValueTextColor(Color.WHITE)

        generoChart.data = pieData
        generoChart.setUsePercentValues(true)
        generoChart.description.isEnabled = false
        generoChart.centerText = "Distribución de atenciones por género"
        generoChart.setCenterTextSize(18f)
        generoChart.animateY(1000)

        generoChart.invalidate() // refrescar gráfico
    }

    private fun setUpNumbers(textView: TextView, value: Int){
        textView.setText(value.toString())
    }


    private fun setUpBarChart(entries: List<BarEntry>, labels: List<String>, title: String,chart: BarChart){

        val dataSet = BarDataSet(entries, title)
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





}