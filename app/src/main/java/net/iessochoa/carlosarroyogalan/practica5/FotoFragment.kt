package net.iessochoa.carlosarroyogalan.practica5

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import net.iessochoa.carlosarroyogalan.practica5.databinding.FragmentFotoBinding

class FotoFragment : Fragment() {
    //Añadimos los binding correspondientes para las fotos
    private var _binding: FragmentFotoBinding? = null
    private val binding get() = _binding!!
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
    }
    //Metodo de comprobación de permisos
    private fun allPermissionsGranted() = PERMISOS_REQUERIDOS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) ==
                PackageManager.PERMISSION_GRANTED
    }
    //Metodo para la inicialización de la cámara
    private fun startCamera() {
        Toast.makeText(requireContext(),
            "Camara iniciada…",
            Toast.LENGTH_SHORT).show()
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

}