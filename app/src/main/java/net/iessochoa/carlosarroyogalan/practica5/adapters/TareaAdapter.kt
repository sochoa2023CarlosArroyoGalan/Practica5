package net.iessochoa.carlosarroyogalan.practica5.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import net.iessochoa.carlosarroyogalan.practica5.R
import net.iessochoa.carlosarroyogalan.practica5.databinding.ItemTareaBinding
import net.iessochoa.carlosarroyogalan.practica5.model.Tarea

class TareaAdapter() :RecyclerView.Adapter<TareaAdapter.TareaViewHolder>()
    {

    var listaTareas: List<Tarea>?=null
       var onTareaClickListener:OnTareaClickListener?=null
    fun setLista(lista:List<Tarea>){
        listaTareas=lista
        //notifica al adaptador que hay cambios y tiene que redibujar el
        notifyDataSetChanged()
    }
        interface OnTareaClickListener{
            //editar tarea que contiene el ViewHolder
            fun onTareaClick(tarea:Tarea?)
            //borrar tarea que contiene el ViewHolder
            fun onTareaBorrarClick(tarea:Tarea?)
        }
    inner class TareaViewHolder(val binding: ItemTareaBinding)
        :RecyclerView.ViewHolder(binding.root){
        init {
            binding.ivBorrar.setOnClickListener(){
                val tarea=listaTareas?.get(this.adapterPosition)

                onTareaClickListener?.onTareaBorrarClick(tarea)
            }
            binding.root.setOnClickListener(){
                val tarea=listaTareas?.get(this.adapterPosition)
                onTareaClickListener?.onTareaClick(tarea)
            }
        }
        }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
                TareaViewHolder {
            val binding = ItemTareaBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
            return TareaViewHolder(binding)
        }

        override fun getItemCount(): Int = listaTareas?.size?:0


        override fun onBindViewHolder(tareaViewHolder: TareaViewHolder, pos: Int) {
            //Nos pasan la posición del item a mostrar en el viewHolder
            with(tareaViewHolder) {
                //cogemos la tarea a mostrar y rellenamos los campos del ViewHolder
                with(listaTareas!!.get(pos)) {
                    binding.tvId.text = id.toString()
                    binding.tvDescripciN.text = descripcion
                    binding.tvTecnico.text = tecnico
                    binding.ratingBar.rating = valoracionCliente
                    //mostramos el icono en función del estado
                    binding.ivEstadow.setImageResource(
                        when (estado) {
                            0 -> R.drawable.ic_abierto
                            1 -> R.drawable.ic_encurso
                            else -> R.drawable.ic_cerrado
                        }
                    )
                    //cambiamos el color de fondo si la prioridad es alta
                    binding.cvItem.setBackgroundResource(
                        if (prioridad == 2)//prioridad alta
                            R.color.prioridad_alta
                        else
                            Color.TRANSPARENT
                    )
                }
            }
        }

    }