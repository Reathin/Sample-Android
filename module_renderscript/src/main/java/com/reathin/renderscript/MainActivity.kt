package com.reathin.renderscript

import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.reathin.rendereffect.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val imageView1 = findViewById<ImageView>(R.id.iv_image_1)
        val imageView2 = findViewById<ImageView>(R.id.iv_image_2)
        val imageView3 = findViewById<ImageView>(R.id.iv_image_3)
        val imageView4 = findViewById<ImageView>(R.id.iv_image_4)

        val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.image_beauty)

        val bitmap1 = BlurUtils.rsBlur(this, bitmap, 10f)
        val bitmap2 =
            BlurUtils.scriptBlur(this, bitmap, bitmap.getWidth(), bitmap.getHeight(), 15f)
        val bitmap3 =
            BlurUtils.scriptBlur(this, bitmap, bitmap.getWidth(), bitmap.getHeight(), 20f)
        val bitmap4 =
            BlurUtils.scriptBlur(this, bitmap, bitmap.getWidth(), bitmap.getHeight(), 25f)

        imageView1.setImageBitmap(bitmap)
        imageView2.setImageBitmap(bitmap2)
        imageView3.setImageBitmap(bitmap3)
        imageView4.setImageBitmap(bitmap4)

        //设置模糊效果 API31以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            val blurEffect1 = RenderEffect.createBlurEffect(1f, 0f, Shader.TileMode.REPEAT)
//            imageView1.setRenderEffect(blurEffect1)
        }
    }
}