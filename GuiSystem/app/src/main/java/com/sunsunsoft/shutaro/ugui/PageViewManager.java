package com.sunsunsoft.shutaro.ugui;

import android.content.Context;
import android.view.View;

/**
 * Created by shutaro on 2016/12/14.
 */

enum PageView {
    IconWindow,
    Test1,
    ScrollWindow,
    ListView,
    ImageView
    ;

    public static PageView toEnum(int value) {
        if (value >= values().length) return PageView.IconWindow;
        return values()[value];
    }
}

public class PageViewManager extends UPageViewManager {
    /**
     * Constructor
     */
    // Singletonオブジェクト
    private static PageViewManager singleton;

    // Singletonオブジェクトを作成する
    public static PageViewManager createInstance(Context context, View parentView) {
        if (singleton == null) {
            singleton = new PageViewManager(context, parentView);
        }
        return singleton;
    }
    public static PageViewManager getInstance() { return singleton; }

    private PageViewManager(Context context, View parentView) {
        mContext = context;
        mParentView = parentView;

        initPages();
    }


    /**
     * 配下のページを追加する
     */
    public void initPages() {
        UPageView page;
        // IconWindow
        page = new PageViewIconWindow(mContext, mParentView);
        pages[PageView.IconWindow.ordinal()] = page;

        // Test1
        page = new PageViewTest1(mContext, mParentView);
        pages[PageView.Test1.ordinal()] = page;

        // ScrollWindow
        page = new PageViewScrollWindow(mContext, mParentView);
        pages[PageView.ScrollWindow.ordinal()] = page;

        // ListView
        page = new PageViewListView(mContext, mParentView);
        pages[PageView.ListView.ordinal()] = page;

        // ImageView
        page = new PageViewImageView(mContext, mParentView);
        pages[PageView.ImageView.ordinal()] = page;
    }
}
