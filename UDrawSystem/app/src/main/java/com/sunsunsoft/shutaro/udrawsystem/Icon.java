package com.sunsunsoft.shutaro.udrawsystem;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

enum IconType {
    Rect,
    Circle,
    Triangle
}
/**
 * アイコンのベースクラス
 */
abstract public class Icon implements Drawable {
    public static final String TAG = "Icon";

    protected IconType type;
    protected PointF pos = new PointF();
    protected Size size = new Size();
    protected Rect rect;
    protected int color;
    protected DrawList drawList;

    // アニメーション用
    public static final int ANIME_FRAME = 20;
    protected boolean isAnimating;
    protected int animeFrame;
    protected int animeFrameMax;

    // Get/Set
    public PointF getPos() {
        return pos;
    }

    public void setPos(float x, float y) {
        this.pos.x = x;
        this.pos.y = y;
    }

    public float getX() {
        return pos.x;
    }
    public float getY() {
        return pos.y;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(int width, int height) {
        this.size.width = width;
        this.size.height = height;
    }

    public void setDrawList(DrawList drawList) {
        this.drawList = drawList;
    }

    public DrawList getDrawList() {
        return drawList;
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


    /**
     * アニメーション開始
     */
    public void startAnim() {
        isAnimating = true;
        animeFrame = 0;
        animeFrameMax = ANIME_FRAME;
    }

    /**
     * アニメーション処理
     *
     * @return
     */
    public boolean animate() {
        if (!isAnimating) return false;

        MyLog.print(TAG, "animate");

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
     * 指定の座標がアイコンの中に含まれるかをチェック
     * @param x
     * @param y
     * @return true: contain / false:not contain
     */
    abstract boolean contains(int x, int y);

    /**
     * アイコンにIDを表示する
     * @param canvas
     * @param paint
     */
    protected void drawId(Canvas canvas, Paint paint) {
        // idを表示
        if (!MyDebug.drawIconId) return;

        DrawList drawList = getDrawList();
        if (drawList == null) return;
        paint.setColor(Color.WHITE);
        paint.setTextSize(30);
        canvas.drawText("" + drawList.getPriority(), pos.x+10, pos.y + size.height - 30, paint);

    }
}
