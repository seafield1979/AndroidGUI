package com.sunsunsoft.shutaro.ugui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.LinkedList;

/**
 * UTextViewのテスト用View
 */

public class TestTextView extends View implements View.OnTouchListener, UButtonCallbacks{
    // ボタンのID
    enum ButtonId {
        Sort("sort window");

        private final String text;

        ButtonId(final String text) {
            this.text = text;
        }

        public String getString() {
            return this.text;
        }
    }

    public static final String TAG = "TestButtonView";
    private static final int TEXT_PRIORITY = 100;

    // サイズ更新用
    private boolean isFirst = true;

    // タッチ情報
    private ViewTouch viewTouch = new ViewTouch();

    private Paint paint = new Paint();

    // UTextView
    private LinkedList<UTextView> textViews = new LinkedList<>();

    // ULogWindow
    private ULogWindow logWindow;

    // get/set
    public TestTextView(Context context) {
        this(context, null);
    }

    public TestTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnTouchListener(this);
    }

    private UTextView addTextView(String text, int textSize, int priority, UTextView.UAlignment
            alignment, float x, float y, int color, int bgColor) {
        UTextView textView = UTextView.createInstance(text, textSize, priority, alignment,
                getWidth(), x, y, color, bgColor);
        textViews.add(textView);
        UDrawManager.getInstance().addDrawable(textView);


        UDrawableRect rectObj = UDrawableRect.createInstance(priority - 1, 0, y, getWidth(), 2,
                color);
        UDrawManager.getInstance().addDrawable(rectObj);

        return textView;
    }

    /**
     * 画面に表示するオブジェクトを生成
     * @param width
     * @param height
     */
    private void initDrawables(int width, int height) {
        // 描画オブジェクトクリア
        UDrawManager.getInstance().init();

        // TextView
        for (int i=0; i<10; i++) {
            String text = "hoge" + (i + 1);

            addTextView(text, 70, TEXT_PRIORITY, UTextView
                    .UAlignment.Center, width / 2, 50 + 100 * i, UColor.getRandomColor(), UColor
                    .getRandomColor());
        }
        addTextView("aaa\nbbb\nccc", 70, TEXT_PRIORITY, UTextView
                .UAlignment.Center, width / 2, 50 + 100 * 10, UColor.getRandomColor(), UColor
                .getRandomColor());


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
    public void click(int id) {
        ULog.print(TAG, "button click:" + id);

        if (id < com.sunsunsoft.shutaro.ugui.TestWindowView.ButtonId.values().length) {
            com.sunsunsoft.shutaro.ugui.TestWindowView.ButtonId buttonId = com.sunsunsoft.shutaro.ugui.TestWindowView.ButtonId.values()[id];
            switch (buttonId) {
                case Sort:
                    invalidate();
                    break;
            }
        }
    }

    public void longClick(int id) {

    }
}
