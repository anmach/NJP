package com.example.expiration

import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.FileReader
import java.io.IOException
import java.util.*

class CSVFile(var path: String) {
    fun read(): ArrayList<Array<String>> {
        val wordsFromFile =
            ArrayList<Array<String>>()
        var reader: BufferedReader? = null
        reader = try {
            BufferedReader(FileReader(path))
        } catch (e: FileNotFoundException) {
            return wordsFromFile
        }
        try {
            var csvLine: String
            while (reader.readLine().also { csvLine = it } != null) {
                val row = csvLine.split(",".toRegex()).toTypedArray()
                wordsFromFile.add(row)
            }
            reader.close()
        } catch (ex: IOException) {
            throw RuntimeException("Error in reading CSV file: $ex")
        }
        return wordsFromFile
    }

    fun readProductDataPairs(): ArrayList<Array<String>> {
        val wordsFromFile = read()
        val pairs =
            ArrayList<Array<String>>()
        var name: String
        var date: String
        val dateManager = DateManager()
        var iter: Int
        for (words in wordsFromFile) {
            iter = 0
            name = ""
            date = ""

            // It has to be {name, date} or {date, name}
            while (iter < words.size) {
                if (dateManager.isItDBDate(words[iter])) {
                    date = words[iter]
                } else if (dateManager.isItDottedDMYDate(words[iter])) {
                    date = dateManager.fromDottedDMYToDBDate(words[iter])
                } else {
                    name = words[iter]
                }
                if (name !== "" && date !== "") {
                    pairs.add(arrayOf(name, date))
                    name = ""
                    date = ""
                }
                iter++
            }
        }
        return pairs
    }

}