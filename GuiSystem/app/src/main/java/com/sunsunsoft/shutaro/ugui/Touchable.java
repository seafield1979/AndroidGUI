package com.sunsunsoft.shutaro.ugui;

/**
 * タッチ系の処理(タッチ、クリック、ドラッグ等）ができるオブジェクト
 */

public interface Touchable {
    public boolean touchEvent(ViewTouch vt);
}
