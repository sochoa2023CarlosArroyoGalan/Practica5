package net.iessochoa.carlosarroyogalan.practica5.ui

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.SeekBar
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import net.iessochoa.carlosarroyogalan.practica5.R
import net.iessochoa.carlosarroyogalan.practica5.databinding.FragmentTareaBinding
import net.iessochoa.carlosarroyogalan.practica5.model.Tarea
import net.iessochoa.carlosarroyogalan.practica5.viewmodel.AppViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class TareaFragment : Fragment() {

    private var _binding: FragmentTareaBinding? = null
    val args: TareaFragmentArgs by navArgs()
    private val viewModel: AppViewModel by activityViewModels()
    //será una tarea nueva si no hay argumento
    val esNuevo by lazy { args.tarea==null }
    //Creamos la varaible que guardará la foto
    var uriFoto=""
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentTareaBinding.inflate(inflater, container, false)
        return binding.root

        binding.ivHacerFoto.setOnClickListener {
            val action = TareaFragmentDirections.hacerFoto(creaTarea())
            findNavController().navigate(action)
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Evento que hace que el boton de la camara funcione.
        binding.ivHacerFoto.setOnClickListener {
            findNavController().navigate(R.id.hacer_Foto)
        }
        binding.root.setOnApplyWindowInsetsListener { view, insets ->
            view.updatePadding(bottom = insets.systemWindowInsetBottom)
            insets
        }

        iniciaSpCategoria()
        iniciaSpPropiedades()
        iniciaSwPagado()
        iniciaRgEstado()
        iniciaSbHoras()
        iniciaFabGuardar()
        iniciaIvBuscarFoto()
        //En caso de que la tarea sea nueva la llamaremos con un if
        if (esNuevo)//nueva tarea
        {//cambiamos el título de la ventana
            (requireActivity() as AppCompatActivity).supportActionBar?.title = "Nueva tarea"
            iniciaTecnico()
        }
        else
            iniciaTarea(args.tarea!!)

    }
    private fun iniciaSpCategoria() {
        ArrayAdapter.createFromResource(
            //contexto suele ser la Activity
            requireContext(),
            //array de strings
            R.array.categoria,
            //layout para mostrar el elemento seleccionado
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Layout para mostrar la apariencia de la lista
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // asignamos el adaptador al spinner
            binding.spCategoria.adapter = adapter
            binding.spCategoria.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        p0: AdapterView<*>?,
                        v: View?,
                        posicion: Int,
                        id: Long
                    ) {
                        val valor = binding.spCategoria.getItemAtPosition(posicion)
                        //creamos el mensaje desde el recurso string parametrizado
                        val mensaje = getString(R.string.mensaje_categoria, valor)
//mostramos el mensaje donde "binding.root" es el ContrainLayout principal
                        Snackbar.make(binding.root, mensaje, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show()
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        binding.nestedScrollView.setBackgroundColor(Color.TRANSPARENT)
                    }
                }
        }
    }
    private fun iniciaSpPropiedades() {
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.prioridad,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spPrioridad.adapter = adapter
            binding.spPrioridad.onItemSelectedListener=object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(p0: AdapterView<*>?, v: View?, posicion: Int, id: Long) {
                    //el array son 3 elementos y "alta" ocupa la tercera posición
                    if(posicion==2){
                        binding.nestedScrollView.setBackgroundColor(requireContext().getColor(R.color.prioridad_alta))
                    }else{//si no es prioridad alta quitamos el color
                        binding.nestedScrollView.setBackgroundColor(Color.TRANSPARENT)
                    }
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {
                    binding.nestedScrollView.setBackgroundColor(Color.TRANSPARENT)
                }
            }

        }
    }
    private fun iniciaSwPagado() {
        binding.swPagado.setOnCheckedChangeListener { _, isChecked ->
            //cambiamos el icono si está marcado o no el switch
            val imagen=if (isChecked) R.drawable.ic_pagado
            else R.drawable.ic_no_pagado
            //asignamos la imagen desde recursos
            binding.ivPagado.setImageResource(imagen)
        }
        //iniciamos a valor false
        binding.swPagado.isChecked=false
        binding.ivPagado.setImageResource(R.drawable.ic_no_pagado)
    }

    private fun iniciaRgEstado() {
        //listener de radioGroup
        binding.rgEstado.setOnCheckedChangeListener { _, checkedId ->
            val imagen= when (checkedId){//el id del RadioButton seleccionado
                //id del cada RadioButon
                R.id.rbAbierta-> R.drawable.ic_abierto
                R.id.rbEnCurso->R.drawable.ic_encurso
                else-> R.drawable.ic_cerrado
            }
            binding.ivEstado.setImageResource(imagen)
        }
        //iniciamos a abierto
        binding.rgEstado.check(R.id.rbAbierta)
    }

    private fun iniciaSbHoras() {
        //asignamos el evento
        binding.sbHoras.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, progreso: Int, p2: Boolean) {
                //Mostramos el progreso en el textview
                binding.tvHoras.text=getString(R.string.horas_trabajadas,progreso)
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {
            }
            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
        })
        //inicio del progreso
        binding.sbHoras.progress=0
        binding.tvHoras.text=getString(R.string.horas_trabajadas,0)
    }
    private fun iniciaTarea(tarea: Tarea) {
        binding.spCategoria.setSelection(tarea.categoria)
        binding.spPrioridad.setSelection(tarea.prioridad)
        binding.swPagado.isChecked = tarea.pagado
        binding.rgEstado.check(
            when (tarea.estado) {
                0 -> R.id.rbAbierta
                1 -> R.id.rbEnCurso
                else -> R.id.rbCerrada
            }
        )
        binding.sbHoras.progress = tarea.horasTrabajo
        binding.rtbValoracion.rating = tarea.valoracionCliente
        binding.etTecnico.setText(tarea.tecnico)
        binding.etDescripcion.setText(tarea.descripcion)
        //Con este código podremos mostrar la imagen
        if (!tarea.fotoUri.isNullOrEmpty())
            binding.ivFoto.setImageURI(tarea.fotoUri.toUri())
        uriFoto=tarea.fotoUri
        //cambiamos el título
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Tarea ${tarea.id}"
    }
    private fun guardaTarea() {
        //recuperamos los datos
        //guardamos la tarea desde el viewmodel
        viewModel.addTarea(creaTarea())
        //salimos de editarFragment
        findNavController().popBackStack()
    }
    private fun creaTarea():Tarea{
        val categoria=binding.spCategoria.selectedItemPosition
        val prioridad=binding.spPrioridad.selectedItemPosition
        val pagado=binding.swPagado.isChecked
        val estado=when (binding.rgEstado.checkedRadioButtonId) {
            R.id.rbAbierta -> 0
            R.id.rbEnCurso -> 1
            else -> 2
        }
        val horas=binding.sbHoras.progress
        val valoracion=binding.rtbValoracion.rating
        val tecnico=binding.etTecnico.text.toString()
        val descripcion=binding.etDescripcion.text.toString()
        //creamos la tarea: si es nueva, generamos un id, en otro caso le asignamos su id
        val tarea = if(esNuevo)

            Tarea(categoria,prioridad,pagado,estado,horas,valoracion,tecnico,descripcion, uriFoto)
        else//venimos de hacer foto

            Tarea(args.tarea!!.id,categoria,prioridad,pagado,estado,horas,valoracion,tecnico,descripcion, uriFoto)
        return tarea
    }
    private fun muestraMensajeError() {
        val mensaje=getString(R.string.mensaje_vacio)
        Snackbar.make(binding.root,mensaje,Snackbar.LENGTH_LONG).setAction("Action", null).show()
    }
    private fun iniciaFabGuardar() {
        binding.fabGuardar.setOnClickListener {
            if (binding.etTecnico.text.toString().isEmpty() || binding.etDescripcion.text.toString().isEmpty())
                muestraMensajeError()
            else
                guardaTarea()
        }
    }
    //Creamos el método que recupera el valor y asignamos el campo tecnico
    private fun iniciaTecnico(){
            //Recuperamos prefencias
        val sharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(requireContext())
            //Recuperamos el nombre de nuestro usuario (tambien hemos cambiado el metodo a MainActivity.PREF_NOMBRE)
        val tecnico = sharedPreferences.getString(MainActivity.PREF_NOMBRE, "")
            //Se lo asignamos a /etTecnico
        binding.etTecnico.setText(tecnico)
    }

    private val TAG = "Practica5"
    private val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    private fun saveBitmapImage(bitmap: Bitmap): Uri? {
        val timestamp = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        var uri: Uri? = null
        //Avisa al escaner de que el usuario ya puede usar los archivos
        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        values.put(MediaStore.Images.Media.DATE_ADDED, timestamp)
        //Avisa de una version mayor o igual a 29
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.DATE_TAKEN, timestamp)
            values.put(
                MediaStore.Images.Media.RELATIVE_PATH,
                "Pictures/" + getString(R.string.app_name)
            )
            values.put(MediaStore.Images.Media.IS_PENDING, true)
            uri = requireContext().contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
            )
            if (uri != null) {
                try {
                    val outputStream = requireContext().contentResolver.openOutputStream(uri)
                    if (outputStream != null) {
                        try {
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                            outputStream.close()
                        } catch (e: Exception) {
                            Log.e(TAG, "saveBitmapImage: ", e)
                        }
                    }
                    values.put(MediaStore.Images.Media.IS_PENDING, false)
                    requireContext().contentResolver.update(uri, values, null, null)
                    // Toast.makeText(requireContext(), "Saved...", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Log.e(TAG, "saveBitmapImage: ", e)
                }
            }
        } else {//No funcional en versiones inferiores a 29
            val imageFileFolder = File(
                Environment.getExternalStorageDirectory()
                    .toString() + '/' + getString(R.string.app_name)
            )
            if (!imageFileFolder.exists()) {
                imageFileFolder.mkdirs()
            }
            val mImageName = "$timestamp.png"
            val imageFile = File(imageFileFolder, mImageName)
            try {
                val outputStream: OutputStream = FileOutputStream(imageFile)
                try {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    outputStream.close()
                } catch (e: Exception) {
                    Log.e(TAG, "saveBitmapImage: ", e)
                }
                values.put(MediaStore.Images.Media.DATA, imageFile.absolutePath)
                requireContext().contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    values
                )
                uri = imageFile.toUri()
                // Toast.makeText(requireContext(), "Saved...", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e(TAG, "saveBitmapImage: ", e)
            }
        }
        return uri
    }
    //Metodo para que cargue la imagen en el formulario
    fun loadFromUri(photoUri: Uri?): Bitmap? {
        var image: Bitmap? = null
        try {
            // Comprobación de que la versiona sea superior a la 27
            image = if (Build.VERSION.SDK_INT > 27) {
                val source = ImageDecoder.createSource(
                    requireContext().contentResolver,
                    photoUri!!
                )
                ImageDecoder.decodeBitmap(source)
            } else {
                // En caso de una version más antigua esta tendrá soporte con bitmap
                MediaStore.Images.Media.getBitmap(requireContext().contentResolver,
                    photoUri)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return image
    }

    //Variable con permiso de solicitud dependiendo de la versión
    private val PERMISOS_REQUERIDOS=when {
        // Build.VERSION.SDK_INT >= 34 -> Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
        Build.VERSION.SDK_INT >= 33 -> Manifest.permission.READ_MEDIA_IMAGES
        else -> Manifest.permission.READ_EXTERNAL_STORAGE
    }

    //Metodo de comprobación de permisos
    fun permisosAceptados() = ContextCompat.checkSelfPermission(
        requireContext(),
        PERMISOS_REQUERIDOS
    ) == PackageManager.PERMISSION_GRANTED

    //petición de foto de la galería Versión <33
    private val solicitudFotoGalleryMenorV33 = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            //uri de la foto elegida
            val uri = result.data?.data
            val uriCopia = saveBitmapImage(loadFromUri(uri)!!)
            //mostramos la foto
            binding.ivFoto.setImageURI(uriCopia)
            //guardamos la uri
            uriFoto = uriCopia?.toString() ?: ""
        }
    }
    //petición de foto de la galería version >=33
    private val solicitudFotoGalleryV33 = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            //uri de la foto elegida
            // val uri = result.data?.data
            val uriCopia = saveBitmapImage(loadFromUri(uri)!!)
            //mostramos la foto
            binding.ivFoto.setImageURI(uriCopia)
            //guardamos la uri
            uriFoto = uriCopia?.toString() ?: ""
        }
    }

    //Solicitud para que el usuario pueda buscar la imagen en su galeria
    private fun buscarFoto() {
        if (Build.VERSION.SDK_INT >= 33)
            solicitudFotoGalleryV33.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        else {
            val intent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            solicitudFotoGalleryMenorV33.launch(intent)
        }
    }
    //Metodo que explica los permisos al usuario
    fun explicarPermisos() {
        AlertDialog.Builder(requireContext())
            .setTitle(android.R.string.dialog_alert_title)
            //TODO:recuerda: el texto en string.xml
            .setMessage("Es necesario el permiso de \"Lectura de fichero\" para mostrar una foto.\nDesea aceptar los permisos?")
        //acción si pulsa si
        .setPositiveButton(android.R.string.ok) { v, _ ->
            //Solicitamos los permisos de nuevo
            solicitudPermisosLauncher.launch(PERMISOS_REQUERIDOS)
            //cerramos el dialogo
            v.dismiss()
        }
            //accion si pulsa no
            .setNegativeButton(android.R.string.cancel) { v, _ -> v.dismiss() }
            .setCancelable(false)
            .create()
            .show()
    }
    //Metodo que solicita los permisos al usuario/
    private val solicitudPermisosLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission has been granted.
                buscarFoto()
            } else {
                // Permission request was denied.
                explicarPermisos()
            }
        }
    //Metodo en el que al pulsar sobre el icono en caso de tener permisos bucará la foto o en caso contrario solicitará permiso/
    fun iniciaIvBuscarFoto() {
        binding.ivBuscarFoto.setOnClickListener() {
            when {
                //La versión 34 el usuario puede dar permiso a fotos individualmente
                //el sistema gestiona los permisos
                Build.VERSION.SDK_INT >= 34->buscarFoto()
                //para otras versiones si tenemos los permisos
                permisosAceptados() -> buscarFoto()
                //no tenemos los permisos : los solicitamos
                else -> solicitudPermisosLauncher.launch(PERMISOS_REQUERIDOS)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}