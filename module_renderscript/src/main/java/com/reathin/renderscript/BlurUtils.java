package com.reathin.renderscript;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RSIllegalArgumentException;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.View;
import com.reathin.renderscript.*;


public class BlurUtils {

    private static long mStartTime;

    public static Bitmap scriptBlur(Context context, Bitmap origin, int outWidth, int outHeight, float radius) {
        if (origin == null || origin.isRecycled()) {
            return null;
        }
        mStartTime = System.currentTimeMillis();
        RenderScript renderScript = RenderScript.create(context.getApplicationContext(), RenderScript.ContextType.NORMAL, RenderScript.CREATE_FLAG_NONE);
        Allocation blurInput = Allocation.createFromBitmap(renderScript, origin);
        Allocation blurOutput = Allocation.createTyped(renderScript, blurInput.getType());
        ScriptIntrinsicBlur blur = null;
        try {
            blur = ScriptIntrinsicBlur.create(renderScript, blurInput.getElement());
        } catch (RSIllegalArgumentException e) {
            if (e.getMessage().contains("Unsuported element type")) {
                blur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
            }
        }
        if (blur == null) {
            //脚本模糊失败
            return null;
        }
        blur.setRadius(range(radius, 0, 25));
        blur.setInput(blurInput);
        blur.forEach(blurOutput);

        Bitmap result = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888);
        blurOutput.copyTo(result);

        //释放
        renderScript.destroy();
        blurInput.destroy();
        blurOutput.destroy();

        long time = (System.currentTimeMillis() - mStartTime);
        Log.i("BlurUtils", "模糊用时：[" + time + "ms]");
        return result;
    }

    public static Bitmap rsBlur(Context context, Bitmap inputBitmap, float blurRadius) {
        // 创建RenderScript实例
        RenderScript rs = RenderScript.create(context);

        // 创建输入和输出Allocation
        Allocation inputAllocation = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation outputAllocation = Allocation.createTyped(rs, inputAllocation.getType());

        // 加载RenderScript脚本
        ScriptC_Blur blurScript = new ScriptC_Blur(rs);
        blurScript.set_inImage(inputAllocation);
        blurScript.set_outImage(outputAllocation);
        blurScript.set_blurRadius(blurRadius);

        // 执行模糊操作
        blurScript.invoke_root();

        // 创建输出位图并复制数据
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap.getWidth(), inputBitmap.getHeight(), inputBitmap.getConfig());
        outputAllocation.copyTo(outputBitmap);

        // 销毁资源和清理
        inputAllocation.destroy();
        outputAllocation.destroy();
        rs.destroy();

        return outputBitmap;
    }

    private static float range(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    }
}