package com.example.appvbg.ui.quejas.detalles

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.example.appvbg.R
import com.example.appvbg.databinding.FragmentDetallesQuejaBinding
import org.json.JSONObject


class DetallesQueja : Fragment(R.layout.fragment_detalles_queja) {
    private val args: DetallesQuejaArgs by navArgs()
    private var _binding: FragmentDetallesQuejaBinding? = null
    private val binding get() = _binding!!
    private var editMode = false

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
        setFieldsFromJSON(quejaJson)

        // Ya puedes acceder a todos los elementos del layout, incluidos los de los includes
        binding.editButton.setOnClickListener {
            if (editMode) {
                val json=buildJSON();
                //se envía
                //se recarga
            }
            editMode=!editMode
            binding.editButton.text = if (editMode) "Guardar" else "Editar"
            setEditMode(editMode)
        }
        ///funcion fetch
        //val newData=fetchData()
        ////funcion para recargar
        //setFieldsFromJSON(newData)

    }

        override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }






    private fun setEditMode(enabled: Boolean) {
        val visibility = if (enabled) View.VISIBLE else View.GONE
        val enabledAndVisible = { et: EditText ->
            et.isEnabled = enabled
            et.visibility = visibility
        }

        fun applyTo(container: ViewGroup) {
            for (i in 0 until container.childCount) {
                val child = container.getChildAt(i)
                when (child) {
                    is EditText -> enabledAndVisible(child)
                    is ViewGroup -> applyTo(child)
                }
            }
        }
        applyTo(binding.includeReporta.root as ViewGroup)
        applyTo(binding.includeAfectada.root as ViewGroup)
        applyTo(binding.includeAgresor.root as ViewGroup)
        applyTo(binding.includeAdicionales.root as ViewGroup)
        fun setLabelTextViewsVisibility(container: ViewGroup, visible: Boolean) {
            val visibility = if (visible) View.VISIBLE else View.GONE
            for (i in 0 until container.childCount) {
                val child = container.getChildAt(i)
                if (child is TextView && child !is EditText) {
                    val resId = child.id
                    if (resId != View.NO_ID) {
                        val name = try {
                            resources.getResourceEntryName(resId)
                        } catch (e: Exception) {
                            null
                        }
                        if (name != null && name.endsWith("Label", ignoreCase = true)) {
                            child.visibility = visibility
                        }
                    }
                } else if (child is ViewGroup) {
                    setLabelTextViewsVisibility(child, visible) // recursividad
                }
            }
        }
        setLabelTextViewsVisibility(binding.includeReporta.root as ViewGroup, !enabled)
        setLabelTextViewsVisibility(binding.includeAfectada.root as ViewGroup, !enabled)
        setLabelTextViewsVisibility(binding.includeAgresor.root as ViewGroup, !enabled)
        setLabelTextViewsVisibility(binding.includeAdicionales.root as ViewGroup, !enabled)
    }
    private fun fetchData(): JSONObject{
        val newJSON=JSONObject()
        //fetch y demás cosas
        return newJSON
    }

    private fun buildJSON(): JSONObject {
        val json = JSONObject()
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
        //falta detalles
        //21
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


        json.put("desea_activar_ruta_atencion_integral", binding.includeAdicionales.rutaIntegralEdit.text.toString())
        json.put("recibir_asesoria_orientacion_sociopedagogica", binding.includeAdicionales.asesoriaEdit.text.toString())
        json.put("orientacion_psicologica", binding.includeAdicionales.orientacionEdit.text.toString())
        json.put("asistencia_juridica", binding.includeAdicionales.asistenciaJuridicaEdit.text.toString())
        json.put("acompañamiento_solicitud_medidas_proteccion_inicial", binding.includeAdicionales.medidasProteccionEdit.text.toString())
        json.put("acompañamiento_ante_instancias_gubernamentales", binding.includeAdicionales.instanciasGubernamentalesEdit.text.toString())
        json.put("interponer_queja_al_comite_asusntos_internos_disciplinarios", binding.includeAdicionales.comiteAsuntosInternosEdit.text.toString())
        json.put("observaciones", binding.includeAdicionales.observacionesEdit.text.toString())
        
        return json;
    }


    private fun setFieldsFromJSON(json: JSONObject) {
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
}
