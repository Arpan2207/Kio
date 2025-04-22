package com.kioskable.app.ui.common.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.kioskable.app.R
import com.kioskable.app.service.kiosk.KioskManager

/**
 * Dialog for admin unlock to exit kiosk mode.
 * Requires PIN authentication.
 */
class AdminUnlockDialog : DialogFragment() {
    
    private lateinit var kioskManager: KioskManager
    private var onUnlockSuccessListener: (() -> Unit)? = null
    
    override fun onAttach(context: Context) {
        super.onAttach(context)
        kioskManager = KioskManager(context)
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_admin_unlock, null)
        
        val pinEditText = view.findViewById<EditText>(R.id.pinEditText)
        val unlockButton = view.findViewById<Button>(R.id.unlockButton)
        val cancelButton = view.findViewById<Button>(R.id.cancelButton)
        
        unlockButton.setOnClickListener {
            val pin = pinEditText.text.toString()
            if (kioskManager.verifyAdminPin(pin)) {
                dismiss()
                onUnlockSuccessListener?.invoke()
            } else {
                Toast.makeText(context, "Invalid PIN", Toast.LENGTH_SHORT).show()
                pinEditText.setText("")
            }
        }
        
        cancelButton.setOnClickListener {
            dismiss()
        }
        
        builder.setView(view)
        builder.setTitle("Admin Unlock")
        
        val dialog = builder.create()
        
        // Show keyboard automatically
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        
        return dialog
    }
    
    /**
     * Sets a listener for successful unlock
     */
    fun setOnUnlockSuccessListener(listener: () -> Unit) {
        onUnlockSuccessListener = listener
    }
    
    companion object {
        const val TAG = "AdminUnlockDialog"
        
        fun newInstance(): AdminUnlockDialog {
            return AdminUnlockDialog()
        }
    }
} 