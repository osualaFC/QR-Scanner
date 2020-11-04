package com.example.qrscannerapp

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import kotlinx.android.synthetic.main.activity_main.*

private const val REQUEST_CODE = 1

class MainActivity : AppCompatActivity() {

    private lateinit var codeScanner: CodeScanner
    private lateinit var str: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setUpPermission()
        scanCode()

        str = textView.text.toString()
        if(Patterns.WEB_URL.matcher(str).matches()) {
            textView.setTextColor(resources.getColor(R.color.colorPrimary))
        }

        textView.setOnClickListener {

                intent = Intent(this, DetailsActivity::class.java)
                intent.putExtra("str", str)
                startActivity(intent)

        }
    }

    private fun scanCode(){

        codeScanner = CodeScanner(this, scanner_view)

        codeScanner.apply{
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS
            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false


            decodeCallback = DecodeCallback {
                runOnUiThread {
                    textView.text = it.text
                }
            }

            errorCallback = ErrorCallback {
                runOnUiThread {
                    Log.i("Main", "scanCode: Camera initialization error ${it.message}")
                }
            }

            scanner_view.setOnClickListener {
                codeScanner.startPreview()
            }
        }
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    private fun setUpPermission(){
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)

        if(permission != PackageManager.PERMISSION_GRANTED){
            makeRequest()
        }
    }

    private fun makeRequest(){
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "You need camera permission to be able to use this app", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}