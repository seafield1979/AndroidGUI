package com.sunsunsoft.shutaro.ugui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

enum ButtonId {
    Test1,
    Test2,
    Test3
}

/**
 * メニューバー、サブViewのサンプル
 */
public class TestButtonView extends View implements OnTouchListener, UButtonCallbacks{

    public static final String TAG = "TestButtonView";

    // サイズ更新用
    private boolean isFirst = true;

    // タッチ情報
    private ViewTouch viewTouch = new ViewTouch();

    private Paint paint = new Paint();

    private boolean resetSize;
    private int newWidth, newHeight;

    // UButton
    private UButton uButton;

    // get/set
    public TestButtonView(Context context) {
        this(context, null);
    }

    public TestButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnTouchListener(this);
    }


    private void initDrawables(int width, int height) {
        if (uButton == null) {
            uButton = new UButton(this, ButtonId.Test1, "test1", 100, 100, width - 100*2, 100,
                    Color.rgb(0,128,0));
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (isFirst) {
            isFirst = false;
            initDrawables(getWidth(), getHeight());
        }

        // 背景塗りつぶし
        canvas.drawColor(Color.WHITE);

        // アンチエリアシング(境界のぼかし)
        paint.setAntiAlias(true);

        // マネージャに登録した描画オブジェクトをまとめて描画
        if (DrawManager.getInstance().draw(canvas, paint)){
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

        if (uButton.touchEvent(viewTouch)) {
//            ULog.print(TAG, "invalidate");
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


    /**
     * UButtonCallbacks
     */

    public void click(UButton button) {
        switch(button.getId()) {
            case Test1:
                ULog.print(TAG, "button click:" + button.getId());
                break;
            case Test2:
                break;
            case Test3:
                break;
        }
    }
    public void longClick(UButton button) {

    }
}
