package com.kioskable.app.service.kiosk

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.kioskable.app.R
import com.kioskable.app.ui.display.ContentDisplayActivity
import com.kioskable.app.ui.main.MainActivity
import timber.log.Timber
import java.util.Timer
import java.util.TimerTask

/**
 * Service to maintain kiosk mode.
 * This service ensures the app stays in kiosk mode, handles device restarts,
 * and restores kiosk mode if the user tries to exit.
 */
class KioskService : Service() {
    
    private lateinit var kioskManager: KioskManager
    private lateinit var sharedPreferences: SharedPreferences
    private var timer: Timer? = null
    private var screenOffReceiver: BroadcastReceiver? = null
    private var screenOnReceiver: BroadcastReceiver? = null
    private var homeButtonReceiver: BroadcastReceiver? = null
    
    override fun onCreate() {
        super.onCreate()
        kioskManager = KioskManager(this)
        sharedPreferences = getSharedPreferences("kiosk_prefs", Context.MODE_PRIVATE)
        
        // Create a notification channel (required for foreground service on Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Kiosk Mode Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Maintains kiosk mode"
                lightColor = Color.BLUE
                lockscreenVisibility = Notification.VISIBILITY_PRIVATE
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
        
        // Register broadcast receivers
        registerScreenOffReceiver()
        registerScreenOnReceiver()
        registerHomeButtonReceiver()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Start as a foreground service to avoid being killed
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
        
        // Check if kiosk mode is enabled in preferences
        val isKioskModeEnabled = sharedPreferences.getBoolean(KioskManager.PREF_KEY_KIOSK_MODE, false)
        
        if (isKioskModeEnabled) {
            // Start kiosk mode monitor
            startKioskModeMonitor()
        }
        
        // Restart if killed
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? {
        return null // We don't need binding for this service
    }
    
    override fun onDestroy() {
        super.onDestroy()
        stopKioskModeMonitor()
        
        // Unregister receivers
        unregisterScreenOffReceiver()
        unregisterScreenOnReceiver()
        unregisterHomeButtonReceiver()
    }
    
    /**
     * Creates the notification for the foreground service
     */
    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Kiosk Mode Active")
            .setContentText("App is running in kiosk mode")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setVisibility(NotificationCompat.VISIBILITY_SECRET)
            .build()
    }
    
    /**
     * Starts a timer to periodically check if kiosk mode is still active
     */
    private fun startKioskModeMonitor() {
        timer?.cancel()
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                // Check if kiosk mode is enabled in preferences but not active on device
                val isKioskModeEnabled = sharedPreferences.getBoolean(KioskManager.PREF_KEY_KIOSK_MODE, false)
                val isKioskModeActive = kioskManager.isKioskModeActive()
                
                if (isKioskModeEnabled && !isKioskModeActive) {
                    Timber.d("Kiosk mode has been exited. Attempting to restore...")
                    launchHomeActivity()
                }
            }
        }, MONITOR_DELAY, MONITOR_INTERVAL)
    }
    
    /**
     * Stops the kiosk mode monitor timer
     */
    private fun stopKioskModeMonitor() {
        timer?.cancel()
        timer = null
    }
    
    /**
     * Launches the content display activity to restore kiosk mode
     */
    private fun launchHomeActivity() {
        val intent = Intent(this, ContentDisplayActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        }
        startActivity(intent)
    }
    
    /**
     * Registers a receiver for screen off events
     */
    private fun registerScreenOffReceiver() {
        screenOffReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Timber.d("Screen turned off")
                // Optional: Do something when screen is turned off
            }
        }
        
        val filter = IntentFilter(Intent.ACTION_SCREEN_OFF)
        registerReceiver(screenOffReceiver, filter)
    }
    
    /**
     * Unregisters the screen off receiver
     */
    private fun unregisterScreenOffReceiver() {
        screenOffReceiver?.let {
            try {
                unregisterReceiver(it)
            } catch (e: Exception) {
                Timber.e(e, "Error unregistering screen off receiver")
            }
        }
        screenOffReceiver = null
    }
    
    /**
     * Registers a receiver for screen on events
     */
    private fun registerScreenOnReceiver() {
        screenOnReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Timber.d("Screen turned on")
                
                // Check if kiosk mode is enabled in preferences but not active on device
                val isKioskModeEnabled = sharedPreferences.getBoolean(KioskManager.PREF_KEY_KIOSK_MODE, false)
                val isKioskModeActive = kioskManager.isKioskModeActive()
                
                if (isKioskModeEnabled && !isKioskModeActive) {
                    Timber.d("Screen turned on, ensuring kiosk mode...")
                    launchHomeActivity()
                }
            }
        }
        
        val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
        registerReceiver(screenOnReceiver, filter)
    }
    
    /**
     * Unregisters the screen on receiver
     */
    private fun unregisterScreenOnReceiver() {
        screenOnReceiver?.let {
            try {
                unregisterReceiver(it)
            } catch (e: Exception) {
                Timber.e(e, "Error unregistering screen on receiver")
            }
        }
        screenOnReceiver = null
    }
    
    /**
     * Registers a receiver for home button presses
     */
    private fun registerHomeButtonReceiver() {
        homeButtonReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Timber.d("Home button pressed")
                
                // Check if kiosk mode is enabled in preferences but not active on device
                val isKioskModeEnabled = sharedPreferences.getBoolean(KioskManager.PREF_KEY_KIOSK_MODE, false)
                if (isKioskModeEnabled) {
                    launchHomeActivity()
                    
                    // Consume the event
                    abortBroadcast()
                }
            }
        }
        
        // Register for home button intent with high priority
        val intentFilter = IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS).apply {
            priority = IntentFilter.SYSTEM_HIGH_PRIORITY
        }
        
        try {
            registerReceiver(homeButtonReceiver, intentFilter)
        } catch (e: Exception) {
            Timber.e(e, "Error registering home button receiver")
        }
    }
    
    /**
     * Unregisters the home button receiver
     */
    private fun unregisterHomeButtonReceiver() {
        homeButtonReceiver?.let {
            try {
                unregisterReceiver(it)
            } catch (e: Exception) {
                Timber.e(e, "Error unregistering home button receiver")
            }
        }
        homeButtonReceiver = null
    }
    
    companion object {
        private const val CHANNEL_ID = "kiosk_service_channel"
        private const val NOTIFICATION_ID = 1
        private const val MONITOR_DELAY = 5000L // 5 seconds
        private const val MONITOR_INTERVAL = 30000L // 30 seconds
    }
} 