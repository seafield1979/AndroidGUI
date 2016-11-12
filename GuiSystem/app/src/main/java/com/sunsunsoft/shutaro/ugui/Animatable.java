package com.sunsunsoft.shutaro.ugui;
/**
 * アニメーションするオブジェクトのインターフェース
 */

public interface Animatable {
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
