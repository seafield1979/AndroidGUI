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
 * メニューバー、サブViewのサンプル
 */
public class TestButtonView extends View implements OnTouchListener, UButtonCallbacks{
    enum ButtonId {
        Test1,
        Test2,
        Test3,
        Test4
    }

    public static final String TAG = "TestButtonView";
    private static final int BUTTON_PRIORITY = 100;

    // サイズ更新用
    private boolean isFirst = true;

    // タッチ情報
    private ViewTouch vt = new ViewTouch();

    private Paint paint = new Paint();

    // UButton
    private UButton[] buttons = new UButton[ButtonId.values().length];

    // UButtons
    private UButtons buttons2;
    private UButtons buttons3;

    // get/set
    public TestButtonView(Context context) {
        this(context, null);
    }

    public TestButtonView(Context context, AttributeSet attrs) {
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

        // UButton
        float y = 100;
        UButtonType buttonType;

        for (int i=0; i<buttons.length; i++) {
            ButtonId id = ButtonId.values()[i];
            if (i < 2) {
                buttonType = UButtonType.BGColor;
            } else {
                buttonType = UButtonType.Press;
            }

            buttons[i] = new UButton(this, buttonType, id.ordinal(), BUTTON_PRIORITY, "test" +
                    (i+1), 100, y,
                    width - 100*2, 120,
                    Color.rgb(0,128,0));


            UDrawManager.getInstance().addDrawable(buttons[i]);
            y += 150;
        }

        // UButtons
        int row = 2;
        int column = 2;
        buttons2 = new UButtons(this, UButtonType.Press, BUTTON_PRIORITY, Color.BLUE,
                Color.WHITE, row, column, 0, y, width, 300);
        for (int i=0; i < row * column; i++) {
            buttons2.add(100 + i, "button " + (100+i));
        }
        y += 300 + 50;

        row = 4;
        column = 1;
        buttons3 = new UButtons(this, UButtonType.Press, BUTTON_PRIORITY, Color.rgb(255, 80, 80),
                Color.WHITE, row, column, 0, y, width, 300);
        buttons3.addFull(200, "hoge");
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

        vt.checkTouchType(e);

        // Button
        if (buttons != null) {
            for (UButton button : buttons) {
                if (button != null && button.touchEvent(vt)) {
                    invalidate();
                }
            }
        }

        // Buttons
        if (buttons2 != null) {
            if (buttons2.touchEvent(vt)) {
                invalidate();
            }
        }
        if (buttons3 != null) {
            if (buttons3.touchEvent(vt)) {
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
        ULog.print(TAG, "button click:" + (button.getId() + 1));

        int id = button.getId();

        if (id < ButtonId.values().length) {
            ButtonId buttonId = ButtonId.values()[id];
            switch (buttonId) {
                case Test1:
                    break;
                case Test2:
                    break;
                case Test3:
                    break;
                case Test4:
                    break;
            }
        } else {

        }
    }
    public void longClick(UButton button) {

    }
}
