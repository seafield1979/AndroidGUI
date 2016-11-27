package com.sunsunsoft.shutaro.ugui;

/**
 * Created by shutaro on 2016/11/15.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * テストViewのテンプレート
 */
public class TestViewTemplate extends View implements View.OnTouchListener, UButtonCallbacks{
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
    private ULogWindow logWindow;

    // get/set
    public TestViewTemplate(Context context) {
        this(context, null);
    }

    public TestViewTemplate(Context context, AttributeSet attrs) {
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
            buttons[i] = new UButtonText(this, UButtonType.Press, id.ordinal(), BUTTON_PRIORITY,
                    id.toString(),
                    100, y,
                    width - 100*2, 120,
                    Color.WHITE,
                    Color.rgb(0,128,0));
            if (buttons[i] != null) {
                UDrawManager.getInstance().addDrawable(buttons[i]);
            }
            y += 150;
        }

        // LogWindow
        logWindow = ULogWindow.createInstance(getContext(), this,LogWindowType.AutoDisappear,
                0, 500, getWidth(), getHeight() - 500);
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
    public boolean UButtonClick(int id) {
        ULog.print(TAG, "button click:" + id);

        if (id < com.sunsunsoft.shutaro.ugui.TestWindowView.ButtonId.values().length) {
            com.sunsunsoft.shutaro.ugui.TestWindowView.ButtonId buttonId = com.sunsunsoft.shutaro.ugui.TestWindowView.ButtonId.values()[id];
            switch (buttonId) {
                case Sort:
                    invalidate();
                    return true;
            }
        }

        return false;
    }
    public boolean UButtonLongClick(int id) {
        return false;
    }
}
