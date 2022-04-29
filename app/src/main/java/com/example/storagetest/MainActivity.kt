package com.example.storagetest

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.storagetest.databinding.ActivityMainBinding
import com.example.storagetest.first.KEY_LANG
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.Rotation
import nl.dionsegijn.konfetti.core.emitter.Emitter
import nl.dionsegijn.konfetti.core.models.Size
import java.util.*
import java.util.concurrent.TimeUnit

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "dataStore")

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        binding.bottomNav.setupWithNavController(navController!!)

        val openDirectoryButton = findViewById<FloatingActionButton>(R.id.fab_open_directory)

        val right = Party(
            speed = 30f,
            maxSpeed = 50f,
            damping = 0.9f,
            angle = -70,
            spread = 90,
            size = listOf(Size.SMALL, Size.LARGE),
            timeToLive = 2000L,
            rotation = Rotation(),
            colors = listOf(
                Color.parseColor("#56F62A"),
                Color.parseColor("#D6AB24"),
                Color.parseColor("#EB4823"),
                Color.parseColor("#A044D4"),
                Color.parseColor("#2AA0F6")
            ),
            emitter = Emitter(duration = 1500, TimeUnit.MILLISECONDS).max(600),
            position = Position.Relative(0.0, 0.6)
        )

        val left = Party(
            speed = 30f,
            maxSpeed = 50f,
            damping = 0.9f,
            angle = 250,
            spread = 90,
            size = listOf(Size.SMALL, Size.LARGE),
            timeToLive = 2000L,
            rotation = Rotation(),
            colors = listOf(
                Color.parseColor("#56F62A"),
                Color.parseColor("#D6AB24"),
                Color.parseColor("#EB4823"),
                Color.parseColor("#A044D4"),
                Color.parseColor("#2AA0F6")
            ),
            emitter = Emitter(duration = 1500, TimeUnit.MILLISECONDS).max(600),
            position = Position.Relative(1.0, 0.6)
        )


        openDirectoryButton.setOnClickListener {
            binding.konfettiView.start(right)
            binding.konfettiView.start(left)
        }

        bindState()
    }

    private fun bindState() {
        navController?.addOnDestinationChangedListener { _, destination, _ ->
            run {
                Log.e("ahihi", "${destination.label}")
            }
        }

        val lang: Flow<String> = this.dataStore.data
            .map { preferences ->
                preferences[stringPreferencesKey(KEY_LANG)] ?: ""
            }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                lang.collect {
                    setLanguage(it)
                    setTextBottom(it)
                }
            }
        }
    }

    private fun setTextBottom(type: String) {
        val itemFirst = binding.bottomNav.menu.findItem(R.id.first)
        val itemCenter = binding.bottomNav.menu.findItem(R.id.center)
        val itemSecond = binding.bottomNav.menu.findItem(R.id.second)

        if (type == "vi") {
            itemFirst.title = "Mot"
            itemCenter.title = "Giua"
            itemSecond.title = "Hai"
        } else {
            itemFirst.title = "First"
            itemCenter.title = "Center"
            itemSecond.title = "Second"
        }
    }

    private fun setLanguage(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val configuration: Configuration = resources.configuration
        configuration.setLocale(locale)
        createConfigurationContext(configuration)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    override fun onDestroy() {
        super.onDestroy()
        navController = null
    }
}
