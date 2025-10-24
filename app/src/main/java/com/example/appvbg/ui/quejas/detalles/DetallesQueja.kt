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
    private lateinit var adapter: ItemAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: DetallesAgendaViewModel
    private val estadosQueja = listOf("Pendiente", "Aprobado", "En Proceso", "Finalizado", "Remitido")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetallesQuejaBinding.inflate(inflater, container, false)
        binding.cambioEstadoTextView.visibility = View.GONE
        binding.opcionesEstadosContainer.visibility = View.GONE
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val quejaJsonString = args.quejaJSON
        data = JSONObject(quejaJsonString)

        // Mostrar ID
        binding.tvQuejaId.text = "ID: #${data.optString("id", "N/A")}"

        // Mostrar datos en labels y edits
        setFieldsFromJSON(data)
        setEditMode(false)


        //Seteo estado y manejo


        // Configuras el adaptador
        val spinnerEstadoAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, estadosQueja)
        spinnerEstadoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerEstado.adapter = spinnerEstadoAdapter

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
            setFieldsFromJSON(data) // revertir cambios
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
        //metadatos
        json.put("id", data.optString("id"))
        json.put("estado", binding.spinnerEstado.selectedItem.toString())
        json.put("unidad",data.optString("unidad"))

        // Reporta
        json.put("fecha_recepcion", binding.includeReporta.fechaReportaEdit.text.toString())
        json.put("reporta_nombre", binding.includeReporta.nombreReportaEdit.text.toString())
        json.put("reporta_sexo", binding.includeReporta.sexoReportaEdit.text.toString())
        json.put("reporta_edad", binding.includeReporta.edadReportaEdit.text.toString())
        json.put("reporta_estamento", binding.includeReporta.estamentoReportaEdit.text.toString())
        json.put("reporta_vicerrectoria_adscrito", binding.includeReporta.viceReportaEdit.text.toString())
        json.put("reporta_dependencia", binding.includeReporta.dependenciaReportaEdit.text.toString())
        json.put("reporta_programa_academico", binding.includeReporta.programaReportaEdit.text.toString())
        json.put("reporta_facultad", binding.includeReporta.facultadReportaEdit.text.toString())
        json.put("reporta_sede", binding.includeReporta.sedeReportaEdit.text.toString())
        json.put("reporta_celular", binding.includeReporta.celularReportaEdit.text.toString())
        json.put("reporta_correo", binding.includeReporta.correoReportaEdit.text.toString())

        // Afectada
        json.put("afectado_nombre", binding.includeAfectada.nombreAfectadaEdit.text.toString())
        json.put("afectado_sexo", binding.includeAfectada.sexoAfectadaEdit.text.toString())
        json.put("afectado_edad", binding.includeAfectada.edadAfectadaEdit.text.toString())
        json.put("afectado_comuna", binding.includeAfectada.comunaAfectadaEdit.text.toString())
        json.put("afectado_estrato_socioeconomico", binding.includeAfectada.estratoAfectadaEdit.text.toString())
        json.put("afectado_condicion_etnico_racial", binding.includeAfectada.etniaAfectadaEdit.text.toString())
        json.put("afectado_tiene_discapacidad", binding.includeAfectada.discapacidadAfectadaEdit.text.toString())
        json.put("afectado_tipo_discapacidad", binding.includeAfectada.tipoDiscapacidadAfectadaEdit.text.toString())
        json.put("afectado_identidad_genero", binding.includeAfectada.identidadGeneroAfectadaEdit.text.toString())
        json.put("afectado_orientacion_sexual", binding.includeAfectada.orientacionSexualAfectadaEdit.text.toString())
        json.put("afectado_estamento", binding.includeAfectada.estamentoAfectadaEdit.text.toString())
        json.put("afectado_vicerrectoria_adscrito", binding.includeAfectada.viceAfectadaEdit.text.toString())
        json.put("afectado_dependencia", binding.includeAfectada.dependenciaAfectadaEdit.text.toString())
        json.put("afectado_programa_academico", binding.includeAfectada.programaAfectadaEdit.text.toString())
        json.put("afectado_facultad", binding.includeAfectada.facultadAfectadaEdit.text.toString())
        json.put("afectado_sede", binding.includeAfectada.sedeAfectadaEdit.text.toString())
        json.put("afectado_celular", binding.includeAfectada.celularAfectadaEdit.text.toString())
        json.put("afectado_correo", binding.includeAfectada.correoAfectadaEdit.text.toString())
        json.put("afectado_tipo_vbg_os", binding.includeAfectada.tipoVBGAfectadaEdit.text.toString())

        // Agresor
        json.put("agresor_nombre", binding.includeAgresor.nombreAgresorEdit.text.toString())
        json.put("agresor_sexo", binding.includeAgresor.sexoAgresorEdit.text.toString())
        json.put("agresor_edad", binding.includeAgresor.edadAgresorEdit.text.toString())
        json.put("agresor_condicion_etnico_racial", binding.includeAgresor.etniaAgresorEdit.text.toString())
        json.put("agresor_tiene_discapacidad", binding.includeAgresor.discapacidadAgresorEdit.text.toString())
        json.put("agresor_tipo_discapacidad", binding.includeAgresor.tipoDiscapacidadAgresorEdit.text.toString())
        json.put("agresor_identidad_genero", binding.includeAgresor.identidadGeneroAgresorEdit.text.toString())
        json.put("agresor_orientacion_sexual", binding.includeAgresor.orientacionSexualAgresorEdit.text.toString())
        json.put("agresor_estamento", binding.includeAgresor.estamentoAgresorEdit.text.toString())
        json.put("agresor_vicerrectoria_adscrito", binding.includeAgresor.viceAgresorEdit.text.toString())
        json.put("agresor_dependencia", binding.includeAgresor.dependenciaAgresorEdit.text.toString())
        json.put("agresor_programa_academico", binding.includeAgresor.programaAgresorEdit.text.toString())
        json.put("agresor_facultad", binding.includeAgresor.facultadAgresorEdit.text.toString())
        json.put("agresor_sede", binding.includeAgresor.sedeAgresorEdit.text.toString())

        // Adicionales
        json.put("desea_activar_ruta_atencion_integral", binding.includeAdicionales.rutaIntegralEdit.text.toString())
        json.put("recibir_asesoria_orientacion_sociopedagogica", binding.includeAdicionales.asesoriaEdit.text.toString())
        json.put("orientacion_psicologica", binding.includeAdicionales.orientacionEdit.text.toString())
        json.put("asistencia_juridica", binding.includeAdicionales.asistenciaJuridicaEdit.text.toString())
        json.put("acompañamiento_solicitud_medidas_proteccion_inicial", binding.includeAdicionales.medidasProteccionEdit.text.toString())
        json.put("acompañamiento_ante_instancias_gubernamentales", binding.includeAdicionales.instanciasGubernamentalesEdit.text.toString())
        json.put("interponer_queja_al_comite_asusntos_internos_disciplinarios", binding.includeAdicionales.comiteAsuntosInternosEdit.text.toString())
        json.put("observaciones", binding.includeAdicionales.observacionesEdit.text.toString())

        return json
    }

    private fun setFieldsFromJSON(json: JSONObject) {
        binding.spinnerEstado.setSelection(estadosQueja.indexOf(json.optString("estado")))
        binding.estadoActualTextView.setText(json.optString("estado"))

        // Reporta
        binding.includeReporta.fechaReportaLabel.setText(json.optString("fecha_recepcion"))
        binding.includeReporta.nombreReportaLabel.setText(json.optString("reporta_nombre"))
        binding.includeReporta.sexoReportaLabel.setText(json.optString("reporta_sexo"))
        binding.includeReporta.edadReportaLabel.setText(json.optString("reporta_edad"))
        binding.includeReporta.estamentoReportaLabel.setText(json.optString("reporta_estamento"))
        binding.includeReporta.viceReportaLabel.setText(json.optString("reporta_vicerrectoria_adscrito"))
        binding.includeReporta.dependenciaReportaLabel.setText(json.optString("reporta_dependencia"))
        binding.includeReporta.programaReportaLabel.setText(json.optString("reporta_programa_academico"))
        binding.includeReporta.facultadReportaLabel.setText(json.optString("reporta_facultad"))
        binding.includeReporta.sedeReportaLabel.setText(json.optString("reporta_sede"))
        binding.includeReporta.celularReportaLabel.setText(json.optString("reporta_celular"))
        binding.includeReporta.correoReportaLabel.setText(json.optString("reporta_correo"))

        // Afectada
        binding.includeAfectada.nombreAfectadaLabel.setText(json.optString("afectado_nombre"))
        binding.includeAfectada.sexoAfectadaLabel.setText(json.optString("afectado_sexo"))
        binding.includeAfectada.edadAfectadaLabel.setText(json.optString("afectado_edad"))
        binding.includeAfectada.comunaAfectadaLabel.setText(json.optString("afectado_comuna"))
        binding.includeAfectada.estratoAfectadaLabel.setText(json.optString("afectado_estrato_socioeconomico"))
        binding.includeAfectada.etniaAfectadaLabel.setText(json.optString("afectado_condicion_etnico_racial"))
        binding.includeAfectada.discapacidadAfectadaLabel.setText(json.optString("afectado_tiene_discapacidad"))
        binding.includeAfectada.tipoDiscapacidadAfectadaLabel.setText(json.optString("afectado_tipo_discapacidad"))
        binding.includeAfectada.identidadGeneroAfectadaLabel.setText(json.optString("afectado_identidad_genero"))
        binding.includeAfectada.orientacionSexualAfectadaLabel.setText(json.optString("afectado_orientacion_sexual"))
        binding.includeAfectada.estamentoAfectadaLabel.setText(json.optString("afectado_estamento"))
        binding.includeAfectada.viceAfectadaLabel.setText(json.optString("afectado_vicerrectoria_adscrito"))
        binding.includeAfectada.dependenciaAfectadaLabel.setText(json.optString("afectado_dependencia"))
        binding.includeAfectada.programaAfectadaLabel.setText(json.optString("afectado_programa_academico"))
        binding.includeAfectada.facultadAfectadaLabel.setText(json.optString("afectado_facultad"))
        binding.includeAfectada.sedeAfectadaLabel.setText(json.optString("afectado_sede"))
        binding.includeAfectada.celularAfectadaLabel.setText(json.optString("afectado_celular"))
        binding.includeAfectada.correoAfectadaLabel.setText(json.optString("afectado_correo"))
        binding.includeAfectada.tipoVBGAfectadaLabel.setText(json.optString("afectado_tipo_vbg_os"))

        // Agresor
        binding.includeAgresor.nombreAgresorLabel.setText(json.optString("agresor_nombre"))
        binding.includeAgresor.sexoAgresorLabel.setText(json.optString("agresor_sexo"))
        binding.includeAgresor.edadAgresorLabel.setText(json.optString("agresor_edad"))
        binding.includeAgresor.etniaAgresorLabel.setText(json.optString("agresor_condicion_etnico_racial"))
        binding.includeAgresor.discapacidadAgresorLabel.setText(json.optString("agresor_tiene_discapacidad"))
        binding.includeAgresor.tipoDiscapacidadAgresorLabel.setText(json.optString("agresor_tipo_discapacidad"))
        binding.includeAgresor.identidadGeneroAgresorLabel.setText(json.optString("agresor_identidad_genero"))
        binding.includeAgresor.orientacionSexualAgresorLabel.setText(json.optString("agresor_orientacion_sexual"))
        binding.includeAgresor.estamentoAgresorLabel.setText(json.optString("agresor_estamento"))
        binding.includeAgresor.viceAgresorLabel.setText(json.optString("agresor_vicerrectoria_adscrito"))
        binding.includeAgresor.dependenciaAgresorLabel.setText(json.optString("agresor_dependencia"))
        binding.includeAgresor.programaAgresorLabel.setText(json.optString("agresor_programa_academico"))
        binding.includeAgresor.facultadAgresorLabel.setText(json.optString("agresor_facultad"))
        binding.includeAgresor.sedeAgresorLabel.setText(json.optString("agresor_sede"))

        // Adicionales
        binding.includeAdicionales.rutaIntegralLabel.setText(json.optString("desea_activar_ruta_atencion_integral"))
        binding.includeAdicionales.asesoriaLabel.setText(json.optString("recibir_asesoria_orientacion_sociopedagogica"))
        binding.includeAdicionales.orientacionLabel.setText(json.optString("orientacion_psicologica"))
        binding.includeAdicionales.asistenciaJuridicaLabel.setText(json.optString("asistencia_juridica"))
        binding.includeAdicionales.medidasProteccionLabel.setText(json.optString("acompañamiento_solicitud_medidas_proteccion_inicial"))
        binding.includeAdicionales.instanciasGubernamentalesLabel.setText(json.optString("acompañamiento_ante_instancias_gubernamentales"))
        binding.includeAdicionales.comiteAsuntosInternosLabel.setText(json.optString("interponer_queja_al_comite_asusntos_internos_disciplinarios"))
        binding.includeAdicionales.observacionesLabel.setText(json.optString("observaciones"))



        /////////////////Edits
        // Reporta
        binding.includeReporta.fechaReportaEdit.setText(json.optString("fecha_recepcion"))
        binding.includeReporta.nombreReportaEdit.setText(json.optString("reporta_nombre"))
        binding.includeReporta.sexoReportaEdit.setText(json.optString("reporta_sexo"))
        binding.includeReporta.edadReportaEdit.setText(json.optString("reporta_edad"))
        binding.includeReporta.estamentoReportaEdit.setText(json.optString("reporta_estamento"))
        binding.includeReporta.viceReportaEdit.setText(json.optString("reporta_vicerrectoria_adscrito"))
        binding.includeReporta.dependenciaReportaEdit.setText(json.optString("reporta_dependencia"))
        binding.includeReporta.programaReportaEdit.setText(json.optString("reporta_programa_academico"))
        binding.includeReporta.facultadReportaEdit.setText(json.optString("reporta_facultad"))
        binding.includeReporta.sedeReportaEdit.setText(json.optString("reporta_sede"))
        binding.includeReporta.celularReportaEdit.setText(json.optString("reporta_celular"))
        binding.includeReporta.correoReportaEdit.setText(json.optString("reporta_correo"))

        // Afectada
        binding.includeAfectada.nombreAfectadaEdit.setText(json.optString("afectado_nombre"))
        binding.includeAfectada.sexoAfectadaEdit.setText(json.optString("afectado_sexo"))
        binding.includeAfectada.edadAfectadaEdit.setText(json.optString("afectado_edad"))
        binding.includeAfectada.comunaAfectadaEdit.setText(json.optString("afectado_comuna"))
        binding.includeAfectada.estratoAfectadaEdit.setText(json.optString("afectado_estrato_socioeconomico"))
        binding.includeAfectada.etniaAfectadaEdit.setText(json.optString("afectado_condicion_etnico_racial"))
        binding.includeAfectada.discapacidadAfectadaEdit.setText(json.optString("afectado_tiene_discapacidad"))
        binding.includeAfectada.tipoDiscapacidadAfectadaEdit.setText(json.optString("afectado_tipo_discapacidad"))
        binding.includeAfectada.identidadGeneroAfectadaEdit.setText(json.optString("afectado_identidad_genero"))
        binding.includeAfectada.orientacionSexualAfectadaEdit.setText(json.optString("afectado_orientacion_sexual"))
        binding.includeAfectada.estamentoAfectadaEdit.setText(json.optString("afectado_estamento"))
        binding.includeAfectada.viceAfectadaEdit.setText(json.optString("afectado_vicerrectoria_adscrito"))
        binding.includeAfectada.dependenciaAfectadaEdit.setText(json.optString("afectado_dependencia"))
        binding.includeAfectada.programaAfectadaEdit.setText(json.optString("afectado_programa_academico"))
        binding.includeAfectada.facultadAfectadaEdit.setText(json.optString("afectado_facultad"))
        binding.includeAfectada.sedeAfectadaEdit.setText(json.optString("afectado_sede"))
        binding.includeAfectada.celularAfectadaEdit.setText(json.optString("afectado_celular"))
        binding.includeAfectada.correoAfectadaEdit.setText(json.optString("afectado_correo"))
        binding.includeAfectada.tipoVBGAfectadaEdit.setText(json.optString("afectado_tipo_vbg_os"))

        // Agresor
        binding.includeAgresor.nombreAgresorEdit.setText(json.optString("agresor_nombre"))
        binding.includeAgresor.sexoAgresorEdit.setText(json.optString("agresor_sexo"))
        binding.includeAgresor.edadAgresorEdit.setText(json.optString("agresor_edad"))
        binding.includeAgresor.etniaAgresorEdit.setText(json.optString("agresor_condicion_etnico_racial"))
        binding.includeAgresor.discapacidadAgresorEdit.setText(json.optString("agresor_tiene_discapacidad"))
        binding.includeAgresor.tipoDiscapacidadAgresorEdit.setText(json.optString("agresor_tipo_discapacidad"))
        binding.includeAgresor.identidadGeneroAgresorEdit.setText(json.optString("agresor_identidad_genero"))
        binding.includeAgresor.orientacionSexualAgresorEdit.setText(json.optString("agresor_orientacion_sexual"))
        binding.includeAgresor.estamentoAgresorEdit.setText(json.optString("agresor_estamento"))
        binding.includeAgresor.viceAgresorEdit.setText(json.optString("agresor_vicerrectoria_adscrito"))
        binding.includeAgresor.dependenciaAgresorEdit.setText(json.optString("agresor_dependencia"))
        binding.includeAgresor.programaAgresorEdit.setText(json.optString("agresor_programa_academico"))
        binding.includeAgresor.facultadAgresorEdit.setText(json.optString("agresor_facultad"))
        binding.includeAgresor.sedeAgresorEdit.setText(json.optString("agresor_sede"))

        // Adicionales
        binding.includeAdicionales.rutaIntegralEdit.setText(json.optString("desea_activar_ruta_atencion_integral"))
        binding.includeAdicionales.asesoriaEdit.setText(json.optString("recibir_asesoria_orientacion_sociopedagogica"))
        binding.includeAdicionales.orientacionEdit.setText(json.optString("orientacion_psicologica"))
        binding.includeAdicionales.asistenciaJuridicaEdit.setText(json.optString("asistencia_juridica"))
        binding.includeAdicionales.medidasProteccionEdit.setText(json.optString("acompañamiento_solicitud_medidas_proteccion_inicial"))
        binding.includeAdicionales.instanciasGubernamentalesEdit.setText(json.optString("acompañamiento_ante_instancias_gubernamentales"))
        binding.includeAdicionales.comiteAsuntosInternosEdit.setText(json.optString("interponer_queja_al_comite_asusntos_internos_disciplinarios"))
        binding.includeAdicionales.observacionesEdit.setText(json.optString("observaciones"))
    }

    private fun sendEdit(json: JSONObject) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val resp = makeRequest(
                    "${APIConstant.BACKEND_URL}api/quejas/${data.optString("id")}/",
                    "PUT",
                    PrefsHelper.getDRFToken(requireContext()) ?: "",
                    json
                )

                withContext(Dispatchers.Main) {
                    if (resp == "error") {
                        Toast.makeText(requireContext(), "Error al editar la queja", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Queja editada", Toast.LENGTH_SHORT).show()
                        data = JSONObject(resp)
                        setFieldsFromJSON(data)
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
