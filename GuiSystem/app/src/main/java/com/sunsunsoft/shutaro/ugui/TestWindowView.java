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
    private static final int WINDOW_NUM = 4;

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
            buttons[i] = new UButton(this, id.ordinal(), id.toString(), 100, y,
                    width - 100*2, 120,
                    Color.rgb(0,128,0));
            y += 150;
        }

        // LogWindow
        logWindow = ULogWindow.createInstance(getContext(), this,LogWindowType.AutoDisappear,
                0, 500, getWidth(), getHeight() - 500);

        // UWindows
        float x = 0;
        y = 0;
        int windowW = width / 2;
        int windowH = height / 2;
        for (int i=0; i<WINDOW_NUM; i++) {
            UWindow window = UTestWindow.createInstance(x, y, windowW, windowH, UColor
                    .getRandomColor());
            windows.add(window);
            x += windowW;
            if (x >= width) {
                x = 0;
                y += windowH;
            }
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
    public void click(UButton button) {
        ULog.print(TAG, "button click:" + button.getId());

        ButtonId buttonId = ButtonId.values()[button.getId()];
        switch(buttonId) {
            case Sort:
                sortWindows(getWidth(), getHeight());
                invalidate();
                break;
        }
    }
    public void longClick(UButton button) {

    }
}
