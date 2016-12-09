package com.sunsunsoft.shutaro.ugui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

/**
 * Created by shutaro on 2016/12/09.
 *
 * UScrollWindowテスト用
 */

public class UScrollWindowTest extends UScrollWindow {

    public UScrollWindowTest(UWindowCallbacks callbacks, int priority, float x, float y, int width, int
            height, int color)
    {
        super(callbacks, priority, x, y, width, height, color);
    }


    public void drawContent(Canvas canvas, Paint paint) {
        // test
        UDraw.drawCircle(canvas, paint, new PointF(500 + pos.x - contentTop.x, 500 + pos.y - contentTop
                        .y),
                50, 5, Color.BLACK);
        UDraw.drawCircle(canvas, paint, new PointF(1000 + pos.x - contentTop.x, 500 + pos.y -
                        contentTop
                                .y),
                50, 5, Color.BLACK);
        UDraw.drawCircle(canvas, paint, new PointF(500 + pos.x - contentTop.x, 1000 + pos.y -
                        contentTop
                                .y),
                50, 5, Color.BLACK);
        UDraw.drawCircle(canvas, paint, new PointF(1000 + pos.x - contentTop.x, 1000 + pos.y -
                        contentTop
                                .y),
                50, 5, Color.BLACK);
    }
}
