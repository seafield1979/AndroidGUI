package com.sunsunsoft.shutaro.ugui;

/**
 * 目的の座標に移動可能なオブジェクトのインターフェース
 */

public interface AutoMovable {

    /**
     * 自動移動開始
     * @param dstX  目的位置x
     * @param dstY  目的位置y
     * @param frame  移動にかかるフレーム数
     */
    void startMove(float dstX, float dstY, int frame);

    /**
     * 移動
     * 移動開始位置、終了位置、経過フレームから現在位置を計算する
     * @return 移動完了したらtrue
     */
    boolean move();
}
