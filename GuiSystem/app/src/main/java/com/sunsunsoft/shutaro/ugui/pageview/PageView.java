package com.sunsunsoft.shutaro.ugui.pageview;

/**
 * Created by shutaro on 2017/06/15.
 */
public enum PageView {
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
