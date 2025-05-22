package com.reathin.composesize

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.reathin.composesize.ui.theme.SampleAndroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Column {
                //黄金搭档：size + padding
                Box(
                    modifier = Modifier
                        .size(120.dp) // 设置黄金比例尺寸
                        .background(Color(0xFFF4D03F)) // 蜂蜜金背景
                        .padding(16.dp) // 呼吸空间
                ) {
                    Text(
                        "VIP座席",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                }
                //满屏暴击：fillMaxSize
//                Box(
//                    modifier = Modifier
//                        .fillMaxSize(0.85f) // 霸占85%的屏幕
//                        .background(Color(0xFF6C5CE7)) // 神秘紫背景
//                ) {
//                    Text(
//                        "沉浸模式",
//                        modifier = Modifier.align(Alignment.Center),
//                        color = Color.White
//                    )
//                }
                //智能伸缩：sizeIn
                var isExpanded by remember { mutableStateOf(false) }

                Box(
                    Modifier
                        .sizeIn(
                            minWidth = 80.dp,  // 最小安全尺寸
                            maxWidth = 200.dp, // 最大膨胀尺寸
                            minHeight = 40.dp,
                            maxHeight = 120.dp
                        )
                        .background(Color(0xFF00B894)) // 薄荷绿
                        .clickable { isExpanded = !isExpanded }
                ) {
                    Text(if(isExpanded) "收缩吧！" else "膨胀吧!!!!！",
                        Modifier.padding(16.dp))
                }
                //wrapContentSize
                Row(Modifier.background(Color.LightGray)) {
                    Box(
                        Modifier
                            .wrapContentSize(Alignment.TopEnd) // 让内容决定大小
                            .background(Color(0xFFE84393)) // 樱花粉
                    ) {
                        Text("New", Modifier.padding(4.dp))
                    }
                    Text(" 限定款球鞋", style = MaterialTheme.typography.bodyLarge)
                }
                //精准控制：requiredSize vs size
                Column {
                    // 倔强青铜（requiredSize）
                    Box(Modifier.requiredSize(80.dp).background(Color.Red))

                    // 秩序白银（size）
                    Box(Modifier.size(80.dp).background(Color.Blue))

                    // 荣耀黄金（父布局约束下）
                    Box(Modifier.size(80.dp).padding(10.dp).background(Color.Green))
                }
                //组合技示例：打造完美卡片
                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .heightIn(min = 120.dp, max = 240.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(R.drawable.img),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.Red)
                        ) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = "购买",
                                modifier = Modifier.size(24.dp).align(Alignment.Center),
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}