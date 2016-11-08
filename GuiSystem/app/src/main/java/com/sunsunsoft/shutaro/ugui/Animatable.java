package com.sunsunsoft.shutaro.ugui;
/**
 * アニメーションするオブジェクトのインターフェース
 */

public interface Animatable {
    double RAD = 3.1415 / 180.0;

    /**
     * アニメーション開始
     */
    void startAnim();

    /**
     * アニメーション処理
     * といいつつフレームのカウンタを増やしているだけ
     * @return true:アニメーション中
     */
    boolean animate();
}
