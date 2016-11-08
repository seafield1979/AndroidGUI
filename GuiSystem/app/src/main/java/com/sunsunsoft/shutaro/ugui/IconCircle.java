package com.sunsunsoft.shutaro.ugui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;

/**
 * 円形のアイコン
 */

public class IconCircle extends IconBase {
    private static final int ICON_W = 150;

    protected int radius;

    public IconCircle(IconWindow parent) {
        this(parent, 0, 0, ICON_W);
    }

    public IconCircle(IconWindow parent, int x, int y, int width) {
        super(parent, IconShape.CIRCLE, x,y,width,width);

        color = Color.rgb(0,255,255);
        this.radius = width / 2;
    }

    public boolean draw(Canvas canvas,Paint paint) {
        return draw(canvas, paint, null, null);
    }

    public boolean draw(Canvas canvas,Paint paint, PointF toScreen, RectF clipRect) {
        if (toScreen == null) {
            toScreen = new PointF(0, 0);
        }
        // クリッピング
        float x = pos.x + toScreen.x;
        float y = pos.y + toScreen.y;
        RectF rect = new RectF(x, y, x + radius * 2, y + radius * 2);
        if (clipRect != null) {
            if (!MyRect.isOverlapping(rect, clipRect)) {
                return false;
            }
        }

        // 内部を塗りつぶし
        paint.setStyle(Paint.Style.FILL);

        if (isDroping) {
            // 外枠のみ
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

        // x,yが円を囲む矩形の左上にくるように座標を調整
        canvas.drawCircle(pos.x+radius + toScreen.x, pos.y+radius + toScreen.y, radius, paint);

        drawId(canvas, paint);
        return true;
    }
}
