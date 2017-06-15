package com.sunsunsoft.shutaro.ugui.pageview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import com.sunsunsoft.shutaro.ugui.R;
import com.sunsunsoft.shutaro.ugui.uview.*;
import com.sunsunsoft.shutaro.ugui.uview.button.*;
import com.sunsunsoft.shutaro.ugui.ViewTouch;

/**
 * Created by shutaro on 2016/12/13.
 *
 * UImageViewのテストページ
 */

public class PageViewImageView extends UPageView implements UButtonCallbacks {
    /**
     * Enums
     */

    /**
     * Consts
     */
    public static final String TAG = "PageViewImageView";

    private static final int MARGIN_H = 50;
    private static final int MARGIN_V = 50;
    private static final int DRAW_PRIORITY = 100;
    private static final int IMAGE_VIEW_W = 150;

    private static final int ButtonId1 = 100;


    /**
     * Member Variables
     */
    private UImageView[] mImageViews = new UImageView[3];
    private UButtonText mButton;

    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    public PageViewImageView(Context context, View parentView) {
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

        float x = MARGIN_H;
        float y = MARGIN_V;

        for (int i=0; i<mImageViews.length; i++) {
            mImageViews[i] = new UImageView(DRAW_PRIORITY, R.drawable.hogeman, x, y, IMAGE_VIEW_W,
                    IMAGE_VIEW_W);
            mImageViews[i].addToDrawManager();
            mImageViews[i].addState(R.drawable.hogeman2);
            y += mImageViews[i].getHeight() + MARGIN_V;
        }

        mButton = new UButtonText(this, UButtonType.Press, ButtonId1,
                DRAW_PRIORITY, "change state", x, y, width - MARGIN_H * 2, 150,
                50, Color.WHITE, Color.rgb(150, 100, 100));
        mButton.addToDrawManager();
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
    /**
     * ボタンがクリックされた時の処理
     * @param id  button id
     * @param pressedOn  押された状態かどうか(On/Off)
     * @return
     */
    public boolean UButtonClicked(int id, boolean pressedOn) {
        switch(id) {
            case ButtonId1:
                for (int i=0; i<mImageViews.length; i++) {
                    mImageViews[i].setNextState();
                }
                break;
        }
        return false;
    }
}
