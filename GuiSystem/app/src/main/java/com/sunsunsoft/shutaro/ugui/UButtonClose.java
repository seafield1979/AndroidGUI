package com.sunsunsoft.shutaro.ugui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * 閉じるボタン
 */

public class UButtonClose extends UButton {

    /**
     * Consts
     */
    private static final int X_LINE_WIDTH = 15;

    /**
     * Member Variables
     */
    private int radius;

    /**
     * Get/Set
     */

    /**
     * Constructor
     */


    public UButtonClose(UButtonCallbacks callbacks, UButtonType type, int id, int priority,
                   float x, float y, int radius, int color)
    {
        super(callbacks, type, id, priority, x, y, radius * 2, radius * 2, color);

        this.radius = radius;
    }

    /**
     * Methods
     */

    @Override
    /**
     * 描画処理
     * @param canvas
     * @param paint
     * @param offset 独自の座標系を持つオブジェクトをスクリーン座標系に変換するためのオフセット値
     */
    public void draw(Canvas canvas, Paint paint, PointF offset) {
        // 内部を塗りつぶし
        paint.setStyle(Paint.Style.FILL);

        // 色
        // 押されていたら明るくする
        int _color = color;

        paint.setColor(_color);

        PointF _pos = new PointF(pos.x, pos.y);
        if (offset != null) {
            _pos.x += offset.x;
            _pos.y += offset.y;
        }

        // 押したら色が変わるボタン
        if (isPressed) {
            _color = pressedColor;
        }
        UDraw.drawCircleFill(canvas, paint,
                new PointF(pos.x, pos.y),
                radius, _color);

        // x
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(X_LINE_WIDTH);
        float x = (float)Math.cos(45 * RAD) * radius * 0.8f;
        float y = (float)Math.sin(45 * RAD) * radius * 0.8f;
        canvas.drawLine(pos.x - x, pos.y - y,
                pos.x + x, pos.y + y, paint);
        canvas.drawLine(pos.x - x, pos.y + y,
                pos.x + x, pos.y - y, paint);
    }
}