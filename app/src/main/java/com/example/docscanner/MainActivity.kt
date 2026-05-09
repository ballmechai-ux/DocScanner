package com.example.docscanner
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<ExtendedFloatingActionButton>(R.id.fabScan).setOnClickListener {
            startActivity(Intent(this, CameraActivity::class.java))
        }
    }
}
