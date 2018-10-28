package com.example.user.androiddemdo

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast


class SQLite(var context: Context) {

    private var dbopenhelper: SQLiteOpenHelper? = null
    private var db: SQLiteDatabase? = null

    companion object {
        var createsql = ""
        var DBName = ""
        var Version: Int = 0
    }

    private inner class SQLiteHelper(context: Context, DBName: String, Version: Int) : SQLiteOpenHelper(context, DBName, null, Version) {

        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL(createsql)
        }

        override fun onUpgrade(db: SQLiteDatabase, oldversion: Int, currentversion: Int) {
            db.execSQL("DROP TABLE QR実績")
            onCreate(db)
        }
    }

    private fun openWritebleSQLite() {
        dbopenhelper = SQLiteHelper(this.context, DBName, Version)
        db = dbopenhelper!!.writableDatabase
    }

    private fun openReadbleSQLite() {
        dbopenhelper = SQLiteHelper(this.context, DBName, Version)
        db = dbopenhelper!!.readableDatabase
    }

    fun closeSQLite() {
        dbopenhelper!!.close()
        db!!.close()
    }

    fun insert(TableName: String, Columns: String, Value: String) {

        try {
            openWritebleSQLite()

            val sql:String = "INSERT INTO $TableName ($Columns) VALUES(?)"
            val statement = db!!.compileStatement(sql)
            try {
                statement.bindString(1, Value)
                statement.executeInsert()
            } catch (e: Exception) {

            } finally {
                statement.close()
            }

        } finally {
            closeSQLite()
        }
    }

    fun select(Table: String, Columns: String): Cursor? {
        try {
            openReadbleSQLite()
            val cursor:Cursor? = db!!.rawQuery("SELECT $Columns FROM $Table", null)
            return cursor
        } catch (e: Exception) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
            return null
        }

    }

    fun execute(SQL: String) {

        try {
            openReadbleSQLite()
            db!!.execSQL(SQL)
        } catch (e: Exception) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show()
        } finally {
            closeSQLite()
        }

    }


}





