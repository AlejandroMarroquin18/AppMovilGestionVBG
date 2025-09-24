package com.example.appvbg.ui.talleres.detalles

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
import com.example.appvbg.api.CalendarApi
import com.example.appvbg.api.PrefsHelper
import com.example.appvbg.api.makeRequest
import com.example.appvbg.databinding.FragmentDetallesTalleresBinding
import com.example.appvbg.ui.agenda.detalles.DetallesAgendaDirections
import com.example.appvbg.ui.quejas.detalles.DetallesQuejaDirections
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject


class DetallesTalleres : Fragment() {
    private val args: DetallesTalleresArgs by navArgs()
    private var _binding: FragmentDetallesTalleresBinding? = null
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
        _binding = FragmentDetallesTalleresBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Aquí se hace el fetch al taller (?
        val tallerJsonString = args.tallerJSON
        data = JSONObject(tallerJsonString)
        setFieldsFromJSON(data)
        // Ya puedes acceder a todos los elementos del layout, incluidos los de los includes

        binding.cancelButton.setOnClickListener {
            editMode = false
            setEditMode(editMode)
            binding.cancelButton.visibility = View.GONE
        }
        binding.editButton.setOnClickListener {
            if (editMode) {
                sendEdit(buildJSON());
            }
            editMode = !editMode
            binding.editButton.text = if (editMode) "Guardar" else "Editar"
            binding.cancelButton.visibility = if (editMode) View.VISIBLE else View.GONE
            setEditMode(editMode)
        }

        binding.deleteButton.setOnClickListener {
            sendDelete();
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
    private fun setEditMode(enabled: Boolean) {
        val isEnabledToHide= if (enabled) View.GONE else View.VISIBLE
        val isEnabledToShow= if (enabled) View.VISIBLE else View.GONE
        
        binding.nombreLabel.setVisibility(isEnabledToHide)
        binding.fechaLabel.setVisibility(isEnabledToHide)
        binding.horaInicioLabel.setVisibility(isEnabledToHide)
        binding.horaFinLabel.setVisibility(isEnabledToHide)
        binding.lugarLabel.setVisibility(isEnabledToHide)
        binding.modalidadLabel.setVisibility(isEnabledToHide)
        binding.beneficiariosLabel.setVisibility(isEnabledToHide)
        binding.talleristasLabel.setVisibility(isEnabledToHide)
        binding.descripcionLabel.setVisibility(isEnabledToHide)


        binding.nombreEdit.setVisibility(isEnabledToShow)
        binding.fechaEdit.setVisibility(isEnabledToShow)
        binding.horaInicioEdit.setVisibility(isEnabledToShow)
        binding.horaFinEdit.setVisibility(isEnabledToShow)
        binding.lugarEdit.setVisibility(isEnabledToShow)
        binding.modalidadEdit.setVisibility(isEnabledToShow)
        binding.beneficiariosEdit.setVisibility(isEnabledToShow)
        binding.talleristasEdit.setVisibility(isEnabledToShow)
        binding.descripcionEdit.setVisibility(isEnabledToShow)


    }

    private fun setFieldsFromJSON(json: JSONObject) {
        binding.nombreLabel.setText(json.optString("name"))
        binding.fechaLabel.setText(json.optString("date"))
        binding.horaInicioLabel.setText(json.optString("start_time"))
        binding.horaFinLabel.setText(json.optString("end_time"))
        binding.lugarLabel.setText(json.optString("location"))
        binding.modalidadLabel.setText(json.optString("modality"))
        binding.beneficiariosLabel.setText(json.optString("slots"))
        binding.talleristasLabel.setText(json.optString("facilitators"))
        binding.descripcionLabel.setText(json.optString("details"))


        binding.nombreEdit.setText(json.optString("name"))
        binding.fechaEdit.setText(json.optString("date"))
        binding.horaInicioEdit.setText(json.optString("start_time"))
        binding.horaFinEdit.setText(json.optString("end_time"))
        binding.lugarEdit.setText(json.optString("location"))
        binding.modalidadEdit.setText(json.optString("modality"))
        binding.beneficiariosEdit.setText(json.optString("slots"))
        binding.talleristasEdit.setText(json.optString("facilitators"))
        binding.descripcionEdit.setText(json.optString("details"))

    }

    private fun buildJSON(): JSONObject {
        val json = JSONObject()

        if (binding.nombreEdit.text.isNotBlank())
            json.put("name", binding.nombreEdit.text.toString())

        if (binding.fechaEdit.text.isNotBlank())
            json.put("date", binding.fechaEdit.text.toString())

        if (binding.horaInicioEdit.text.isNotBlank())
            json.put("start_time", binding.horaInicioEdit.text.toString())

        if (binding.horaFinEdit.text.isNotBlank())
            json.put("end_time", binding.horaFinEdit.text.toString())

        if (binding.lugarEdit.text.isNotBlank())
            json.put("location", binding.lugarEdit.text.toString())

        if (binding.modalidadEdit.text.isNotBlank())
            json.put("modality", binding.modalidadEdit.text.toString())

        if (binding.beneficiariosEdit.text.isNotBlank())
            json.put("slots", binding.beneficiariosEdit.text.toString().toInt()) // cuidado con int

        if (binding.talleristasEdit.text.isNotBlank()) {
            //todo Falta manejar los facilitators, la verdad no entendi como van

        }
        if (binding.descripcionEdit.text.isNotBlank())
            json.put("details", binding.descripcionEdit.text.toString())

        return json
    }

    //private fun buildJSON(): JSONObject {}

    private fun sendEdit(json: JSONObject){
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val resp = makeRequest(
                    """${APIConstant.BACKEND_URL}api/talleres/${data.optString("id")}/""",
                    "PATCH",
                    PrefsHelper.getDRFToken(requireContext()) ?: "",
                    json
                )
                withContext(Dispatchers.Main) {
                    if (resp == "error") {

                            Toast.makeText(
                                requireContext(),
                                "Error al editar la queja",
                                Toast.LENGTH_SHORT
                            )
                                .show()

                    } else {

                        Toast.makeText(requireContext(), "Queja editada", Toast.LENGTH_SHORT).show()
                        setFieldsFromJSON(JSONObject(resp))
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
                    """${APIConstant.BACKEND_URL}api/talleres/${data.optString("id")}/""",
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
                    val action = DetallesTalleresDirections.actionDetallesTallerToVerTalleres()
                    findNavController().navigate(action)
                }
            }catch (e: Exception){
                Log.e("Error", e.toString())
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Error al eliminar la queja",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }


}