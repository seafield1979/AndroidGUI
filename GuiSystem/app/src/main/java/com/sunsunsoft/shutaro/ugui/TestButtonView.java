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
    private ViewTouch viewTouch = new ViewTouch();

    private Paint paint = new Paint();

    // UButton
    private UButton[] buttons = new UButton[ButtonId.values().length];

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
            y += 150;
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
        ULog.print(TAG, "button click:" + (button.getId() + 1));

        ButtonId buttonId = ButtonId.values()[button.getId()];
        switch(buttonId) {
            case Test1:
                break;
            case Test2:
                break;
            case Test3:
                break;
            case Test4:
                break;
        }
    }
    public void longClick(UButton button) {

    }
}
