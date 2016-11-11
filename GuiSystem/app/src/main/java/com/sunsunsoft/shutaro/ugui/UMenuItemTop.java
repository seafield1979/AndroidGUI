package com.sunsunsoft.shutaro.ugui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import java.util.LinkedList;

/**
 * メニューバーのトップ項目
 * アイコンをクリックすると子要素があったら子要素をOpen/Closeする
 *
 */
public class UMenuItemTop extends UMenuItem {

    private static final int CHILD_MARGIN_V = 30;

    // Member variables
    private LinkedList<UMenuItemChild> childItems;
    private boolean isOpened;


    // Constructor
    public UMenuItemTop(UMenuBar parent, MenuItemId id, Bitmap icon) {
        super(parent, id, icon);
    }

    // Get/Set
    public LinkedList<UMenuItemChild> getChildItems() {
        return childItems;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void setOpened(boolean opened) {
        isOpened = opened;
    }


    /**
     * 子要素を追加する
     * @param child
     */
    public void addItem(UMenuItemChild child) {
        if (childItems == null) {
            childItems = new LinkedList<>();
        }

        // 座標を設定する
        child.setBasePos( pos.x, pos.y - ((childItems.size() + 1) * (UMenuItem.ITEM_H + CHILD_MARGIN_V)));
        child.setParentPos( pos );

        childItems.add(child);
    }

    /**
     * 描画処理
     * @param canvas
     * @param paint
     * @param parentPos
     */
    public void draw(Canvas canvas, Paint paint, PointF parentPos) {
        // 子要素をまとめて描画
        if (childItems != null) {
            for (int i=0; i<childItems.size(); i++) {
                UMenuItemChild child = childItems.get(i);
                if (isOpened || child.isMoving()) {
                    child.draw(canvas, paint, parentPos);
                }
            }
        }

        super.draw(canvas, paint, parentPos);
    }

    /**
     * クリック処理
     * @param clickX
     * @param clickY
     * @return
     */
    public boolean checkClick(ViewTouch vt, float clickX, float clickY) {
        if (pos.x <= clickX && clickX <= pos.x + ITEM_W &&
                pos.y <= clickY && clickY <= pos.y + ITEM_H)
        {
            ULog.print("UMenuItem", "clicked");
            if (vt.type == TouchType.Click) {
                // 子要素を持っていたら Open/Close
                if (childItems != null) {
                    if (isOpened) {
                        isOpened = false;
                        closeMenu();
                    } else {
                        isOpened = true;
                        openMenu();
                    }
                    ULog.print("UMenuItem", "isOpened " + isOpened);
                }

                // タッチされた時の処理
                if (mCallbacks != null) {
                    mCallbacks.callback1(id);
                }
                // アニメーション
                startAnim();
            }

            return true;
        }

        // 子要素
        if (isOpened()) {
            if (childItems != null) {
                for (UMenuItemChild child : childItems) {
                    if (child.checkClick(vt, clickX, clickY)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public PointF getDrawOffset() {
        return null;
    }

    /**
     * メニューをOpenしたときの処理
     */
    public void openMenu() {
        if (childItems == null) return;

        isOpened = true;
        for (UMenuItemChild item : childItems) {
            item.openMenu();
        }
    }

    /**
     * メニューをCloseしたときの処理
     */
    public void closeMenu() {
        if (childItems == null) return;

        isOpened = false;
        for (UMenuItemChild item : childItems) {
            item.closeMenu();
        }
    }

    /**
     * メニューのOpen/Close時の子要素の移動処理
     * @return  true:移動中 / false:移動完了
     */
    public boolean moveChilds() {
        if (childItems == null) return false;

        // 移動中のものが１つでもあったら false になる
        boolean allFinished = true;

        for (UMenuItemChild item : childItems) {
            if (item.move()) {
                allFinished = false;
            }
        }

        return !allFinished;
    }

    /**
     * 自分と子アイテムのアニメーション処理を行う
     * @return true:アニメーション中
     */
    @Override
    public boolean animate() {
        boolean allFinished = true;
        if (super.animate()) {
            allFinished = false;
        }

        // 子要素
        if (childItems != null) {
            for (UMenuItemChild item : childItems) {
                if (item.animate()) {
                    allFinished = false;
                }
            }
        }
        return !allFinished;
    }
}
