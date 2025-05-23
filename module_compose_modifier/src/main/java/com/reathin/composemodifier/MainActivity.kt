package com.reathin.composemodifier

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.reathin.composemodifier.ui.theme.SampleAndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SampleAndroidTheme {
                Column(modifier = Modifier.padding(16.dp)) {
                    // 固定尺寸的魔法药水
                    Box(
                        modifier = Modifier
                            .size(200.dp)   // 魔杖一挥，200x200的结界
                            .background(Color.LightGray)
                    ) {
                        Text("我是方盒子")   // 被困在结界里的文字
                    }

                    // 充气魔法（充满父容器）
                    Button(
                        onClick = { /* 咒语 */ },
                        modifier = Modifier.fillMaxWidth()  // 横向充气到最大
                    ) {
                        Text("胖胖按钮")
                    }
                    // 渐变色咒语
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.verticalGradient(  // 从上到下的彩虹瀑布
                                    colors = listOf(Color.Red, Color.Yellow, Color.Green)
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Text("彩虹桥", color = Color.White)
                    }

                    // 边框魔法阵
                    Box(
                        modifier = Modifier
                            .border(4.dp, Color.Magenta, CircleShape)  // 圆形粉色魔法阵
                            .size(120.dp)
                    )

                    var isExpanded by remember { mutableStateOf(false) }

                    Box(
                        modifier = Modifier
                            .clickable { isExpanded = !isExpanded }  // 点击触发咒语
                            .scale(if (isExpanded) 2f else 1f)      // 放大两倍
                            .rotate(if (isExpanded) 360f else 0f)  // 旋转特效
                            .background(Color.Cyan)
                            .size(100.dp)
                    ) {
                        Text(if (isExpanded) "Boom!" else "点我")
                    }
                    Box {
                        // 红色卡片（z轴位置2层）
                        Box(
                            modifier = Modifier
                                .zIndex(2f)
                                .offset(x = 20.dp, y = 20.dp)
                                .size(100.dp)
                                .background(Color.Red)
                        )

                        // 蓝色卡片（z轴位置1层）
                        Box(
                            modifier = Modifier
                                .zIndex(1f)
                                .size(100.dp)
                                .background(Color.Blue)
                        )
                    }
                    // 魔法汉堡制作流程
                    Modifier
                        .padding(8.dp)        // 第一步：加包装纸
                        .background(Color.Magenta) // 第二步：涂奶油
                        .size(200.dp)         // 第三步：定型
                        .clickable { }        // 第四步：装感应芯片
                        .shadow(4.dp)        // 第五步：撒金粉
                }
            }
        }
    }
}