package com.example.appvbg

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
import com.example.appvbg.ui.quejas.QuejasFragmentDirections
import com.example.appvbg.ui.agenda.crear_cita.CrearCita

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
    }

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
}