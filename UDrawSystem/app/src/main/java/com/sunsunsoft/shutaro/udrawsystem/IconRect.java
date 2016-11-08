package com.sunsunsoft.shutaro.udrawsystem;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
    public void draw(Canvas canvas, Paint paint) {
        // 描画方法
        paint.setStyle(Paint.Style.FILL);
        // 線の太さ
//        paint.setStrokeWidth(3);
        // 色
        paint.setColor(color);

        // 四角形描画
        canvas.drawRect(rect, paint);
    }

    /**
     * アニメーション処理
     *
     * @return
     */
    public boolean animate() {
        return true;
    }

    /**
     * アニメーション中かどうか
     *
     * @return
     */
    public boolean isAnimating() {
        return false;
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