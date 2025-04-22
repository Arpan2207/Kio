package com.kioskable.app.service.kiosk

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import timber.log.Timber

/**
 * DeviceAdminReceiver implementation that handles device admin policy control.
 * This is necessary for implementing lock task mode (kiosk mode) with full functionality.
 */
class KioskDeviceAdminReceiver : DeviceAdminReceiver() {
    
    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
        Timber.i("Device admin enabled")
        Toast.makeText(context, "Device admin enabled", Toast.LENGTH_SHORT).show()
    }
    
    override fun onDisabled(context: Context, intent: Intent) {
        super.onDisabled(context, intent)
        Timber.i("Device admin disabled")
        Toast.makeText(context, "Device admin disabled", Toast.LENGTH_SHORT).show()
    }
    
    override fun onLockTaskModeEntering(context: Context, intent: Intent, pkg: String) {
        super.onLockTaskModeEntering(context, intent, pkg)
        Timber.i("Lock task mode entering")
        Toast.makeText(context, "Entering kiosk mode", Toast.LENGTH_SHORT).show()
    }
    
    override fun onLockTaskModeExiting(context: Context, intent: Intent) {
        super.onLockTaskModeExiting(context, intent)
        Timber.i("Lock task mode exiting")
        Toast.makeText(context, "Exiting kiosk mode", Toast.LENGTH_SHORT).show()
    }
    
    companion object {
        // Key for the admin preferences
        const val PREF_KEY_ADMIN_ACTIVE = "device_admin_active"
    }
} 