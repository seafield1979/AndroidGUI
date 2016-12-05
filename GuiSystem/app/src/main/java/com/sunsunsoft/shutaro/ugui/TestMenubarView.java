package com.sunsunsoft.shutaro.ugui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * UMenuBarのテスト用フラグメント
 */

public class TestMenubarView extends View implements View.OnTouchListener, UButtonCallbacks, UMenuItemCallbacks{
    enum ButtonId {
        Test1,
        Test2,
        Test3
    }

    public static final String TAG = "TestButtonView";
    private static final int BUTTON_PRIORITY = 100;

    // サイズ更新用
    private boolean isFirst = true;

    // タッチ情報
    private ViewTouch viewTouch = new ViewTouch();

    private Paint paint = new Paint();

    // UButton
    private UButtonText[] buttons = new UButtonText[3];

    // MenuBar
    private UMenuBar menuBar;

    // get/set
    public TestMenubarView(Context context) {
        this(context, null);
    }

    public TestMenubarView(Context context, AttributeSet attrs) {
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

        // buttons
        float y = 100;
        for (int i=0; i<buttons.length; i++) {
            buttons[i] = new UButtonText(this, UButtonType.Press, i, BUTTON_PRIORITY, "test" +
                    (i+1),
                    100, y, width - 100*2, 120,
                    Color.WHITE,
                    Color.rgb(0,128,0));
            y += 150;
        }

        // MenuBar
        menuBar = UMenuBar.createInstance(this, this, getWidth(), getHeight(), Color.BLACK);
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

        // Windowアクション(手前から順に処理する)
        if (menuBar.doAction()) {
            invalidate();
        }

        // 描画オブジェクトをまとめて描画
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

        // Buttons
        for (UButton button : buttons) {
            if (button.touchEvent(viewTouch)) {
                invalidate();
            }
        }

        // MenuBar
        if (menuBar.touchEvent(viewTouch)) {
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

        ButtonId buttonId = ButtonId.values()[id];
        switch(buttonId) {
            case Test1:
                return true;
            case Test2:
                return true;
            case Test3:
                return true;
        }
        return false;
    }
    public boolean UButtonLongClick(int id) {
        return false;
    }

    /**
     * UMenuItemCallbacksインターフェース
     */
    /**
     *
     * @param id
     */
    public void menuItemClicked(MenuItemId id, int stateId)
    {
        ULog.print(TAG, "clicked:" + id);
        switch (id) {
            case AddCard1:
                break;
            case AddCard2:
                break;
            case AddCard3:
                break;
            case AddBook:
                break;
            case AddBox:
                break;
            case Sort1:
                break;
            case Sort2:
                break;
            case Sort3:
                break;
            case ListType1:
                break;
            case ListType2:
                break;
            case ListType3:
                break;
            case Debug1:
                break;
            case Debug2:
                break;
            case Debug3:
                break;
        }
    }

    /**
     *
     */
    public void menuItemCallback2() {

    }
}
