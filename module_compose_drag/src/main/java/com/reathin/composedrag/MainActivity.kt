package com.reathin.composedrag

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.reathin.composedrag.ui.theme.PolygonShape
import com.reathin.composedrag.ui.theme.SampleAndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SampleAndroidTheme {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "欢迎来到Compose拖拽演示",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    // 🍄 蘑菇按钮
                    Button(
                        onClick = { },
                        modifier = Modifier.size(100.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp, 30.dp)
                                    .background(
                                        color = Color.White,
                                        shape = RoundedCornerShape(50)
                                    )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("采蘑菇", color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    // 🔶 六边形头像
                    Image(
                        painter = painterResource(R.drawable.img),
                        contentDescription = null,
                        modifier = Modifier.clip(PolygonShape(sides = 6)) // 六边形裁剪
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    // 拖拽演示
                    MagicDraggableText()
                }
            }
        }
    }
}

@Composable
fun MagicDraggableText() {
    // 状态管理三兄弟
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset { IntOffset(offsetX.toInt(), offsetY.toInt()) }
                .background(
                    color = when {
                        isDragging -> Color(0xFFFFA500).copy(alpha = 0.7f) // 飞行时橙色尾迹
                        else -> Color(0xFFE0E0E0) // 静止时灰色机身
                    },
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(16.dp)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            isDragging = true
                            Log.d("Drag", "🚀 发射！")
                        },
                        onDrag = { _, amount ->
                            offsetX += amount.x // X轴位移累加器
                            offsetY += amount.y // Y轴位移累加器
                        },
                        onDragEnd = {
                            isDragging = false
                            Log.d("Drag", "🛬 降落点：($offsetX, $offsetY)")
                        }
                    )
                }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (isDragging) "哇！我在飞~" else "点我起飞 ✈️",
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Image(
                    painter = painterResource(R.drawable.img),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .clip(PolygonShape(sides = 6)) // 六边形裁剪
                )
            }
        }
    }
}