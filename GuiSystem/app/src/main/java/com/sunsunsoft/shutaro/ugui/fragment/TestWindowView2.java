package com.sunsunsoft.shutaro.ugui.fragment;

/**
 * Created by shutaro on 2017/06/19.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.sunsunsoft.shutaro.ugui.ViewTouch;
import com.sunsunsoft.shutaro.ugui.util.UColor;
import com.sunsunsoft.shutaro.ugui.util.ULog;
import com.sunsunsoft.shutaro.ugui.uview.UDrawManager;
import com.sunsunsoft.shutaro.ugui.uview.button.UButtonCallbacks;
import com.sunsunsoft.shutaro.ugui.uview.window.UTestWindow;
import com.sunsunsoft.shutaro.ugui.uview.window.UTestWindow2;
import com.sunsunsoft.shutaro.ugui.uview.window.UWindow;
import com.sunsunsoft.shutaro.ugui.uview.window.UWindowCallbacks;

/**
 * Created by shutaro on 2017/06/19
 *
 * UWindowの親子構造をテストするView
 */

public class TestWindowView2 extends View implements View.OnTouchListener, UButtonCallbacks,
        UWindowCallbacks {
    // ボタンのID
    enum ButtonId {
        Sort("sort window")
        ;

        private final String text;

        ButtonId(final String text) {
            this.text = text;
        }

        public String getString() {
            return this.text;
        }
    }

    public static final String TAG = "TestButtonView2";

    // サイズ更新用
    private boolean isFirst = true;

    // タッチ情報
    private ViewTouch viewTouch = new ViewTouch();

    private Paint paint = new Paint();

    // UWindows
    private UWindow mTopWindow;

    // get/set
    public TestWindowView2(Context context) {
        this(context, null);
    }

    public TestWindowView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnTouchListener(this);
    }

    /**
     * 画面に表示するオブジェクトを生成
     * @param width
     * @param height
     */
    private void initDrawables(int width, int height) {
        // 描画オブジェクトクリア
        UDrawManager.getInstance().init();

        // UWindows
        int windowW = width - 200;
        int windowH = height - 200;
        mTopWindow = UTestWindow.createInstance(this, 100, 100, windowW, windowH, UColor
                    .getRandomColor());
        mTopWindow.setContentSize(2000,2000,true);

        UTestWindow2 window = UTestWindow2.createInstance(this, 100, 100,
                windowW, windowH, UColor.getRandomColor());
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
        if (UDrawManager.getInstance().draw(canvas, paint)){
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
        boolean refresh = false;

        viewTouch.checkTouchType(e);

        // Windows
        if (UDrawManager.getInstance().touchEvent(viewTouch)) {
            refresh = true;
        }

        if (refresh) {
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

        return ret;
    }

    /**
     * UButtonCallbacks
     */
    public boolean UButtonClicked(int id, boolean pressedOn) {
        ULog.print(TAG, "button click:" + id);

        if (id < ButtonId.values().length) {
            ButtonId buttonId = ButtonId.values()[id];
            switch (buttonId) {
                case Sort:
                    invalidate();
                    return true;
            }
        }
        return false;
    }

    /**
     * UWindowCallbacks
     */
    public void windowClose(UWindow window) {
        UDrawManager.getInstance().removeDrawable(window);
    }
}
