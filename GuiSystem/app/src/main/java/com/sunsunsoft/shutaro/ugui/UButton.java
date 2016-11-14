package com.sunsunsoft.shutaro.ugui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;


enum UButtonType {
    BGColor,    // color changing
    Press       // pressed down
}

/**
 * 自前のボタン
 * 生成後ViewのonDraw内で draw メソッドを呼ぶと表示される
 * ボタンが押されたときの動作はtypeで指定できる
 *   BGColor ボタンの背景色が変わる
 *   Press   ボタンがへこむ
 */

public class UButton extends Drawable {
    public static final String TAG = "UButton";
    public static final int DRAW_PRIORITY = 100;
    private static final int PRESS_Y = 20;
    private static final int BUTTON_RADIUS = 20;

    private int id;
    protected UButtonType type;
    private UButtonCallbacks mCallbacks;
    private boolean isPressed;
    private String text;
    private int textColor;
    private int pressedColor;

    // Get/Set
    public int getId() {
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

    public UButton(UButtonCallbacks callbacks, int id, String text,
                   float x, float y, int width, int height, int color)
    {
        this(callbacks, UButtonType.BGColor, id, DRAW_PRIORITY, text, x, y, width, height, color);
    }

    public UButton(UButtonCallbacks callbacks, UButtonType type, int id, int priority, String text,
                   float x, float y, int width, int height, int color)
    {
        super(priority, x, y, width, height);
        this.id = id;
        this.mCallbacks = callbacks;
        this.type = type;
        this.color = color;
        this.text = text;
        this.textColor = Color.WHITE;
        if (type == UButtonType.BGColor) {
            this.pressedColor = UColor.addBrightness(color, 0.5f);
        } else {
            this.pressedColor = UColor.addBrightness(color, -0.5f);
        }
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

        paint.setColor(_color);

        PointF _pos = new PointF(pos.x, pos.y);
        if (offset != null) {
            _pos.x += offset.x;
            _pos.y += offset.y;
        }

        int _height = size.height;

        if (type == UButtonType.Press) {
            // 押したら凹むボタン
            if (isPressed) {
                _pos.y += PRESS_Y;
            } else {
                // ボタンの影用に下に矩形を描画
                UDraw.drawRoundRectFill(canvas, paint, _pos.x, _pos.y,
                        _pos.x + size.width, _pos.y + size.height, BUTTON_RADIUS, pressedColor);
            }
            _height -= PRESS_Y;

        } else {
            // 押したら色が変わるボタン
            if (isPressed) {
                _color = pressedColor;
            }
        }
        UDraw.drawRoundRectFill(canvas, paint, _pos.x, _pos.y, _pos.x + size.width, _pos.y + _height, BUTTON_RADIUS, _color);

        // テキスト
        if (text != null) {
            Rect bound = new Rect();
            paint.setTextSize(50);
            paint.setColor(textColor);

            // センタリング
            paint.getTextBounds(text, 0, text.length(), bound);
            Paint.FontMetrics fontMetrics = paint.getFontMetrics();
            float baseY = _pos.y + _height / 2 - (fontMetrics.ascent + fontMetrics
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
        return touchEvent(vt, null);
    }

    public boolean touchEvent(ViewTouch vt, PointF offset) {
        boolean done = false;
        if (offset == null) {
            offset = new PointF();
        }
        if (vt.isTouchUp()) {
            if (isPressed) {
                isPressed = false;
                done = true;
            }
        }

        switch(vt.type) {
            case None:
                break;
            case Touch:
                if (rect.contains((int)vt.touchX(-offset.x), (int)vt.touchY(-offset.y))) {
                    isPressed = true;
                    done = true;
                }
                break;
            case Click:
            case LongClick:
                isPressed = false;
                if (rect.contains((int)vt.touchX(-offset.x), (int)vt.touchY(-offset.y))) {
                    mCallbacks.click(this);
                    done = true;
                }
                break;
            case MoveEnd:

                break;
        }
        return done;
    }
}
