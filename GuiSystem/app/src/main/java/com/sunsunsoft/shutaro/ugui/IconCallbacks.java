package com.sunsunsoft.shutaro.ugui;

/**
 * アイコンをクリックしたりドロップした時のコールバック
 */

public interface IconCallbacks {
    void clickIcon(IconBase icon);
    void longClickIcon(IconBase icon);
    void dropToIcon(IconBase icon);
}
