package com.sunsunsoft.shutaro.ugui;

import android.graphics.Bitmap;
import android.graphics.PointF;

/**
 * メニューバーの子要素
 * メニューバーのトップ要素をクリックすると表示される
 */

public class MenuItemChild extends MenuItem implements AutoMovable{
    private static final int MOVING_FRAME = 10;

    // ベースの座標、移動アニメーション時にはこの座標に向かって移動する
    private PointF basePos = new PointF();

    // 親項目の座標。メニューが閉じるときはこの座標に向かって移動する
    private PointF parentPos;

    // 移動用
    private boolean isMoving;
    private int movingFrame;
    private int movingFrameMax;
    private float srcX, srcY;
    private float dstX, dstY;


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

    public MenuItemChild(MenuBar parent, MenuItemId id, Bitmap icon) {
        super(parent, id, icon);
    }

    public boolean checkClick(ViewTouch vt, float clickX, float clickY) {
        if (pos.x <= clickX && clickX <= pos.x + ITEM_W &&
                pos.y <= clickY && clickY <= pos.y + ITEM_H)
        {
            if (vt.type == TouchType.Click) {
                MyLog.print("MenuItem", "clicked");
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
        srcX = pos.x;
        srcY = pos.y;
        this.dstX = dstX;
        this.dstY = dstY;
        movingFrame = 0;
        movingFrameMax = frame;
        isMoving = true;
    }

    /**
     * 移動
     * 移動開始位置、終了位置、経過フレームから現在位置を計算する
     * @return 移動完了したらtrue
     */
    public boolean move() {
        if (!isMoving) return true;

        float ratio = (float)movingFrame / (float)movingFrameMax;
        pos.x = srcX + ((dstX - srcX) * ratio);
        pos.y = srcY + ((dstY - srcY) * ratio);

        movingFrame++;
        if (movingFrame >= movingFrameMax) {
            isMoving = false;
            pos.x = dstX;
            pos.y = dstY;
            return true;
        }
        return false;
    }
}
