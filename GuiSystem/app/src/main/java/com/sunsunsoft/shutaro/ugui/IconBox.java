package com.sunsunsoft.shutaro.ugui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import java.util.List;

/**
 * 子要素を持つことができるIcon
 */

public class IconBox extends Icon {
    public static final int ICON_W = 150;
    public static final int DUMMY_ICON_NUM = 10;

    private View mParentView;
    private IconManager mIconManager;

    // ボックスの中身を表示しているウィンドウ
    private IconWindow subWindow;

    // Get/Set
    public IconManager getIconManager() {
        return mIconManager;
    }
    public List<Icon> getIcons() {
        return mIconManager.getIcons();
    }

    public IconWindow getSubWindow() {
        return subWindow;
    }

    public void setSubWindow(IconWindow subWindow) {
        this.subWindow = subWindow;
    }

    public IconBox(View parentView, IconWindow parentWindow) {
        super(parentWindow, IconShape.BOX, 0, 0, ICON_W, ICON_W);
        mParentView = parentView;

        color = MyColor.getRandomColor();

        // ダミーで子要素を追加
        mIconManager = IconManager.createInstance(parentView, parentWindow);
        for (int i=0; i<DUMMY_ICON_NUM; i++) {
            Icon icon = mIconManager.addIcon(IconShape.RECT, AddPos.Tail);
            icon.setColor(color);
        }
    }

    public void draw(Canvas canvas, Paint paint, PointF offset) {
        Rect drawRect = null;
        if (offset != null) {
            drawRect = new Rect(rect.left + (int)offset.x,
                    rect.top + (int)offset.y,
                    rect.right + (int)offset.x,
                    rect.bottom + (int)offset.y);
        } else {
            drawRect = rect;
        }

        // 内部を塗りつぶし
        paint.setStyle(Paint.Style.FILL);
        // 色
        if (isDroping) {
            // 内部を塗りつぶし
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
        canvas.drawRect(rect.left, rect.top, rect.right, rect.bottom,  paint);

        drawId(canvas, paint);
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

    @Override
    public void drop() {
        super.drop();
    }
}
