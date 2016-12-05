package com.sunsunsoft.shutaro.ugui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by shutaro on 2016/12/05.
 *
 * UViewPageテスト用のクラス
 */

public class PageViewTest1 extends UPageView implements UButtonCallbacks{
    /**
     * Enums
     */
    enum ButtonId {
        Test1,
        Test2,
        Test3,
        Test4
    }

    /**
     * Consts
     */
    public static final String TAG = "PageViewTest1";
    private static final int BUTTON_PRIORITY = 100;
    private static final int IMAGE_BUTTON_ID = 10;

    /**
     * Member Variables
     */
    // UButton
    private UButtonText[] buttons = new UButtonText[ButtonId.values().length];


    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    public PageViewTest1(Context context, View parentView) {
        super(context, parentView, PageView.Test1.getDrawId());
    }

    /**
     * Methods
     */

    /**
     * ページの描画オブジェクトを初期化
     */
    public void initDrawables() {
        int width = mParentView.getWidth();
        int height = mParentView.getHeight();

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

            buttons[i] = new UButtonText(this, buttonType, id.ordinal(), BUTTON_PRIORITY, "test" +
                    (i+1), 100, y,
                    width - 100*2, 120,
                    Color.WHITE,
                    Color.rgb(0,128,0));


            UDrawManager.getInstance().addDrawable(buttons[i]);
            y += 150;
        }

    }

    public boolean draw(Canvas canvas, Paint paint) {
        super.draw(canvas, paint);

        return false;
    }

    public boolean touchEvent(ViewTouch vt) {
        return false;
    }

    public boolean onBackKeyDown() {
        return false;
    }

    /**
     * Callbacks
     */

    /**
     * UButtonCallbacks
     */

    public boolean UButtonClick(int id) {
        ULog.print(TAG, "button click:" + (id + 1));

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
            return true;
        } else {
            switch(id) {
                case IMAGE_BUTTON_ID:

                    break;
            }
        }
        return false;
    }
    public boolean UButtonLongClick(int id) {
        return false;
    }
}
