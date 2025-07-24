package com.example.appvbg.ui.agenda.detalles

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.appvbg.R
import com.example.appvbg.databinding.FragmentDetallesAgendaBinding

import org.json.JSONObject


class DetallesAgenda : Fragment() {
    private val args: DetallesAgendaArgs by navArgs()
    private var _binding: FragmentDetallesAgendaBinding? = null
    private val binding get() = _binding!!
    private var editMode = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetallesAgendaBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val agendaJsonString = args.agendaJSON
        val agendaJson = JSONObject(agendaJsonString)
        
        setFieldsFromJSON(agendaJson)
        
        
        
        
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


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null


    }

    private fun setEditMode(enabled: Boolean) {
        binding.tituloLabel.setVisibility(if (enabled) View.GONE else View.VISIBLE)
        binding.fechaLabel.setVisibility(if (enabled) View.GONE else View.VISIBLE)
        binding.horaInicioLabel.setVisibility(if (enabled) View.GONE else View.VISIBLE)
        binding.horaFinalizacionLabel.setVisibility(if (enabled) View.GONE else View.VISIBLE)
        binding.lugarLabel.setVisibility(if (enabled) View.GONE else View.VISIBLE)
        binding.IDCasoLabel.setVisibility(if (enabled) View.GONE else View.VISIBLE)
        binding.detallesLabel.setVisibility(if (enabled) View.GONE else View.VISIBLE)
        binding.emailsLabel.setVisibility(if (enabled) View.GONE else View.VISIBLE)
        binding.colorLabel.setVisibility(if (enabled) View.GONE else View.VISIBLE)


        binding.tituloEdit.setVisibility(if (enabled) View.VISIBLE else View.GONE)
        binding.fechaEdit.setVisibility(if (enabled) View.VISIBLE else View.GONE)
        binding.horaInicioEdit.setVisibility(if (enabled) View.VISIBLE else View.GONE)
        binding.horaFinalizacionEdit.setVisibility(if (enabled) View.VISIBLE else View.GONE)
        binding.lugarEdit.setVisibility(if (enabled) View.VISIBLE else View.GONE)
        binding.IDCasoEdit.setVisibility(if (enabled) View.VISIBLE else View.GONE)
        binding.detallesEdit.setVisibility(if (enabled) View.VISIBLE else View.GONE)
        binding.emailsEdit.setVisibility(if (enabled) View.VISIBLE else View.GONE)
        binding.colorEdit.setVisibility(if (enabled) View.VISIBLE else View.GONE)
    }

    private fun setFieldsFromJSON(json: JSONObject) {
        binding.tituloLabel.setText(json.optString("title"))
        binding.fechaLabel.setText(json.optString("date"))
        binding.horaInicioLabel.setText(json.optString("startHour"))
        binding.horaFinalizacionLabel.setText(json.optString("endHour"))
        binding.lugarLabel.setText(json.optString("location"))
        binding.IDCasoLabel.setText(json.optString("IDCaso"))
        binding.detallesLabel.setText(json.optString("details"))
        binding.emailsLabel.setText(json.optString("emails"))
        binding.colorLabel.setText(json.optString("color"))
        
        
        binding.tituloEdit.setText(json.optString("title"))
        binding.fechaEdit.setText(json.optString("date"))
        binding.horaInicioEdit.setText(json.optString("startHour"))
        binding.horaFinalizacionEdit.setText(json.optString("endHour"))
        binding.lugarEdit.setText(json.optString("location"))
        binding.IDCasoEdit.setText(json.optString("IDCaso"))
        binding.detallesEdit.setText(json.optString("details"))
        binding.emailsEdit.setText(json.optString("emails"))
        binding.colorEdit.setText(json.optString("color"))
        
        

    }



}