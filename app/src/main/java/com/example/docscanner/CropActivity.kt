package com.example.docscanner
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import java.io.File
class CropActivity : AppCompatActivity() {
    private lateinit var imgDocument: ImageView
    private lateinit var cropView: CropOverlayView
    private lateinit var btnConfirm: MaterialButton
    private lateinit var btnRetake: MaterialButton
    private var imagePath = ""
    private var originalBitmap: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop)
        imagePath = intent.getStringExtra("imagePath") ?: ""
        imgDocument = findViewById(R.id.imgDocument)
        cropView = findViewById(R.id.cropOverlay)
        btnConfirm = findViewById(R.id.btnConfirm)
        btnRetake = findViewById(R.id.btnRetake)
        originalBitmap = BitmapFactory.decodeFile(imagePath)
        imgDocument.setImageBitmap(originalBitmap)
        btnRetake.setOnClickListener { finish() }
        btnConfirm.setOnClickListener { cropAndNext() }
    }
    private fun cropAndNext() {
        val bitmap = originalBitmap ?: return
        val rect = cropView.getCropRect()
        val scaleX = bitmap.width.toFloat() / imgDocument.width
        val scaleY = bitmap.height.toFloat() / imgDocument.height
        val left = (rect.left * scaleX).toInt().coerceIn(0, bitmap.width)
        val top = (rect.top * scaleY).toInt().coerceIn(0, bitmap.height)
        val width = (rect.width() * scaleX).toInt().coerceIn(1, bitmap.width - left)
        val height = (rect.height() * scaleY).toInt().coerceIn(1, bitmap.height - top)
        val cropped = Bitmap.createBitmap(bitmap, left, top, width, height)
        val cropFile = File(getExternalFilesDir(null), "CROP_${System.currentTimeMillis()}.jpg")
        cropFile.outputStream().use { cropped.compress(Bitmap.CompressFormat.JPEG, 95, it) }
        startActivity(Intent(this, PreviewActivity::class.java)
            .putExtra("croppedPath", cropFile.absolutePath))
    }
}
