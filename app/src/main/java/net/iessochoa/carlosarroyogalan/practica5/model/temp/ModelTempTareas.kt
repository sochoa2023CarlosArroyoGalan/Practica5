package net.iessochoa.carlosarroyogalan.practica5.model.temp

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import net.iessochoa.carlosarroyogalan.practica5.model.Tarea
import kotlin.random.Random

object ModelTempTareas {
    //lista de tareas
    private val tareas = ArrayList<Tarea>()
    //LiveData para observar en la vista los cambios en la lista
    private val tareasLiveData = MutableLiveData<List<Tarea>>(tareas)
    //el context que suele ser necesario en acceso a datos
    private lateinit var application: Application
    //Permite iniciar el objeto Singleton
    operator fun invoke(context: Context){
        this.application= context.applicationContext as Application
        iniciaPruebaTareas()
    }
    fun getAllTareas(): LiveData<List<Tarea>> {
        tareasLiveData.value= tareas
        return tareasLiveData
    }
    fun addTarea(tarea: Tarea) {
        val pos = tareas.indexOf(tarea)
        if (pos < 0) {//si no existe
            tareas.add(tarea)
        } else {
            //si existe se sustituye
            tareas.set(pos, tarea)
        }
        //actualiza el LiveData
        tareasLiveData.value = tareas
    }
    fun iniciaPruebaTareas() {
        val tecnicos = listOf(
            "Pepe Gotero",
            "Sacarino Pómez",
            "Mortadelo Fernández",
            "Filemón López",
            "Zipi Climent",
            "Zape Gómez"
        )
        lateinit var tarea: Tarea
        (1..10).forEach({
            tarea = Tarea(
                (0..4).random(),
                (0..2).random(),
                Random.nextBoolean(),
                (0..2).random(),
                (0..30).random(),
                (0..5).random().toFloat(),
                tecnicos.random(),
                "tarea $it Lorem ipsum dolor sit amet, consectetur adipiscing elit. Mauris consequat ligula et vehicula mattis. \n Etiam tristique ornare lacinia. Vestibulum lacus magna, dignissim et tempor id, convallis sed augue"
            )
            tareas.add(tarea)
        })
        //actualizamos el LiveData
        tareasLiveData.value = tareas
    }
    fun delTarea(tarea: Tarea) {
        tareas.remove(tarea)
        tareasLiveData.value = tareas
    }
    fun getTareasFiltroSinPagar(soloSinPagar:Boolean): LiveData<List<Tarea>> {
        //devuelve el LiveData con la lista filtrada o entera
        tareasLiveData.value=if(soloSinPagar)
            tareas.filter { !it.pagado } as ArrayList<Tarea>
        else
            tareas
        return tareasLiveData
    }
    fun getTareasFiltroEstado(estado:Int): LiveData<List<Tarea>> {
        tareasLiveData.value = if (estado == 2)
            tareas.filter { it.estado == estado } as java.util.ArrayList<Tarea>
        else if (estado == 1)
            tareas.filter { it.estado == estado } as java.util.ArrayList<Tarea>
        else if (estado == 0)
            tareas.filter { it.estado == estado } as java.util.ArrayList<Tarea>
        else
            tareas
        return tareasLiveData
    }
}