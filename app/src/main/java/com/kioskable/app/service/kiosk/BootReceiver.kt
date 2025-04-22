package com.kioskable.app.service.kiosk

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import com.kioskable.app.ui.main.MainActivity
import timber.log.Timber

/**
 * BroadcastReceiver to start the app automatically when the device boots up.
 * This is essential for kiosk functionality to ensure the app runs at all times.
 */
class BootReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Timber.i("Boot completed received")
            
            // Check if kiosk mode was enabled
            val sharedPreferences = context.getSharedPreferences("kiosk_prefs", Context.MODE_PRIVATE)
            val isKioskModeEnabled = sharedPreferences.getBoolean(KioskManager.PREF_KEY_KIOSK_MODE, false)
            
            if (isKioskModeEnabled) {
                Timber.i("Kiosk mode was enabled, starting app")
                
                // Start the KioskService
                startKioskService(context)
                
                // Launch the main activity
                launchApp(context)
            }
        }
    }
    
    /**
     * Starts the KioskService as a foreground service
     */
    private fun startKioskService(context: Context) {
        val serviceIntent = Intent(context, KioskService::class.java)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }
    
    /**
     * Launches the main activity
     */
    private fun launchApp(context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        context.startActivity(intent)
    }
} 