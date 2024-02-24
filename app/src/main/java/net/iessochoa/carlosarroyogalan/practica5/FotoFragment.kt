package net.iessochoa.carlosarroyogalan.practica5

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues
import android.content.pm.PackageManager
import android.net.Uri
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import net.iessochoa.carlosarroyogalan.practica5.databinding.FragmentFotoBinding
import java.text.SimpleDateFormat
import java.util.Locale

class FotoFragment : Fragment() {
    //Añadimos los binding correspondientes para las fotos

    companion object{
        private const val TAG = "Practica5_CameraX"
        //Se crea la el formato de la imagen, es decir, se guardara con el nombre de la fecha actual
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }
    private var _binding: FragmentFotoBinding? = null
    private val binding get() = _binding!!
    //Se crea la variable que hará la foto de la imagen
    val args: FotoFragmentArgs by navArgs()
    private var imageCapture: ImageCapture? = null
    //Se crea un array que tenga los permisos necesarios para la cámara
    private val PERMISOS_REQUERIDOS =
        mutableListOf (
            Manifest.permission.CAMERA
        ).apply {
            //En caso de la version de android ser menor o igual a la 9 se pide permiso de escritura
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            solicitudPermisosLauncher.launch(PERMISOS_REQUERIDOS)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla el layout del fragmento
        _binding = FragmentFotoBinding.inflate(inflater, container, false)
        return binding.root

        binding.ivMuestra.setOnClickListener(){
            var tarea=args.tarea?.copy(fotoUri =uriFoto.toString())
            val action =
                FotoFragmentDirections.actionFotoFragmentToTareaFragment(tarea)
            findNavController().navigate(action)
        }
    }
    //Metodo de comprobación de permisos
    private fun allPermissionsGranted() = PERMISOS_REQUERIDOS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) ==
                PackageManager.PERMISSION_GRANTED
    }
    //Metodo para la inicialización de la cámara
    private fun startCamera() {
        //Vinculacion del ciclo de vida de las camaras y el de el propietario
        val cameraProviderFuture =
            ProcessCameraProvider.getInstance(requireContext())
        //Agrega un elemento Runnable como primer argumento
        cameraProviderFuture.addListener({
            // Vinculacion de el ciclo de vida de la camara con LifecycleOwner dentro del proceso de la aplicación
            val cameraProvider: ProcessCameraProvider =
                cameraProviderFuture.get()
            //Inicialización de preview en la que llamamos a su compilación para obtener un provedor de plataforma desde el visor para configurarlo en la vista previa
            val preview = Preview.Builder()
                .build()
                .also {

                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder().build()
            // Selecciona la camara como default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                // Antes de unir desunimos los otros bind
                cameraProvider.unbindAll()
                // uUsamos casos de usos de bind en la camara
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
//segundo argumento
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    //Instancia la cual nos permita enviar una solicitud de permisos que actuaran dependiendo de la elección del usuario
    val solicitudPermisosLauncher = registerForActivityResult(
        //realizamos una solicitud de multiples permisos
        ActivityResultContracts.RequestMultiplePermissions()
    ) { isGranted: Map<String, Boolean> ->
        if (allPermissionsGranted()) {
            //Si nos dan permiso la camara de inicializará
            startCamera()
        } else {
            // En caso contrario se le explican los permisos al usuario
            explicarPermisos()
        }
    }
    //Método el cual explica los permisos al usuario
    fun explicarPermisos() {
        AlertDialog.Builder(requireContext())
            .setTitle(android.R.string.dialog_alert_title)
            .setMessage("Son necesarios los permisos para hacer una foto.\nDesea aceptar los permisos?")
        //Si el usuario pulsa si se le solicitaran los permisos de nuevo y se cerrará el dialogo
        .setPositiveButton(android.R.string.ok) { v, _ ->
            solicitudPermisosLauncher.launch(PERMISOS_REQUERIDOS)
            v.dismiss()
        }
            //En caso de que pulse no se cerrará el fragment
            .setNegativeButton(android.R.string.cancel) { v, _ ->
                v.dismiss()

                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
            .setCancelable(false)
            .create()
            .show()
    }

    private var uriFoto: Uri?=null


    private fun takePhoto() {
        // Referencia de la imagen que se ha capturado
        val imageCapture = imageCapture ?: return

        // Nombre temporal y lugar de almacenamiento
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())

        // Valores para el almacenamiento de entrada
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")

            // Funciona en versiones superiores que android 9
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + getString(R.string.app_name))
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                requireActivity().contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

        // Set up image capture listener, which is triggered after photo has been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                    binding.ivMuestra.setImageURI(output.savedUri)
                    uriFoto = output.savedUri
                }
            }
        )

    }

}