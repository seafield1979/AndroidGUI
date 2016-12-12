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

        ButtonId(final String text) {
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
    private UButtonText[] buttons = new UButtonText[ButtonId.values().length];

    // ULogWindow
    private ULogWindow[] logWindows = new ULogWindow[2];

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
        // 描画オブジェクトクリア
        UDrawManager.getInstance().init();

        // Buttons
        float y = 100;
        for (int i=0; i<buttons.length; i++) {
            ButtonId id = ButtonId.values()[i];
            String title = id.getString();

            buttons[i] = new UButtonText(this, UButtonType.Press, id.ordinal(), BUTTON_PRIORITY,
                    title, 100, y,
                    width - 100*2, 120,
                    50, Color.WHITE, Color.rgb(0,128,0));
            UDrawManager.getInstance().addDrawable(buttons[i]);
            y += 150;
        }

        // LogWindow
        logWindows[0] = ULogWindow.createInstance(getContext(), this, LogWindowType.AutoDisappear,
                0, 500, getWidth(),
                getHeight() - 500);
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (isFirst) {
            isFirst = false;
            initDrawables(getWidth(), getHeight());
        }
        // 毎フレームの処理
        if (logWindows[0].doAction()) {
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
    public boolean UButtonClicked(int id, boolean pressedOn) {
        ULog.print(TAG, "button click:" + id);

        ButtonId buttonId = ButtonId.values()[id];
        switch(buttonId) {
            case ShowToggle:
                logWindows[0].toggle();
                return true;
            case AddLog:
                logWindows[0].addLog("hoge", Color.WHITE);
                return true;
            case Clear:
                logWindows[0].clear();
                return true;
            case MoveUp:
                logWindows[0].startMoving(0,0, 20);
                return true;
            case MoveDown:
                logWindows[0].startMoving(0,500, 20);
                return true;
        }
        return false;
    }
}
