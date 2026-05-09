package com.example.docscanner
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
class CameraActivity : AppCompatActivity() {
    private lateinit var previewView: PreviewView
    private lateinit var btnCapture: MaterialButton
    private lateinit var btnBack: ImageButton
    private lateinit var btnFlash: ImageButton
    private var imageCapture: ImageCapture? = null
    private var flashEnabled = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        previewView = findViewById(R.id.previewView)
        btnCapture = findViewById(R.id.btnCapture)
        btnBack = findViewById(R.id.btnBack)
        btnFlash = findViewById(R.id.btnFlash)
        startCamera()
        btnCapture.setOnClickListener { takePhoto() }
        btnBack.setOnClickListener { finish() }
        btnFlash.setOnClickListener { toggleFlash() }
    }
    private fun startCamera() {
        val future = ProcessCameraProvider.getInstance(this)
        future.addListener({
            val provider = future.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY).build()
            provider.unbindAll()
            provider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageCapture)
        }, ContextCompat.getMainExecutor(this))
    }
    private fun takePhoto() {
        val capture = imageCapture ?: return
        val file = File(getExternalFilesDir(null),
            "SCAN_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg")
        capture.takePicture(
            ImageCapture.OutputFileOptions.Builder(file).build(),
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    startActivity(Intent(this@CameraActivity, CropActivity::class.java)
                        .putExtra("imagePath", file.absolutePath))
                }
                override fun onError(exc: ImageCaptureException) {
                    Log.e("Camera", "failed: ${exc.message}")
                }
            })
    }
    private fun toggleFlash() {
        flashEnabled = !flashEnabled
        imageCapture?.flashMode = if (flashEnabled)
            ImageCapture.FLASH_MODE_ON else ImageCapture.FLASH_MODE_OFF
    }
}
