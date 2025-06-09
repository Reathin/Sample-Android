package com.reathin.floating;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private WindowManager windowManager;
    private WindowManager.LayoutParams params;
    private ImageView floatView;
    private static final int OVERLAY_PERMISSION_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.btnStartFloat).setOnClickListener(v -> {
            // 检查是不是Android 6.0以上的"高级游乐园"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 看看通行证有没有过期
                if (!Settings.canDrawOverlays(this)) {
                    // 弹出申请对话框
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, OVERLAY_PERMISSION_CODE);
                } else {
                    showFloatingWindow(); // 亮出我们的悬浮窗！
                }
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void showFloatingWindow() {
        // 窗口管家（WindowManager）登场
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // 给悬浮窗穿件"衣服"
        floatView = new ImageView(this);
        floatView.setImageResource(R.drawable.ic_float);
        // 设置悬浮窗的大小为100dp
        int size = (int) (100 * getResources().getDisplayMetrics().density);
        floatView.setLayoutParams(new ViewGroup.LayoutParams(size, size));

        // 悬浮窗的"身份证信息"
        params = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                        // Android 8.0+用这个
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                        // 老版本用这个
                        WindowManager.LayoutParams.TYPE_PHONE,
                // 别抢焦点，做个安静的美窗
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                // 半透明效果更高级
                PixelFormat.TRANSLUCENT
        );

        // 初始位置：屏幕左上角+向下偏移100像素
        params.gravity = Gravity.START | Gravity.TOP;
        params.x = 0;
        params.y = 100;

        // 把悬浮窗"放"到屏幕上
        windowManager.addView(floatView, params);

        floatView.setOnTouchListener(new View.OnTouchListener() {
            private int startX, startY;
            private float touchX, touchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    // 手指按下
                    case MotionEvent.ACTION_DOWN:
                        startX = params.x;
                        startY = params.y;
                        // 记录触点X
                        touchX = event.getRawX();
                        // 记录触点Y
                        touchY = event.getRawY();
                        return true;
                    // 手指滑动
                    case MotionEvent.ACTION_MOVE:
                        // 计算新位置
                        params.x = startX + (int) (event.getRawX() - touchX);
                        params.y = startY + (int) (event.getRawY() - touchY);
                        windowManager.updateViewLayout(floatView, params);
                        floatView.setAlpha(0.7f);
                        return true;
                    // 手指松开
                    case MotionEvent.ACTION_UP:
                        // 触发吸边效果
                        autoAttachToEdge();
                        return true;
                }
                return false;
            }
        });
    }

    private void autoAttachToEdge() {
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int viewCenterX = params.x + floatView.getWidth() / 2;

        // 判断吸附方向
        final int targetX = (viewCenterX < screenWidth / 2) ? 0 : screenWidth - floatView.getWidth();

        // 添加弹性动画
        ValueAnimator animator = ValueAnimator.ofInt(params.x, targetX);
        animator.addUpdateListener(animation -> {
            params.x = (int) animation.getAnimatedValue();
            windowManager.updateViewLayout(floatView, params);
        });
        animator.setDuration(300).start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                floatView.setAlpha(1.0f);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OVERLAY_PERMISSION_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    Settings.canDrawOverlays(this)) {
                showFloatingWindow(); // 用户同意后显示
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (floatView != null) {
            // 防止内存泄漏
            windowManager.removeView(floatView);
        }
    }

}