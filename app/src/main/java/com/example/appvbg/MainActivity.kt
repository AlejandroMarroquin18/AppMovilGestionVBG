package com.example.appvbg

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.appvbg.databinding.ActivityMainBinding
import com.example.appvbg.splashactivity.SplashActivity
import com.example.appvbg.ui.agenda.crear_cita.CrearCita
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        // Navigation Controller
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController

        // AppBar sin drawer
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.quejasFragment,
                R.id.agendaFragment,
                R.id.estadisticasMainFragment,
                R.id.opcionesMainFragment,
                R.id.talleresMainFragment
                //R.id.estadisticasQuejasFragment,
                //R.id.estadisticasAgendaFragment,
                //R.id.estadisticasTalleresFragment,
                //R.id.crearTallerFragment
                //R.id.verTalleresFragment,
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        binding.bottomNavView.setupWithNavController(navController)

        // FAB según destino
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val fab = binding.appBarMain.fab
            when (destination.id) {
                R.id.quejasFragment -> {
                    fab.show()
                    fab.isEnabled = true
                    fab.setOnClickListener {
                        fab.isEnabled = false
                        try {
                            navController.navigate(R.id.action_quejasFragment_to_crearQueja)
                        } catch (e: Exception) {
                            fab.isEnabled = true
                        }
                    }
                }
                R.id.agendaFragment -> {
                    fab.show()
                    fab.isEnabled = true
                    fab.setOnClickListener {
                        fab.isEnabled = false
                        try{
                            val bottomSheet = CrearCita()
                            bottomSheet.show(supportFragmentManager, bottomSheet.tag)
                        } catch (e: Exception) {
                            fab.isEnabled=true
                        }

                    }
                }
                else -> fab.hide()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_settings) {
            Snackbar.make(binding.root, "Settings clicked", Snackbar.LENGTH_SHORT).show()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun doLogout() {
        val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        sharedPref.edit().clear().apply()
        val intent = Intent(this, SplashActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
