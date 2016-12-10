package com.sunsunsoft.shutaro.ugui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by shutaro on 2016/12/09.
 */

public class PageViewListView  extends UPageView
        implements UButtonCallbacks, UListItemCallbacks {
    /**
     * Enums
     */

    /**
     * Consts
     */
    public static final String TAG = "PageViewListView";

    /**
     * Member Variables
     */
    private UListView mListView;

    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    public PageViewListView(Context context, View parentView) {
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

        mListView = new UListView(null, this, 100, 0, 0, width, height, Color.WHITE);
        mListView.addToDrawManager();
        mListView.addDummyItems(20);
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

        return false;
    }

    /**
     * UListItemCallbacks
     */
    public void ListItemClicked(UListItem item) {
        ULog.print(TAG, "list item clicked:" + item.getIndex());
    }
}
