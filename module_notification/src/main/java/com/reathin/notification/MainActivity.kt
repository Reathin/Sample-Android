package com.reathin.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_NOTIFICATION_PERMISSION = 100
    }

    private var currentProgress = 0
    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval = 5000L // 5秒更新一次

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 添加按钮并设置点击事件
        findViewById<Button>(R.id.btnShowNotification).setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // 请求通知权限
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ActivityCompat.requestPermissions(
                        this, arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        REQUEST_NOTIFICATION_PERMISSION
                    )
                }
                return@setOnClickListener
            }
            showProgressNotification()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限获取成功，显示通知
                showProgressNotification()
            } else {
                // 权限被拒绝，可以显示提示或引导用户去设置中开启权限
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun showProgressNotification() {
        if (Build.VERSION.SDK_INT >= 36) {
            // Android 16对应API 36
            // 🎨 定制你的彩虹进度条
            val progressStyle = Notification.ProgressStyle().apply {
                setProgress(0)
                setProgressTrackerIcon(
                    Icon.createWithResource(
                        this@MainActivity,
                        R.drawable.delivery_bike
                    )
                ) // 小电驴图标
                setProgressSegments(
                    listOf(
                        Notification.ProgressStyle.Segment(300).setColor(Color.RED),    // 危险区：红灯路段
                        Notification.ProgressStyle.Segment(500)
                            .setColor(Color.YELLOW), // 缓冲带：找楼号ing
                        Notification.ProgressStyle.Segment(200).setColor(Color.GREEN)   // 最后冲刺！
                    )
                )
                setProgressPoints(
                    listOf(
                        Notification.ProgressStyle.Point(800),// 温馨提示：此处可抢优惠券
                        Notification.ProgressStyle.Point(950).setColor(Color.MAGENTA)
                    )
                )
            }

            // 📢 组装通知大喇叭
            val notification = Notification.Builder(this, "food_channel").apply {
                setSmallIcon(R.drawable.ic_notification)  // 必须有的小图标
                setContentTitle("【饿了吗】骑手已接单")
                setContentText("黄袍加身的小哥正在穿越时空")
                setStyle(progressStyle) // 注入灵魂！
            }.build()

            val notificationManager = NotificationManagerCompat.from(this)
            val name = "Food Delivery"
            val descriptionText = "Food delivery notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("food_channel", name, importance).apply {
                description = descriptionText
            }
            // 注册通知渠道
            notificationManager.createNotificationChannel(channel)
            notificationManager.notify(666, notification)

            // 启动进度更新
            startProgressUpdates()
        }
    }

    private fun startProgressUpdates() {
        currentProgress = 0
        updateProgress()
    }

    @SuppressLint("MissingPermission")
    private fun updateProgress() {
        if (currentProgress >= 1000) {
            // 进度完成，显示送达通知
            showDeliveredNotification()
            return
        }

        currentProgress += 200 // 每次增加200单位进度

        if (Build.VERSION.SDK_INT >= 36) {
            val progressStyle = Notification.ProgressStyle().apply {
                setProgress(currentProgress)
                setProgressTrackerIcon(
                    Icon.createWithResource(
                        this@MainActivity,
                        R.drawable.delivery_bike
                    )
                )
                setProgressSegments(
                    listOf(
                        Notification.ProgressStyle.Segment(300).setColor(Color.RED),
                        Notification.ProgressStyle.Segment(500).setColor(Color.YELLOW),
                        Notification.ProgressStyle.Segment(200).setColor(Color.GREEN)
                    )
                )
                setProgressPoints(
                    listOf(
                        Notification.ProgressStyle.Point(800),
                        Notification.ProgressStyle.Point(950).setColor(Color.MAGENTA)
                    )
                )
            }

            val notification = Notification.Builder(this, "food_channel").apply {
                setSmallIcon(R.drawable.ic_notification)
                setContentTitle("【饿了吗】骑手配送中")
                setContentText("已完成 ${currentProgress / 10}% 的配送路程")
                setStyle(progressStyle)
                setOngoing(true)
            }.build()

            NotificationManagerCompat.from(this).notify(666, notification)
        }

        // 安排下一次更新
        handler.postDelayed({ updateProgress() }, updateInterval)
    }

    @SuppressLint("MissingPermission")
    private fun showDeliveredNotification() {
        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, "food_channel").apply {
                setSmallIcon(R.drawable.ic_notification)
                setContentTitle("【饿了吗】订单已送达")
                setContentText("您的美食已送达，请享用！")
                setOngoing(false)
            }.build()
        } else {
            Notification.Builder(this).apply {
                setSmallIcon(R.drawable.ic_notification)
                setContentTitle("【饿了吗】订单已送达")
                setContentText("您的美食已送达，请享用！")
                setPriority(Notification.PRIORITY_DEFAULT)
                setOngoing(false)
            }.build()
        }

        NotificationManagerCompat.from(this).notify(666, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        // 移除所有回调，防止内存泄漏
        handler.removeCallbacksAndMessages(null)
    }
}