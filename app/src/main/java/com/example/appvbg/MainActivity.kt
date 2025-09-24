package com.example.appvbg

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.example.appvbg.databinding.ActivityMainBinding
import com.example.appvbg.ui.agenda.AgendaFragment
import com.example.appvbg.ui.quejas.QuejasFragmentDirections
import com.example.appvbg.ui.agenda.crear_cita.CrearCita
import com.example.appvbg.ui.agenda.crear_cita.NewEvent
import android.util.Log
import android.view.MenuItem
import androidx.navigation.ui.NavigationUI
import com.example.appvbg.splashactivity.SplashActivity
import com.google.android.gms.auth.api.identity.ClearTokenRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.RevokeAccessRequest
import com.google.android.gms.common.api.Scope

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)


        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)



        // Detectar el fragmento activo y cambiar el FAB según corresponda
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val fab = binding.appBarMain.fab

            when (destination.id) {
                R.id.quejasFragment -> {
                    //fab.setImageResource(R.drawable.ic_add)
                    fab.show()
                    fab.setOnClickListener {
                        // Acción para quejasFragment
                        findNavController(R.id.nav_host_fragment_content_main)
                            .navigate(R.id.action_quejasFragment_to_crearQueja)
                        //Snackbar.make(fab, "Añadir queja", Snackbar.LENGTH_SHORT).show()
                    }
                }

                R.id.agendaFragment -> {
                    //fab.setImageResource(R.drawable.ic_event)
                    fab.show()
                    // Registrar el listener aquí
                    supportFragmentManager.setFragmentResultListener("crearCitaRequestKey", this) { _, bundle ->
                        val nuevoEvento = bundle.getParcelable<NewEvent>("nuevo_evento")
                        if (nuevoEvento != null) {
                            // Buscar el fragmento actual en el NavHost
                            val currentFragment = supportFragmentManager
                                .findFragmentById(R.id.nav_host_fragment_content_main)
                                ?.childFragmentManager
                                ?.fragments
                                ?.firstOrNull() as? AgendaFragment

                            currentFragment?.recibirNuevoEvento(nuevoEvento)
                        }
                    }

                    fab.setOnClickListener {
                        // Acción para agendaFragment
                        val bottomSheet = CrearCita()
                        bottomSheet.show(supportFragmentManager, bottomSheet.tag)
                    }
                }

                else -> {
                    fab.hide()
                }
            }
        }



        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.quejasFragment,
                R.id.estadisticasQuejasFragment,
                R.id.verTalleresFragment,
                R.id.estadisticasTalleresFragment,
                R.id.crearTallerFragment,
                R.id.agendaFragment,
                R.id.estadisticasAgendaFragment
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.logout -> {
                    doLogout()
                    true
                }
                else -> {
                    // Deja que NavigationUI maneje los demás items
                    NavigationUI.onNavDestinationSelected(menuItem, navController)
                    binding.drawerLayout.closeDrawers()
                    true
                }
            }
        }



    }/**
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logout -> {
                // Aquí haces el logout
                // Ejemplo: limpiar token, cerrar sesión, redirigir a LoginActivity
                Log.d("MainActivity", "Logout presionado")

                // Si tienes un
                // de logout:
                doLogout()

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    */

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        //p
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    private fun doLogout(){
        // Borrar todos los datos guardados en AppPrefs
        val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        sharedPref.edit().clear().apply()


        /*
        val revokeAccessRequest = RevokeAccessRequest.builder()
            .setAccount(account)
            .setScopes(requestedScopes)
            .build()

        Identity.getAuthorizationClient(activity)
            .revokeAccess(revokeAccessRequest)
            .addOnSuccessListener {
                Log.i(TAG, "Acceso revocado exitosamente")
                // Aquí puedes realizar otras acciones de logout, como
                // redirigir a la pantalla de inicio de sesión.
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Fallo al revocar el acceso", e)
            }
        */
        // Redirigir al LoginActivity
        val intent = Intent(this, SplashActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}