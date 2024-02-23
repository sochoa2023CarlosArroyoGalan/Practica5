package net.iessochoa.carlosarroyogalan.practica5.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import net.iessochoa.carlosarroyogalan.practica5.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        //Busca la preferencia de version
        val buildVersion: Preference? = findPreference("buildVersion")
        //Se define una acción para la preferencia, añadiendo nuestra pagina
        buildVersion?.setOnPreferenceClickListener {
            startActivity(
                Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://portal.edu.gva.es/03013224/"))
            )
            //Devuelve un booleano para confirmas el cambio/
            false
        }
        val telefonoContacto: Preference? = findPreference("telefonoContacto")

// Definimos la acción para la preferencia del teléfono de contacto/
        telefonoContacto?.setOnPreferenceClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:966912260")
            startActivity(intent)
            // Devolvemos un booleano para indicar si se acepta el cambio o no
            false
            }
        }
}