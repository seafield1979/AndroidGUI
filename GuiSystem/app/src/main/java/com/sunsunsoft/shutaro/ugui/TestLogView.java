package com.sunsunsoft.shutaro.ugui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * ULogWindowのテスト用 View
 */

public class TestLogView extends View implements OnTouchListener, UButtonCallbacks{
    enum ButtonId {
        ShowToggle("show/hide"),
        AddLog("add log"),
        Clear("clear"),
        MoveUp("move up"),
        MoveDown("move down")
        ;

        private final String text;

        private ButtonId(final String text) {
            this.text = text;
        }

        public String getString() {
            return this.text;
        }
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

    // get/set
    public TestLogView(Context context) {
        this(context, null);
    }

    public TestLogView(Context context, AttributeSet attrs) {
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
            String title = id.getString();
            buttons[i] = new UButton(this, BUTTON_PRIORITY, id.ordinal(), title, 100, y, width -
                    100*2,
                    120,
                    Color.rgb(0,128,0));
            y += 150;
        }

        // LogWindow
        logWindow = ULogWindow.createInstance(getContext(), this, 0, 500, getWidth(), getHeight()
                - 500);
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

        viewTouch.checkTouchType(e);

        for (UButton button : buttons) {
            if (button.touchEvent(viewTouch)) {
                invalidate();
            }
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
            case ShowToggle:
                logWindow.toggle();
                break;
            case AddLog:
                logWindow.addLog("hoge", Color.WHITE);
                break;
            case Clear:
                logWindow.clear();
                break;
            case MoveUp:
                logWindow.startMove(0,0, 20);
                break;
            case MoveDown:
                logWindow.startMove(0,500, 20);
                break;
        }
    }
    public void longClick(UButton button) {

    }
}
