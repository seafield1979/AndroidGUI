package com.sunsunsoft.shutaro.udrawsystem;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * カスタムView
 */

public class MyView extends View implements OnTouchListener{

    private static final int RECT_ICON_NUM = 50;
    private static final int CIRCLE_ICON_NUM = 50;

    private boolean isFirst = true;
    private Paint paint = new Paint();
    private Context context;

    private DrawManager mDrawManager = new DrawManager();
    private IconManager mIconManager = new IconManager(mDrawManager);
    private ViewTouch viewTouch = new ViewTouch();

    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnTouchListener(this);
    }

    private void init(int width, int height) {

        float x = 0, y = 0;
        for (int i=0; i<RECT_ICON_NUM; i++) {
            mIconManager.addIcon(IconType.Rect, x, y, MyColor.getRandom());
            x += IconRect.ICON_W + 30;
            if (x + IconRect.ICON_W + 30 > width) {
                x = 0;
                y += IconRect.ICON_H + 30;
            }
        }
        x = 50;
        y = 50;
        for (int i=0; i<CIRCLE_ICON_NUM; i++) {
            mIconManager.addIcon(IconType.Circle, x, y, MyColor.getRandom());
            x += IconRect.ICON_W + 30;
            if (x + IconRect.ICON_W + 30 > width) {
                x = 0;
                y += IconRect.ICON_H + 30;
            }
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (isFirst) {
            isFirst = false;
            init(getWidth(), getHeight());
        }

        // 背景塗りつぶし
        canvas.drawColor(Color.WHITE);

        // アンチエリアシング(境界のぼかし)
        paint.setAntiAlias(true);

        mDrawManager.draw(canvas, paint);
    }

    /**
     * タッチイベント処理
     * @param v
     * @param e
     * @return
     */
    public boolean onTouch(View v, MotionEvent e) {
        boolean ret = true;

        viewTouch.checkTouchType(e);

        if (mIconManager.touchEvent(viewTouch)) {
            invalidate();
        }

        switch(e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // trueを返す。こうしないと以降のMoveイベントが発生しなくなる。
                ret = true;
                break;
            case MotionEvent.ACTION_UP:
                ret = true;
                break;
            case MotionEvent.ACTION_MOVE:
                ret = true;
                break;
            default:
        }

        // コールバック
        return ret;
    }
}
