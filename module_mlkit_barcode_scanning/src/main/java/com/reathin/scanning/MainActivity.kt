package com.reathin.scanning

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.common.Barcode
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private val cameraExecutor = Executors.newSingleThreadExecutor()  // 专用工作线程
    private val viewFinder by lazy { findViewById<PreviewView>(R.id.viewFinder) }
    private val resultsLayout by lazy { findViewById<LinearLayout>(R.id.resultsLayout) }
    private val resultsContainer by lazy { findViewById<LinearLayout>(R.id.resultsContainer) }


    // 权限请求启动器
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // 权限已授予，启动相机
            startCamera()
        } else {
            // 权限被拒绝，显示提示
            Toast.makeText(this, "需要相机权限才能扫描二维码", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 检查权限并开始拍照
        checkCameraPermissionAndStart()
    }

    private fun checkCameraPermissionAndStart() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                // 权限已授予，直接启动相机
                startCamera()
            }

            else -> {
                // 请求相机权限
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // 创建预览画面
            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(viewFinder.surfaceProvider)
            }

            // 创建二维码识别管道
            val qrAnalyzer = ImageAnalysis.Builder().build().apply {
                setAnalyzer(cameraExecutor, QRCodeAnalyzer { codes ->
                    // 识别结果回调区 ▼
                    runOnUiThread {
                        showScanResults(codes)
                    }
                })
            }

            try {
                // 组装所有部件！启动！
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    CameraSelector.DEFAULT_BACK_CAMERA,  // 使用后置摄像头
                    preview,
                    qrAnalyzer
                )
            } catch (e: Exception) {
                Log.e("CAMERA", "启动失败", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    // 显示扫描结果的核心方法
    private fun showScanResults(codes: List<Barcode>) {
        // 清空之前的结果
        resultsLayout.removeAllViews()

        if (codes.isEmpty()) {
            // 没有扫描到结果时显示提示
            val emptyView = TextView(this).apply {
                text = "🕵️‍♀️ 正在寻找二维码..."
                setTextColor(Color.WHITE)
            }
            resultsLayout.addView(emptyView)
            return
        }

        // 动态添加每个二维码结果
        codes.forEachIndexed { index, barcode ->
            val resultView = createResultView(barcode, index)
            resultsLayout.addView(resultView)
        }
    }

    // 创建单个结果视图
    private fun createResultView(barcode: Barcode, index: Int): TextView {
        return TextView(this).apply {
            // 解析二维码内容
            val content = barcode.rawValue ?: "未知内容"

            // 格式化显示文本
            text =
                "✅ 二维码 ${index + 1}:${content.take(50)}${if (content.length > 50) "..." else ""}"
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            textSize = 14f

            // 添加点击事件查看完整内容
            setOnClickListener {
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("二维码详情")
                    .setMessage(content)
                    .setPositiveButton("复制") { _, _ ->
                        copyToClipboard(content)
                        Toast.makeText(this@MainActivity, "已复制到剪贴板", Toast.LENGTH_SHORT)
                            .show()
                    }
                    .setNegativeButton("关闭", null)
                    .show()
            }
        }
    }

    // 复制到剪贴板工具方法
    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("二维码内容", text)
        clipboard.setPrimaryClip(clip)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}