package com.sunsunsoft.shutaro.udrawsystem;

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
    protected IconType type;
    protected PointF pos = new PointF();
    protected Size size = new Size();
    protected Rect rect;
    protected int color;


    // Get/Set
    public PointF getPos() {
        return pos;
    }

    public void setPos(float x, float y) {
        this.pos.x = x;
        this.pos.y = y;
    }

//    public void setPos(float x, float y, boolean update) {
//        this.pos.x = x;
//        this.pos.y = y;
//        if (update) {
//            updateRect();
//        }
//    }

    public Size getSize() {
        return size;
    }

    public void setSize(int width, int height) {
        this.size.width = width;
        this.size.height = height;
    }

//    public void setSize(int width, int height, boolean update) {
//        this.size.width = width;
//        this.size.height = height;
//        if (update) {
//            updateRect();
//        }
//    }

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
     * 指定の座標がアイコンの中に含まれるかをチェック
     * @param x
     * @param y
     * @return true: contain / false:not contain
     */
    abstract boolean contains(int x, int y);
}
