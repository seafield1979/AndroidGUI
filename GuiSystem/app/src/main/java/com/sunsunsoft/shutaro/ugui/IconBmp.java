package com.sunsunsoft.shutaro.ugui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

/**
 * 画像のアイコン
 */

public class IconBmp extends Icon {
    private static final int ICON_W = 150;

    private Bitmap bmp;


    public IconBmp(IconWindow parent, Bitmap bmp) {
        this(parent, 0, 0, ICON_W, ICON_W, bmp);
    }
    public IconBmp(IconWindow parent, int x, int y, int width, int height, Bitmap bmp) {
        super(parent, IconType.IMAGE, x, y, width, height);

        this.bmp = bmp;
    }

    public void draw(Canvas canvas,Paint paint, PointF offset) {
        if (bmp == null) return;

        // 領域の幅に合わせて伸縮
        Rect rect = new Rect((int)pos.x, (int)pos.y, size.width, size.height);
        canvas.drawBitmap(bmp, new Rect(0, 0, bmp.getWidth(), bmp.getHeight()), rect, paint);
        drawId(canvas, paint);

        return;
    }
}
