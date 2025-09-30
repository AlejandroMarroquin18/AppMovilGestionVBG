package com.example.appvbg.ui.quejas.detalles

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.appvbg.APIConstant
import com.example.appvbg.R
import com.example.appvbg.api.PrefsHelper
import com.example.appvbg.api.makeRequest
import com.example.appvbg.databinding.FragmentDetallesQuejaBinding
import org.json.JSONObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetallesQueja : Fragment(R.layout.fragment_detalles_queja) {
    private val args: DetallesQuejaArgs by navArgs()
    private var _binding: FragmentDetallesQuejaBinding? = null
    private val binding get() = _binding!!
    private var editMode = false
    private var data= JSONObject();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetallesQuejaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val quejaJsonString = args.quejaJSON
        val quejaJson = JSONObject(quejaJsonString)

        // Mostrar ID de la queja
        binding.tvQuejaId.text = "ID: #${quejaJson.optString("id", "N/A")}"

        setFieldsFromJSON(quejaJson)
        setEditMode(false) // Iniciar en modo visualización
        data = JSONObject(quejaJsonString)
        setFieldsFromJSON(data)

        // Mostrar ID de la queja
        binding.tvQuejaId.text = "ID: #${data.optString("id", "N/A")}"


        setEditMode(false) // Iniciar en modo visualización

        binding.editButton.setOnClickListener {
            if (editMode) {
                val json = buildJSON()
                // Aquí iría el código para enviar los datos
                Toast.makeText(requireContext(), "Datos guardados", Toast.LENGTH_SHORT).show()
                // Después de guardar, volver a modo visualización
                editMode = false
                binding.editButton.text = "Editar"
                setEditMode(false)
            } else {
                editMode = true
                binding.editButton.text = "Guardar"
                setEditMode(true)
            }
        }



        binding.cancelButton.setOnClickListener {
            editMode = false
            setEditMode(editMode)
            binding.cancelButton.visibility = View.GONE
        }
        binding.editButton.setOnClickListener {
            if (editMode) {
                val newJSON=buildJSON();
                sendEdit(newJSON);

            }
            editMode = !editMode
            binding.editButton.text = if (editMode) "Guardar" else "Editar"
            binding.cancelButton.visibility = if (editMode) View.VISIBLE else View.GONE
            setEditMode(editMode)
        }

        binding.deleteButton.setOnClickListener {
            sendDelete();

        }
        binding.btnImprimir.setOnClickListener {
            Toast.makeText(requireContext(), "Funcionalidad de impresión", Toast.LENGTH_SHORT).show()
            // Aquí puedes implementar la funcionalidad de impresión/exportación
        }




        binding.btnImprimir.setOnClickListener {
            Toast.makeText(requireContext(), "Funcionalidad de impresión", Toast.LENGTH_SHORT).show()
            // Aquí puedes implementar la funcionalidad de impresión/exportación
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setEditMode(enabled: Boolean) {
        val visibility = if (enabled) View.VISIBLE else View.GONE
        val labelVisibility = if (enabled) View.GONE else View.VISIBLE
        val labelVisibility = if (enabled) View.GONE else View.VISIBLE
        val enabledAndVisible = { et: EditText ->
            et.isEnabled = enabled
            et.visibility = visibility
        }

        // Función recursiva para aplicar a todos los elementos
        fun applyToViewGroup(container: ViewGroup) {
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
                            } catch (e: Exception) {
                                // Ignorar si no se puede obtener el nombre del recurso
                            }
                        }
                    }
                    is ViewGroup -> applyToViewGroup(child)
                }
            }
        }

        // Aplicar a todos los includes
        applyToViewGroup(binding.includeReporta.root)
        applyToViewGroup(binding.includeAfectada.root)
        applyToViewGroup(binding.includeAgresor.root)
        applyToViewGroup(binding.includeAdicionales.root)
    }

    private fun fetchData(): JSONObject {
        val newJSON = JSONObject()
        // Implementar lógica para fetch de datos si es necesario
        return newJSON
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
                            } catch (e: Exception) {
                                // Ignorar si no se puede obtener el nombre del recurso
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
        Toast.makeText(requireContext(),json.optString("afectado_nombre"),Toast.LENGTH_SHORT).show()
        return json;
    }

    private fun setFieldsFromJSON(json: JSONObject) {
        // Reporta - Labels
        binding.includeReporta.fechaReportaLabel.text = json.optString("fecha_recepcion", "No especificado")
        binding.includeReporta.nombreReportaLabel.text = json.optString("reporta_nombre", "No especificado")
        binding.includeReporta.sexoReportaLabel.text = json.optString("reporta_sexo", "No especificado")
        binding.includeReporta.edadReportaLabel.text = json.optString("reporta_edad", "No especificado")
        binding.includeReporta.estamentoReportaLabel.text = json.optString("reporta_estamento", "No especificado")
        binding.includeReporta.viceReportaLabel.text = json.optString("reporta_vicerrectoria_adscrito", "No especificado")
        binding.includeReporta.dependenciaReportaLabel.text = json.optString("reporta_dependencia", "No especificado")
        binding.includeReporta.programaReportaLabel.text = json.optString("reporta_programa_academico", "No especificado")
        binding.includeReporta.facultadReportaLabel.text = json.optString("reporta_facultad", "No especificado")
        binding.includeReporta.sedeReportaLabel.text = json.optString("reporta_sede", "No especificado")
        binding.includeReporta.celularReportaLabel.text = json.optString("reporta_celular", "No especificado")
        binding.includeReporta.correoReportaLabel.text = json.optString("reporta_correo", "No especificado")

        // Reporta - EditTexts
        binding.includeReporta.fechaReportaEdit.setText(json.optString("fecha_recepcion", ""))
        binding.includeReporta.nombreReportaEdit.setText(json.optString("reporta_nombre", ""))
        binding.includeReporta.sexoReportaEdit.setText(json.optString("reporta_sexo", ""))
        binding.includeReporta.edadReportaEdit.setText(json.optString("reporta_edad", ""))
        binding.includeReporta.estamentoReportaEdit.setText(json.optString("reporta_estamento", ""))
        binding.includeReporta.viceReportaEdit.setText(json.optString("reporta_vicerrectoria_adscrito", ""))
        binding.includeReporta.dependenciaReportaEdit.setText(json.optString("reporta_dependencia", ""))
        binding.includeReporta.programaReportaEdit.setText(json.optString("reporta_programa_academico", ""))
        binding.includeReporta.facultadReportaEdit.setText(json.optString("reporta_facultad", ""))
        binding.includeReporta.sedeReportaEdit.setText(json.optString("reporta_sede", ""))
        binding.includeReporta.celularReportaEdit.setText(json.optString("reporta_celular", ""))
        binding.includeReporta.correoReportaEdit.setText(json.optString("reporta_correo", ""))

        // Afectada - Labels
        binding.includeAfectada.nombreAfectadaLabel.text = json.optString("afectado_nombre", "No especificado")
        binding.includeAfectada.sexoAfectadaLabel.text = json.optString("afectado_sexo", "No especificado")
        binding.includeAfectada.edadAfectadaLabel.text = json.optString("afectado_edad", "No especificado")
        binding.includeAfectada.comunaAfectadaLabel.text = json.optString("afectado_comuna", "No especificado")
        binding.includeAfectada.estratoAfectadaLabel.text = json.optString("afectado_estrato_socioeconomico", "No especificado")
        binding.includeAfectada.etniaAfectadaLabel.text = json.optString("afectado_condicion_etnico_racial", "No especificado")
        binding.includeAfectada.discapacidadAfectadaLabel.text = json.optString("afectado_tiene_discapacidad", "No especificado")
        binding.includeAfectada.tipoDiscapacidadAfectadaLabel.text = json.optString("afectado_tipo_discapacidad", "No especificado")
        binding.includeAfectada.identidadGeneroAfectadaLabel.text = json.optString("afectado_identidad_genero", "No especificado")
        binding.includeAfectada.orientacionSexualAfectadaLabel.text = json.optString("afectado_orientacion_sexual", "No especificado")
        binding.includeAfectada.estamentoAfectadaLabel.text = json.optString("afectado_estamento", "No especificado")
        binding.includeAfectada.viceAfectadaLabel.text = json.optString("afectado_vicerrectoria_adscrito", "No especificado")
        binding.includeAfectada.dependenciaAfectadaLabel.text = json.optString("afectado_dependencia", "No especificado")
        binding.includeAfectada.programaAfectadaLabel.text = json.optString("afectado_programa_academico", "No especificado")
        binding.includeAfectada.facultadAfectadaLabel.text = json.optString("afectado_facultad", "No especificado")
        binding.includeAfectada.sedeAfectadaLabel.text = json.optString("afectado_sede", "No especificado")
        binding.includeAfectada.celularAfectadaLabel.text = json.optString("afectado_celular", "No especificado")
        binding.includeAfectada.correoAfectadaLabel.text = json.optString("afectado_correo", "No especificado")
        binding.includeAfectada.tipoVBGAfectadaLabel.text = json.optString("afectado_tipo_vbg_os", "No especificado")

        // Afectada - EditTexts
        binding.includeAfectada.nombreAfectadaEdit.setText(json.optString("afectado_nombre", ""))
        binding.includeAfectada.sexoAfectadaEdit.setText(json.optString("afectado_sexo", ""))
        binding.includeAfectada.edadAfectadaEdit.setText(json.optString("afectado_edad", ""))
        binding.includeAfectada.comunaAfectadaEdit.setText(json.optString("afectado_comuna", ""))
        binding.includeAfectada.estratoAfectadaEdit.setText(json.optString("afectado_estrato_socioeconomico", ""))
        binding.includeAfectada.etniaAfectadaEdit.setText(json.optString("afectado_condicion_etnico_racial", ""))
        binding.includeAfectada.discapacidadAfectadaEdit.setText(json.optString("afectado_tiene_discapacidad", ""))
        binding.includeAfectada.tipoDiscapacidadAfectadaEdit.setText(json.optString("afectado_tipo_discapacidad", ""))
        binding.includeAfectada.identidadGeneroAfectadaEdit.setText(json.optString("afectado_identidad_genero", ""))
        binding.includeAfectada.orientacionSexualAfectadaEdit.setText(json.optString("afectado_orientacion_sexual", ""))
        binding.includeAfectada.estamentoAfectadaEdit.setText(json.optString("afectado_estamento", ""))
        binding.includeAfectada.viceAfectadaEdit.setText(json.optString("afectado_vicerrectoria_adscrito", ""))
        binding.includeAfectada.dependenciaAfectadaEdit.setText(json.optString("afectado_dependencia", ""))
        binding.includeAfectada.programaAfectadaEdit.setText(json.optString("afectado_programa_academico", ""))
        binding.includeAfectada.facultadAfectadaEdit.setText(json.optString("afectado_facultad", ""))
        binding.includeAfectada.sedeAfectadaEdit.setText(json.optString("afectado_sede", ""))
        binding.includeAfectada.celularAfectadaEdit.setText(json.optString("afectado_celular", ""))
        binding.includeAfectada.correoAfectadaEdit.setText(json.optString("afectado_correo", ""))
        binding.includeAfectada.tipoVBGAfectadaEdit.setText(json.optString("afectado_tipo_vbg_os", ""))

        // Agresor - Labels
        binding.includeAgresor.nombreAgresorLabel.text = json.optString("agresor_nombre", "No especificado")
        binding.includeAgresor.sexoAgresorLabel.text = json.optString("agresor_sexo", "No especificado")
        binding.includeAgresor.edadAgresorLabel.text = json.optString("agresor_edad", "No especificado")
        binding.includeAgresor.etniaAgresorLabel.text = json.optString("agresor_condicion_etnico_racial", "No especificado")
        binding.includeAgresor.discapacidadAgresorLabel.text = json.optString("agresor_tiene_discapacidad", "No especificado")
        binding.includeAgresor.tipoDiscapacidadAgresorLabel.text = json.optString("agresor_tipo_discapacidad", "No especificado")
        binding.includeAgresor.identidadGeneroAgresorLabel.text = json.optString("agresor_identidad_genero", "No especificado")
        binding.includeAgresor.orientacionSexualAgresorLabel.text = json.optString("agresor_orientacion_sexual", "No especificado")
        binding.includeAgresor.estamentoAgresorLabel.text = json.optString("agresor_estamento", "No especificado")
        binding.includeAgresor.viceAgresorLabel.text = json.optString("agresor_vicerrectoria_adscrito", "No especificado")
        binding.includeAgresor.dependenciaAgresorLabel.text = json.optString("agresor_dependencia", "No especificado")
        binding.includeAgresor.programaAgresorLabel.text = json.optString("agresor_programa_academico", "No especificado")
        binding.includeAgresor.facultadAgresorLabel.text = json.optString("agresor_facultad", "No especificado")
        binding.includeAgresor.sedeAgresorLabel.text = json.optString("agresor_sede", "No especificado")

        // Agresor - EditTexts
        binding.includeAgresor.nombreAgresorEdit.setText(json.optString("agresor_nombre", ""))
        binding.includeAgresor.sexoAgresorEdit.setText(json.optString("agresor_sexo", ""))
        binding.includeAgresor.edadAgresorEdit.setText(json.optString("agresor_edad", ""))
        binding.includeAgresor.etniaAgresorEdit.setText(json.optString("agresor_condicion_etnico_racial", ""))
        binding.includeAgresor.discapacidadAgresorEdit.setText(json.optString("agresor_tiene_discapacidad", ""))
        binding.includeAgresor.tipoDiscapacidadAgresorEdit.setText(json.optString("agresor_tipo_discapacidad", ""))
        binding.includeAgresor.identidadGeneroAgresorEdit.setText(json.optString("agresor_identidad_genero", ""))
        binding.includeAgresor.orientacionSexualAgresorEdit.setText(json.optString("agresor_orientacion_sexual", ""))
        binding.includeAgresor.estamentoAgresorEdit.setText(json.optString("agresor_estamento", ""))
        binding.includeAgresor.viceAgresorEdit.setText(json.optString("agresor_vicerrectoria_adscrito", ""))
        binding.includeAgresor.dependenciaAgresorEdit.setText(json.optString("agresor_dependencia", ""))
        binding.includeAgresor.programaAgresorEdit.setText(json.optString("agresor_programa_academico", ""))
        binding.includeAgresor.facultadAgresorEdit.setText(json.optString("agresor_facultad", ""))
        binding.includeAgresor.sedeAgresorEdit.setText(json.optString("agresor_sede", ""))

        // Adicionales - Labels
        binding.includeAdicionales.rutaIntegralLabel.text = json.optString("desea_activar_ruta_atencion_integral", "No especificado")
        binding.includeAdicionales.asesoriaLabel.text = json.optString("recibir_asesoria_orientacion_sociopedagogica", "No especificado")
        binding.includeAdicionales.orientacionLabel.text = json.optString("orientacion_psicologica", "No especificado")
        binding.includeAdicionales.asistenciaJuridicaLabel.text = json.optString("asistencia_juridica", "No especificado")
        binding.includeAdicionales.medidasProteccionLabel.text = json.optString("acompañamiento_solicitud_medidas_proteccion_inicial", "No especificado")
        binding.includeAdicionales.instanciasGubernamentalesLabel.text = json.optString("acompañamiento_ante_instancias_gubernamentales", "No especificado")
        binding.includeAdicionales.comiteAsuntosInternosLabel.text = json.optString("interponer_queja_al_comite_asusntos_internos_disciplinarios", "No especificado")
        binding.includeAdicionales.observacionesLabel.text = json.optString("observaciones", "No especificado")

        // Adicionales - EditTexts
        binding.includeAdicionales.rutaIntegralEdit.setText(json.optString("desea_activar_ruta_atencion_integral", ""))
        binding.includeAdicionales.asesoriaEdit.setText(json.optString("recibir_asesoria_orientacion_sociopedagogica", ""))
        binding.includeAdicionales.orientacionEdit.setText(json.optString("orientacion_psicologica", ""))
        binding.includeAdicionales.asistenciaJuridicaEdit.setText(json.optString("asistencia_juridica", ""))
        binding.includeAdicionales.medidasProteccionEdit.setText(json.optString("acompañamiento_solicitud_medidas_proteccion_inicial", ""))
        binding.includeAdicionales.instanciasGubernamentalesEdit.setText(json.optString("acompañamiento_ante_instancias_gubernamentales", ""))
        binding.includeAdicionales.comiteAsuntosInternosEdit.setText(json.optString("interponer_queja_al_comite_asusntos_internos_disciplinarios", ""))
        binding.includeAdicionales.observacionesEdit.setText(json.optString("observaciones", ""))
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
                    """${APIConstant.BACKEND_URL}api/quejas/${data.optString("id")}/""",
                    "PUT",
                    PrefsHelper.getDRFToken(requireContext()) ?: "",
                    json
                )

                withContext(Dispatchers.Main) {
                    if (resp == "error") {
                        Toast.makeText(
                            requireContext(),
                            "Error al editar la queja",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Queja editada",
                            Toast.LENGTH_SHORT
                        ).show()
                        setFieldsFromJSON(JSONObject(resp)) // mover aquí, en Main
                    }
                }
            } catch (e: Exception) {
                Log.e("Error", e.toString())
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Error al editar la queja",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun sendDelete() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val resp = makeRequest(
                    """${APIConstant.BACKEND_URL}api/quejas/${data.optString("id")}/""",
                    "DELETE",
                    PrefsHelper.getDRFToken(requireContext()) ?: ""
                )
                if (resp == "error") {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Error al eliminar la queja", Toast.LENGTH_SHORT).show()
                    }
                }else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "Queja eliminada", Toast.LENGTH_SHORT).show()
                    }
                    val action = DetallesQuejaDirections.actionDetallesQuejaToQuejasFragment()
                    findNavController().navigate(action)
                }
            }catch (e: Exception){
                Log.e("Error", e.toString())
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error al eliminar la queja", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}
