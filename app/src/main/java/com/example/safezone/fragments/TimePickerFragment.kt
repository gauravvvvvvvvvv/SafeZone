package com.example.safezone.fragments

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*

/**
 * DialogFragment for picking a time
 */
class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {
    
    private var onTimeSetListener: ((hourOfDay: Int, minute: Int) -> Unit)? = null
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current time as the default values for the picker
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        
        // Create a new instance of TimePickerDialog and return it
        return TimePickerDialog(
            requireContext(),
            this,
            hour,
            minute,
            DateFormat.is24HourFormat(requireContext())
        )
    }
    
    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        // Do something with the time chosen by the user
        onTimeSetListener?.invoke(hourOfDay, minute)
    }
    
    /**
     * Sets a callback for when a time is selected
     */
    fun setOnTimeSetListener(listener: (hourOfDay: Int, minute: Int) -> Unit) {
        onTimeSetListener = listener
    }
    
    companion object {
        const val TAG = "TimePickerFragment"
    }
}
