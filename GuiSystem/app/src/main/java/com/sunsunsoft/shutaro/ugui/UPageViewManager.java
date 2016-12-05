package com.sunsunsoft.shutaro.ugui;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

enum PageView {
    IconWindow,
    Test1
}

/**
 * Created by shutaro on 2016/12/05.
 *
 * 各ページを管理するクラス
 * 現在のページ番号を元に配下の PageView の処理を呼び出す
 */

public class UPageViewManager {
    /**
     * Enums
     */
    /**
     * Consts
     */

    /**
     * Member Variables
     */
    private Context mContext;
    private View mParentView;
    private PageView mPageId;
    private UPageView[] pages = new UPageView[PageView.values().length];

    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    public UPageViewManager(Context context, View parentView) {
        mContext = context;
        mParentView = parentView;
        mPageId = PageView.Test1;

        initPages();
    }

    /**
     * Methods
     */
    /**
     * 配下のページを追加する
     */
    public void initPages() {
        UPageView page = new PageViewIconWindow(mContext, mParentView);
        pages[PageView.IconWindow.ordinal()] = page;

        page = new PageViewTest1();
        pages[PageView.Test1.ordinal()] = page;


    }

    /**
     * 描画処理
     * 配下のUViewPageの描画処理を呼び出す
     * @param canvas
     * @param paint
     * @return
     */
    public boolean draw(Canvas canvas, Paint paint) {
        return pages[mPageId.ordinal()].draw(canvas, paint);
    }

    /**
     * タッチ処理
     * 配下のUViewPageのタッチ処理を呼び出す
     * @param vt
     * @return
     */
    public boolean touchEvent(ViewTouch vt) {
        return false;
    }

    /**
     * バックキーが押されたときの処理
     * @return
     */
    public boolean onBackKeyDown() {
        return pages[mPageId.ordinal()].onBackKeyDown();
    }

    /**
     * 表示ページを切り替える
     * @param pageId
     */
    public void changePage(PageView pageId) {
        if (pageId.ordinal() >= PageView.values().length) {
            return;
        }
        mPageId = pageId;
    }

    /**
     * ページに表示するウィンドウを初期化する
     * @param width
     * @param height
     */
    public void initDrawables(int width, int height) {
        pages[mPageId.ordinal()].initDrawables(width, height);
    }


    /**
     * Callbacks
     */

}
