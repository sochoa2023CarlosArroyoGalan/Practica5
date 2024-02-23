package net.iessochoa.carlosarroyogalan.practica5.ui

import android.app.AlertDialog
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private lateinit var tareaAdapter: TareaAdapter

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
        iniciaCRUD()
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
        //Creamos el adaptador
        tareaAdapter = TareaAdapter()

        with(binding.rvTareas) {
            //Creación del layoutManager
            //layoutManager = LinearLayoutManager(activity)
            //Asignación del adaptador
            val orientation=resources.configuration.orientation
            layoutManager =if(orientation==Configuration.ORIENTATION_PORTRAIT)
                //Posición Vertical
                LinearLayoutManager(activity)
            else//Posición Horizontal, en este caso ponemos el spanCount a 2 para tener 2 columnas
                GridLayoutManager(activity,2)
            adapter = tareaAdapter
        }
        //Llamamos el metodo de deslizamiento en RecyclerView
        iniciaSwiped()
    }

    private fun iniciaCRUD(){
        //Creamos una nueva tarea
        binding.fabNuevo.setOnClickListener{
            //Se crea una accion que envia un nulo al querer crear una nueva tarea
            val action=ListaFragmentDirections.actionEditar(null)
            findNavController().navigate(action)
        }
        tareaAdapter.onTareaClickListener = object : TareaAdapter.OnTareaClickListener
            {
            //Llamamos el metodo para la edicion de la tarea
            override fun onTareaClick(tarea: Tarea?) {
                val action = ListaFragmentDirections.actionEditar(tarea)
                findNavController().navigate(action)
            }
            //Llamamos el método para que salga el cuadro de texto a la hora de borrar
            override fun onTareaBorrarClick(tarea: Tarea?) {
                //borramos la tarea
                borrarTarea(tarea!!)
            }
        }
    }
    //Añadimos el metodo que hará que cuando intentemos borrar una tarea nos salga un cuadro de dialogo en caso de que queramos confirmar
    fun borrarTarea(tarea:Tarea){
        AlertDialog.Builder(activity as Context)
            .setTitle(android.R.string.dialog_alert_title)
            //recuerda: todo el texto en string.xml
            .setMessage("Desea borrar la Tarea ${tarea.id}?")
            //acción si pulsa si
            .setPositiveButton(android.R.string.ok){v,_->
                //borramos la tarea
                viewModel.delTarea(tarea)
                //cerramos el dialogo
                v.dismiss()
            }
            //accion si pulsa no
            .setNegativeButton(android.R.string.cancel){v,_->v.dismiss()}
            .setCancelable(false)
            .create()
            .show()
    }
    //A continuacion crearemos el evento de animación a la hora de borrar una tarea
    fun iniciaSwiped(){
        //Codigo para detectar el deslizamiento
        val itemTouchHelperCallback =
            object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or
                        ItemTouchHelper.RIGHT) {
                //Overide para cuando cuando detecte el movimiento
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder,
                                      direction: Int) {
                    //Posición de la tarea
                    val tareaDelete=tareaAdapter.listaTareas?.get(viewHolder.adapterPosition)
                    //Eliminacion de la tarea llamando al metodo anterior
                            if (tareaDelete != null) {
                                viewModel.delTarea(tareaDelete)
                            }
                }
            }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        //Evento enlazado con RecyclerView
        itemTouchHelper.attachToRecyclerView(binding.rvTareas)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}