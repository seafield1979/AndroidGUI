package com.sunsunsoft.shutaro.ugui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;

import static com.sunsunsoft.shutaro.ugui.UDebug.drawIconId;

/**
 * アイコンの形
 */

enum IconType {
    RECT,
    CIRCLE,
    IMAGE,
    BOX
}

/**
 * ViewのonDrawで描画するアイコンの情報
 */

abstract public class UIcon extends Drawable implements AutoMovable {

    private static final String TAG = "UIcon";
    private static int count;

    public int id;
    protected UIconWindow parentWindow;
    private UIconCallbacks mCallbacks;
    protected DrawList drawList;

    // アニメーション用
    public static final int ANIME_FRAME = 20;

    // ドラッグ中のアイコンが上にある状態
    protected boolean isDroping;

    protected IconType type;

    public UIcon(UIconWindow parentWindow, IconType type, float x, float y, int width, int
            height)
    {
        super(x, y, width, height);
        this.parentWindow = parentWindow;
        this.mCallbacks = parentWindow.getIconCallbacks();
        this.id = count;
        this.type = type;
        this.setPos(x, y);
        this.setSize(width, height);
        updateRect();
        this.color = Color.rgb(0,0,0);
        count++;
    }

    public IconType getType() { return type; }


    public UIconWindow getParentWindow() {
        return parentWindow;
    }
    public void setParentWindow(UIconWindow parentWindow) {
        this.parentWindow = parentWindow;
    }

    public void click() {
        Log.v(TAG, "click");
        startAnim();
        if (mCallbacks != null) {
            mCallbacks.clickIcon(this);
        }
    }
    public void longClick() {
        Log.v(TAG, "long click");
        if (mCallbacks != null) {
            mCallbacks.longClickIcon(this);
        }
    }
    public void moving() {
        Log.v(TAG, "moving");
    }
    public void drop() {
        Log.v(TAG, "drop");
        if (mCallbacks != null) {
            mCallbacks.dropToIcon(this);
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

    /**
     * ドロップをチェックする
     */
    public boolean checkDrop(float dropX, float dropY) {
        if (pos.x <= dropX && dropX <= getRight() &&
                pos.y <= dropY && dropY <= getBottom() )
        {
            return true;
        }
        return false;
    }

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
            return new PointF(parentWindow.pos.x - parentWindow.contentTop.x,
                    parentWindow.pos.y - parentWindow.contentTop.y);
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
}
