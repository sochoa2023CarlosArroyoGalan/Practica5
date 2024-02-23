package net.iessochoa.carlosarroyogalan.practica5.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
//Marcamos la clase como tabla
@Entity(tableName = "tareas")
@Parcelize
data class Tarea(
    //indicamos que es una clave primaria
    @PrimaryKey(autoGenerate = true)
    var id:Long?=null,//id único
    val categoria:Int,
    val prioridad:Int,
    val pagado:Boolean,
    val estado:Int,
    val horasTrabajo:Int,
    val valoracionCliente:Float,
    val tecnico:String,
    val descripcion:String
):Parcelable {
    constructor( categoria:Int,
                 prioridad:Int,
                 pagado:Boolean,
                 estado:Int,
                 horasTrabajo:Int,
                 valoracionCliente:Float,
                 tecnico:String,
                 //Modificamos los campos cambiando el id a null para que cree una nueva en la base de datos
                 descripcion:String):this(null,categoria,prioridad,pagado,estado,horasTrabajo,valoracionCliente, tecnico, descripcion){}
    companion object {
        var idContador = 1L//iniciamos contador de tareas
        private fun generateId(): Long {
            return idContador++//sumamos uno al contador

        }
    }
    override fun equals(other: Any?): Boolean {
        return (other is Tarea)&&(this.id == other?.id)
    }
}

