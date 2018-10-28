package com.example.user.androiddemdo

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import java.net.URL
import java.net.URLConnection

class MainActivity : AppCompatActivity() {

    private var DB: SQLite? = null
    private val REQUEST_PERMISSION = 1000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SQLite.createsql = "CREATE TABLE QR実績 (ID INTEGER,QR TEXT,PRIMARY KEY(ID));"
        SQLite.DBName = "AndroidDemo.db"
        SQLite.Version = 2
        DB = SQLite(applicationContext)

        checkPermission()

        val job = async(UI) {
            var httpclient = HttpURLCommunicate()
            Tx_Device.text =  httpclient.HttpGet(URL("https://httpbin.org/ip"))
        }
        Bt_QR.setOnClickListener { view -> IntentIntegrator(view.context as Activity).initiateScan() }

        Bt_test.setOnClickListener { view ->
            FileIO.DB = DB
            val f = Intent(view.context, FileIO::class.java)
            startActivity(f)
        }
    }

    fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this@MainActivity,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                        REQUEST_PERMISSION)

            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                val resultdata = result.contents
                Toast.makeText(this, "Scan:$resultdata", Toast.LENGTH_LONG).show()
                DB!!.insert("QR実績", "QR", resultdata)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

}
