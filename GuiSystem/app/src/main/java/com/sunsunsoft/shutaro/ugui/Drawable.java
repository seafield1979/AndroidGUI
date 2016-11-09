package com.sunsunsoft.shutaro.ugui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

/**
 * DrawManagerに登録できるようになるインターフェース
 *
 */

public interface Drawable {
    double RAD = 3.1415 / 180.0;

    /**
     * 描画処理
     * @param canvas
     * @param paint
     */
    void draw(Canvas canvas, Paint paint, PointF offset );

    /**
     * 描画オフセットを取得する
     * @return
     */
    public PointF getDrawOffset();

    /**
     * 描画範囲の矩形を取得
     * @return
     */
    Rect getRect();

    public void setDrawList(DrawList drawList);
    public DrawList getDrawList();

    /**
     * アニメーション開始
     */
    void startAnim();

    /**
     * アニメーション処理
     * onDrawからの描画処理で呼ばれる
     * @return true:アニメーション中
     */
    boolean animate();

    /**
     * アニメーション中かどうか
     * @return
     */
    boolean isAnimating();
}
