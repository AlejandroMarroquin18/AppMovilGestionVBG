package com.example.appvbg.ui.talleres.detalles

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.navigation.fragment.navArgs
import com.example.appvbg.R
import com.example.appvbg.databinding.FragmentDetallesTalleresBinding
import org.json.JSONObject


class DetallesTalleres : Fragment() {
    private val args: DetallesTalleresArgs by navArgs()
    private var _binding: FragmentDetallesTalleresBinding? = null
    private val binding get() = _binding!!
    private var editMode = false

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
        val tallerJson = JSONObject(tallerJsonString)
        setFieldsFromJSON(tallerJson)
        // Ya puedes acceder a todos los elementos del layout, incluidos los de los includes

        binding.editButton.setOnClickListener {
            if (editMode) {
                //val json=buildJSON();
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
        binding.nombreLabel.setVisibility(if (enabled) View.GONE else View.VISIBLE)
        binding.fechaLabel.setVisibility(if (enabled) View.GONE else View.VISIBLE)
        binding.horaInicioLabel.setVisibility(if (enabled) View.GONE else View.VISIBLE)
        binding.horaFinLabel.setVisibility(if (enabled) View.GONE else View.VISIBLE)
        binding.lugarLabel.setVisibility(if (enabled) View.GONE else View.VISIBLE)
        binding.modalidadLabel.setVisibility(if (enabled) View.GONE else View.VISIBLE)
        binding.beneficiariosLabel.setVisibility(if (enabled) View.GONE else View.VISIBLE)
        binding.talleristasLabel.setVisibility(if (enabled) View.GONE else View.VISIBLE)
        binding.descripcionLabel.setVisibility(if (enabled) View.GONE else View.VISIBLE)


        binding.nombreEdit.setVisibility(if (enabled) View.VISIBLE else View.GONE)
        binding.fechaEdit.setVisibility(if (enabled) View.VISIBLE else View.GONE)
        binding.horaInicioEdit.setVisibility(if (enabled) View.VISIBLE else View.GONE)
        binding.horaFinEdit.setVisibility(if (enabled) View.VISIBLE else View.GONE)
        binding.lugarEdit.setVisibility(if (enabled) View.VISIBLE else View.GONE)
        binding.modalidadEdit.setVisibility(if (enabled) View.VISIBLE else View.GONE)
        binding.beneficiariosEdit.setVisibility(if (enabled) View.VISIBLE else View.GONE)
        binding.talleristasEdit.setVisibility(if (enabled) View.VISIBLE else View.GONE)
        binding.descripcionEdit.setVisibility(if (enabled) View.VISIBLE else View.GONE)


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


        binding.nombreEdit.setText(json.optString("nombre"))
        binding.fechaEdit.setText(json.optString("fecha"))
        binding.horaInicioEdit.setText(json.optString("horaInicio"))
        binding.horaFinEdit.setText(json.optString("horaFin"))
        binding.lugarEdit.setText(json.optString("lugar"))
        binding.modalidadEdit.setText(json.optString("modalidad"))
        binding.beneficiariosEdit.setText(json.optString("beneficiarios"))
        binding.talleristasEdit.setText(json.optString("talleristas"))
        binding.descripcionEdit.setText(json.optString("descripcion"))
        


    }

    //private fun buildJSON(): JSONObject {}


}