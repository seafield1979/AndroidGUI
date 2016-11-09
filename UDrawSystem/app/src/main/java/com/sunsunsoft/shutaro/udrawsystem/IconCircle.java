package com.sunsunsoft.shutaro.udrawsystem;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

/**
 * 円形アイコン
 */

public class IconCircle extends Icon{
    public static final int ICON_RAD = 50;

    private int radius;

    public IconCircle(float x, float y, int color) {
        type = IconType.Circle;
        pos.x = x;
        pos.y = y;
        this.radius = ICON_RAD;
        size.width = this.radius * 2;
        size.height = this.radius * 2;
        this.color = color;
        updateRect();
    }

    /**
     * 描画処理
     * @param canvas
     * @param paint
     */
    public void draw(Canvas canvas, Paint paint, PointF offset)
    {
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
        PointF drawPos = null;
        if (offset != null) {
            drawPos = new PointF(pos.x + offset.x, pos.y + offset.y);
        } else {
            drawPos = pos;
        }
        canvas.drawCircle(drawPos.x + radius, drawPos.y + radius, radius, paint);

        drawId(canvas, paint);
    }

    /**
     * 描画範囲の矩形を取得
     * @return
     */
    public Rect getRect()
    {
        return rect;
    }

    /**
     * 指定の座標がアイコンの中に含まれるかをチェック
     * @param x
     * @param y
     * @return true: contain / false:not contain
     */
    public boolean contains(int x, int y) {
        // 円の中心からの距離で判定する
        if (radius * radius >= (pos.x + radius - x) * (pos.y + radius - y)) {
            return true;
        }
        return false;
    }
}
