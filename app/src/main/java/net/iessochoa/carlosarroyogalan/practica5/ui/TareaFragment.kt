package net.iessochoa.carlosarroyogalan.practica5.ui

import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import net.iessochoa.carlosarroyogalan.practica5.R
import net.iessochoa.carlosarroyogalan.practica5.databinding.FragmentTareaBinding
import net.iessochoa.carlosarroyogalan.practica5.model.Tarea
import net.iessochoa.carlosarroyogalan.practica5.viewmodel.AppViewModel

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class TareaFragment : Fragment() {

    private var _binding: FragmentTareaBinding? = null
    val args: TareaFragmentArgs by navArgs()
    private val viewModel: AppViewModel by activityViewModels()
    //será una tarea nueva si no hay argumento
    val esNuevo by lazy { args.tarea==null }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentTareaBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*binding.buttonSecond.setOnClickListener {
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }*/
        binding.root.setOnApplyWindowInsetsListener { view, insets ->
            view.updatePadding(bottom = insets.systemWindowInsetBottom)
            insets
        }

        iniciaSpCategoria()
        iniciaSpPropiedades()
        iniciaSwPagado()
        iniciaRgEstado()
        iniciaSbHoras()
        iniciaFabGuardar()
        //En caso de que la tarea sea nueva la llamaremos con un if
        if (esNuevo)//nueva tarea
        {//cambiamos el título de la ventana
            (requireActivity() as AppCompatActivity).supportActionBar?.title = "Nueva tarea"
            iniciaTecnico()
        }
        else
            iniciaTarea(args.tarea!!)

    }
    private fun iniciaSpCategoria() {
        ArrayAdapter.createFromResource(
            //contexto suele ser la Activity
            requireContext(),
            //array de strings
            R.array.categoria,
            //layout para mostrar el elemento seleccionado
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Layout para mostrar la apariencia de la lista
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // asignamos el adaptador al spinner
            binding.spCategoria.adapter = adapter
            binding.spCategoria.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        p0: AdapterView<*>?,
                        v: View?,
                        posicion: Int,
                        id: Long
                    ) {
                        val valor = binding.spCategoria.getItemAtPosition(posicion)
                        //creamos el mensaje desde el recurso string parametrizado
                        val mensaje = getString(R.string.mensaje_categoria, valor)
//mostramos el mensaje donde "binding.root" es el ContrainLayout principal
                        Snackbar.make(binding.root, mensaje, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        binding.nestedScrollView.setBackgroundColor(Color.TRANSPARENT)
                    }
                }
        }
    }
    private fun iniciaSpPropiedades() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.prioridad,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spPrioridad.adapter = adapter
            binding.spPrioridad.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, v: View?, posicion: Int, id: Long) {
                    //el array son 3 elementos y "alta" ocupa la tercera posición
                    if(posicion==2){
                        binding.nestedScrollView.setBackgroundColor(requireContext().getColor(R.color.prioridad_alta))
                    }else{//si no es prioridad alta quitamos el color
                        binding.nestedScrollView.setBackgroundColor(Color.TRANSPARENT)
                    }
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {
                    binding.nestedScrollView.setBackgroundColor(Color.TRANSPARENT)
                }
            }

        }
    }
    private fun iniciaSwPagado() {
        binding.swPagado.setOnCheckedChangeListener { _, isChecked ->
            //cambiamos el icono si está marcado o no el switch
            val imagen=if (isChecked) R.drawable.ic_pagado
            else R.drawable.ic_no_pagado
            //asignamos la imagen desde recursos
            binding.ivPagado.setImageResource(imagen)
        }
        //iniciamos a valor false
        binding.swPagado.isChecked=false
        binding.ivPagado.setImageResource(R.drawable.ic_no_pagado)
    }

    private fun iniciaRgEstado() {
        //listener de radioGroup
        binding.rgEstado.setOnCheckedChangeListener { _, checkedId ->
            val imagen= when (checkedId){//el id del RadioButton seleccionado
                //id del cada RadioButon
                R.id.rbAbierta-> R.drawable.ic_abierto
                R.id.rbEnCurso->R.drawable.ic_encurso
                else-> R.drawable.ic_cerrado
            }
            binding.ivEstado.setImageResource(imagen)
        }
        //iniciamos a abierto
        binding.rgEstado.check(R.id.rbAbierta)
    }

    private fun iniciaSbHoras() {
        //asignamos el evento
        binding.sbHoras.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, progreso: Int, p2: Boolean) {
                //Mostramos el progreso en el textview
                binding.tvHoras.text=getString(R.string.horas_trabajadas,progreso)
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {
            }
            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })
        //inicio del progreso
        binding.sbHoras.progress=0
        binding.tvHoras.text=getString(R.string.horas_trabajadas,0)
    }
    private fun iniciaTarea(tarea: Tarea) {
        binding.spCategoria.setSelection(tarea.categoria)
        binding.spPrioridad.setSelection(tarea.prioridad)
        binding.swPagado.isChecked = tarea.pagado
        binding.rgEstado.check(
            when (tarea.estado) {
                0 -> R.id.rbAbierta
                1 -> R.id.rbEnCurso
                else -> R.id.rbCerrada
            }
        )
        binding.sbHoras.progress = tarea.horasTrabajo
        binding.rtbValoracion.rating = tarea.valoracionCliente
        binding.etTecnico.setText(tarea.tecnico)
        binding.etDescripcion.setText(tarea.descripcion)
        //cambiamos el título
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Tarea ${tarea.id}"
    }
    private fun guardaTarea() {
        //recuperamos los datos
        val categoria=binding.spCategoria.selectedItemPosition
        val prioridad=binding.spPrioridad.selectedItemPosition
        val pagado=binding.swPagado.isChecked
        val estado=when (binding.rgEstado.checkedRadioButtonId) {
            R.id.rbAbierta -> 0
            R.id.rbEnCurso -> 1
            else -> 2
        }
        val horas=binding.sbHoras.progress
        val valoracion=binding.rtbValoracion.rating
        val tecnico=binding.etTecnico.text.toString()
        val descripcion=binding.etDescripcion.text.toString()
        //creamos la tarea: si es nueva, generamos un id, en otro caso le asignamos su id
        val tarea = if(esNuevo)

            Tarea(categoria,prioridad,pagado,estado,horas,valoracion,tecnico,descripcion)
        else

            Tarea(args.tarea!!.id,categoria,prioridad,pagado,estado,horas,valoracion,tecnico,descripcion)
        //guardamos la tarea desde el viewmodel
        viewModel.addTarea(tarea)
        //salimos de editarFragment
        findNavController().popBackStack()
    }
    private fun muestraMensajeError() {
        val mensaje=getString(R.string.mensaje_vacio)
        Snackbar.make(binding.root,mensaje,Snackbar.LENGTH_LONG).setAction("Action", null).show()
    }
    private fun iniciaFabGuardar() {
        binding.fabGuardar.setOnClickListener {
            if (binding.etTecnico.text.toString().isEmpty() || binding.etDescripcion.text.toString().isEmpty())
                muestraMensajeError()
            else
                guardaTarea()
        }
    }
    //Creamos el método que recupera el valor y asignamos el campo tecnico
    private fun iniciaTecnico(){
            //Recuperamos prefencias
        val sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(requireContext())
            //Recuperamos el nombre de nuestro usuario (tambien hemos cambiado el metodo a MainActivity.PREF_NOMBRE)
        val tecnico = sharedPreferences.getString(MainActivity.PREF_NOMBRE, "")
            //Se lo asignamos a etTecnico
        binding.etTecnico.setText(tecnico)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}