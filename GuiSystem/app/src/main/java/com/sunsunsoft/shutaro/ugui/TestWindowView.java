package com.sunsunsoft.shutaro.ugui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by shutaro on 2016/11/10.
 */

public class TestWindowView extends View implements View.OnTouchListener, UButtonCallbacks{
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

    // ウィンドウの種類
    enum WindowId {
        Main,
        Sub
    }

    public static final String TAG = "TestButtonView";
    private static final int BUTTON_PRIORITY = 100;

    // サイズ更新用
    private boolean isFirst = true;

    // タッチ情報
    private ViewTouch viewTouch = new ViewTouch();

    private Paint paint = new Paint();

    // UButton
    private UButton[] buttons = new UButton[ButtonId.values().length];

    // ULogWindow
    private ULogWindow logWindow;

    // UWindows
    private UWindow[] windows = new UWindow[2];

    // get/set
    public TestWindowView(Context context) {
        this(context, null);
    }

    public TestWindowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnTouchListener(this);
    }

    /**
     * 画面に表示するオブジェクトを生成
     * @param width
     * @param height
     */
    private void initDrawables(int width, int height) {

        // Buttons
        float y = 100;
        for (int i=0; i<buttons.length; i++) {
            ButtonId id = ButtonId.values()[i];
            buttons[i] = new UButton(this, BUTTON_PRIORITY, id.ordinal(), id.toString(), 100, y,
                    width -
                    100*2,
                    120,
                    Color.rgb(0,128,0));
            y += 150;
        }

        // LogWindow
        logWindow = ULogWindow.createInstance(getContext(), this, 0, 500, getWidth(), getHeight()
                - 500);

        // UWindows
        float x = 0;
        y = 0;
        int windowH = height / 2;
        for (int i=0; i<windows.length; i++) {
            windows[i] = UTestWindow.createInstance(x, y, width, windowH, UColor
                    .getRandomColor());
            y += windowH;
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (isFirst) {
            isFirst = false;
            initDrawables(getWidth(), getHeight());
        }
        // 毎フレームの処理
        if (logWindow.doAction()) {
            invalidate();
        }

        for (UWindow window : windows) {
            if (window.doAction()) {
                invalidate();
            }
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

        // Buttons
        for (UButton button : buttons) {
            if (button.touchEvent(viewTouch)) {
                refresh = true;
            }
        }

        // Windows
        for (UWindow window : windows) {
            if (window.touchEvent(viewTouch)) {
                refresh = true;
                break;
            }
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
    public void click(UButton button) {
        ULog.print(TAG, "button click:" + button.getId());

        ButtonId buttonId = ButtonId.values()[button.getId()];
        switch(buttonId) {
            case Sort:
                break;
        }
    }
    public void longClick(UButton button) {

    }
}
