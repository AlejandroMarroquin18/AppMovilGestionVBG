package com.example.appvbg.ui.agenda.crear_queja

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.appvbg.R
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope

import com.example.appvbg.databinding.FragmentCrearQuejaBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.Calendar

class CrearQueja : Fragment(R.layout.fragment_crear_queja) {
    private var _binding: FragmentCrearQuejaBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding= FragmentCrearQuejaBinding.inflate(inflater, container, false)


        val sino = listOf("Si", "No")
        val sexo= listOf("Femenino","Masculino")
        val estamento= listOf("Estudiante","Docente","Funcionario",
            "Externo","Usuario de las instalaciones")
        val identidad_genero_opt= listOf("Cisgénero","Transgénero",
            "No binario", "Género fluido", "Otro"
        )
        val orientacion_sexual_opt= listOf("Heterosexual","Homosexual",
            "Bisexual","Pansexual","Asexual","Queer","Demisexual",
            "Otro"
        )
        val tipoVBG_opt= listOf("Economica","Sexual","Fisica")
        val condicion_etnica= listOf("Indígena","Negro(a)","Mulato")
        val facultades = listOf("Artes Integradas",
            "Ciencias Naturales y Exactas",
            "Ciencias de la Administración",
            "Salud",
            "Ciencias Sociales y Económicas",
            "Humanidades",
            "Ingeniería",
            "Educación y Pedagogía",
            "Psicología",
            "Derecho y Ciencia Política")
        val sedes= listOf("Melendez","San Fernando","Buga",
            "Caicedonia","Cartago","Norte del Cauca",
            "Pacífico","Pacífico","Palmira","Tuluá",
            "Yumbo","Zazal",)

        binding.includeReporta.sexoReportaEdit.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sexo)
        //binding.includeReporta.etniaReportaEdit.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, condicion_etnica)
        binding.includeReporta.estamentoReportaEdit.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, estamento)

        binding.includeAfectada.sedeAfectadaEdit.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sedes)
        binding.includeAfectada.facultadAfectadaEdit.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, facultades)
        binding.includeAfectada.sexoAfectadaEdit.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sexo)
        binding.includeAfectada.etniaAfectadaEdit.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, condicion_etnica)
        binding.includeAfectada.orientacionSexualAfectadaEdit.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, orientacion_sexual_opt)
        binding.includeAfectada.identidadGeneroAfectadaEdit.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, identidad_genero_opt)
        binding.includeAfectada.estamentoAfectadaEdit.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, estamento)
        binding.includeAfectada.discapacidadAfectadaEdit.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sino)
        binding.includeAfectada.tipoVBGAfectadaEdit.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, tipoVBG_opt)
        binding.includeReporta.facultadReportaEdit.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, facultades)
        binding.includeReporta.sedeReportaEdit.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sedes)



        binding.includeAgresor.sexoAgresorEdit.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sexo)
        binding.includeAgresor.etniaAgresorEdit.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, condicion_etnica)
        binding.includeAgresor.orientacionSexualAgresorEdit.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, orientacion_sexual_opt)
        binding.includeAgresor.identidadGeneroAgresorEdit.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, identidad_genero_opt)
        binding.includeAgresor.estamentoAgresorEdit.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, estamento)
        binding.includeAgresor.discapacidadAgresorEdit.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sino)
        binding.includeAgresor.facultadAgresorEdit.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, facultades)
        binding.includeAgresor.sedeAgresorEdit.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sedes)





        binding.includeAdicional.rutaIntegralEdit.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sino)
        binding.includeAdicional.asesoriaEdit.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sino)
        binding.includeAdicional.orientacionEdit.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sino)
        binding.includeAdicional.asistenciaJuridicaEdit.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sino)
        binding.includeAdicional.medidasProteccionEdit.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sino)
        binding.includeAdicional.instanciasGubernamentalesEdit.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sino)
        binding.includeAdicional.comiteAsuntosInternosEdit.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sino)
        //binding.includeAdicional.observacionesEdit.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, sino)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCrear.setOnClickListener {
            val jsonData = buildJSON()
            lifecycleScope.launch {
                val respuesta = withContext(Dispatchers.IO) {
                    enviarQuejaJson("http://192.168.0.32:8000/api/quejas/", jsonData)
                }
                //Toast.makeText(requireContext(), respuesta, Toast.LENGTH_LONG).show()
                //clearFields()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    
    private fun buildJSON(): JSONObject {
        val json = JSONObject()

        val calendar = Calendar.getInstance()
        val dia = calendar.get(Calendar.DAY_OF_MONTH)
        val mes = calendar.get(Calendar.MONTH) + 1 // Se suma 1 porque enero es 0
        val anio = calendar.get(Calendar.YEAR)

        val fechaFormateada = "$dia/$mes/$anio"

        json.put("fecha_recepcion", fechaFormateada)
        json.put("reporta_nombre", binding.includeReporta.nombreReportaEdit.text.toString())
        json.put("reporta_sexo", binding.includeReporta.sexoReportaEdit.selectedItem as String)
        json.put("reporta_edad", binding.includeReporta.edadReportaEdit.text.toString())
        json.put("reporta_estamento", binding.includeReporta.estamentoReportaEdit.selectedItem as String)
        json.put("reporta_vicerrectoria_adscrito", binding.includeReporta.viceReportaEdit.text.toString())
        json.put("reporta_dependencia", binding.includeReporta.dependenciaReportaEdit.text.toString())
        json.put("reporta_programa_academico", binding.includeReporta.programaReportaEdit.text.toString())
        json.put("reporta_facultad", binding.includeReporta.facultadReportaEdit.selectedItem as String)
        json.put("reporta_sede", binding.includeReporta.sedeReportaEdit.selectedItem as String)
        json.put("reporta_celular", binding.includeReporta.celularReportaEdit.text.toString())
        json.put("reporta_correo", binding.includeReporta.correoReportaEdit.text.toString())

        json.put("afectado_nombre", binding.includeAfectada.nombreAfectadaEdit.text.toString())
        json.put("afectado_sexo", binding.includeAfectada.sexoAfectadaEdit.selectedItem as String)
        json.put("afectado_edad", binding.includeAfectada.edadAfectadaEdit.text.toString())
        json.put("afectado_comuna", binding.includeAfectada.comunaAfectadaEdit.text.toString())
        json.put("afectado_estrato_socioeconomico", binding.includeAfectada.estratoAfectadaEdit.text.toString())
        json.put("afectado_condicion_etnico_racial", binding.includeAfectada.etniaAfectadaEdit.selectedItem as String)
        json.put("afectado_tiene_discapacidad", binding.includeAfectada.discapacidadAfectadaEdit.selectedItem as String)
        json.put("afectado_tipo_discapacidad", binding.includeAfectada.tipoDiscapacidadAfectadaEdit.text.toString())
        json.put("afectado_identidad_genero", binding.includeAfectada.identidadGeneroAfectadaEdit.selectedItem as String)
        json.put("afectado_orientacion_sexual", binding.includeAfectada.orientacionSexualAfectadaEdit.selectedItem as String)
        json.put("afectado_estamento", binding.includeAfectada.estamentoAfectadaEdit.selectedItem as String)
        json.put("afectado_vicerrectoria_adscrito", binding.includeAfectada.viceAfectadaEdit.text.toString())
        json.put("afectado_dependencia", binding.includeAfectada.dependenciaAfectadaEdit.text.toString())
        json.put("afectado_programa_academico", binding.includeAfectada.programaAfectadaEdit.text.toString())

        json.put("afectado_facultad", binding.includeAfectada.facultadAfectadaEdit.selectedItem as String)

        json.put("afectado_sede", binding.includeAfectada.sedeAfectadaEdit.selectedItem as String)
        json.put("afectado_celular", binding.includeAfectada.celularAfectadaEdit.text.toString())
        json.put("afectado_correo", binding.includeAfectada.correoAfectadaEdit.text.toString())
        json.put("afectado_tipo_vbg_os", binding.includeAfectada.tipoVBGAfectadaEdit.selectedItem as String)
        //falta detalles
        //21
        json.put("agresor_nombre", binding.includeAgresor.nombreAgresorEdit.text.toString())
        json.put("agresor_sexo", binding.includeAgresor.sexoAgresorEdit.selectedItem as String)
        json.put("agresor_edad", binding.includeAgresor.edadAgresorEdit.text.toString())
        json.put("agresor_condicion_etnico_racial", binding.includeAgresor.etniaAgresorEdit.selectedItem as String)
        json.put("agresor_tiene_discapacidad", binding.includeAgresor.discapacidadAgresorEdit.selectedItem as String)
        json.put("agresor_tipo_discapacidad", binding.includeAgresor.tipoDiscapacidadAgresorEdit.text.toString())
        json.put("agresor_identidad_genero", binding.includeAgresor.identidadGeneroAgresorEdit.selectedItem as String)
        json.put("agresor_orientacion_sexual", binding.includeAgresor.orientacionSexualAgresorEdit.selectedItem as String)
        json.put("agresor_estamento", binding.includeAgresor.estamentoAgresorEdit.selectedItem as String)
        json.put("agresor_vicerrectoria_adscrito", binding.includeAgresor.viceAgresorEdit.text.toString())
        json.put("agresor_dependencia", binding.includeAgresor.dependenciaAgresorEdit.text.toString())
        json.put("agresor_programa_academico", binding.includeAgresor.programaAgresorEdit.text.toString())
        json.put("agresor_facultad", binding.includeAgresor.facultadAgresorEdit.selectedItem as String)
        json.put("agresor_sede", binding.includeAgresor.sedeAgresorEdit.selectedItem as String)


        json.put("desea_activar_ruta_atencion_integral", binding.includeAdicional.rutaIntegralEdit.selectedItem as String)
        json.put("recibir_asesoria_orientacion_sociopedagogica", binding.includeAdicional.asesoriaEdit.selectedItem as String)
        json.put("orientacion_psicologica", binding.includeAdicional.orientacionEdit.selectedItem as String)
        json.put("asistencia_juridica", binding.includeAdicional.asistenciaJuridicaEdit.selectedItem as String)
        json.put("acompañamiento_solicitud_medidas_proteccion_inicial", binding.includeAdicional.medidasProteccionEdit.selectedItem as String)
        json.put("acompañamiento_ante_instancias_gubernamentales", binding.includeAdicional.instanciasGubernamentalesEdit.selectedItem as String)
        json.put("interponer_queja_al_comite_asusntos_internos_disciplinarios", binding.includeAdicional.comiteAsuntosInternosEdit.selectedItem as String)
        json.put("observaciones", binding.includeAdicional.observacionesEdit.text.toString())


        //relleno
        json.put("nombre","1")
        json.put("sede","2")
        json.put("codigo","3")
        json.put("tipo_de_acompanamiento","4")
        json.put("fecha","3")
        json.put("estado","1")
        json.put("detalles","no se")
        json.put("facultad", "5")
        json.put("unidad", "6")




        return json;
    }
    fun enviarQuejaJson(apiUrl: String, jsonData: JSONObject): String {
        try {
            val url = URL(apiUrl)
            val conn = url.openConnection() as HttpURLConnection

            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; utf-8")
            conn.setRequestProperty("Accept", "application/json")
            conn.doOutput = true
            conn.doInput = true

            // Escribir el JSON en el cuerpo de la solicitud
            val outputWriter = OutputStreamWriter(conn.outputStream, "UTF-8")
            outputWriter.write(jsonData.toString())
            outputWriter.flush()
            outputWriter.close()

            // Leer la respuesta
            val responseCode = conn.responseCode
            val inputStream = if (responseCode in 200..299) {
                conn.inputStream
            } else {
                conn.errorStream
            }

            val response = inputStream.bufferedReader().use { it.readText() }
            conn.disconnect()

            return response
        } catch (e: Exception) {
            e.printStackTrace()
            return "Error: ${e.message}"
        }
    }




}