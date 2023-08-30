package com.sixtyninefourtwenty.yetanotherevilinsultgenerator

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sixtyninefourtwenty.customactionmode.AbstractActionMode
import com.sixtyninefourtwenty.customactionmode.FadingActionMode
import com.sixtyninefourtwenty.theming.applyTheming
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.data.repository.PreferencesRepository
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.databinding.ActivityMainBinding
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.shared.model.Insult
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.ui.generator.GeneratorFragmentDirections
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.ui.generator.GeneratorViewModel
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils.ACTION_SHOW_INFO_FROM_IOTD_NOTIFICATION
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils.IOTD_EXTRA_KEY
import com.sixtyninefourtwenty.yetanotherevilinsultgenerator.utils.IOTD_NOTIFICATION_CHANNEL_ID

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var _actionMode: AbstractActionMode
    val actionMode get() = _actionMode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createIotdNotificationChannel()
        setInitialPreferences()
        applyTheming(
            material2ThemeStyleRes = R.style.Theme_YetAnotherEvilInsultGenerator,
            material3CustomColorsThemeStyleRes = R.style.Theme_YetAnotherEvilInsultGenerator_Material3_Android11,
            material3DynamicColorsThemeStyleRes = R.style.Theme_YetAnotherEvilInsultGenerator_Material3
        )

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        _actionMode = FadingActionMode(binding.actionModeToolbar, onBackPressedDispatcher)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_generator, R.id.navigation_offline, R.id.navigation_settings)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        handleLaunchedIntent()
    }

    private fun setInitialPreferences() {
        PreferencesRepository.initializeInitialValues(this)
    }

    private fun handleLaunchedIntent() {
        if (intent.action == ACTION_SHOW_INFO_FROM_IOTD_NOTIFICATION) {
            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            if (navController.currentDestination?.id == R.id.navigation_generator) {
                val insult = Insult.fromJson(intent.getStringExtra(IOTD_EXTRA_KEY)!!)
                val generatorViewModel = ViewModelProvider(this, GeneratorViewModel.Factory)[GeneratorViewModel::class.java]
                generatorViewModel.addOrUpdateLocalInsult(insult)
                navController.navigate(GeneratorFragmentDirections.actionNavigationGeneratorToNavigationInsultInfo(insult.toJson()))
            }
        }
    }

    private fun createIotdNotificationChannel() {
        NotificationManagerCompat.from(this)
            .createNotificationChannel(
                NotificationChannelCompat.Builder(IOTD_NOTIFICATION_CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_DEFAULT)
                .setName(getString(R.string.insult_of_the_day))
                .setDescription(getString(R.string.iotd_notification_channel_description))
                .build())
    }

    companion object {
        fun createActionShowIotdIntent(context: Context, insult: Insult) = Intent(ACTION_SHOW_INFO_FROM_IOTD_NOTIFICATION)
            .setClass(context, MainActivity::class.java)
            .putExtra(IOTD_EXTRA_KEY, insult.toJson())
    }
}