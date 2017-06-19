package com.sunsunsoft.shutaro.ugui.pageview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.sunsunsoft.shutaro.ugui.uview.UDrawManager;
import com.sunsunsoft.shutaro.ugui.util.ULog;
import com.sunsunsoft.shutaro.ugui.uview.UPageView;
import com.sunsunsoft.shutaro.ugui.uview.scrollbar.UScrollWindowTest;
import com.sunsunsoft.shutaro.ugui.uview.window.*;
import com.sunsunsoft.shutaro.ugui.uview.button.*;
import com.sunsunsoft.shutaro.ugui.ViewTouch;

/**
 * Created by shutaro on 2016/12/09.
 */

public class PageViewScrollWindow extends UPageView implements UButtonCallbacks, UWindowCallbacks {
    /**
     * Enums
     */
    /**
     * Consts
     */
    public static final String TAG = "PageViewTest1";
    private static final int BUTTON_PRIORITY = 100;
    private static final int IMAGE_BUTTON_ID = 10;

    private static final int ButtonIdOK1 = 100;

    /**
     * Member Variables
     */
    // UScrollWindow
    private UScrollWindowTest mScrollWindow;

    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    public PageViewScrollWindow(Context context, View parentView) {
        super(context, parentView);
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


        mScrollWindow = new UScrollWindowTest(this, 1, 0, 0, width, height, Color.WHITE);
        mScrollWindow.addToDrawManager();
        mScrollWindow.setContentSize(2000,2000, true);
        mScrollWindow.updateWindow();

        UButtonText button = new UButtonText(this, UButtonType.BGColor, ButtonIdOK1, BUTTON_PRIORITY, "test", 100, 100,
                width - 100*2, 120,
                50, Color.WHITE, Color.rgb(0,128,0));


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

    public boolean UButtonClicked(int id, boolean pressedOn) {
        ULog.print(TAG, "button click:" + (id + 1));
        switch (id) {
            case ButtonIdOK1:
                break;
        }
        return false;
    }
    public boolean UButtonLongClick(int id) {
        return false;
    }

    /**
     * UWindowCallbacks
     */
    public void windowClose(UWindow window) {

    }
}
