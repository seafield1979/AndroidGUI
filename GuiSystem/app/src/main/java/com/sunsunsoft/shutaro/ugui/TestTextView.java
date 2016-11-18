package com.sunsunsoft.shutaro.ugui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;

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

    // UEditText
    private UEditText editText;

    // ULogWindow
    private ULogWindow logWindow;

    private EditText mEditText;
    private RelativeLayout topLayout;

    // get/set
    public void setTopLayout(RelativeLayout topLayout) {
        this.topLayout = topLayout;
        mEditText.setVisibility(INVISIBLE);
        topLayout.addView(mEditText);
    }

    public EditText getEditText() {
        return mEditText;
    }

    public TestTextView(Context context) {
        this(context, null);
    }

    public TestTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnTouchListener(this);

        // EditText
        mEditText = new EditText(getContext());
        RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        layout.addRule(RelativeLayout.ALIGN_PARENT_TOP, R.id.top_layout);
        layout.addRule(RelativeLayout.ALIGN_PARENT_LEFT, R.id.top_layout);
        mEditText.setLayoutParams(layout);
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
        float y = 50;
        // 描画オブジェクトクリア
        UDrawManager.getInstance().init();

        // TextView
        for (int i=0; i<5; i++) {
            String text = "hoge" + (i + 1);

            addTextView(text, 70, TEXT_PRIORITY, UTextView
                    .UAlignment.Center, width / 2, y, UColor.getRandomColor(), UColor
                    .getRandomColor());
            y += 100;
        }
        addTextView("aaa\nbbb\nccc", 70, TEXT_PRIORITY, UTextView
                .UAlignment.Center, width / 2, y, UColor.getRandomColor(), UColor
                .getRandomColor());
        y += 200;

        editText = new UEditText(getContext(), mEditText, 71, 100, y, 300, 100);
        UDrawManager.getInstance().addDrawable(editText);

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
