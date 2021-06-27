package com.example.expiration

import android.database.sqlite.SQLiteDatabase
import java.util.*

/**
 * Class for communication with database
 *
 */
class DBCommunicator(var database: SQLiteDatabase) {
    // INSERT
    /**
     * Inserts into table products values given in parameters
     * @param productName name of the product
     * @param date expiration date of the product
     */
    fun insertProduct(productName: String, date: String) {
        database.execSQL("INSERT INTO products(name, expiration_date) VALUES('$productName','$date');")
    }

    // SELECT
    /**
     * Selects from table products names of products with given expiration date
     * @param date expiration date of the product
     * @return ArrayList with names of products in Strings
     */
    fun selectProductNamesByDate(date: String): ArrayList<String> {
        val products = ArrayList<String>()
        val resultSet = database.rawQuery(
            "SELECT name FROM products WHERE expiration_date = ?",
            arrayOf(date)
        )
        if (resultSet.moveToFirst()) {
            products.add(resultSet.getString(0))
            while (resultSet.moveToNext()) {
                products.add(resultSet.getString(0))
            }
            resultSet.close()
        }
        return products
    }

    fun selectAllDatesWithOrder(): ArrayList<String> {
        val expirationDates =
            ArrayList<String>()
        val resultSet = database.rawQuery(
            "SELECT DISTINCT expiration_date FROM products ORDER BY expiration_date;",
            null
        )
        if (resultSet.moveToFirst()) {
            expirationDates.add(resultSet.getString(0))
            while (resultSet.moveToNext()) {
                expirationDates.add(resultSet.getString(0))
            }
            resultSet.close()
        }
        return expirationDates
    }

    // DELETE
    fun deleteOneProductByNameAndDate(
        productName: String,
        date: String
    ) {
        database.execSQL(
            "DELETE FROM products WHERE rowid IN " +
                    "(SELECT rowid FROM products WHERE name='" + productName + "' AND expiration_date='" + date + "' LIMIT 1);"
        )
    }

    fun deleteProductsBeforeDate(date: String) {
        database.execSQL("DELETE FROM products WHERE expiration_date < '$date';")
    }


    fun clearDatabase() {
        database.execSQL("DELETE FROM products")
    }

    init {
        database.execSQL("CREATE TABLE IF NOT EXISTS products(id ROWID PRIMARY KEY, name TEXT NOT NULL, expiration_date DATE NOT NULL);")
    }
}