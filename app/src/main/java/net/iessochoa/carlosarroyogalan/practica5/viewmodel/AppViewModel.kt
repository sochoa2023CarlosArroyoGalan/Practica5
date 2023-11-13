package net.iessochoa.carlosarroyogalan.practica5.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import net.iessochoa.carlosarroyogalan.practica5.model.Tarea
import net.iessochoa.carlosarroyogalan.practica5.repository.Repository

class AppViewModel(application: Application) : AndroidViewModel(application) {
    //repositorio
    private val repositorio:Repository
    //liveData de lista de tareas
    val tareasLiveData :LiveData<List<Tarea>>
    //inicio ViewModel
    init {
        //inicia repositorio
        Repository(getApplication<Application>().applicationContext)
        repositorio=Repository
        tareasLiveData =repositorio.getAllTareas()
    }
    fun addTarea(tarea: Tarea) = repositorio.addTarea(tarea)
    fun delTarea(tarea: Tarea) = repositorio.delTarea(tarea)
}
