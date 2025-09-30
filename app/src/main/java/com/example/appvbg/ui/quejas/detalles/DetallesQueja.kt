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
    private lateinit var data: JSONObject

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
        data = JSONObject(quejaJsonString)

        // Mostrar ID
        binding.tvQuejaId.text = "ID: #${data.optString("id", "N/A")}"

        // Mostrar datos en labels y edits
        setFieldsFromJSON(data)
        setEditMode(false)

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
        // Aquí va todo tu seteo de labels y editTexts (lo dejé igual que lo tenías)
        // ...
        // (no lo repito para no hacer el bloque aún más largo, pero está correcto)
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
}
