package com.sunsunsoft.shutaro.ugui.uview.icon;

/**
 * Created by shutaro on 2017/06/15.
 *
 * アイコンをクリックしたりドロップした時のコールバック
 */
public interface UIconCallbacks {
    void clickIcon(UIcon icon);
    void longClickIcon(UIcon icon);
    void dropToIcon(UIcon icon);
}