package com.example.appvbg.ui.quejas.detalles

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appvbg.APIConstant
import com.example.appvbg.R
import com.example.appvbg.api.PrefsHelper
import com.example.appvbg.api.makeRequest
import com.example.appvbg.databinding.FragmentDetallesQuejaBinding
import com.example.appvbg.ui.agenda.detalles.DetallesAgendaViewModel
import com.example.appvbg.ui.agenda.detalles.HistorialQueja
import org.json.JSONObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.Int
import java.text.SimpleDateFormat
import java.util.*


class DetallesQueja : Fragment(R.layout.fragment_detalles_queja) {
    private val args: DetallesQuejaArgs by navArgs()
    private var _binding: FragmentDetallesQuejaBinding? = null
    private val binding get() = _binding!!
    private var editMode = false
    private lateinit var data: JSONObject
    private var reportaData: JSONObject? = null
    private var afectadoData: JSONObject?= null
    private var agresorData: JSONObject?= null
    private lateinit var adapter: ItemAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: DetallesAgendaViewModel
    private val estadosQueja = listOf("Pendiente", "Aprobado", "En Proceso", "Finalizado", "Remitido")
    private val prioridades = listOf("Pendiente", "Baja", "Media", "Alta", "Crítica")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetallesQuejaBinding.inflate(inflater, container, false)
        binding.cambioEstadoTextView.visibility = View.GONE
        binding.opcionesEstadosContainer.visibility = View.GONE
        binding.cambioPrioridadTextView.visibility = View.GONE
        binding.opcionesPrioridadContainer.visibility = View.GONE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val quejaJsonString = args.quejaJSON
        data = JSONObject(quejaJsonString)
        reportaData = data.optJSONObject("persona_reporta")?: null
        afectadoData = data.optJSONObject("persona_afectada")?: null
        agresorData = data.optJSONObject("persona_acusada")?: null






        // Mostrar ID
        binding.tvQuejaId.text = "ID: #${data.optString("id", "N/A")}"

        // Mostrar datos en labels y edits
        setFieldsFromJSON(data,reportaData,afectadoData,agresorData)
        setEditMode(false)


        //Seteo estado y manejo


        // Configuras el adaptador
        val spinnerEstadoAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, estadosQueja)
        spinnerEstadoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerEstado.adapter = spinnerEstadoAdapter

        val spinnerPrioridadAdapter= ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, prioridades)
        spinnerPrioridadAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPrioridad.adapter = spinnerPrioridadAdapter

        // Variable para guardar el estado anterior
        var estadoAnterior: String? = null

        binding.spinnerEstado.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val estadoActual = parent.getItemAtPosition(position).toString()

                // Evita ejecutar la función al cargar por primera vez
                if (estadoAnterior != null && estadoActual != estadoAnterior) {
                    binding.cambioEstadoTextView.visibility = View.VISIBLE
                    binding.opcionesEstadosContainer.visibility = View.VISIBLE
                }

                // Actualiza el estado anterior
                estadoAnterior = estadoActual
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No hace nada
            }
        }
        binding.estadoActualTextView.setText(data.optString("estado"))
        binding.spinnerEstado.setSelection(estadosQueja.indexOf(data.optString("estado")))
        binding.cancelarEstadoButton.setOnClickListener {
            binding.cambioEstadoTextView.visibility = View.GONE
            binding.opcionesEstadosContainer.visibility = View.GONE
            binding.spinnerEstado.setSelection(estadosQueja.indexOf(data.optString("estado")))

        }
        binding.guardarEstadoButton.setOnClickListener {
            val newEstado= binding.spinnerEstado.selectedItem.toString()
            //enviar al backend
            val newQueja = buildJSON()
            sendEdit(newQueja)
            binding.cambioEstadoTextView.visibility = View.GONE
            binding.opcionesEstadosContainer.visibility = View.GONE
            //binding.estadoActualTextView.setText(newEstado)
        }
        ///////////////////////////////Prioridad////////////////////////////////

        // Variable para guardar el prioridad anterior
        var prioridadAnterior: String? = null

        binding.spinnerPrioridad.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val prioridadActual = parent.getItemAtPosition(position).toString()

                // Evita ejecutar la función al cargar por primera vez
                if (prioridadAnterior != null && prioridadActual != prioridadAnterior) {
                    binding.cambioPrioridadTextView.visibility = View.VISIBLE
                    binding.opcionesPrioridadContainer.visibility = View.VISIBLE
                }

                // Actualiza el prioridad anterior
                prioridadAnterior = prioridadActual
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No hace nada
            }
        }
        binding.prioridadActualTextView.setText(data.optString("prioridad"))
        binding.spinnerPrioridad.setSelection(prioridades.indexOf(data.optString("prioridad")))
        binding.cancelarPrioridadButton.setOnClickListener {
            binding.cambioPrioridadTextView.visibility = View.GONE
            binding.opcionesPrioridadContainer.visibility = View.GONE
            binding.spinnerPrioridad.setSelection(prioridades.indexOf(data.optString("prioridad")))

        }
        binding.guardarPrioridadButton.setOnClickListener {
            val newPrioridad= binding.spinnerPrioridad.selectedItem.toString()
            //enviar al backend
            val newQueja = buildJSON()
            sendEdit(newQueja)
            binding.cambioPrioridadTextView.visibility = View.GONE
            binding.opcionesPrioridadContainer.visibility = View.GONE
        }

        ////////////////////////////////////////////////////////////////////////


        //seteo historial
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = ItemAdapter(
            mutableListOf(),
            onSaveClicked = { item ->
                try {

                viewModel.editarHistorial(requireContext(), item.queja_id, item)

                } catch (e: Exception) {
                    Log.e("QuejasFragment", "Error al navegar a detalles", e)
                    Toast.makeText(requireContext(), "Error al abrir detalles", Toast.LENGTH_SHORT).show()
                }
            },
            onDeleteClicked = { item ->
                viewModel.eliminarHistorial(requireContext(), item.queja_id)


            }
        )
        recyclerView.adapter = adapter

        viewModel = ViewModelProvider(this)[DetallesAgendaViewModel::class.java]
        viewModel.historial.observe(viewLifecycleOwner) { itemList ->
            adapter.updateItems(itemList)
        }
        viewModel.fetchHistorial(requireContext(), data.optString("id").toInt())




        binding.createHistButton.setOnClickListener {

            if(binding.includeHistorial.root.visibility==View.VISIBLE) {

                setCreatingMode(false)
                binding.createHistButton.visibility= View.GONE
            }else{
                setCreatingMode(true)
            }
        }


        binding.includeHistorial.root.visibility = View.GONE

        val fechaActual = Date()
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val newDate = formato.format(fechaActual)
        binding.includeHistorial.fechaCreateText.setText(newDate)

        binding.includeHistorial.cancelarCrearRegistroButton.setOnClickListener {
            setCreatingMode(false)
            binding.includeHistorial.tipoCreateText.setText("")
            binding.includeHistorial.descripcionCreateText.setText("")
            binding.createHistButton.visibility= View.VISIBLE

        }

        binding.includeHistorial.crearRegistroButton.setOnClickListener {
            val bind = binding.includeHistorial
            val newHistorial= HistorialQueja(
                0,
                bind.fechaCreateText.text.toString(),
                data.optString("id").toInt(),
                bind.descripcionCreateText.text.toString(),
                bind.tipoCreateText.text.toString(),
                0
            )
            viewModel.crearHistorial(requireContext(), newHistorial)

            setCreatingMode(false)
            binding.createHistButton.visibility= View.VISIBLE
        }


        binding.editButton.setOnClickListener {
            if (editMode) {
                val newJSON = buildJSON()
                sendEdit(newJSON)
            }
            editMode = !editMode
            binding.editButton.text = if (editMode) "Guardar" else "Editar"
            binding.cancelButton.visibility = if (editMode) View.VISIBLE else View.GONE
            setEditMode(editMode)
        }

        binding.cancelButton.setOnClickListener {
            editMode = false
            setFieldsFromJSON(data,reportaData,afectadoData,agresorData) // revertir cambios
            setEditMode(false)
            binding.editButton.text = "Editar"
            binding.cancelButton.visibility = View.GONE
        }

        binding.deleteButton.setOnClickListener {
            sendDelete()
        }

        binding.btnImprimir.setOnClickListener {
            Toast.makeText(requireContext(), "Funcionalidad de impresión", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setEditMode(enabled: Boolean) {
        val visibility = if (enabled) View.VISIBLE else View.GONE
        val labelVisibility = if (enabled) View.GONE else View.VISIBLE

        fun applyToViewGroup(container: ViewGroup) {
            for (i in 0 until container.childCount) {
                val child = container.getChildAt(i)
                when (child) {
                    is EditText -> {
                        child.visibility = visibility
                        child.isEnabled = enabled
                    }
                    is TextView -> {
                        if (child.id != View.NO_ID) {
                            try {
                                val resName = resources.getResourceEntryName(child.id)
                                if (resName.endsWith("Label", ignoreCase = true)) {
                                    child.visibility = labelVisibility
                                }
                            } catch (_: Exception) {
                            }
                        }
                    }
                    is ViewGroup -> applyToViewGroup(child)
                }
            }
        }

        applyToViewGroup(binding.includeReporta.root)
        applyToViewGroup(binding.includeAfectada.root)
        applyToViewGroup(binding.includeAgresor.root)
        applyToViewGroup(binding.includeAdicionales.root)
    }

    private fun buildJSON(): JSONObject {
        val json = JSONObject()

        val afectadaJSON= JSONObject()
        val agresorJSON= JSONObject()
        val reportaJSON= JSONObject()


        //metadatos
        json.put("id", data.optString("id"))
        json.put("estado", binding.spinnerEstado.selectedItem.toString())
        json.put("unidad",data.optString("unidad"))
        json.put("prioridad",binding.spinnerPrioridad.selectedItem.toString())
        json.put("tipo_de_acompanamiento",data.optString("tipo_de_acompanamiento"))

        // Reporta
        reportaJSON.put("fecha_recepcion", binding.includeReporta.fechaReportaEdit.text.toString())
        reportaJSON.put("nombre", binding.includeReporta.nombreReportaEdit.text.toString())
        reportaJSON.put("sexo", binding.includeReporta.sexoReportaEdit.text.toString())
        reportaJSON.put("edad", binding.includeReporta.edadReportaEdit.text.toString())
        reportaJSON.put("estamento", binding.includeReporta.estamentoReportaEdit.text.toString())
        reportaJSON.put("vicerrectoria_adscrito", binding.includeReporta.viceReportaEdit.text.toString())
        reportaJSON.put("dependencia", binding.includeReporta.dependenciaReportaEdit.text.toString())
        reportaJSON.put("programa_academico", binding.includeReporta.programaReportaEdit.text.toString())
        reportaJSON.put("facultad", binding.includeReporta.facultadReportaEdit.text.toString())
        reportaJSON.put("sede", binding.includeReporta.sedeReportaEdit.text.toString())
        reportaJSON.put("celular", binding.includeReporta.celularReportaEdit.text.toString())
        reportaJSON.put("correo", binding.includeReporta.correoReportaEdit.text.toString())

        // Afectada
        afectadaJSON.put("nombre", binding.includeAfectada.nombreAfectadaEdit.text.toString())
        afectadaJSON.put("sexo", binding.includeAfectada.sexoAfectadaEdit.text.toString())
        afectadaJSON.put("edad", binding.includeAfectada.edadAfectadaEdit.text.toString())
        afectadaJSON.put("tipo_documento_identidad",binding.includeAfectada.tipoDocumentoAfectadaEdit.text.toString())
        afectadaJSON.put("documento_identidad",binding.includeAfectada.documentoIdentidadAfectadaEdit.text.toString())
        afectadaJSON.put("redes_apoyo", binding.includeAfectada.redesApoyoAfectadaEdit.text.toString())
        afectadaJSON.put("codigo", binding.includeAfectada.codigoAfectadaEdit.text.toString())
        afectadaJSON.put("semestre", binding.includeAfectada.semestreAfectadaEdit.text.toString())
        afectadaJSON.put("comuna", binding.includeAfectada.comunaAfectadaEdit.text.toString())
        afectadaJSON.put("direccion", binding.includeAfectada.direccionAfectadaEdit.text.toString())
        afectadaJSON.put("barrio", binding.includeAfectada.barrioAfectadaEdit.text.toString())
        afectadaJSON.put("ciudad_origen", binding.includeAfectada.ciudadOrigenAfectadaEdit.text.toString())
        afectadaJSON.put("estrato_socioeconomico", binding.includeAfectada.estratoAfectadaEdit.text.toString())
        afectadaJSON.put("condicion_etnico_racial", binding.includeAfectada.etniaAfectadaEdit.text.toString())
        afectadaJSON.put("tiene_discapacidad", binding.includeAfectada.discapacidadAfectadaEdit.text.toString())
        afectadaJSON.put("tipo_discapacidad", binding.includeAfectada.tipoDiscapacidadAfectadaEdit.text.toString())
        afectadaJSON.put("identidad_genero", binding.includeAfectada.identidadGeneroAfectadaEdit.text.toString())
        afectadaJSON.put("orientacion_sexual", binding.includeAfectada.orientacionSexualAfectadaEdit.text.toString())
        afectadaJSON.put("estamento", binding.includeAfectada.estamentoAfectadaEdit.text.toString())
        afectadaJSON.put("vicerrectoria_adscrito", binding.includeAfectada.viceAfectadaEdit.text.toString())
        afectadaJSON.put("dependencia", binding.includeAfectada.dependenciaAfectadaEdit.text.toString())
        afectadaJSON.put("programa_academico", binding.includeAfectada.programaAfectadaEdit.text.toString())
        afectadaJSON.put("facultad", binding.includeAfectada.facultadAfectadaEdit.text.toString())
        afectadaJSON.put("sede", binding.includeAfectada.sedeAfectadaEdit.text.toString())
        afectadaJSON.put("celular", binding.includeAfectada.celularAfectadaEdit.text.toString())
        afectadaJSON.put("correo", binding.includeAfectada.correoAfectadaEdit.text.toString())
        afectadaJSON.put("tipo_vbg_os", binding.includeAfectada.tipoVBGAfectadaEdit.text.toString())
        afectadaJSON.put("detalles_caso", binding.includeAfectada.detallesCasoAfectadaEdit.text.toString())
        afectadaJSON.put("ha_hecho_denuncia", binding.includeAfectada.haHechoDenunciaAfectadaEdit.text.toString())
        afectadaJSON.put("denuncias_previas", binding.includeAfectada.denunciasPreviasAfectadaEdit.text.toString())


        // Agresor
        agresorJSON.put("nombre", binding.includeAgresor.nombreAgresorEdit.text.toString())
        agresorJSON.put("sexo", binding.includeAgresor.sexoAgresorEdit.text.toString())
        agresorJSON.put("edad", binding.includeAgresor.edadAgresorEdit.text.toString())
        agresorJSON.put("semestre", binding.includeAgresor.semestreAgresorEdit.text.toString())
        agresorJSON.put("barrio", binding.includeAgresor.barrioAgresorEdit.text.toString())
        agresorJSON.put("ciudad_origen", binding.includeAgresor.ciudadOrigenAgresorEdit.text.toString())
        agresorJSON.put("condicion_etnico_racial", binding.includeAgresor.etniaAgresorEdit.text.toString())
        agresorJSON.put("tiene_discapacidad", binding.includeAgresor.discapacidadAgresorEdit.text.toString())
        agresorJSON.put("tipo_discapacidad", binding.includeAgresor.tipoDiscapacidadAgresorEdit.text.toString())
        agresorJSON.put("identidad_genero", binding.includeAgresor.identidadGeneroAgresorEdit.text.toString())
        agresorJSON.put("orientacion_sexual", binding.includeAgresor.orientacionSexualAgresorEdit.text.toString())
        agresorJSON.put("estamento", binding.includeAgresor.estamentoAgresorEdit.text.toString())
        agresorJSON.put("vicerrectoria_adscrito", binding.includeAgresor.viceAgresorEdit.text.toString())
        agresorJSON.put("dependencia", binding.includeAgresor.dependenciaAgresorEdit.text.toString())
        agresorJSON.put("programa_academico", binding.includeAgresor.programaAgresorEdit.text.toString())
        agresorJSON.put("facultad", binding.includeAgresor.facultadAgresorEdit.text.toString())
        agresorJSON.put("sede", binding.includeAgresor.sedeAgresorEdit.text.toString())
        agresorJSON.put("factores_riesgo", binding.includeAgresor.factoresRiesgoAgresorEdit.text.toString())
        agresorJSON.put("tiene_denuncias", binding.includeAgresor.tieneDenunciasAgresorEdit.text.toString())
        agresorJSON.put("detalles_denuncias", binding.includeAgresor.detallesDenunciasAgresorEdit.text.toString())


        // Adicionales
        json.put("desea_activar_ruta_atencion_integral", binding.includeAdicionales.rutaIntegralEdit.text.toString())
        json.put("recibir_asesoria_orientacion_sociopedagogica", binding.includeAdicionales.asesoriaEdit.text.toString())
        json.put("orientacion_psicologica", binding.includeAdicionales.orientacionEdit.text.toString())
        json.put("asistencia_juridica", binding.includeAdicionales.asistenciaJuridicaEdit.text.toString())
        json.put("acompañamiento_solicitud_medidas_proteccion_inicial", binding.includeAdicionales.medidasProteccionEdit.text.toString())
        json.put("acompañamiento_ante_instancias_gubernamentales", binding.includeAdicionales.instanciasGubernamentalesEdit.text.toString())

        json.put("interponer_queja_al_cade", binding.includeAdicionales.quejaCADEEdit.text.toString())
        json.put("interponer_queja_oficina_control_interno", binding.includeAdicionales.quejaControlInternoEdit.text.toString())
        json.put("interponer_queja_a_rectoria", binding.includeAdicionales.quejaRectoriaEdit.text.toString())

        json.put("observaciones", binding.includeAdicionales.observacionesEdit.text.toString())



        json.put("persona_reporta", reportaJSON)
        json.put("persona_afectada",afectadaJSON)
        json.put("persona_acusada",agresorJSON)

        return json
    }

    private fun setFieldsFromJSON(json: JSONObject, reportaJSON: JSONObject?, afectadaJSON: JSONObject?, agresorJSON: JSONObject?) {
        binding.spinnerEstado.setSelection(estadosQueja.indexOf(json.optString("estado")))
        binding.spinnerPrioridad.setSelection(prioridades.indexOf(json.optString("prioridad")))
        binding.estadoActualTextView.setText(json.optString("estado"))
        binding.prioridadActualTextView.setText(json.optString("prioridad"))

        // Reporta
        if(reportaJSON !=null){

            binding.includeReporta.fechaReportaLabel.setText(reportaJSON.optString("fecha_recepcion"))
            binding.includeReporta.nombreReportaLabel.setText(reportaJSON.optString("nombre"))
            binding.includeReporta.sexoReportaLabel.setText(reportaJSON.optString("sexo"))
            binding.includeReporta.edadReportaLabel.setText(reportaJSON.optString("edad"))
            binding.includeReporta.estamentoReportaLabel.setText(reportaJSON.optString("estamento"))
            binding.includeReporta.viceReportaLabel.setText(reportaJSON.optString("vicerrectoria_adscrito"))
            binding.includeReporta.dependenciaReportaLabel.setText(reportaJSON.optString("dependencia"))
            binding.includeReporta.programaReportaLabel.setText(reportaJSON.optString("programa_academico"))
            binding.includeReporta.facultadReportaLabel.setText(reportaJSON.optString("facultad"))
            binding.includeReporta.sedeReportaLabel.setText(reportaJSON.optString("sede"))
            binding.includeReporta.celularReportaLabel.setText(reportaJSON.optString("celular"))
            binding.includeReporta.correoReportaLabel.setText(reportaJSON.optString("correo"))

            // Edits
            binding.includeReporta.fechaReportaEdit.setText(json.optString("fecha_recepcion"))
            binding.includeReporta.nombreReportaEdit.setText(reportaJSON.optString("nombre"))
            binding.includeReporta.sexoReportaEdit.setText(reportaJSON.optString("sexo"))
            binding.includeReporta.edadReportaEdit.setText(reportaJSON.optString("edad"))
            binding.includeReporta.estamentoReportaEdit.setText(reportaJSON.optString("estamento"))
            binding.includeReporta.viceReportaEdit.setText(reportaJSON.optString("vicerrectoria_adscrito"))
            binding.includeReporta.dependenciaReportaEdit.setText(reportaJSON.optString("dependencia"))
            binding.includeReporta.programaReportaEdit.setText(reportaJSON.optString("programa_academico"))
            binding.includeReporta.facultadReportaEdit.setText(reportaJSON.optString("facultad"))
            binding.includeReporta.sedeReportaEdit.setText(reportaJSON.optString("sede"))
            binding.includeReporta.celularReportaEdit.setText(reportaJSON.optString("celular"))
            binding.includeReporta.correoReportaEdit.setText(reportaJSON.optString("correo"))

        }



        // Afectada

        if (afectadaJSON != null){
            binding.includeAfectada.nombreAfectadaLabel.setText(afectadaJSON.optString("nombre"))
            binding.includeAfectada.sexoAfectadaLabel.setText(afectadaJSON.optString("sexo"))
            binding.includeAfectada.edadAfectadaLabel.setText(afectadaJSON.optString("edad"))
            binding.includeAfectada.tipoDocumentoAfectadaLabel.setText(afectadaJSON.optString("tipo_documento"))
            binding.includeAfectada.documentoIdentidadAfectadaLabel.setText(afectadaJSON.optString("documento_identidad"))
            binding.includeAfectada.redesApoyoAfectadaLabel.setText(afectadaJSON.optString("redes_apoyo"))
            binding.includeAfectada.codigoAfectadaLabel.setText(afectadaJSON.optString("codigo"))
            binding.includeAfectada.semestreAfectadaLabel.setText(afectadaJSON.optString("semestre"))
            binding.includeAfectada.comunaAfectadaLabel.setText(afectadaJSON.optString("comuna"))
            binding.includeAfectada.direccionAfectadaLabel.setText(afectadaJSON.optString("direccion"))
            binding.includeAfectada.barrioAfectadaLabel.setText(afectadaJSON.optString("barrio"))
            binding.includeAfectada.ciudadOrigenAfectadaLabel.setText(afectadaJSON.optString("ciudad_origen"))
            binding.includeAfectada.estratoAfectadaLabel.setText(afectadaJSON.optString("estrato_socioeconomico"))
            binding.includeAfectada.etniaAfectadaLabel.setText(afectadaJSON.optString("condicion_etnico_racial"))
            binding.includeAfectada.discapacidadAfectadaLabel.setText(afectadaJSON.optString("tiene_discapacidad"))
            binding.includeAfectada.tipoDiscapacidadAfectadaLabel.setText(afectadaJSON.optString("tipo_discapacidad"))
            binding.includeAfectada.identidadGeneroAfectadaLabel.setText(afectadaJSON.optString("identidad_genero"))
            binding.includeAfectada.orientacionSexualAfectadaLabel.setText(afectadaJSON.optString("orientacion_sexual"))
            binding.includeAfectada.estamentoAfectadaLabel.setText(afectadaJSON.optString("estamento"))
            binding.includeAfectada.viceAfectadaLabel.setText(afectadaJSON.optString("vicerrectoria_adscrito"))
            binding.includeAfectada.dependenciaAfectadaLabel.setText(afectadaJSON.optString("dependencia"))
            binding.includeAfectada.programaAfectadaLabel.setText(afectadaJSON.optString("programa_academico"))
            binding.includeAfectada.facultadAfectadaLabel.setText(afectadaJSON.optString("facultad"))
            binding.includeAfectada.sedeAfectadaLabel.setText(afectadaJSON.optString("sede"))
            binding.includeAfectada.celularAfectadaLabel.setText(afectadaJSON.optString("celular"))
            binding.includeAfectada.correoAfectadaLabel.setText(afectadaJSON.optString("correo"))
            binding.includeAfectada.tipoVBGAfectadaLabel.setText(afectadaJSON.optString("tipo_vbg_os"))
            binding.includeAfectada.detallesCasoAfectadaLabel.setText(afectadaJSON.optString("detalles_caso"))
            binding.includeAfectada.haHechoDenunciaAfectadaLabel.setText(afectadaJSON.optString("ha_hecho_denuncia"))
            binding.includeAfectada.denunciasPreviasAfectadaLabel.setText(afectadaJSON.optString("denuncias_previas"))

            // Edits
            binding.includeAfectada.nombreAfectadaEdit.setText(afectadaJSON.optString("nombre"))
            binding.includeAfectada.sexoAfectadaEdit.setText(afectadaJSON.optString("sexo"))
            binding.includeAfectada.edadAfectadaEdit.setText(afectadaJSON.optString("edad"))
            binding.includeAfectada.tipoDocumentoAfectadaEdit.setText(afectadaJSON.optString("tipo_documento"))
            binding.includeAfectada.documentoIdentidadAfectadaEdit.setText(afectadaJSON.optString("documento_identidad"))
            binding.includeAfectada.redesApoyoAfectadaEdit.setText(afectadaJSON.optString("redes_apoyo"))
            binding.includeAfectada.codigoAfectadaEdit.setText(afectadaJSON.optString("codigo"))
            binding.includeAfectada.semestreAfectadaEdit.setText(afectadaJSON.optString("semestre"))
            binding.includeAfectada.comunaAfectadaEdit.setText(afectadaJSON.optString("comuna"))
            binding.includeAfectada.direccionAfectadaEdit.setText(afectadaJSON.optString("direccion"))
            binding.includeAfectada.barrioAfectadaEdit.setText(afectadaJSON.optString("barrio"))
            binding.includeAfectada.ciudadOrigenAfectadaEdit.setText(afectadaJSON.optString("ciudad_origen"))
            binding.includeAfectada.estratoAfectadaEdit.setText(afectadaJSON.optString("estrato_socioeconomico"))
            binding.includeAfectada.etniaAfectadaEdit.setText(afectadaJSON.optString("condicion_etnico_racial"))
            binding.includeAfectada.discapacidadAfectadaEdit.setText(afectadaJSON.optString("tiene_discapacidad"))
            binding.includeAfectada.tipoDiscapacidadAfectadaEdit.setText(afectadaJSON.optString("tipo_discapacidad"))
            binding.includeAfectada.identidadGeneroAfectadaEdit.setText(afectadaJSON.optString("identidad_genero"))
            binding.includeAfectada.orientacionSexualAfectadaEdit.setText(afectadaJSON.optString("orientacion_sexual"))
            binding.includeAfectada.estamentoAfectadaEdit.setText(afectadaJSON.optString("estamento"))
            binding.includeAfectada.viceAfectadaEdit.setText(afectadaJSON.optString("vicerrectoria_adscrito"))
            binding.includeAfectada.dependenciaAfectadaEdit.setText(afectadaJSON.optString("dependencia"))
            binding.includeAfectada.programaAfectadaEdit.setText(afectadaJSON.optString("programa_academico"))
            binding.includeAfectada.facultadAfectadaEdit.setText(afectadaJSON.optString("facultad"))
            binding.includeAfectada.sedeAfectadaEdit.setText(afectadaJSON.optString("sede"))
            binding.includeAfectada.celularAfectadaEdit.setText(afectadaJSON.optString("celular"))
            binding.includeAfectada.correoAfectadaEdit.setText(afectadaJSON.optString("correo"))
            binding.includeAfectada.tipoVBGAfectadaEdit.setText(afectadaJSON.optString("tipo_vbg_os"))
            binding.includeAfectada.detallesCasoAfectadaEdit.setText(afectadaJSON.optString("detalles_caso"))
            binding.includeAfectada.haHechoDenunciaAfectadaEdit.setText(afectadaJSON.optString("ha_hecho_denuncia"))
            binding.includeAfectada.denunciasPreviasAfectadaEdit.setText(afectadaJSON.optString("denuncias_previas"))


        }


        // Agresor
        if(agresorJSON != null){
            binding.includeAgresor.nombreAgresorLabel.setText(agresorJSON.optString("nombre"))
            binding.includeAgresor.sexoAgresorLabel.setText(agresorJSON.optString("sexo"))
            binding.includeAgresor.edadAgresorLabel.setText(agresorJSON.optString("edad"))
            binding.includeAgresor.semestreAgresorLabel.setText(agresorJSON.optString("semestre"))
            binding.includeAgresor.barrioAgresorLabel.setText(agresorJSON.optString("barrio"))
            binding.includeAgresor.ciudadOrigenAgresorLabel.setText(agresorJSON.optString("ciudad_origen"))
            binding.includeAgresor.etniaAgresorLabel.setText(agresorJSON.optString("condicion_etnico_racial"))
            binding.includeAgresor.discapacidadAgresorLabel.setText(agresorJSON.optString("tiene_discapacidad"))
            binding.includeAgresor.tipoDiscapacidadAgresorLabel.setText(agresorJSON.optString("tipo_discapacidad"))
            binding.includeAgresor.identidadGeneroAgresorLabel.setText(agresorJSON.optString("identidad_genero"))
            binding.includeAgresor.orientacionSexualAgresorLabel.setText(agresorJSON.optString("orientacion_sexual"))
            binding.includeAgresor.estamentoAgresorLabel.setText(agresorJSON.optString("estamento"))
            binding.includeAgresor.viceAgresorLabel.setText(agresorJSON.optString("vicerrectoria_adscrito"))
            binding.includeAgresor.dependenciaAgresorLabel.setText(agresorJSON.optString("dependencia"))
            binding.includeAgresor.programaAgresorLabel.setText(agresorJSON.optString("programa_academico"))
            binding.includeAgresor.facultadAgresorLabel.setText(agresorJSON.optString("facultad"))
            binding.includeAgresor.sedeAgresorLabel.setText(agresorJSON.optString("sede"))
            binding.includeAgresor.factoresRiesgoAgresorLabel.setText(agresorJSON.optString("factores_riesgo"))
            binding.includeAgresor.tieneDenunciasAgresorLabel.setText(agresorJSON.optString("tiene_denuncias"))
            binding.includeAgresor.detallesDenunciasAgresorLabel.setText(agresorJSON.optString("detalles_denuncias"))

            // Edits
            binding.includeAgresor.nombreAgresorEdit.setText(agresorJSON.optString("nombre"))
            binding.includeAgresor.sexoAgresorEdit.setText(agresorJSON.optString("sexo"))
            binding.includeAgresor.edadAgresorEdit.setText(agresorJSON.optString("edad"))
            binding.includeAgresor.semestreAgresorEdit.setText(agresorJSON.optString("semestre"))
            binding.includeAgresor.barrioAgresorEdit.setText(agresorJSON.optString("barrio"))
            binding.includeAgresor.ciudadOrigenAgresorEdit.setText(agresorJSON.optString("ciudad_origen"))
            binding.includeAgresor.etniaAgresorEdit.setText(agresorJSON.optString("condicion_etnico_racial"))
            binding.includeAgresor.discapacidadAgresorEdit.setText(agresorJSON.optString("tiene_discapacidad"))
            binding.includeAgresor.tipoDiscapacidadAgresorEdit.setText(agresorJSON.optString("tipo_discapacidad"))
            binding.includeAgresor.identidadGeneroAgresorEdit.setText(agresorJSON.optString("identidad_genero"))
            binding.includeAgresor.orientacionSexualAgresorEdit.setText(agresorJSON.optString("orientacion_sexual"))
            binding.includeAgresor.estamentoAgresorEdit.setText(agresorJSON.optString("estamento"))
            binding.includeAgresor.viceAgresorEdit.setText(agresorJSON.optString("vicerrectoria_adscrito"))
            binding.includeAgresor.dependenciaAgresorEdit.setText(agresorJSON.optString("dependencia"))
            binding.includeAgresor.programaAgresorEdit.setText(agresorJSON.optString("programa_academico"))
            binding.includeAgresor.facultadAgresorEdit.setText(agresorJSON.optString("facultad"))
            binding.includeAgresor.sedeAgresorEdit.setText(agresorJSON.optString("sede"))
            binding.includeAgresor.factoresRiesgoAgresorEdit.setText(agresorJSON.optString("factores_riesgo"))
            binding.includeAgresor.tieneDenunciasAgresorEdit.setText(agresorJSON.optString("tiene_denuncias"))
            binding.includeAgresor.detallesDenunciasAgresorEdit.setText(agresorJSON.optString("detalles_denuncias"))

        }



        // Adicionales
        binding.includeAdicionales.rutaIntegralLabel.setText(json.optString("desea_activar_ruta_atencion_integral"))
        binding.includeAdicionales.asesoriaLabel.setText(json.optString("recibir_asesoria_orientacion_sociopedagogica"))
        binding.includeAdicionales.orientacionLabel.setText(json.optString("orientacion_psicologica"))
        binding.includeAdicionales.asistenciaJuridicaLabel.setText(json.optString("asistencia_juridica"))
        binding.includeAdicionales.medidasProteccionLabel.setText(json.optString("acompañamiento_solicitud_medidas_proteccion_inicial"))
        binding.includeAdicionales.instanciasGubernamentalesLabel.setText(json.optString("acompañamiento_ante_instancias_gubernamentales"))
        binding.includeAdicionales.quejaCADELabel.setText(json.optString("interponer_queja_al_cade"))
        binding.includeAdicionales.quejaControlInternoLabel.setText(json.optString("interponer_queja_oficina_control_interno"))
        binding.includeAdicionales.quejaRectoriaLabel.setText(json.optString("interponer_queja_a_rectoria"))
        binding.includeAdicionales.observacionesLabel.setText(json.optString("observaciones"))

        /////////////////Edits
        binding.includeAdicionales.rutaIntegralEdit.setText(json.optString("desea_activar_ruta_atencion_integral"))
        binding.includeAdicionales.asesoriaEdit.setText(json.optString("recibir_asesoria_orientacion_sociopedagogica"))
        binding.includeAdicionales.orientacionEdit.setText(json.optString("orientacion_psicologica"))
        binding.includeAdicionales.asistenciaJuridicaEdit.setText(json.optString("asistencia_juridica"))
        binding.includeAdicionales.medidasProteccionEdit.setText(json.optString("acompañamiento_solicitud_medidas_proteccion_inicial"))
        binding.includeAdicionales.instanciasGubernamentalesEdit.setText(json.optString("acompañamiento_ante_instancias_gubernamentales"))
        binding.includeAdicionales.quejaCADEEdit.setText(json.optString("interponer_queja_al_cade"))
        binding.includeAdicionales.quejaControlInternoEdit.setText(json.optString("interponer_queja_oficina_control_interno"))
        binding.includeAdicionales.quejaRectoriaEdit.setText(json.optString("interponer_queja_a_rectoria"))
        binding.includeAdicionales.observacionesEdit.setText(json.optString("observaciones"))
    }

    private fun sendEdit(json: JSONObject) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val resp = makeRequest(
                    "${APIConstant.BACKEND_URL}api/quejas/${data.optString("id")}/",
                    "PATCH",
                    PrefsHelper.getDRFToken(requireContext()) ?: "",
                    json
                )

                withContext(Dispatchers.Main) {
                    if (resp == "error") {
                        Toast.makeText(requireContext(), "Error al editar la queja", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Queja editada", Toast.LENGTH_SHORT).show()
                        data = JSONObject(resp)
                        setFieldsFromJSON(data,reportaData,afectadoData,agresorData)
                    }
                }
            } catch (e: Exception) {
                Log.e("Error", e.toString())
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error al editar la queja", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun sendDelete() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val resp = makeRequest(
                    "${APIConstant.BACKEND_URL}api/quejas/${data.optString("id")}/",
                    "DELETE",
                    PrefsHelper.getDRFToken(requireContext()) ?: ""
                )
                withContext(Dispatchers.Main) {
                    if (resp == "error") {
                        Toast.makeText(requireContext(), "Error al eliminar la queja", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Queja eliminada", Toast.LENGTH_SHORT).show()
                        val action = DetallesQuejaDirections.actionDetallesQuejaToQuejasFragment()
                        findNavController().navigate(action)
                    }
                }
            } catch (e: Exception) {
                Log.e("Error", e.toString())
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error al eliminar la queja", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setCreatingMode(enabled: Boolean){
        binding.includeHistorial.root.visibility = if (enabled) View.VISIBLE else View.GONE

    }


    private class ItemAdapter(
        private var items: MutableList<HistorialQueja>,
        private val onSaveClicked: (HistorialQueja) -> Unit,
        private val onDeleteClicked: (HistorialQueja) -> Unit
    ) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.historial_item, parent, false)
            return ItemViewHolder(view)
        }

        override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
            val item = items.getOrNull(position)
            item?.let {
                holder.bind(it, onSaveClicked, onDeleteClicked)
            }
        }

        fun updateItems(newItems: List<HistorialQueja>) {
            items.clear()
            items.addAll(newItems)
            notifyDataSetChanged()
        }

        override fun getItemCount(): Int = items.size

        class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            private val idTextView: TextView = itemView.findViewById(R.id.idTextView)
            private val fechaTextView: TextView = itemView.findViewById(R.id.fechaTextView)
            private val descripcionTextView: TextView = itemView.findViewById(R.id.descripcionTextView)
            private val tipoTextView: TextView = itemView.findViewById(R.id.tipoTextView)
            private val numeroTextView: TextView = itemView.findViewById(R.id.numeroTextView)
            private val tipoEditText: EditText = itemView.findViewById(R.id.tipoEditText)
            private val descripcionEditText: EditText = itemView.findViewById(R.id.descripcionEditText)

            private val editButton: ImageButton = itemView.findViewById(R.id.editButton)
            private val sendEditButton: Button = itemView.findViewById(R.id.sendEditButton)
            private val cancelEditButton: Button = itemView.findViewById(R.id.cancelEditButton)
            private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)


            fun bind(item: HistorialQueja, onSaveClicked: (HistorialQueja) -> Unit, onDeleteClicked: (HistorialQueja) -> Unit) {

                idTextView.text = "${item.id}"
                fechaTextView.text = item.fecha
                descripcionTextView.text = item.descripcion
                tipoTextView.text = item.tipo
                numeroTextView.text = "${item.numero}"
                tipoEditText.setText(item.tipo)
                descripcionEditText.setText(item.descripcion)



                sendEditButton.setOnClickListener {
                    val updatedItem = item.copy(
                        tipo = tipoEditText.text.toString(),
                        descripcion = descripcionEditText.text.toString()
                    )
                    onSaveClicked(updatedItem)
                    // Actualiza el UI con los cambios
                    tipoTextView.text = updatedItem.tipo
                    descripcionTextView.text = updatedItem.descripcion
                    setEditMode(false)
                }


                cancelEditButton.setOnClickListener {
                    setEditMode(false)
                }
                editButton.setOnClickListener { 
                    setEditMode(true)
                }
                deleteButton.setOnClickListener { onDeleteClicked(item) }
            }

            private fun setEditMode(enabled: Boolean) {
                val editTextVisibility = if (enabled) View.VISIBLE else View.GONE

                val textViewVissibility = if (enabled) View.GONE else View.VISIBLE

                // Campos de edición
                tipoEditText.visibility = editTextVisibility
                descripcionEditText.visibility = editTextVisibility

                // TextViews originales
                tipoTextView.visibility = textViewVissibility
                descripcionTextView.visibility = textViewVissibility

                // Botones
                editButton.visibility = textViewVissibility
                sendEditButton.visibility = editTextVisibility
                cancelEditButton.visibility = editTextVisibility
            }



        }
    }


}
