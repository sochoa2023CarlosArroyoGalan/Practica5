package net.iessochoa.carlosarroyogalan.practica5.model.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import net.iessochoa.carlosarroyogalan.practica5.model.Tarea
//Se marca la interfaz como Dao
@Dao
interface TareasDao {
    //Creamos los métodos de inserción y de borrar
    @Insert(onConflict =OnConflictStrategy.REPLACE)
    suspend fun addTarea(tarea: Tarea)
    @Delete
    suspend fun delTarea(tarea: Tarea)

    //Añadimos los diferentes metodos de búsqueda
    @Query("SELECT * FROM tareas ")
    fun getAllTareas():LiveData<List<Tarea>>
    @Query("SELECT * FROM tareas WHERE pagado= :soloSinPagar")
    fun getTareasFiltroSinPagar(soloSinPagar:Boolean):LiveData<List<Tarea>>
    @Query("SELECT * FROM tareas WHERE estado= :estado")
    fun getTareasFiltroEstado(estado:Int):LiveData<List<Tarea>>
    @Query("SELECT * FROM tareas WHERE (pagado= :soloSinPagar) AND(estado= :estado)")
    fun getTareasFiltroSinPagarEstado(soloSinPagar:Boolean, estado:Int):LiveData<List<Tarea>>
}