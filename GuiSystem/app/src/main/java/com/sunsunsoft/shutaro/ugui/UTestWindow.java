package com.sunsunsoft.shutaro.ugui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

/**
 * UWindowのテスト用のシンプルなWindow
 */
public class UTestWindow extends UWindow {
    public static final String TAG  = "UTestWindow";
    private static final int DRAW_PRIORITY = 100;

    private UTestWindow(float x, float y, int width, int height, int color) {
        super(DRAW_PRIORITY, x, y, width, height, color);

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
    public static UTestWindow createInstance(float x, float y, int width, int height, int color) {
        UTestWindow instance = new UTestWindow(x, y, width, height, color);

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

        // 範囲外なら除外
        if (!(rect.contains((int)vt.getX(), (int)vt.getY()))) {
            return false;
        }

        switch (vt.type) {
            case Click:
                // BGの色を変更する
                bgColor = UColor.getRandomColor();
                break;
            case Moving:
                if (vt.isMoveStart()) {
                }
                pos.x += vt.moveX;
                pos.y += vt.moveY;
                updateRect();
                break;
        }

        return true;
    }

    /**
     * 描画処理
     * @param canvas
     * @param paint
     * @param offset
     */
    public void draw(Canvas canvas, Paint paint, PointF offset) {
        // BG
        UDraw.drawRectFill(canvas, paint, getRect(), bgColor);
    }

    public PointF getDrawOffset() {
        return null;
    }
}
