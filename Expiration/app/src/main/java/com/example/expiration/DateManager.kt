package com.example.expiration

import java.util.*

/**
 * Class for converting and checking dates and date types
 *
 */
class DateManager {
    fun fromDBDateToCalendar(dbDate: String): Calendar? {
        val splitDate = dbDate.split("-".toRegex(), 6).toTypedArray()
        if (isItYMDDate(splitDate)) {
            val day: Int
            val month: Int
            val year: Int
            year = splitDate[0].toInt()
            month = splitDate[1].toInt()
            day = splitDate[2].toInt()
            val date = Calendar.getInstance()
            date[Calendar.DAY_OF_MONTH] = day
            date[Calendar.MONTH] = month
            date[Calendar.YEAR] = year
        }
        return null
    }

    /**
     * @param dbDate date in format YYYY-MM-DD
     * @return date in format DD.MM.YYYY
     */
    fun fromDBDateToDottedDMY(dbDate: String): String {
        var normalDate = ""
        val splitDate = dbDate.split("-".toRegex(), 6).toTypedArray()
        if (isItYMDDate(splitDate)) {
            val day: Int
            val month: Int
            val year: Int
            year = splitDate[0].toInt()
            month = splitDate[1].toInt()
            day = splitDate[2].toInt()
            if (day < 10) {
                normalDate += "0"
            }
            normalDate += "$day."
            if (month < 10) {
                normalDate += "0"
            }
            normalDate += "$month.$year"
        } else {
            return ""
        }
        return normalDate
    }

    /**
     * @param normalDate date in format DD.MM.YYYY
     * @return date in format YYYY-MM-DD
     */
    fun fromDottedDMYToDBDate(normalDate: String): String {
        var dbDate = ""
        val splitDate =
            normalDate.split("\\.".toRegex(), 6).toTypedArray()
        if (isItDMYDate(splitDate)) {
            try {
                val day: Int
                val month: Int
                val year: Int
                year = splitDate[2].toInt()
                month = splitDate[1].toInt()
                day = splitDate[0].toInt()
                dbDate = "$year-"
                if (month < 10) {
                    dbDate += "0"
                }
                dbDate += "$month-"
                if (day < 10) {
                    dbDate += "0"
                }
                dbDate += day
            } catch (nfe: NumberFormatException) {
            }
        }
        return dbDate
    }

    /**
     * Method converts date to different format and check if it's in future
     * @param normalDate date in format DD.MM.YYYY
     * @return date in format YYYY-MM-DD
     */
    fun fromDottedDMYToDBDateWithFutureDateCheck(normalDate: String): String {
        var dbDate = ""
        val splitDate =
            normalDate.split("\\.".toRegex(), 6).toTypedArray()
        if (splitDate.size == 3) {
            try {
                val day: Int
                val month: Int
                val year: Int
                year = splitDate[2].toInt()
                month = splitDate[1].toInt()
                day = splitDate[0].toInt()
                val date = Calendar.getInstance()
                date[Calendar.DAY_OF_MONTH] = day
                date[Calendar.MONTH] = month - 1
                date[Calendar.YEAR] = year
                if (!futureDateCheck(date)) {
                    return ""
                }
                if (!dateExistCheck(date)) {
                    return ""
                }
                dbDate = "$year-"
                if (month < 10) {
                    dbDate += "0"
                }
                dbDate += "$month-"
                if (day < 10) {
                    dbDate += "0"
                }
                dbDate += day
            } catch (nfe: NumberFormatException) {
            }
        }
        return dbDate
    }

    /**
     *
     * @param day
     * @param month
     * @param year
     * @return date in format YYYY-MM-DD
     */
    fun fromIntsToDBDate(day: Int, month: Int, year: Int): String {
        var dbDate = "$year-"
        if (month < 9) {
            dbDate += "0"
        }
        dbDate += "$month-"
        if (day < 10) {
            dbDate += "0"
        }
        dbDate += day
        return dbDate
    }

    /**
     *
     * @param date date in format DD.MM.YYYY
     * @return int[] with day, month and year
     */
    fun fromDottedDMYDateToDMYInts(date: String): IntArray {
        var day = 1
        var month = 1
        var year = 2000
        val splitDate = date.split("\\.".toRegex(), 6).toTypedArray()
        if (splitDate.size == 3) {
            try {
                day = splitDate[0].toInt()
                month = splitDate[1].toInt()
                year = splitDate[2].toInt()
            } catch (nfe: NumberFormatException) {
            }
        }
        return intArrayOf(day, month, year)
    }

    /**
     * Check if given String is date in format YYYY-MM-DD
     * @param date
     * @return true if it is date in this format
     */
    fun isItDBDate(date: String): Boolean {
        val splitDate = date.split("-".toRegex(), 6).toTypedArray()
        return if (isItYMDDate(splitDate)) {
            true
        } else {
            false
        }
    }

    /**
     * Check if given String is date in format DD.MM.YYYY
     * @param date
     * @return true if it is date in this format
     */
    fun isItDottedDMYDate(date: String): Boolean {
        val splitDate = date.split("\\.".toRegex(), 6).toTypedArray()
        return if (isItDMYDate(splitDate)) {
            true
        } else {
            false
        }
    }

    /**
     * Check if given String[] contains day, month and year and if it is valid date
     * @param splitDate
     * @return true if it is date
     */
    fun isItDMYDate(splitDate: Array<String>): Boolean {
        return if (splitDate.size == 3) {
            try {
                val day: Int
                val month: Int
                val year: Int
                year = splitDate[2].toInt()
                month = splitDate[1].toInt()
                day = splitDate[0].toInt()
                val date = Calendar.getInstance()
                date[Calendar.DAY_OF_MONTH] = day
                date[Calendar.MONTH] = month - 1
                date[Calendar.YEAR] = year
                if (!dateExistCheck(date)) {
                    return false
                }
                true
            } catch (nfe: NumberFormatException) {
                false
            }
        } else {
            false
        }
    }

    /**
     * Check if given String[] contains year, month and day and if it is valid date
     * @param splitDate
     * @return true if it is date
     */
    fun isItYMDDate(splitDate: Array<String>): Boolean {
        return if (splitDate.size == 3) {
            try {
                val day: Int
                val month: Int
                val year: Int
                year = splitDate[0].toInt()
                month = splitDate[1].toInt()
                day = splitDate[2].toInt()
                val date = Calendar.getInstance()
                date[Calendar.DAY_OF_MONTH] = day
                date[Calendar.MONTH] = month - 1
                date[Calendar.YEAR] = year
                if (!dateExistCheck(date)) {
                    return false
                }
                true
            } catch (nfe: NumberFormatException) {
                false
            }
        } else {
            false
        }
    }

    /**
     * Check if date given in parameters is in future
     * @param date
     * @return true if it is future date
     */
    fun futureDateCheck(date: Calendar): Boolean {
        val today = Calendar.getInstance()
        return if (date.timeInMillis > today.timeInMillis) {
            true
        } else {
            false
        }
    }

    /**
     * Check if String given in parameters is date, what's more, future date
     * @param dbDate
     * @return true if it is future date
     */
    fun dbDateFutureDateCheck(dbDate: String): Boolean {
        val date = fromDBDateToCalendar(dbDate)
        if (date != null) {
            if (futureDateCheck(date)) {
                return true
            }
        }
        return false
    }

    /**
     * Check if day, month and year given in parameters make date, what's more, future date
     * @param day
     * @param month
     * @param year
     * @return true if it is future date
     */
    fun intFutureDateCheck(day: Int, month: Int, year: Int): Boolean {
        val cal = Calendar.getInstance()
        cal[Calendar.DAY_OF_MONTH] = day
        cal[Calendar.MONTH] = month
        cal[Calendar.YEAR] = year
        return if (futureDateCheck(cal)) {
            true
        } else {
            false
        }
    }

    /**
     * Checks if date given in parameters is valid date
     * @param date maybe a valid date
     * @return true if it's valid date
     */
    private fun dateExistCheck(date: Calendar): Boolean {
        try {
            val numb = date.timeInMillis
        } catch (e: Exception) {
            return false
        }
        return true
    }
}