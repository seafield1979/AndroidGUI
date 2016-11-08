package com.sunsunsoft.shutaro.udrawsystem;

import android.graphics.Canvas;
import android.graphics.Paint;
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
    public void draw(Canvas canvas, Paint paint)
    {
        // 描画方法
        paint.setStyle(Paint.Style.FILL);
        // 線の太さ
//        paint.setStrokeWidth(3);
        // 色
        paint.setColor(color);

        // 四角形描画
        canvas.drawCircle(pos.x + radius, pos.y + radius, radius, paint);
    }

    /**
     * アニメーション処理
     * @return
     */
    public boolean animate()
    {
        return true;
    }

    /**
     * アニメーション中かどうか
     * @return
     */
    public boolean isAnimating()
    {
        return false;
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
