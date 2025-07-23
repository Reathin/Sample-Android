package com.reathin.scanning

import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

class QRCodeAnalyzer(private val onDetect: (List<Barcode>) -> Unit) : ImageAnalysis.Analyzer {

    // 设置只识别二维码（避免误认条形码）
    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()

    private val scanner = BarcodeScanning.getClient(options)  // 创建识别器实例

    @ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            // 将相机画面转为可识别格式
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            scanImage(image, imageProxy)  // 开始扫描！
        } else {
            // 如果图像为空，直接关闭 imageProxy
            imageProxy.close()
        }
    }

    private fun scanImage(image: InputImage, imageProxy: ImageProxy) {
        scanner.process(image)
            .addOnSuccessListener { codes ->
                onDetect(codes)  // 传递图像尺寸
            }
            .addOnFailureListener { exception ->
                // 处理识别失败的情况
                exception.printStackTrace()
            }
            .addOnCompleteListener {
                imageProxy.close()  // 关闭当前帧，准备下一帧
            }
    }
}