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
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.appvbg.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.example.appvbg.splashactivity.SplashActivity
import com.example.appvbg.ui.agenda.AgendaFragment
import com.example.appvbg.ui.agenda.crear_cita.CrearCita
import com.example.appvbg.ui.agenda.crear_cita.NewEvent
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

        // Configurar el Floating Action Button
        binding.appBarMain.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        //navController = findNavController(R.id.nav_host_fragment_content_main)
        // Configurar Navigation Controller
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController


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

        // Configurar Navigation Controller
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController

        // Configurar drawer layout
        drawerLayout = binding.drawerLayout

        // Configurar AppBarConfiguration con los destinos principales
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

        // Manejar visibilidad del botón de menú según el destino
        navController.addOnDestinationChangedListener { _, destination, _ ->
            // Mostrar botón de menú hamburguesa solo en destinos principales
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

            // Ocultar FAB en algunos fragments si es necesario
            when (destination.id) {
                R.id.nav_home, R.id.quejasFragment, R.id.agendaFragment -> {
                    binding.appBarMain.fab.show()
                }
                else -> {
                    binding.appBarMain.fab.hide()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                // Manejar clic en settings
                Snackbar.make(binding.root, "Settings clicked", Snackbar.LENGTH_SHORT).show()
                return true
            }
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
        // Manejar items de navegación manualmente si es necesario
        when (item.itemId) {
            R.id.logout -> {
                // Manejar logout
                Snackbar.make(binding.root, "Cerrar sesión", Snackbar.LENGTH_SHORT).show()
                drawerLayout.closeDrawer(GravityCompat.START)
                return true
            }
            else -> {
                // Dejar que el NavigationController maneje la navegación normal
                drawerLayout.closeDrawer(GravityCompat.START)
                return false
            }
        }
    }

    // Función para ocultar/mostrar la bottom navigation
    fun setBottomNavigationVisibility(visible: Boolean) {
        if (visible) {
            binding.bottomNavView.visibility = android.view.View.VISIBLE
        } else {
            binding.bottomNavView.visibility = android.view.View.GONE
        }
    }

    // Función para ocultar/mostrar el FAB
    fun setFabVisibility(visible: Boolean) {
        if (visible) {
            binding.appBarMain.fab.show()
        } else {
            binding.appBarMain.fab.hide()
        }
    }

    // Función para cambiar el comportamiento del FAB
    fun setFabOnClickListener(listener: android.view.View.OnClickListener) {
        binding.appBarMain.fab.setOnClickListener(listener)
    }

    // Función para cambiar el icono del FAB
    fun setFabIcon(@androidx.annotation.DrawableRes iconRes: Int) {
        binding.appBarMain.fab.setImageResource(iconRes)
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Manejar items de navegación manualmente si es necesario
        when (item.itemId) {
            R.id.logout -> {
                // Manejar logout
                    doLogout()
                return true
            }
            else -> {
                // Dejar que el NavigationController maneje la navegación normal
                drawerLayout.closeDrawer(GravityCompat.START)
                return false
            }
        }
    }

    // Función para ocultar/mostrar la bottom navigation
    fun setBottomNavigationVisibility(visible: Boolean) {
        if (visible) {
            binding.bottomNavView.visibility = android.view.View.VISIBLE
        } else {
            binding.bottomNavView.visibility = android.view.View.GONE
        }
    }

    // Función para ocultar/mostrar el FAB
    fun setFabVisibility(visible: Boolean) {
        if (visible) {
            binding.appBarMain.fab.show()
        } else {
            binding.appBarMain.fab.hide()
        }
    }

    // Función para cambiar el comportamiento del FAB
    fun setFabOnClickListener(listener: android.view.View.OnClickListener) {
        binding.appBarMain.fab.setOnClickListener(listener)
    }

    // Función para cambiar el icono del FAB
    fun setFabIcon(@androidx.annotation.DrawableRes iconRes: Int) {
        binding.appBarMain.fab.setImageResource(iconRes)
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