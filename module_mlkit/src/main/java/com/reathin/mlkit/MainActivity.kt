package com.reathin.mlkit

import android.R.attr.bitmap
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceDetectorOptions.PERFORMANCE_MODE_FAST
import com.reathin.mlkit.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(mBinding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1、配置人脸检测器
        val faceDetectorOptions = FaceDetectorOptions.Builder()
            .setPerformanceMode(PERFORMANCE_MODE_FAST).build()
        //2、获取人脸检测器
        val detector: FaceDetector = FaceDetection.getClient(faceDetectorOptions)

        mBinding.button.setOnClickListener {
            // 3、从资源中加载图片
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.image)
            val image = InputImage.fromBitmap(bitmap, 0)
            detector.process(image).addOnSuccessListener {
                mBinding.imageView.setImageBitmap(drawWithRectangle(bitmap, it))
            }.addOnFailureListener {
                Toast.makeText(this@MainActivity, "检测失败", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun drawWithRectangle(bitmap: Bitmap, faces: List<Face>): Bitmap {
        //复制一个新的Bitmap
        val copiedBitmap: Bitmap = bitmap.copy(bitmap.getConfig(), true)
        for (face in faces) {
            //获取边界状态
            val bounds: Rect = face.boundingBox
            // 初始化Paint
            val paint = Paint()
            // 设置矩形颜色
            paint.setColor(Color.GREEN)
            // 设置绘制样式为轮廓绘制
            paint.style = Paint.Style.STROKE
            // 设置为你需要的宽度
            paint.strokeWidth = 6f
            val canvas = Canvas(copiedBitmap)
            canvas.drawRect(bounds, paint)
        }
        return copiedBitmap
    }
}