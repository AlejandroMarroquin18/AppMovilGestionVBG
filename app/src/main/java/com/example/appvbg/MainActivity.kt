package com.example.appvbg

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.appvbg.databinding.ActivityMainBinding
import com.example.appvbg.splashactivity.SplashActivity
import com.example.appvbg.ui.agenda.crear_cita.CrearCita
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        // Configurar Navigation Controller
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController

        // Configurar drawer layout
        drawerLayout = binding.drawerLayout

        // Configurar AppBarConfiguration con los destinos principales
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.quejasFragment,
                R.id.agendaFragment,
                R.id.verTalleresFragment,
                R.id.estadisticasMainFragment,
                R.id.estadisticasQuejasFragment,
                R.id.estadisticasAgendaFragment,
                R.id.estadisticasTalleresFragment,
                R.id.crearTallerFragment
            ),
            drawerLayout
        )

        // Configurar ActionBar con NavController
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Configurar NavigationView (barra lateral)
        binding.navView.setupWithNavController(navController)
        binding.navView.setNavigationItemSelectedListener(this)

        // Configurar BottomNavigationView
        binding.bottomNavView.setupWithNavController(navController)

        // Detectar fragmento activo y cambiar FAB
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val fab = binding.appBarMain.fab

            when (destination.id) {
                R.id.quejasFragment -> {
                    fab.show()
                    fab.setOnClickListener {
                        navController.navigate(R.id.action_quejasFragment_to_crearQueja)
                    }
                }
                R.id.agendaFragment -> {
                    fab.show()
                    fab.setOnClickListener {
                        val bottomSheet = CrearCita()
                        bottomSheet.show(supportFragmentManager, bottomSheet.tag)
                    }
                }
                else -> fab.hide()
            }

            // Manejar ícono de navegación
            val isTopLevelDestination = appBarConfiguration.topLevelDestinations.contains(destination.id)
            if (isTopLevelDestination) {
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                binding.appBarMain.toolbar.setNavigationIcon(R.drawable.ic_menu_slideshow)
                binding.appBarMain.toolbar.setNavigationOnClickListener {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
            } else {
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                binding.appBarMain.toolbar.setNavigationOnClickListener {
                    navController.navigateUp()
                }
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

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                doLogout()
                return true
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return false
    }

    // Función para ocultar/mostrar la bottom navigation
    fun setBottomNavigationVisibility(visible: Boolean) {
        binding.bottomNavView.visibility = if (visible) android.view.View.VISIBLE else android.view.View.GONE
    }

    // Función para ocultar/mostrar el FAB
    fun setFabVisibility(visible: Boolean) {
        if (visible) binding.appBarMain.fab.show() else binding.appBarMain.fab.hide()
    }

    // Función para cambiar el comportamiento del FAB
    fun setFabOnClickListener(listener: android.view.View.OnClickListener) {
        binding.appBarMain.fab.setOnClickListener(listener)
    }

    // Función para cambiar el icono del FAB
    fun setFabIcon(@androidx.annotation.DrawableRes iconRes: Int) {
        binding.appBarMain.fab.setImageResource(iconRes)
    }

    private fun doLogout() {
        // Borrar todos los datos guardados en AppPrefs
        val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        sharedPref.edit().clear().apply()

        // Redirigir al SplashActivity
        val intent = Intent(this, SplashActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
