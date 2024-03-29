package net.iessochoa.carlosarroyogalan.practica5.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import net.iessochoa.carlosarroyogalan.practica5.R
import net.iessochoa.carlosarroyogalan.practica5.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
        //Creamos estas constantes en la activity principal para evitar errores en el código
    public companion object{
        val PREF_NOMBRE="nombre"
        val PREF_COLOR_PRIORIDAD="color_prioridad"
        val PREF_AVISO_NUEVAS="aviso_nuevas"
    }
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            //Llamamiento en caso de que el usuario pulse sobre la acción
            R.id.action_prueba -> actionPrueba()
            //Llamamos el método anteriormente creado
            R.id.action_settings -> actionSettings()
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
    //Creamos un método para la acción de prueba
    fun actionPrueba():Boolean{
        Toast.makeText(this,"Prueba de menú",Toast.LENGTH_SHORT).show()
        return true
    }
    //Creamos un método el cual llamará al fragment de Navigation
    private fun actionSettings(): Boolean {
        findNavController(R.id.nav_host_fragment_content_main).navigate(R.id.settingsFragment)
        return true
    }
}