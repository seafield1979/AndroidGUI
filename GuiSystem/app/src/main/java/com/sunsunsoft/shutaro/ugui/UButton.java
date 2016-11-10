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

public class UButton extends DrawableClass implements Touchable {
    public static final String TAG = "UButton";
    public static final int DRAW_PRIORITY = 100;
    private static final int PRESS_Y = 30;

    private ButtonId id;
    private UButtonCallbacks mCallbacks;
    private boolean isPressed;
    private String text;
    private int textColor;
    private int pressedColor;

    // Get/Set
    public ButtonId getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public UButton(UButtonCallbacks callbacks, ButtonId id, String text, float x, float y, int width, int height, int color)
    {
        super(x, y, width, height);
        this.id = id;
        this.mCallbacks = callbacks;
        this.color = color;
        this.text = text;
        this.textColor = Color.WHITE;
        this.pressedColor = UColor.addBrightness(color, 2.0f);

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
        // 押されていたら明るくする
        int _color = color;
        if (isPressed) {
            _color = pressedColor;
        }

        if (isAnimating) {
            double v1 = ((double)animeFrame / (double)animeFrameMax) * 180;
            int alpha = (int)((1.0 -  Math.sin(v1 * RAD)) * 255);
            paint.setColor((alpha << 24) | (_color & 0xffffff));
        } else {
            paint.setColor(_color);
        }

        PointF _pos = new PointF(pos.x, pos.y);

//        if (isPressed) {
//            _pos.y += PRESS_Y;
//        }
//
//        if (offset != null) {
//            _pos.x += offset.x;
//            _pos.y += offset.y;
//        }
        UDraw.drawRoundRectFill(canvas, paint, _pos.x, _pos.y, _pos.x + size.width, _pos.y + size
                .height, 20, _color);

        // テキスト
        if (text != null) {
            Rect bound = new Rect();
            paint.setTextSize(50);
            paint.setColor(textColor);

            // センタリング
            paint.getTextBounds(text, 0, text.length(), bound);
            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
            float baseY = pos.y + size.height / 2 - (fontMetrics.ascent + fontMetrics
                    .descent) / 2;

            canvas.drawText(text, _pos.x + (size.width - bound.width()) / 2, baseY, paint);
        }
    }

    public void click() {
        Log.v(TAG, "click");
        if (mCallbacks != null) {
            mCallbacks.click(this);
        }
    }

    /**
     * Touchable Interface
     */

    /**
     * タッチイベント
     * @param vt
     * @return true:イベントを処理した(再描画が必要)
     */
    public boolean touchEvent(ViewTouch vt) {
        boolean done = false;
//        ULog.print(TAG, "vt:" + vt.type);
        switch(vt.type) {
            case None:
                break;
            case Touch:
                if (rect.contains((int)vt.touchX(), (int)vt.touchY())) {
                    isPressed = true;
                    done = true;
                }
                break;
            case Click:
            case LongClick:
                isPressed = false;
                done = true;
                if (rect.contains((int)vt.touchX(), (int)vt.touchY())) {
                    mCallbacks.click(this);
                }
                break;
            case TouchUp:
            case MoveEnd:
                if (isPressed) {
                    isPressed = false;
                    done = true;
                }
                break;
        }
        return done;
    }
}
