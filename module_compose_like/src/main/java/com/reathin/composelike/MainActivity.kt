package com.reathin.composelike

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.reathin.composelike.ui.theme.SampleAndroidTheme
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

// 用枚举告诉代码：「现在该哪一步了？」
enum class AnimationState {
    IDLE,          // 静止状态
    CIRCLE_GROW,   // 背景圆圈扩散
    THUMB_JUMP,    // 点赞图标弹跳
    DOT_EXPLOSION, // 小圆点爆发
    FINISH         // 动画结束
}


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SampleAndroidTheme {
                // 添加Box容器使按钮居中
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LikeButton()
                }
            }
        }
    }
}

@Composable
@Preview
fun LikeButton() {
    // 🌟 核心状态控制
    var isLiked by remember { mutableStateOf(false) }
    var currentState by remember { mutableStateOf(AnimationState.IDLE) }

    // 📏 单位转换（解决DP/PX混乱问题！）
    val density = LocalDensity.current
    val maxCircleSize = 48.dp
    val maxCirclePx = with(density) { maxCircleSize.toPx() }

    // 🎬 动画过渡控制
    val transition = updateTransition(currentState, label = "LikeTransition")

    // ✨ 定义动画数值
    // 背景圆圈半径
    val circleRadius by transition.animateFloat(
        transitionSpec = {
            when {
                AnimationState.IDLE isTransitioningTo AnimationState.CIRCLE_GROW ->
                    tween(300, easing = FastOutSlowInEasing)

                else -> snap()
            }
        }, label = "circleRadius"
    ) { state ->
        when (state) {
            AnimationState.CIRCLE_GROW -> maxCirclePx
            else -> 0f
        }
    }

    // 👍 大拇指图标动画（弹跳效果）
    val thumbScale by transition.animateFloat(
        transitionSpec = {
            keyframes {
                durationMillis = 600
                0f at 0 with LinearEasing
                1.2f at 150
                1f at 300
            }
        }, label = "thumbScale"
    ) { state ->
        when (state) {
            AnimationState.THUMB_JUMP -> 1f
            AnimationState.FINISH -> 1f
            AnimationState.IDLE -> 1f  // 确保初始状态也显示
            else -> 0f
        }
    }

    // 💥 爆炸小圆点
    val dotsProgress by transition.animateFloat(
        transitionSpec = { tween(500) },
        label = "dotsProgress"
    ) { state ->
        when (state) {
            AnimationState.DOT_EXPLOSION -> 1f
            else -> 0f
        }
    }

    // 🕹 点击触发动画
    LaunchedEffect(isLiked) {
        if (isLiked) {
            currentState = AnimationState.CIRCLE_GROW
            delay(300)
            currentState = AnimationState.THUMB_JUMP
            delay(300)
            currentState = AnimationState.DOT_EXPLOSION
            delay(800)
            currentState = AnimationState.FINISH
        } else {
            currentState = AnimationState.IDLE
        }
    }

    // 🖌 绘制界面
    Box(
        modifier = Modifier
            .size(72.dp)
            .clickable { isLiked = !isLiked },
        contentAlignment = Alignment.Center
    ) {
        // 背景扩散圆圈
        if (currentState != AnimationState.IDLE) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    color = Color.Red.copy(alpha = 0.2f),
                    radius = circleRadius,
                    style = Stroke(width = 2.dp.toPx())
                )
            }
        }

        // 大拇指图标（带弹跳）
        Icon(
            imageVector = Icons.Default.ThumbUp,
            contentDescription = "Like",
            modifier = Modifier
                .size(32.dp)
                .graphicsLayer {
                    scaleX = thumbScale
                    scaleY = thumbScale
                },
            tint = if (isLiked) Color.Red else Color.Gray
        )

        // 爆炸小圆点
        if (dotsProgress > 0) {
            val angles = listOf(0f, 45f, 90f, 135f, 180f, 225f, 270f, 315f)
            val radius = with(LocalDensity.current) { 48.dp.toPx() } * dotsProgress
            val dotRadius = with(LocalDensity.current) { 4.dp.toPx() }

            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)

                angles.forEach { angle ->
                    val radians = Math.toRadians(angle.toDouble())
                    val x = center.x + radius * cos(radians).toFloat()
                    val y = center.y + radius * sin(radians).toFloat()
                    
                    drawCircle(
                        color = Color(0xFFFF6B6B).copy(alpha = 1 - dotsProgress),
                        radius = dotRadius,
                        center = Offset(x, y)
                    )
                }
            }
        }
    }
}