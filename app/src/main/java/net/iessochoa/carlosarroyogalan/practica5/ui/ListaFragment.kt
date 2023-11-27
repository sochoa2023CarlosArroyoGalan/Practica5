package net.iessochoa.carlosarroyogalan.practica5.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import net.iessochoa.carlosarroyogalan.practica5.R
import net.iessochoa.carlosarroyogalan.practica5.adapters.TareaAdapter
import net.iessochoa.carlosarroyogalan.practica5.databinding.FragmentListaBinding
import net.iessochoa.carlosarroyogalan.practica5.model.Tarea
import net.iessochoa.carlosarroyogalan.practica5.viewmodel.AppViewModel

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ListaFragment : Fragment() {

    private var _binding: FragmentListaBinding? = null
    private val viewModel: AppViewModel by activityViewModels()
    lateinit var tareaAdapter: TareaAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentListaBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iniciaRecyclerView()
        iniciaFiltros()
        viewModel.tareasLiveData.observe(viewLifecycleOwner,
            Observer<List<Tarea>> { lista ->
                //actualizaLista(lista)
                tareaAdapter.setLista(lista)
            })

        viewModel.tareasLiveData.observe(viewLifecycleOwner, Observer<List<Tarea>> { lista ->
            actualizaLista(lista)
        })
        /*binding.buttonFirst.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }*/

        binding.fabNuevo.setOnClickListener {
            //creamos acción enviamos argumento nulo porque queremos crear NuevaTarea
            val action=ListaFragmentDirections.actionEditar(null)
            findNavController().navigate(action)

        }
//para prueba, editamos una tarea aleatoria
        /**binding.btPruebaEdicion.setOnClickListener{
//cogemos la lista actual de Tareas que tenemos en el ViewModel. No es lo más correcto
            val lista= viewModel.tareasLiveData.value
            //buscamos una tarea aleatoriamente
            val tarea=lista?.get((0..lista.lastIndex).random())
            //se la enviamos a TareaFragment para su edición
            val action=ListaFragmentDirections.actionEditar(tarea)
            findNavController().navigate(action)
        }*/
        //Llamada al metodo.
        iniciaFiltros()

        binding.root.setOnApplyWindowInsetsListener { view, insets ->
            view.updatePadding(bottom = insets.systemWindowInsetBottom)
            insets
        }
    }
    private fun actualizaLista(lista: List<Tarea>?) {
        //creamos un string modificable
        val listaString = buildString {
            lista?.forEach() {
                //añadimos al final del string
                append(
                    "${it.id}-${it.tecnico}-${
                        //mostramos un trozo de la descripción
                        if (it.descripcion.length < 21) it.descripcion
                        else
                            it.descripcion.subSequence(0, 20)
                    }-${
                        if (it.pagado) "SI-PAGADO" else
                            "NO-PAGADO"
                    }-" + when (it.estado) {
                        0 -> "ABIERTA"
                        1 -> "EN_CURSO"
                        else -> "CERRADA"
                    } + "\n"
                )
            }
        }
      //  binding.tvLista.setText(listaString)
    }
    private fun iniciaFiltros(){
        binding.swSinPagar.setOnCheckedChangeListener( ) { _,isChecked->
            //actualiza el LiveData SoloSinPagarLiveData que a su vez modifica tareasLiveData
            //mediante el Transformation
            viewModel.setSoloSinPagar(isChecked)
        }
        binding.rgEstadoList.setOnCheckedChangeListener {_, checkedId ->
            val estado = when (checkedId){
                R.id.rbAbierta2 -> 0
                R.id.rbEnCurso2 -> 1
                R.id.rbCerrada2 -> 2
                R.id.rbTodas -> 3
                else -> 1
            }
            viewModel.setEstado(estado)
        }
    }
    private fun iniciaRecyclerView() {
        //creamos el adaptador
        tareaAdapter = TareaAdapter()

        with(binding.rvTareas) {
            //Creamos el layoutManager
            layoutManager = LinearLayoutManager(activity)
            //le asignamos el adaptador
            adapter = tareaAdapter
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}