package com.reathin.exif

import android.Manifest
import android.content.Intent
import android.media.ExifInterface
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.Metadata
import com.drew.metadata.MetadataException
import com.drew.metadata.exif.ExifIFD0Directory
import com.reathin.exif.databinding.ActivityMainBinding
import java.io.File
import java.io.IOException


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
        mBinding.btnPhoto.setOnClickListener {
            // 创建Intent来选择照片
            // 创建Intent来选择照片
            val intent = Intent(Intent.ACTION_PICK)
            intent.setType("image/*")
            startActivityForResult(intent, 2024)
        }

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            1000
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2024 && resultCode == RESULT_OK && data != null) {
            val selectedImageUri = data.data
            // 使用selectedImageUri处理照片，例如显示在ImageView中
            mBinding.ivPhoto.setImageURI(selectedImageUri)
            if (selectedImageUri == null) {
                return
            }
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(selectedImageUri, filePathColumn, null, null, null)
            if (cursor != null) {
                cursor.moveToFirst()
                val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                val picturePath = cursor.getString(columnIndex)
                // picturePath就是图片的路径
                mBinding.tvDataApi.text = getExifData(picturePath)
                mBinding.tvDataMetadata.text = readExifWithMetadataExtractor(picturePath)
                cursor.close()
            }
        }
    }

    /**
     * 使用原生API
     */
    private fun getExifData(imageFilePath: String?): String {
        return try {
            val exifInterface = ExifInterface(imageFilePath!!)

            // 读取特定标签的EXIF数据
            val datetime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME)
            val cameraMake = exifInterface.getAttribute(ExifInterface.TAG_MAKE)
            val cameraModel = exifInterface.getAttribute(ExifInterface.TAG_MODEL)
            //读取其他信息...
            //exifInterface.getAttribute(ExifInterface.TAG_APERTURE_VALUE)
            // 返回EXIF数据或错误信息
            "使用原生API读取EXIF数据:\nDate: $datetime\nCamera Make: $cameraMake\nCamera Model: $cameraModel"
        } catch (e: IOException) {
            e.printStackTrace()
            "读取EXIF数据失败"
        }
    }

    private fun readExifWithMetadataExtractor(imageFilePath: String): String {
        return try {
            val imageFile = File(imageFilePath)
            val metadata: Metadata? = ImageMetadataReader.readMetadata(imageFile)
            val exifIFD0Directory: ExifIFD0Directory? = metadata?.getFirstDirectoryOfType(
                ExifIFD0Directory::class.java
            )
            if (exifIFD0Directory != null) {
                // 获取并打印特定的EXIF标签
                val dateTime: String = exifIFD0Directory.getString(ExifIFD0Directory.TAG_DATETIME)
                val cameraMake = exifIFD0Directory.getString(ExifIFD0Directory.TAG_MAKE)
                val cameraModel = exifIFD0Directory.getString(ExifIFD0Directory.TAG_MODEL)

                // 返回EXIF数据
                "使用第三方读取EXIF数据:\nDate: $dateTime\nCamera Make: $cameraMake\nCamera Model: $cameraModel"
            } else {
                "没有EXIF数据"
            }
        } catch (e: IOException) {
            e.printStackTrace()
            "读取EXIF数据失败"
        } catch (e: MetadataException) {
            e.printStackTrace()
            "读取EXIF数据失败"
        }
    }
}