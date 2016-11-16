package com.sunsunsoft.shutaro.ugui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * タッチイベントの処理
 */

public class TestTouchView extends View implements View.OnTouchListener, UButtonCallbacks, ViewTouchCallbacks {
    enum ButtonId {
        Clear1("clear1"),
        Clear2("clear2")
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
    private ViewTouch vt = new ViewTouch(this);

    private Paint paint = new Paint();
    private Context mContext;

    // UButton
    private UButtonText[] buttons = new UButtonText[ButtonId.values().length];

    // ULogWindow
    private ULogWindow[] logWindows = new ULogWindow[2];

    // get/set
    public TestTouchView(Context context) {
        this(context, null);
    }

    public TestTouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnTouchListener(this);
        mContext = context;
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
            buttons[i] = new UButtonText(this, UButtonType.Press, id.ordinal(),
                    BUTTON_PRIORITY, title,
                    100, y, width - 100*2, 120,
                    Color.WHITE,
                    Color.rgb(0,128,0));
            UDrawManager.getInstance().addDrawable(buttons[i]);
            y += 150;
        }

        // LogWindow
        logWindows[0] = ULogWindow.createInstance(getContext(), this, LogWindowType.Fix,
                0, getHeight()/2,
                getWidth()/2,
                getHeight()/2);
        logWindows[1] = ULogWindow.createInstance(getContext(), this, LogWindowType.Fix,
                getWidth()/2, getHeight()/2,
                getWidth(),
                getHeight()/2);
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (isFirst) {
            isFirst = false;
            initDrawables(getWidth(), getHeight());
        }
        // 毎フレームの処理
        for (ULogWindow logWindow : logWindows) {
            if (logWindow.doAction()) {
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

        vt.checkTouchType(e);

        for (UButton button : buttons) {
            if (button.touchEvent(vt)) {
                invalidate();
            }
        }

        if (vt.isTouchUp()) {
            logWindows[0].addLog("TouchUp");
            invalidate();
        }
        // タッチイベントのログをとる
        switch(vt.type) {
            case None:
                break;
            case Touch:        // タッチ開始
                logWindows[0].addLog("Touch " + vt.touchX() + " " + vt.touchY());
                invalidate();
                break;
            case LongPress:
                logWindows[0].addLog("LongPress");
                invalidate();
                break;
            case Click:        // ただのクリック（タップ)
                logWindows[0].addLog("Click " + vt.touchX() + " " + vt.touchY());
                invalidate();
                break;
            case LongClick:    // 長クリック
                logWindows[0].addLog("Long Click " + vt.touchX() + " " + vt.touchY());
                invalidate();
                break;
            case Moving:       // 移動
                logWindows[0].addLog("Move " + vt.getX() + " " + vt.getY());
                invalidate();
                break;
            case MoveEnd:      // 移動終了
                logWindows[0].addLog("MoveEnd");
                invalidate();
                break;
            case MoveCancel:    // 移動キャンセル
                logWindows[0].addLog("MoveCancel");
                invalidate();
                break;
        }
        if (vt.isMoveStart()) {
            logWindows[0].addLog("Move Start:" + vt.getX() + " " + vt.getY());
        }

        switch(e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // trueを返す。こうしないと以降のMoveイベントが発生しなくなる。
                ret = true;
                logWindows[1].addLog("ACTION_DOWN " + e.getX() + " " + e.getY());
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                ret = true;
                logWindows[1].addLog("ACTION_UP");
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                ret = true;
                logWindows[1].addLog("ACTION_MOVE " + e.getX() + " " + e.getY());
                invalidate();
                break;
            default:
        }

        return ret;
    }


    /**
     * UButtonCallbacks
     */
    public void click(int id) {
        ULog.print(TAG, "button click:" + id);

        ButtonId buttonId = ButtonId.values()[id];
        switch(buttonId) {
            case Clear1:
                logWindows[0].clear();
                break;
            case Clear2:
                logWindows[1].clear();
                break;
        }
    }
    public void longClick(int id) {
    }

    /**
     * ViewTouchCallbacks
     */
    public void longPressed() {
        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logWindows[0].addLog("long pressed");
                invalidate();
            }
        });
    }
}
