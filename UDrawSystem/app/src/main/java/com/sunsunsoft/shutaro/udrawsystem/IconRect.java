package com.sunsunsoft.shutaro.udrawsystem;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

/**
 * 矩形アイコン
 */

public class IconRect extends Icon {
    public static final int ICON_W = 100;
    public static final int ICON_H = 100;

    public IconRect(float x, float y, int color) {
        type = IconType.Rect;
        pos.x = x;
        pos.y = y;
        size.width = ICON_W;
        size.height = ICON_H;
        this.color = color;
        updateRect();
    }

    /**
     * 描画処理
     *
     * @param canvas
     * @param paint
     */
    public void draw(Canvas canvas, Paint paint, PointF offset) {
        // 描画方法
        paint.setStyle(Paint.Style.FILL);
        // 色
        if (isAnimating) {
            double v1 = ((double)animeFrame / (double)animeFrameMax) * 180;
            int alpha = (int)((1.0 -  Math.sin(v1 * RAD)) * 255);
            paint.setColor((alpha << 24) | (color & 0xffffff));
        } else {
            paint.setColor(color);
        }

        // 四角形描画
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

        drawId(canvas, paint);
    }



    /**
     * 描画範囲の矩形を取得
     *
     * @return
     */
    public Rect getRect() {
        return rect;
    }

    /**
     * 指定の座標がアイコンの中に含まれるかをチェック
     *
     * @param x
     * @param y
     * @return true: contain / false:not contain
     */
    public boolean contains(int x, int y) {
        if (rect.contains(x,y)) {
            return true;
        }
        return false;
    }
}