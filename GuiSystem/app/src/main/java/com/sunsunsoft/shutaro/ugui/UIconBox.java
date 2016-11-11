package com.sunsunsoft.shutaro.ugui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.View;

import java.util.List;

/**
 * 子要素を持つことができるIcon
 */

public class UIconBox extends UIcon {
    public static final int ICON_W = 150;
    public static final int DUMMY_ICON_NUM = 10;

    private View mParentView;
    private UIconManager mIconManager;

    // ボックスの中身を表示しているウィンドウ
    private UIconWindow subWindow;

    // Get/Set
    public UIconManager getIconManager() {
        return mIconManager;
    }
    public List<UIcon> getIcons() {
        return mIconManager.getIcons();
    }

    public UIconWindow getSubWindow() {
        return subWindow;
    }

    public UIconBox(View parentView, UIconWindow parentWindow) {
        super(parentWindow, IconType.BOX, 0, 0, ICON_W, ICON_W);
        mParentView = parentView;

        color = UColor.getRandomColor();

        // ダミーで子要素を追加
        UIconWindow[] windows = parentWindow.getWindows();
        subWindow = windows[WindowType.Sub.ordinal()];

        mIconManager = UIconManager.createInstance(parentView, subWindow);
        for (int i=0; i<DUMMY_ICON_NUM; i++) {
            UIcon icon = mIconManager.addIcon(IconType.RECT, AddPos.Tail);
            icon.setColor(color);
        }

    }

    public void draw(Canvas canvas, Paint paint, PointF offset) {

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

        Rect drawRect = null;
        if (offset != null) {
            drawRect = new Rect(rect.left + (int)offset.x,
                    rect.top + (int)offset.y,
                    rect.right + (int)offset.x,
                    rect.bottom + (int)offset.y);
        } else {
            drawRect = rect;
        }
        canvas.drawRect(drawRect,  paint);

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
