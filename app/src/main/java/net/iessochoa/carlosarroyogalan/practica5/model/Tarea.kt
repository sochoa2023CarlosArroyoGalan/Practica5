package net.iessochoa.carlosarroyogalan.practica5.model

data class Tarea(
    var id:Long?=null,//id único
    val categoria:Int,
    val prioridad:Int,
    val pagado:Boolean,
    val estado:Int,
    val horasTrabajo:Int,
    val valoracionCliente:Float,
    val tecnico:String,
    val descripcion:String
) {
    constructor( categoria:Int,
                 prioridad:Int,
                 pagado:Boolean,
                 estado:Int,
                 horasTrabajo:Int,
                 valoracionCliente:Float,
                 tecnico:String,
                 descripcion:String):this(generateId(),categoria,prioridad,pagado,estado,horasTrabajo,valoracionCliente, tecnico, descripcion){}
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

