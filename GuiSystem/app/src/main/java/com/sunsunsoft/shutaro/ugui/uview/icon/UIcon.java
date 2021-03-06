package com.sunsunsoft.shutaro.ugui.uview.icon;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;

import com.sunsunsoft.shutaro.ugui.ViewTouch;
import com.sunsunsoft.shutaro.ugui.util.UColor;
import com.sunsunsoft.shutaro.ugui.uview.DrawList;
import com.sunsunsoft.shutaro.ugui.uview.DrawPriority;
import com.sunsunsoft.shutaro.ugui.uview.UDraw;
import com.sunsunsoft.shutaro.ugui.uview.UDrawManager;
import com.sunsunsoft.shutaro.ugui.uview.UDrawable;

import static com.sunsunsoft.shutaro.ugui.uview.UDebug.drawIconId;


/**
 * ViewのonDrawで描画するアイコンの情報
 */

abstract public class UIcon extends UDrawable {

    private static final String TAG = "UIcon";
    private static final int DRAW_PRIORITY = 200;
    public static final int DRAG_ICON_PRIORITY = 10;
    private static int count;

    public int id;
    protected UIconWindow parentWindow;
    private UIconCallbacks callbacks;
    protected DrawList drawList;

    // アニメーション用
    public static final int ANIME_FRAME = 20;

    // 各種状態
    protected boolean isChecking;      // 選択可能状態(チェックボックスが表示される)
    protected boolean isChecked;       // 選択中
    protected boolean isDraging;        // ドラッグ中
    protected boolean isDroping;        // ドロップ中(上に他のアイコンがドラッグ)
    protected boolean isTouched;        // タッチ中
    protected boolean isLongTouched;    // 長押し中

    protected int touchedColor;
    protected int longPressedColor;

    protected IconType type;

    /**
     * Get/Set
     */

    private void clearFlags() {
        isTouched = false;
        isLongTouched = false;
        isDraging = false;
    }
    public IconType getType(){ return type; }

    /**
     * Constructor
     */
    public UIcon(UIconWindow parentWindow, UIconCallbacks iconCallbacks, IconType type, float x,
                 float y, int
            width, int
            height)
    {
        super(DRAW_PRIORITY, x, y, width, height);
        this.parentWindow = parentWindow;
        this.callbacks = iconCallbacks;
        this.id = count;
        this.type = type;
        this.setPos(x, y);
        this.setSize(width, height);
        updateRect();
        count++;
    }

    public void setColor(int color) {
        this.color = color;
        this.touchedColor = UColor.addBrightness(color, 0.3f);
        this.longPressedColor = UColor.addBrightness(color, 0.6f);
    }

    public UIconWindow getParentWindow() {
        return parentWindow;
    }
    public void setParentWindow(UIconWindow parentWindow) {
        this.parentWindow = parentWindow;
    }

    public void click() {
        Log.v(TAG, "click");

        startAnim();
        if (isChecking) if (isChecked) {
            isChecked = false;
        } else {
            isChecked = true;
            this.drawPriority = DrawPriority.DragIcon.p();
        }
        else {
            if (callbacks != null) {
                callbacks.clickIcon(this);
            }
        }
    }
    public void longClick() {
        Log.v(TAG, "long click");
        if (callbacks != null) {
            callbacks.longClickIcon(this);
        }
    }
    public void moving() {
        Log.v(TAG, "moving");
    }
    public void drop() {
        Log.v(TAG, "drop");
        if (callbacks != null) {
            callbacks.dropToIcon(this);
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
        if (getRect().contains((int)clickX, (int)clickY))
        {
            click();
            return true;
        }
        return false;
    }

    /**
     * ドロップをチェックする
     */
    public boolean checkDrop(float dropX, float dropY) {
        if (getRect().contains((int)dropX, (int)dropY))
        {
            return true;
        }
        return false;
    }

    /**
     * アイコンを描画
     */
    public void draw(Canvas canvas, Paint paint, PointF offset) {
        drawIcon(canvas, paint, offset);

        if (isChecking) {
            float _x = pos.x + offset.x;
            float _y = pos.y + offset.y;
            int width = 70;
            UDraw.drawCheckbox(canvas, paint, isChecked, _x + 10, _y + size.height - width - 10, width,
                    Color.rgb
                    (100,100,200));
        }
    }

    /**
     * アイコンを描画する
     */
    abstract protected void drawIcon(Canvas canvas, Paint paint, PointF offset);

    /**
     * アイコンにIDを表示する
     * @param canvas
     * @param paint
     */
    protected void drawId(Canvas canvas, Paint paint) {
        // idを表示
        if (drawIconId) {
            paint.setColor(Color.WHITE);
            paint.setTextSize(30);
            canvas.drawText("" + id, pos.x+10, pos.y + size.height - 30, paint);
        }
    }


    /*
        Drawableインターフェース
     */
    public void setDrawList(DrawList drawList) {
        this.drawList = drawList;
    }

    public DrawList getDrawList() {
        return drawList;
    }


    /**
     * 描画オフセットを取得する
     * @return
     */
    public PointF getDrawOffset() {
        // 親Windowの座標とスクロール量を取得
        if (parentWindow != null) {
            return new PointF(parentWindow.getPos().x - parentWindow.getContentTop().x,
                    parentWindow.getPos().y - parentWindow.getContentTop().y);
        }
        return null;
    }

    /**
     * アニメーション開始
     */
    public void startAnim() {
        isAnimating = true;
        animeFrame = 0;
        animeFrameMax = ANIME_FRAME;
        if (parentWindow != null) {
            parentWindow.setAnimating(true);
        }
    }

    /**
     * アニメーション処理
     * といいつつフレームのカウンタを増やしているだけ
     * @return true:アニメーション中
     */
    public boolean animate() {
        if (!isAnimating) return false;
        if (animeFrame >= animeFrameMax) {
            isAnimating = false;
            return false;
        }

        animeFrame++;
        return true;
    }

    /**
     * アニメーション中かどうか
     * @return
     */
    public boolean isAnimating() {
        return isAnimating;
    }

    /**
     * タッチイベント処理
     * 親のUIconWindowで処理するのでここでは何もしない
     * @param vt
     * @return
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
            clearFlags();
        }
        switch (vt.type) {
            case Touch:
                if (getRect().contains((int)vt.touchX(offset.x), (int)vt.touchY(offset.y))) {
                    isTouched = true;
                    done = true;
                }
                break;
            case LongPress:
                if (getRect().contains((int)vt.getX(offset.x), (int)vt.getY(offset.y))) {
                    isLongTouched = true;
                    isChecking = true;
                    isChecked = true;
                    done = true;
                }
                break;
            case Click:
                if (getRect().contains((int)vt.touchX(offset.x), (int)vt.touchY(offset.y))) {
                    click();
                    done = true;
                }
                break;
            case LongClick:
                if (getRect().contains((int)vt.touchX(offset.x), (int)vt.touchY(offset.y))) {
                    longClick();
                    done = true;
                }
                break;
            case Moving:
                if (vt.isMoveStart()) {
                    if (getRect().contains((int)vt.touchX(offset.x), (int)vt.touchY(offset.y))) {
                        isDraging = true;
                        done = true;
                    }
                }
                if (isDraging) {
                    done = true;
                }
              break;
            case MoveEnd:
                isDraging = false;
                break;
            case MoveCancel:
                isDraging = false;
                break;
        }

        return done;
    }
}
