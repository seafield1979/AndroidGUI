package com.sunsunsoft.shutaro.ugui;

/**
 * アイコンをクリックしたりドロップした時のコールバック
 */

public interface IconCallbacks {
    void clickIcon(Icon icon);
    void longClickIcon(Icon icon);
    void dropToIcon(Icon icon);
}
