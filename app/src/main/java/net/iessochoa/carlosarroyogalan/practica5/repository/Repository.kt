package net.iessochoa.carlosarroyogalan.practica5.repository

import android.app.Application
import android.content.Context
import net.iessochoa.carlosarroyogalan.practica5.model.Tarea
import net.iessochoa.carlosarroyogalan.practica5.model.temp.ModelTempTareas

object Repository {
    private lateinit var modelTareas:ModelTempTareas
    //el context suele ser necesario para recuperar datos
    private lateinit var application: Application
    //inicio del objeto singleton
    operator fun invoke(context: Context){
        this.application= context.applicationContext as Application
        //iniciamos el modelo
        ModelTempTareas(application)
        modelTareas=ModelTempTareas
    }
    fun addTarea(tarea: Tarea)= modelTareas.addTarea(tarea)
    fun delTarea(tarea: Tarea)= modelTareas.delTarea(tarea)
    fun getAllTareas()=modelTareas.getAllTareas()
    fun getTareasFiltroSinPagar (soloSinPagar:Boolean)=
        modelTareas.getTareasFiltroSinPagar(soloSinPagar)
    fun getTareaFiltroEstado (estado:Int) = modelTareas.getTareasFiltroEstado(estado)
}