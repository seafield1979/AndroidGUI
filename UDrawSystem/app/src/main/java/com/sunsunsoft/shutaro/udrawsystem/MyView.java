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

    private IconManager mIconManager = new IconManager();
    private ViewTouch viewTouch = new ViewTouch();
    private UWindowTest1 window;

    public MyView(Context context) {
        super(context);
    }

    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnTouchListener(this);
    }

    private void init(int width) {
        if (window == null) {
            window = UWindowTest1.createWindow(0, 0, width, 500, Color.WHITE);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (isFirst) {
            isFirst = false;
            init(getWidth());
        }

        // 背景塗りつぶし
        canvas.drawColor(Color.WHITE);

        // アンチエリアシング(境界のぼかし)
        paint.setAntiAlias(true);

        if (DrawManager.getInstance().draw(canvas, paint)) {
            invalidate();
        }
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

        if (window != null) {
            if (window.touchEvent(viewTouch)) {
                invalidate();
            }
        }

        switch(viewTouch.type) {
            case Click:
                break;
            case Moving:
                break;
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
