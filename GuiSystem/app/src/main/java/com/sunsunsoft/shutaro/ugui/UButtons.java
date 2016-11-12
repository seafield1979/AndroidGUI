package com.sunsunsoft.shutaro.ugui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

/**
 * 複数のボタンを管理するクラス
 */

public class UButtons extends Drawable {

    /**
     * コンストラクタ
     */
    public UButtons(float x, float y, int width, int height) {
        super(1000, x, y, width, height);
    }

    public void draw(Canvas canvas, Paint paint, PointF offset) {

    }

    public boolean touchEvent(ViewTouch vt) {
        return false;
    }
}
