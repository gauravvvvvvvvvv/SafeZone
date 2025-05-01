package com.example.safezone.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.safezone.R

object CustomToast {
    
    fun showSuccessToast(context: Context, message: String) {
        show(context, message, R.color.safe)
    }
    
    fun showWarningToast(context: Context, message: String) {
        show(context, message, R.color.warning)
    }
    
    fun showErrorToast(context: Context, message: String) {
        show(context, message, R.color.danger)
    }
    
    private fun show(context: Context, message: String, colorRes: Int) {
        val inflater = LayoutInflater.from(context)
        val layout: View = inflater.inflate(R.layout.custom_toast, null)
        
        val text: TextView = layout.findViewById(R.id.text_toast_message)
        text.text = message
        
        // Set background color
        layout.setBackgroundColor(context.getColor(colorRes))
        
        val toast = Toast(context)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout
        toast.show()
    }
}
