package com.sunsunsoft.shutaro.ugui.uview.icon;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

/**
 * 四角形のアイコン
 */

public class UIconRect extends UIcon {
    public static final String TAG = "UIconRect";
    private static final int ICON_W = 200;
    private static final int ICON_H = 150;

    public UIconRect(UIconWindow parent, UIconCallbacks iconCallbacks) {
        this(parent, iconCallbacks, 0, 0, ICON_W, ICON_H);
    }

    public UIconRect(UIconWindow parent, UIconCallbacks iconCallbacks, int x, int y, int width, int height) {
        super(parent, iconCallbacks, IconType.RECT, x,y,width,height);

        color = Color.rgb(0,255,255);
    }

    public void drawIcon(Canvas canvas,Paint paint, PointF offset) {

        // 内部を塗りつぶし
        paint.setStyle(Paint.Style.FILL);
        // 色
        if (isLongTouched) {
            paint.setColor(longPressedColor);
        }
        else if (isTouched) {
            paint.setColor(touchedColor);
        }
        else if (isDroping) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(2);
            paint.setColor(Color.BLACK);
        } else if (isAnimating) {
            double v1 = ((double)animeFrame / (double)animeFrameMax) * 180;
            int alpha = (int)((1.0 -  Math.sin(v1 * RAD)) * 255);
            paint.setColor((alpha << 24) | (color & 0xffffff));
        } else {
            paint.setColor(color);
        }

        Rect drawRect = null;
        if (offset != null) {
            drawRect = new Rect(rect.left + (int)offset.x,
                    rect.top + (int)offset.y,
                    rect.right + (int)offset.x,
                    rect.bottom + (int)offset.y);
        } else {
            drawRect = rect;
        }
        canvas.drawRect(drawRect, paint);
    }

    @Override
    public void click() {
        super.click();
    }

    @Override
    public void longClick() {
        super.longClick();
    }

    @Override
    public void moving() {
        super.moving();
    }
}
