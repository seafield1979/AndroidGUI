package com.sunsunsoft.shutaro.ugui;

import android.graphics.Bitmap;
import android.graphics.PointF;

/**
 * メニューバーの子要素
 * メニューバーのトップ要素をクリックすると表示される
 */

public class UMenuItemChild extends UMenuItem implements AutoMovable{
    private static final int MOVING_FRAME = 10;

    // ベースの座標、移動アニメーション時にはこの座標に向かって移動する
    private PointF basePos = new PointF();

    // 親項目の座標。メニューが閉じるときはこの座標に向かって移動する
    private PointF parentPos;

    // Get/Set
    public PointF getBasePos() {
        return basePos;
    }

    public void setBasePos(PointF basePos) {
        this.basePos = basePos;
    }
    public void setBasePos(float x, float y) {
        basePos.x = x;
        basePos.y = y;
    }
    public void setParentPos(PointF parentPos) {
        this.parentPos = parentPos;
        setPos(parentPos.x, parentPos.y);        // 初期座標は親アイコンの裏
    }

    public boolean isMoving() {
        return isMoving;
    }

    public UMenuItemChild(UMenuBar parent, MenuItemId id, Bitmap icon) {
        super(parent, id, icon);
    }

    public boolean checkClick(ViewTouch vt, float clickX, float clickY) {
        if (pos.x <= clickX && clickX <= pos.x + ITEM_W &&
                pos.y <= clickY && clickY <= pos.y + ITEM_H)
        {
            if (vt.type == TouchType.Click) {
                ULog.print("UMenuItem", "clicked");
                // タッチされた時の処理
                if (mCallbacks != null) {
                    mCallbacks.callback1(id);
                }
                // アニメーション
                startAnim();
            }

            return true;
        }
        return false;
    }

    /**
     * メニューを開いた時の処理
     */
    public void openMenu() {
        startMove(basePos.x, basePos.y, MOVING_FRAME);
    }

    /**
     * メニューを閉じた時の処理
     */
    public void closeMenu() {
        startMove(parentPos.x, parentPos.y, MOVING_FRAME);
    }

    public PointF getDrawOffset() {
        return null;
    }
}
