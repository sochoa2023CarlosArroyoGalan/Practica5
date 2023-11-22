package net.iessochoa.carlosarroyogalan.practica5.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import net.iessochoa.carlosarroyogalan.practica5.model.Tarea
import net.iessochoa.carlosarroyogalan.practica5.repository.Repository

class AppViewModel(application: Application) : AndroidViewModel(application) {
    //repositorio
    private val repositorio:Repository
    //liveData de lista de tareas
    val tareasLiveData :LiveData<List<Tarea>>
    //inicio ViewModel
    private val soloSinPagarLiveData=MutableLiveData<Boolean>(false)

    private val estadoliveData=MutableLiveData<Int>(3)
    init {
        //inicia repositorio
        Repository(getApplication<Application>().applicationContext)
        repositorio=Repository
        //  tareasLiveData=soloSinPagarLiveData.switchMap {soloSinPagar->
          //  Repository.getTareasFiltroSinPagar(soloSinPagar)}
            tareasLiveData=estadoliveData.switchMap { estado -> Repository.getTareaFiltroEstado(estado) }
    }
    fun addTarea(tarea: Tarea) = repositorio.addTarea(tarea)
    fun delTarea(tarea: Tarea) = repositorio.delTarea(tarea)
    fun setSoloSinPagar(soloSinPagar:Boolean){soloSinPagarLiveData.value=soloSinPagar}

    fun setEstado(estado : Int){estadoliveData.value=estado}
}
