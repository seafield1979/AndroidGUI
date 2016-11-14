package com.sunsunsoft.shutaro.ugui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * 自前の描画処理
 * OnDrawの中から呼び出す
 */

public class UDraw {
    // ラジアン角度
    public static final double RAD = 3.1415 / 180.0;

    /**
     * 矩形描画 (ライン）
     * @param canvas
     * @param paint
     * @param rect
     * @param width 線の太さ
     * @param color
     */
    public static void drawRect(Canvas canvas, Paint paint, Rect rect, int width, int color) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(width);
        paint.setColor(color);
        canvas.drawRect(rect, paint);
    }

    /**
     * 角丸矩形描画 (ライン）
     * @param canvas
     * @param paint
     * @param rect
     * @param width 線の太さ
     * @param color
     */
    public static void drawRoundRect(Canvas canvas, Paint paint, RectF rect, int width,
                                     float radius, int color) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(width);
        paint.setColor(color);
        canvas.drawRoundRect(rect, radius, radius, paint);
    }

    /**
     * 矩形描画(塗りつぶし)
     * @param canvas
     * @param paint
     * @param rect
     * @param color
     */
    public static void drawRectFill(Canvas canvas, Paint paint, Rect rect, int color) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        canvas.drawRect(rect, paint);
    }

    /**
     * 角丸四角形(塗りつぶし)
     * @param canvas
     * @param paint
     * @param rect
     * @param radius    角の半径
     * @param color
     */
    public static void drawRoundRectFill(Canvas canvas, Paint paint, RectF rect, float radius, int
            color) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        canvas.drawRoundRect(rect, radius, radius, paint);
    }

    /**
     * 円描画(線)
     * @param canvas
     * @param paint
     * @param center
     * @param radius
     * @param width
     * @param color
     */
    public static void drawCircle(Canvas canvas, Paint paint, PointF center, float radius, int width, int color)
    {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(width);
        paint.setColor(color);
        canvas.drawCircle(center.x, center.y, radius, paint);
    }

    /**
     * 円描画(塗りつぶし)
     * @param canvas
     * @param paint
     * @param center
     * @param radius
     * @param color
     */
    public static void drawCircleFill(Canvas canvas, Paint paint, PointF center, float radius, int color) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        canvas.drawCircle(center.x, center.y, radius, paint);
    }

    /**
     * 三角形描画(線)
     * @param canvas
     * @param paint
     * @param center
     * @param radius
     * @param color
     */
    public static void drawTriangle(Canvas canvas, Paint paint, PointF center, float radius, int width, int color) {
        Path path = trianglePath(center, radius);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        paint.setStrokeWidth(width);
        canvas.drawPath(path, paint);
    }

    /**
     * 三角形描画(塗りつぶし)
     * @param canvas
     * @param paint
     * @param center
     * @param radius
     * @param color
     */
    public static void drawTriangleFill(Canvas canvas, Paint paint, PointF center, float radius, int color) {
        Path path = trianglePath(center, radius);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color);
        canvas.drawPath(path, paint);
    }

    private static Path trianglePath(PointF center, float radius) {
// 中心から半径の位置にある３点で三角形を描画する
        Point p1, p2, p3;
        float baseAngle = 180;
        float angle = baseAngle + 90;
        p1 = new Point((int)(Math.cos(angle * RAD) * radius),
                (int)(Math.sin(angle * RAD) * radius));

        angle = baseAngle + 210;
        p2 = new Point((int)(Math.cos(angle * RAD) * radius),
                (int)(Math.sin(angle * RAD) * radius));

        angle = baseAngle + 330;
        p3 = new Point((int)(Math.cos(angle * RAD) * radius),
                (int)(Math.sin(angle * RAD) * radius));

        // 線を３つつなぐ
        Path path = new Path();
        path.moveTo(p1.x + center.x, p1.y + center.y);
        path.lineTo(p2.x + center.x, p2.y + center.y);
        path.lineTo(p3.x + center.x, p3.y + center.y);
        path.lineTo(p1.x + center.x, p1.y + center.y);
        path.close();

        return path;
    }


    /**
     * チェックボックスを描画する
     * @param canvas
     * @param paint
     * @param isChecked
     * @param x
     * @param y
     * @param width
     * @param color  チェック時の色(みチェック時は灰色)
     */
    public static void drawCheckbox(Canvas canvas, Paint paint, boolean isChecked,
                               float x, float y, float width, int color )
    {
        RectF rect = new RectF(x, y, x + width, y + width);

        if (isChecked) {
            // 枠
            drawRoundRectFill(canvas, paint, rect, 10, color );

            // チェック
            Path path = new Path();
            paint.setStyle(Paint.Style.STROKE);
            path.moveTo(x + width * 0.2f, y + width * 0.4f);
            path.lineTo(x + width * 0.4f, y + width * 0.7f);
            path.lineTo(x + width * 0.75f, y + width * 0.25f);
            paint.setColor(Color.WHITE);
            paint.setStrokeWidth(6);
            canvas.drawPath(path, paint);

        } else {
            // 枠
            drawRoundRect(canvas, paint, rect, 10, 15, Color.rgb(140,140,140) );
        }
    }
}
