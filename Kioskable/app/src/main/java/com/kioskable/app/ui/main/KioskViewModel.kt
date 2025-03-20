package com.kioskable.app.ui.main

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.kioskable.app.service.kiosk.KioskManager
import com.kioskable.app.service.kiosk.KioskService
import timber.log.Timber

/**
 * ViewModel for managing kiosk mode functionality.
 */
class KioskViewModel(application: Application) : AndroidViewModel(application) {
    
    private val kioskManager = KioskManager(application)
    
    private val _isKioskModeActive = MutableLiveData<Boolean>()
    val isKioskModeActive: LiveData<Boolean> = _isKioskModeActive
    
    private val _isDeviceAdminActive = MutableLiveData<Boolean>()
    val isDeviceAdminActive: LiveData<Boolean> = _isDeviceAdminActive
    
    init {
        checkKioskStatus()
    }
    
    /**
     * Checks the current status of kiosk mode and device admin
     */
    fun checkKioskStatus() {
        _isKioskModeActive.value = kioskManager.isKioskModeActive()
        _isDeviceAdminActive.value = kioskManager.isDeviceAdminActive()
    }
    
    /**
     * Enables kiosk mode for the app
     */
    fun enableKioskMode(activity: android.app.Activity) {
        if (!kioskManager.isDeviceAdminActive()) {
            // Request device admin permission first
            kioskManager.requestDeviceAdmin(activity)
            return
        }
        
        try {
            // Start kiosk service
            startKioskService(activity)
            
            // Enable kiosk mode
            kioskManager.enableKioskMode(activity, arrayOf(activity.packageName))
            
            // Update status
            _isKioskModeActive.value = true
        } catch (e: Exception) {
            Timber.e(e, "Failed to enable kiosk mode")
        }
    }
    
    /**
     * Disables kiosk mode for the app
     */
    fun disableKioskMode(activity: android.app.Activity) {
        try {
            // Disable kiosk mode
            kioskManager.disableKioskMode(activity)
            
            // Stop kiosk service
            stopKioskService(activity)
            
            // Update status
            _isKioskModeActive.value = false
        } catch (e: Exception) {
            Timber.e(e, "Failed to disable kiosk mode")
        }
    }
    
    /**
     * Sets the admin PIN for unlocking kiosk mode
     */
    fun setAdminPin(pin: String) {
        kioskManager.setAdminPin(pin)
    }
    
    /**
     * Verifies the admin PIN
     */
    fun verifyAdminPin(pin: String): Boolean {
        return kioskManager.verifyAdminPin(pin)
    }
    
    /**
     * Starts the kiosk service
     */
    private fun startKioskService(context: Context) {
        val serviceIntent = Intent(context, KioskService::class.java)
        context.startService(serviceIntent)
    }
    
    /**
     * Stops the kiosk service
     */
    private fun stopKioskService(context: Context) {
        val serviceIntent = Intent(context, KioskService::class.java)
        context.stopService(serviceIntent)
    }
} 