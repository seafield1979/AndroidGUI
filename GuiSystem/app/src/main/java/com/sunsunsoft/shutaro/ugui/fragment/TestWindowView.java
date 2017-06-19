package com.sunsunsoft.shutaro.ugui.fragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.sunsunsoft.shutaro.ugui.uview.button.*;
import com.sunsunsoft.shutaro.ugui.util.UColor;
import com.sunsunsoft.shutaro.ugui.uview.UDrawManager;
import com.sunsunsoft.shutaro.ugui.util.ULog;
import com.sunsunsoft.shutaro.ugui.uview.window.UTestWindow;
import com.sunsunsoft.shutaro.ugui.uview.window.*;
import com.sunsunsoft.shutaro.ugui.ViewTouch;

/**
 * Created by shutaro on 2016/11/10.
 */

public class TestWindowView extends View implements View.OnTouchListener, UButtonCallbacks,
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

    // ウィンドウの種類
    enum WindowId {
        Main,
        Sub
    }

    public static final String TAG = "TestButtonView";
    private static final int BUTTON_PRIORITY = 100;
    private static final int WINDOW_NUM = 4;

    // サイズ更新用
    private boolean isFirst = true;

    // タッチ情報
    private ViewTouch viewTouch = new ViewTouch();

    private Paint paint = new Paint();

    // UButton
    private UButtonText[] buttons = new UButtonText[ButtonId.values().length];

    // UWindows
    private UWindowList windows = UWindowList.getInstance();

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
        // 描画オブジェクトクリア
        UDrawManager.getInstance().init();

        // Buttons
        float y = 100;
        for (int i=0; i<buttons.length; i++) {
            ButtonId id = ButtonId.values()[i];
            buttons[i] = new UButtonText(this, UButtonType.Press, id.ordinal(), BUTTON_PRIORITY,
                    id.toString(),
                    100, y, width - 100*2, 120,
                    50, Color.WHITE, Color.rgb(0,128,0));
            if (buttons[i] != null) {
                UDrawManager.getInstance().addDrawable(buttons[i]);
            }
            y += 150;
        }

        // UWindows
        float x = 0;
        y = 0;
        int windowW = width / 2;
        int windowH = height / 2;
        for (int i=0; i<WINDOW_NUM; i++) {
            UWindow window = UTestWindow.createInstance(this, x, y, windowW, windowH, UColor
                    .getRandomColor());
            windows.add(window);
            x += windowW;
            if (x >= width) {
                x = 0;
                y += windowH;
            }
            window .setContentSize(2000,2000,true);
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
     * ウィンドウを整列
     */
    public void sortWindows(int width, int height) {
        float x = 0;
        float y = 0;
        int windowW = width / 2;
        int windowH = height / 2;
        for (UWindow window : windows.getLists()) {
            window.setPos(x, y);
            ULog.print(TAG, "pos:" + x + " " + y);
            x += windowW;
            if (x >= width) {
                x = 0;
                y += windowH;
            }
        }
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
                    sortWindows(getWidth(), getHeight());
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
        windows.remove(window);
    }
}
