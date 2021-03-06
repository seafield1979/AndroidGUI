package com.sunsunsoft.shutaro.ugui.uview.button;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;

import com.sunsunsoft.shutaro.ugui.ViewTouch;
import com.sunsunsoft.shutaro.ugui.util.UColor;
import com.sunsunsoft.shutaro.ugui.uview.UDrawable;


/**
 * クリックでイベントが発生するボタン
 *
 * 生成後ViewのonDraw内で draw メソッドを呼ぶと表示される
 * ボタンが押されたときの動作はtypeで指定できる
 *   BGColor ボタンの背景色が変わる
 *   Press   ボタンがへこむ
 *   Press2  ボタンがへこむ。へこんだ状態が維持される
 */

abstract public class UButton extends UDrawable {
    /**
     * Consts
     */
    public static final String TAG = "UButton";
    public static final int PRESS_Y = 16;
    public static final int BUTTON_RADIUS = 16;
    public static final int DISABLED_COLOR = Color.rgb(160,160,160);

    /**
     * Member Variables
     */
    protected int id;
    protected UButtonType type;
    protected UButtonCallbacks buttonCallback;
    protected boolean enabled;          // falseならdisableでボタンが押せなくなる
    protected boolean isPressed;
    protected int pressedColor;
    protected int disabledColor;        // enabled == false のときの色
    protected int disabledColor2;       // eanbled == false のときの濃い色
    protected boolean pressedOn;        // Press2タイプの時のOn状態

    /**
     * Get/Set
     */
    public int getId() {
        return id;
    }

    public boolean isPressedOn() {
        return pressedOn;
    }

    public void setPressedOn(boolean pressedOn) {
        this.pressedOn = pressedOn;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Constructor
     */
    public UButton(UButtonCallbacks callbacks, UButtonType type, int id, int priority,
                   float x, float y, int width, int height, int color)
    {
        super(priority, x, y, width, height);
        this.id = id;
        this.enabled = true;
        this.buttonCallback = callbacks;
        this.type = type;
        this.color = color;
        if (type == UButtonType.BGColor) {
            this.pressedColor = UColor.addBrightness(color, 0.3f);
        } else {
            this.pressedColor = UColor.addBrightness(color, -0.2f);
        }
        disabledColor = DISABLED_COLOR;
        disabledColor2 = UColor.addBrightness(disabledColor, -0.2f);
    }

    /**
     * Methods
     */
    /**
     * 描画オフセットを取得する
     * @return
     */
    public PointF getDrawOffset() {
        // 親Windowの座標とスクロール量を取得
        return null;
    }

    /**
     * 描画処理
     * @param canvas
     * @param paint
     * @param offset 独自の座標系を持つオブジェクトをスクリーン座標系に変換するためのオフセット値
     */
    abstract public void draw(Canvas canvas, Paint paint, PointF offset);

    /**
     * UDrawable Interface
     */
    /**
     * タッチアップイベント
     */
    public boolean touchUpEvent(ViewTouch vt) {
        boolean done = false;

        if (vt.isTouchUp()) {
            if (isPressed) {
                isPressed = false;
                done = true;
            }
        }
        return done;
    }

    /**
     * タッチイベント
     * @param vt
     * @return true:イベントを処理した(再描画が必要)
     */
    public boolean touchEvent(ViewTouch vt) {
        return touchEvent(vt, null);
    }

    public boolean touchEvent(ViewTouch vt, PointF offset) {
        if (!enabled) return false;

        boolean done = false;
        if (offset == null) {
            offset = new PointF();
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
                    if (type == UButtonType.Press3) {
                        // Off -> On に切り替わる一回目だけイベント発生
                        if (pressedOn == false) {
                            click();
                            pressedOn = true;
                        }
                    } else {
                        click();
                        done = true;
                        if (type == UButtonType.Press2) {
                            pressedOn = !pressedOn;
                        }
                    }
                }
                break;
            case MoveEnd:

                break;
        }
        return done;
    }

    public void click() {
        Log.v(TAG, "click");
        if (buttonCallback != null) {
            buttonCallback.UButtonClicked(id, pressedOn);
        }
    }
}
