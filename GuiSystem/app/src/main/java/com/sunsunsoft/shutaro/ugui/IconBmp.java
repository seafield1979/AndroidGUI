package com.sunsunsoft.shutaro.ugui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

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
        super(parent, IconShape.IMAGE, x, y, width, height);

        this.bmp = bmp;
    }

    public boolean draw(Canvas canvas,Paint paint) {
        return draw(canvas, paint, null, null);
    }

    public boolean draw(Canvas canvas,Paint paint, PointF toScreen, RectF clipRect) {
        if (bmp == null) return false;

        if (toScreen == null) {
            toScreen = new PointF(0,0);
        }
        Point drawPos = new Point((int)(pos.x + toScreen.x), (int)(pos.y + toScreen.y));
        RectF rect = new RectF(drawPos.x, drawPos.y, drawPos.x + size.width, drawPos.y + size
                .height);

        // クリッピング処理
        // 表示領域外なら描画しない
        if (clipRect != null) {
            if (rect.contains(clipRect)) {
                return false;
            }
        }

        // そのままのサイズで描画
        //         canvas.drawBitmap(bmp, x, y, paint);

        // 領域の幅に合わせて伸縮
        canvas.drawBitmap(bmp, new Rect(0, 0, bmp.getWidth(), bmp.getHeight()), rect, paint);
        drawId(canvas, paint);

        return true;
    }
}
