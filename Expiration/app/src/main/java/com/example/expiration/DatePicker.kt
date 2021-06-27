package com.example.expiration

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import java.util.*

class DatePicker : DialogFragment, OnDateSetListener {
    private var day: Int
    private var month: Int
    private var year: Int
    private var dateText: EditText? = null

    constructor(editText: EditText?) {
        val c = Calendar.getInstance()
        c.add(Calendar.DAY_OF_MONTH, 1)
        year = c[Calendar.YEAR]
        month = c[Calendar.MONTH]
        day = c[Calendar.DAY_OF_MONTH]
        dateText = editText
        putDateIntoEditText()
    }

    constructor(editText: EditText?, day: Int, month: Int, year: Int) {
        this.year = year
        this.month = month - 1
        this.day = day
        dateText = editText
        putDateIntoEditText()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return DatePickerDialog(requireActivity(), this, year, month, day)
    }

    override fun onDateSet(
        datePicker: DatePicker,
        i: Int,
        i1: Int,
        i2: Int
    ) {
        day = i2
        month = i1
        year = i
        if (dateText != null) {
            putDateIntoEditText()
        }
    }

    private fun putDateIntoEditText() {
        var newDate = ""
        if (day < 10) {
            newDate += "0"
        }
        newDate += "$day."
        if (month < 9) {
            newDate += "0"
        }
        newDate += (month + 1).toString() + "." + year
        dateText!!.setText(newDate)
    }
}