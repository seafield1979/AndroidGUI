package com.sunsunsoft.shutaro.ugui.uview.button;

/**
 * Created by shutaro on 2017/06/15.
 * UButtonがタップされた時のコールバック処理
 */
public interface UButtonCallbacks {
    /**
     * ボタンがクリックされた時の処理
     * @param id  button id
     * @param pressedOn  押された状態かどうか(On/Off)
     * @return
     */
    boolean UButtonClicked(int id, boolean pressedOn);
}
