package com.example.user.androiddemdo

import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast

import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class FileIO : AppCompatActivity() {

    private var directoryName = ""
    private var Tx_DirectoryName: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fileio)
        Tx_DirectoryName = findViewById(R.id.Tx_DirectoryName)
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(editable: Editable) {
                directoryName = ""
                directoryName = Tx_DirectoryName!!.text.toString()
                if (directoryName !== "") directoryName = File.separator + directoryName
            }
        }
        Tx_DirectoryName!!.addTextChangedListener(textWatcher)
        findViewById<View>(R.id.Bt_Copy).setOnClickListener { view ->
            val sourceFilePath = Environment.getExternalStorageDirectory().toString() + directoryName + File.separator + "test.CSV"
            val targetFilePath = filesDir.toString() + File.separator + "test.CSV"
            try {
                val message = if (copyFile(sourceFilePath, targetFilePath)) "コピー成功" else "コピー失敗"
                Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        findViewById<View>(R.id.Bt_Delete).setOnClickListener { view ->
            val deleteFilePath = Environment.getExternalStorageDirectory().toString() + directoryName + File.separator + "test.CSV"
            val message = if (deleteInputFile(deleteFilePath)) "削除成功" else "削除失敗"
            Toast.makeText(view.context, message, Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.Bt_Create).setOnClickListener { exportSQLitetoCSV() }
    }

    private fun deleteInputFile(deleteFilePath: String): Boolean {
        val deleteFile = File(deleteFilePath)
        if (!deleteFile.exists()) Toast.makeText(this, "ファイル無し", Toast.LENGTH_SHORT).show()
        return deleteFile.delete()
    }

    @Throws(IOException::class)
    private fun copyFile(sourcePath: String, targetPath: String): Boolean {
        var inStream: FileInputStream? = null
        var outStream: FileOutputStream? = null

        try {
            val inputFile = File(sourcePath)
            if (!inputFile.exists()) {
                Toast.makeText(this, "ファイル無し", Toast.LENGTH_SHORT).show()
                return false
            }

            inStream = FileInputStream(inputFile)
            outStream = FileOutputStream(File(targetPath))

            val inChannel = inStream.channel
            val outChannel = outStream.channel

            var pos: Long = 0
            while (pos < inChannel.size()) {
                pos += inChannel.transferTo(pos, inChannel.size(), outChannel)
            }
        } catch (e: FileNotFoundException) {
            return false
        } catch (e: IOException) {
            return false
        } finally {
            if (inStream != null) inStream.close()
            if (outStream != null) outStream.close()
        }
        return true
    }

    protected fun exportSQLitetoCSV() {

        var data = ""
        val BR = System.getProperty("line.separator")
        val c = DB!!.select("QR実績", "ID,QR")
        if (c == null) {
            Toast.makeText(this, "Data:", Toast.LENGTH_LONG).show()
        }
        while (c!!.moveToNext()) {
            data += (c.getString(c.getColumnIndex("ID")) + ","
                    + c.getString(c.getColumnIndex("QR")) + BR)
        }
        c.close()
        DB!!.closeSQLite()

        val directoryPath = Environment.getExternalStorageDirectory().toString() + directoryName
        if (!File(directoryPath).exists()) {
            File(directoryPath).mkdirs()
        }
        val file = File(directoryPath, "test.csv")
        try {
            var stream: FileOutputStream? =  FileOutputStream(file)
            stream!!.write(data.toByteArray())
            stream.close()
            Toast.makeText(this, "出力完了", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }

    }

    companion object {

        var DB: SQLite? = null
    }
}

