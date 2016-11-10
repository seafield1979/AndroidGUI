package com.sunsunsoft.shutaro.ugui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;

import static com.sunsunsoft.shutaro.ugui.UDebug.drawIconId;

/**
 * ViewのonDrawで描画するアイコンの情報
 */
abstract public class Icon implements AutoMovable, Drawable {

    private static final String TAG = "Icon";
    private static int count;

    public int id;
    protected IconWindow parentWindow;
    private IconCallbacks mCallbacks;
    protected DrawList drawList;

    protected PointF pos = new PointF();
    protected Size size = new Size();
    protected Rect rect;

    // 移動用
    protected boolean isMoving;
    protected int movingFrame;
    protected int movingFrameMax;
    protected PointF srcPos = new PointF();
    protected PointF dstPos = new PointF();

    // アニメーション用
    public static final int ANIME_FRAME = 20;
    protected boolean isAnimating;
    protected int animeFrame;
    protected int animeFrameMax;

    // ドラッグ中のアイコンが上にある状態
    protected boolean isDroping;

    protected IconType type;

    protected int color;

    public Icon(IconWindow parentWindow, IconType type, float x, float y, int width, int
            height)
    {
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


    // 座標、サイズのGet/Set
    public float getX() {
        return pos.x;
    }
    public void setX(float x) {
        pos.x = x;
    }

    public float getY() {
        return pos.y;
    }
    public void setY(float y) {
        pos.y = y;
    }

    public void setPos(float x, float y) {
        pos.x = x;
        pos.y = y;
        updateRect();
    }
    public void setPos(PointF pos) {
        this.pos.x = pos.x;
        this.pos.y = pos.y;
        updateRect();
    }

    protected void updateRect() {
        if (rect == null) {
            rect = new Rect((int)pos.x, (int)pos.y, (int)pos.x + size.width, (int)pos.y + size.height);
        } else {
            rect.left = (int) pos.x;
            rect.right = (int) pos.x + size.width;
            rect.top = (int) pos.y;
            rect.bottom = (int) pos.y + size.height;
        }
    }

    public float getRight() {
        return pos.x + size.width;
    }
    public float getBottom() {
        return pos.y + size.height;
    }

    public int getWidth() {
        return size.width;
    }
    public void setWidth(int w) {
        size.width = w;
    }

    public int getHeight() {
        return size.height;
    }
    public void setHeight(int h) {
        size.height = h;
    }

    public void setSize(int width, int height) {
        size.width = width;
        size.height = height;
        updateRect();
    }
    public Rect getRect() {return rect;}
    public Rect getRectWithOffset(PointF offset) {
        return new Rect(rect.left + (int)offset.x, rect.top + (int)offset.y,
                rect.right + (int)offset.x, rect.bottom + (int)offset.y);
    }

    // 移動
    public void move(float moveX, float moveY) {
        pos.x += moveX;
        pos.y += moveY;
        updateRect();
    }

    // 色
    public int getColor() {
        return color;
    }
    public void setColor(int color) {
        this.color = color;
    }

    public IconWindow getParentWindow() {
        return parentWindow;
    }
    public void setParentWindow(IconWindow parentWindow) {
        this.parentWindow = parentWindow;
    }

    /**
     * 自動移動開始
     * @param dstX  目的位置x
     * @param dstY  目的位置y
     * @param frame  移動にかかるフレーム数
     */
    public void startMove(float dstX, float dstY, int frame) {
        if (pos.x == dstX && pos.y == dstY) {
            return;
        }
        srcPos.x = pos.x;
        srcPos.y = pos.y;
        dstPos.x = dstX;
        dstPos.y = dstY;
        movingFrame = 0;
        movingFrameMax = frame;
        isMoving = true;
    }

    /**
     * 移動
     * 移動開始位置、終了位置、経過フレームから現在位置を計算する
     * @return true:移動中
     */
    public boolean move() {
        if (!isMoving) return false;

        float ratio = (float)movingFrame / (float)movingFrameMax;

        movingFrame++;
        if (movingFrame >= movingFrameMax) {
            isMoving = false;
            setPos(dstPos);
            return false;
        } else {
            setPos(srcPos.x + ((dstPos.x - srcPos.x) * ratio),
                    srcPos.y + ((dstPos.y - srcPos.y) * ratio));
        }
        return true;
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
