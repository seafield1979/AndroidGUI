package com.sunsunsoft.shutaro.ugui;

/**
 * アイコンをクリックしたりドロップした時のコールバック
 */

public interface UIconCallbacks {
    void clickIcon(UIcon icon);
    void longClickIcon(UIcon icon);
    void dropToIcon(UIcon icon);
}
