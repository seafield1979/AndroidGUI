package com.sunsunsoft.shutaro.ugui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

/**
 * UWindowのテスト用のシンプルなWindow
 */
public class UTestWindow extends UWindow {
    /**
     * Consts
     */
    public static final String TAG  = "UTestWindow";
    private static final int DRAW_PRIORITY = 100;

    /**
     * Member Variables
     */

    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    private UTestWindow(float x, float y, int width, int height, int color) {
        super(null, DRAW_PRIORITY, x, y, width, height, color, 50, 10, 10);
    }

    /**
     * インスタンスを生成する
     * @param x
     * @param y
     * @param width
     * @param height
     * @param color
     * @return
     */
    public static UTestWindow createInstance(UWindowCallbacks callbacks, float x, float y, int
            width, int height, int color) {
        UTestWindow instance = new UTestWindow(x, y, width, height, color);
        instance.windowCallbacks = callbacks;
        instance.addCloseIcon();
        UDrawManager.getInstance().addDrawable(instance);
        return instance;
    }

    /**
     * 毎フレームの処理
     * @return　true:再描画
     */
    public boolean doAction() {
        return false;
    }

    /**
     * タッチ処理
     * @param vt
     * @return trueならViewを再描画
     */
    public boolean touchEvent(ViewTouch vt) {
        if (!isShow) return false;

        boolean redraw = super.touchEvent(vt);

        switch (vt.type) {
            case Click:
                // BGの色を変更する
                bgColor = UColor.getRandomColor();
                redraw = true;
                break;
            case Moving:
                if (vt.isMoveStart()) {

                }
                if (rect.contains((int)vt.getX(), (int)vt.getY())) {
                    pos.x += vt.moveX;
                    pos.y += vt.moveY;
                    updateRect();
                    redraw = true;
                }
                break;
        }

        return redraw;
    }


    /**
     * コンテンツを描画する
     * @param canvas
     * @param paint
     */
    public void drawContent(Canvas canvas, Paint paint, PointF offset ) {
        // BG
        UDraw.drawRectFill(canvas, paint, getRect(), bgColor, 0, 0);
    }


    public PointF getDrawOffset() {
        return null;
    }
}
