package com.sunsunsoft.shutaro.udrawsystem;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * DrawManagerに登録できるようになるインターフェース
 *
 */

public interface Drawable {
    /**
     * 描画処理
     * @param canvas
     * @param paint
     */
    void draw(Canvas canvas, Paint paint );

    /**
     * アニメーション処理
     * @return
     */
    boolean animate();

    /**
     * アニメーション中かどうか
     * @return
     */
    boolean isAnimating();

    /**
     * 描画範囲の矩形を取得
     * @return
     */
    Rect getRect();
}
