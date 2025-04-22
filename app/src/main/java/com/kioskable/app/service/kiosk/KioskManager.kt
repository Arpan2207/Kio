package com.kioskable.app.service.kiosk

import android.app.Activity
import android.app.ActivityManager
import android.app.admin.DevicePolicyManager
import android.app.admin.SystemUpdatePolicy
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.UserManager
import android.provider.Settings
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.annotation.RequiresApi
import timber.log.Timber

/**
 * Manager class for handling kiosk mode operations.
 * This includes entering/exiting lock task mode, setting device admin, disabling status bar, etc.
 */
class KioskManager(private val context: Context) {
    
    private val devicePolicyManager: DevicePolicyManager by lazy {
        context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
    }
    
    private val adminComponentName: ComponentName by lazy {
        ComponentName(context, KioskDeviceAdminReceiver::class.java)
    }
    
    private val activityManager: ActivityManager by lazy {
        context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    }
    
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("kiosk_prefs", Context.MODE_PRIVATE)
    }
    
    /**
     * Checks if device admin is active
     */
    fun isDeviceAdminActive(): Boolean {
        return devicePolicyManager.isAdminActive(adminComponentName)
    }
    
    /**
     * Requests device admin permission
     */
    fun requestDeviceAdmin(activity: Activity) {
        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
            putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponentName)
            putExtra(
                DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "Device admin is required for kiosk mode to function properly."
            )
        }
        activity.startActivityForResult(intent, REQUEST_CODE_ENABLE_ADMIN)
    }
    
    /**
     * Enables kiosk mode with lock task
     */
    fun enableKioskMode(activity: Activity, packageNames: Array<String>? = null) {
        if (!isDeviceAdminActive()) {
            Timber.w("Device admin is not active, requesting permission")
            requestDeviceAdmin(activity)
            return
        }
        
        // Set kiosk mode preferences
        sharedPreferences.edit().putBoolean(PREF_KEY_KIOSK_MODE, true).apply()
        
        if (packageNames != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Set lock task packages
            devicePolicyManager.setLockTaskPackages(adminComponentName, packageNames)
        }
        
        // Enable advanced lock task settings for better kiosk experience
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setupAdvancedKioskPolicies()
        }
        
        // Start lock task mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.startLockTask()
        }
        
        // Hide system UI
        hideSystemUI(activity)
    }
    
    /**
     * Disables kiosk mode
     */
    fun disableKioskMode(activity: Activity) {
        // Clear kiosk mode preferences
        sharedPreferences.edit().putBoolean(PREF_KEY_KIOSK_MODE, false).apply()
        
        // Stop lock task mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (activityManager.lockTaskModeState != ActivityManager.LOCK_TASK_MODE_NONE) {
                activity.stopLockTask()
            }
        }
        
        // Reset advanced kiosk policies if needed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            resetAdvancedKioskPolicies()
        }
        
        // Show system UI
        showSystemUI(activity)
    }
    
    /**
     * Checks if kiosk mode is currently active
     */
    fun isKioskModeActive(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return activityManager.lockTaskModeState != ActivityManager.LOCK_TASK_MODE_NONE
        } else {
            sharedPreferences.getBoolean(PREF_KEY_KIOSK_MODE, false)
        }
    }
    
    /**
     * Verifies admin unlock using PIN
     */
    fun verifyAdminPin(pin: String): Boolean {
        val correctPin = sharedPreferences.getString(PREF_KEY_ADMIN_PIN, DEFAULT_ADMIN_PIN)
        return pin == correctPin
    }
    
    /**
     * Sets admin PIN for unlocking
     */
    fun setAdminPin(pin: String) {
        sharedPreferences.edit().putString(PREF_KEY_ADMIN_PIN, pin).apply()
    }
    
    /**
     * Hides system UI (status bar, navigation bar)
     */
    private fun hideSystemUI(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11+ (API 30+)
            activity.window.setDecorFitsSystemWindows(false)
            activity.window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            // For Android versions before 11
            @Suppress("DEPRECATION")
            activity.window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
            )
        }
        
        // Prevent screenshots and screen recordings for security
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        
        // Keep screen always on
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
    
    /**
     * Shows system UI (status bar, navigation bar)
     */
    private fun showSystemUI(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11+ (API 30+)
            activity.window.setDecorFitsSystemWindows(true)
            activity.window.insetsController?.show(
                WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars()
            )
        } else {
            // For Android versions before 11
            @Suppress("DEPRECATION")
            activity.window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            )
        }
        
        // Remove flags
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }
    
    /**
     * Sets up advanced kiosk policies for Android M and above
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupAdvancedKioskPolicies() {
        if (!isDeviceAdminActive()) return
        
        try {
            // Set system update policy to disable OTA updates
            devicePolicyManager.setSystemUpdatePolicy(
                adminComponentName,
                SystemUpdatePolicy.createWindowedInstallPolicy(0, 0)
            )
            
            // Disable keyguard and status bar
            devicePolicyManager.setKeyguardDisabled(adminComponentName, true)
            devicePolicyManager.setStatusBarDisabled(adminComponentName, true)
            
            // If Android 9.0+ (API 28+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                // Disable removing user restrictions during kiosk mode
                val userManager = context.getSystemService(Context.USER_SERVICE) as UserManager
                // Restrict various user capabilities
                devicePolicyManager.addUserRestriction(
                    adminComponentName,
                    UserManager.DISALLOW_SAFE_BOOT
                )
                devicePolicyManager.addUserRestriction(
                    adminComponentName,
                    UserManager.DISALLOW_FACTORY_RESET
                )
                devicePolicyManager.addUserRestriction(
                    adminComponentName,
                    UserManager.DISALLOW_ADD_USER
                )
                devicePolicyManager.addUserRestriction(
                    adminComponentName,
                    UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA
                )
                devicePolicyManager.addUserRestriction(
                    adminComponentName,
                    UserManager.DISALLOW_ADJUST_VOLUME
                )
            }
        } catch (e: Exception) {
            Timber.e(e, "Error setting up advanced kiosk policies")
        }
    }
    
    /**
     * Resets advanced kiosk policies
     */
    @RequiresApi(Build.VERSION_CODES.M)
    private fun resetAdvancedKioskPolicies() {
        if (!isDeviceAdminActive()) return
        
        try {
            // Reset system update policy
            devicePolicyManager.setSystemUpdatePolicy(adminComponentName, null)
            
            // Enable keyguard and status bar
            devicePolicyManager.setKeyguardDisabled(adminComponentName, false)
            devicePolicyManager.setStatusBarDisabled(adminComponentName, false)
            
            // If Android 9.0+ (API 28+)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                // Remove user restrictions
                devicePolicyManager.clearUserRestriction(
                    adminComponentName,
                    UserManager.DISALLOW_SAFE_BOOT
                )
                devicePolicyManager.clearUserRestriction(
                    adminComponentName,
                    UserManager.DISALLOW_FACTORY_RESET
                )
                devicePolicyManager.clearUserRestriction(
                    adminComponentName,
                    UserManager.DISALLOW_ADD_USER
                )
                devicePolicyManager.clearUserRestriction(
                    adminComponentName,
                    UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA
                )
                devicePolicyManager.clearUserRestriction(
                    adminComponentName,
                    UserManager.DISALLOW_ADJUST_VOLUME
                )
            }
        } catch (e: Exception) {
            Timber.e(e, "Error resetting advanced kiosk policies")
        }
    }
    
    companion object {
        const val REQUEST_CODE_ENABLE_ADMIN = 123
        const val PREF_KEY_KIOSK_MODE = "kiosk_mode_active"
        const val PREF_KEY_ADMIN_PIN = "admin_pin"
        const val DEFAULT_ADMIN_PIN = "1234"
        
        // Helper method to check if we have the necessary permission to put the device in kiosk mode
        fun hasKioskModePermission(context: Context): Boolean {
            val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
            val component = ComponentName(context, KioskDeviceAdminReceiver::class.java)
            
            val permissionStatus = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                dpm.isAffiliationIdPresent || dpm.isDeviceOwnerApp(context.packageName)
            } else {
                dpm.isDeviceOwnerApp(context.packageName)
            }
            
            return permissionStatus && dpm.isAdminActive(component)
        }
    }
} 