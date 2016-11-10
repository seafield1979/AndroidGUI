package com.sunsunsoft.shutaro.ugui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;

/**
 * 自前のボタン
 * 生成後ViewのonDraw内で draw メソッドを呼ぶと表示される
 */

public class UButton extends DrawableClass {
    private static final String TAG = "UButton";
    public static final int DRAW_PRIORITY = 100;

    private ButtonId id;
    private UButtonCallbacks mCallbacks;

    public UButton(UButtonCallbacks callbacks, ButtonId id, float x, float y, int width, int height, int color)
    {
        super(x, y, width, height);

        this.id = id;
        this.mCallbacks = callbacks;
        this.color = color;

        DrawManager.getInstance().addDrawable(DRAW_PRIORITY, this);
    }

    /**
     * 描画オフセットを取得する
     * @return
     */
    public PointF getDrawOffset() {
        // 親Windowの座標とスクロール量を取得
        return null;
    }

    public void draw(Canvas canvas, Paint paint, PointF offset) {
        // 内部を塗りつぶし
        paint.setStyle(Paint.Style.FILL);
        // 色
        if (isAnimating) {
            double v1 = ((double)animeFrame / (double)animeFrameMax) * 180;
            int alpha = (int)((1.0 -  Math.sin(v1 * RAD)) * 255);
            paint.setColor((alpha << 24) | (color & 0xffffff));
        } else {
            paint.setColor(color);
        }

        Rect drawRect = null;
        if (offset != null) {
            drawRect = new Rect(rect.left + (int)offset.x,
                    rect.top + (int)offset.y,
                    rect.right + (int)offset.x,
                    rect.bottom + (int)offset.y);
        } else {
            drawRect = rect;
        }
        canvas.drawRect(drawRect, paint);
    }

    public void click() {
        Log.v(TAG, "click");
        startAnim();
        if (mCallbacks != null) {
            mCallbacks.click(this);
        }
    }
    public void longClick() {
        Log.v(TAG, "long click");
        if (mCallbacks != null) {
            mCallbacks.longClick(this);
        }
    }

    /**
     * アイコンのタッチ処理
     * @param tx
     * @param ty
     * @return
     */
    public boolean checkTouch(float tx, float ty) {
        if (pos.x <= tx && tx <= getRight() &&
                pos.y <= ty && ty <= getBottom() )
        {
            return true;
        }
        return false;
    }

    /**
     * クリックのチェックとクリック処理。このメソッドはすでにクリック判定された後の座標が渡される
     * @param clickX
     * @param clickY
     * @return
     */
    public boolean checkClick(float clickX, float clickY) {
        if (pos.x <= clickX && clickX <= getRight() &&
                pos.y <= clickY && clickY <= getBottom() )
        {
            click();
            return true;
        }
        return false;
    }
}
