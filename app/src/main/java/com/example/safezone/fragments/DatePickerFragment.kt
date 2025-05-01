package com.example.safezone.fragments

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.*

/**
 * DialogFragment for picking a date
 */
class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {
    
    private var onDateSetListener: ((year: Int, month: Int, day: Int) -> Unit)? = null
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // Use the current date as the default date in the picker
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        
        // Create a new instance of DatePickerDialog and return it
        return DatePickerDialog(requireContext(), this, year, month, day)
    }
    
    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        // Do something with the date chosen by the user
        onDateSetListener?.invoke(year, month, day)
    }
    
    /**
     * Sets a callback for when a date is selected
     */
    fun setOnDateSetListener(listener: (year: Int, month: Int, day: Int) -> Unit) {
        onDateSetListener = listener
    }
    
    companion object {
        const val TAG = "DatePickerFragment"
    }
}
