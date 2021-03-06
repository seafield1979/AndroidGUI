package com.sunsunsoft.shutaro.ugui.uview;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.View;

import com.sunsunsoft.shutaro.ugui.ViewTouch;
import com.sunsunsoft.shutaro.ugui.uview.window.UWindow;

import java.util.LinkedList;


/**
 * メニューバー
 * メニューに表示する項目を管理する
 */
abstract public class UMenuBar extends UWindow {

    public static final int DRAW_PRIORITY = 90;
    public static final int MENU_BAR_H = 180;
    protected static final int MARGIN_L = 30;
    protected static final int MARGIN_H = 50;
    protected static final int MARGIN_TOP = 15;


    protected UMenuItemCallbacks mMenuItemCallbacks;
    protected LinkedList<UMenuItem> topItems;
    protected LinkedList<UMenuItem> items;
    protected DrawList mDrawList;
    protected boolean isAnimating;

    /**
     * Get/Set
     */
    public void setAnimating(boolean animating) {
        isAnimating = animating;
    }

    /**
     * Constructor
     */
    public UMenuBar(UMenuItemCallbacks callbackClass,
                    int parentW, int parentH,
                    int bgColor)
    {
        super(null, DRAW_PRIORITY, 0, parentH - MENU_BAR_H, parentW, MENU_BAR_H, bgColor);
        mMenuItemCallbacks = callbackClass;
        topItems = new LinkedList<>();
        items = new LinkedList<>();
    }

    /**
     * Methods
     */
    /**
     * メニューバーを初期化
     */
    abstract protected void initMenuBar();

    protected void updateBGSize() {
        size.width = MARGIN_L + topItems.size() * (UMenuItem.ITEM_W + MARGIN_H);
    }

    /**
     * メニューのトップ項目を追加する
     * @param menuId
     * @param image
     */
    protected UMenuItem addTopMenuItem(int menuId, Bitmap image) {
        UMenuItem item = new UMenuItem(this, menuId, true, image);
        item.setCallbacks(mMenuItemCallbacks);
        item.setShow(true);

        topItems.add(item);
        items.add(item);

        // 座標設定
        item.setPos(MARGIN_H + (UMenuItem.TOP_ITEM_W + MARGIN_H) * (topItems.size() - 1), MARGIN_TOP);
        return item;
    }

    /**
     * 子メニューを追加する
     * @param parent
     * @param menuId
     * @param image
     * @return
     */
    protected UMenuItem addMenuItem(UMenuItem parent, int menuId, Bitmap image) {
        UMenuItem item = new UMenuItem(this, menuId, false, image);
        item.setCallbacks(mMenuItemCallbacks);
        item.setmParentItem(parent);
        // 子要素は初期状態では非表示。オープン時に表示される
        item.setShow(false);

        parent.addItem(item);

        items.add(item);
        return item;
    }

    /**
     * メニューのアクション
     * メニューアイテムを含めて何かしらの処理を行う
     *
     * @return true:処理中 / false:完了
     */
    @Override
    public DoActionRet doAction() {
        if (!isShow) return DoActionRet.None;

        DoActionRet ret = DoActionRet.None;
        for (UMenuItem item : topItems) {
            DoActionRet _ret = item.doAction();
            switch(_ret) {
                case Done:
                    return _ret;
                case Redraw:
                    ret = _ret;
                    break;

            }
        }

        return ret;
    }

    /**
     * タッチ処理を行う
     * 現状はクリック以外は受け付けない
     * メニューバー以下の項目(メニューの子要素も含めて全て)のクリック判定
     */
    public boolean touchEvent(ViewTouch vt, PointF offset) {
        if (!isShow) return false;

        boolean done = false;
        float clickX = vt.touchX() - pos.x;
        float clickY = vt.touchY() - pos.y;

        // 渡されるクリック座標をメニューバーの座標系に変換
        for (UMenuItem item : topItems) {
            if (item == null) continue;

            if (item.checkTouch(vt, clickX, clickY)) {
                done = true;
                // クリック時に後ろのアイテムに反応するのを防ぐ
                vt.setTouching(false);

                if (item.isOpened()) {
                    // 他に開かれたメニューを閉じる
                    closeAllMenu(item);
                }
                break;
            }
            if (done) break;
        }

        // メニューバーの領域をクリックしていたら、メニュー以外がクリックされるのを防ぐためにtrueを返す
        if (!done) {
            if (0 <= clickX && clickX <= size.width &&
                    0 <= clickY && clickY <= size.height)
            {
                // クリック時に後ろのアイテムに反応するのを防ぐ
                vt.setTouching(false);
                return true;
            }
        }
        return done;
    }


    /**
     * メニューを閉じる
     * @param excludedItem
     */
    protected void closeAllMenu(UMenuItem excludedItem) {
        for (UMenuItem item : topItems) {
            if (item == excludedItem) continue;
            item.closeMenu();
        }
    }

    /**
     * メニュー項目の座標をスクリーン座標で取得する
     */
    public PointF getItemPos(int itemId) {
        UMenuItem item = items.get(itemId);
        if (item == null) {
            return new PointF();
        }
        PointF itemPos = item.getPos();
        return new PointF(toScreenX(itemPos.x), toScreenY(itemPos.y));
    }

    /*
        Drawableインターフェースメソッド
     */
    /**
     * 描画処理
     * @param canvas
     * @param paint
     */
    public void drawContent(Canvas canvas, Paint paint, PointF offset ) {
        if (!isShow) return;

        // 背景描画
//        UDraw.drawRoundRectFill(canvas, paint, new RectF(pos.x, pos.y, pos.x + getWidth() + 30,
//                pos.y + getHeight()),
//                30, UColor.LightGreen, 0, 0);

        // トップのアイテムから描画
        for (UMenuItem item : topItems) {
            if (item != null && item.isShow()) {
                item.draw(canvas, paint, pos);
            }
        }
        return;
    }

    /**
     * アニメーション処理
     * onDrawからの描画処理で呼ばれる
     * @return true:アニメーション中
     */
    public boolean animate() {
        if (!isAnimating) return false;
        boolean allFinished = true;

        for (UMenuItem item : topItems) {
            if (item.animate()) {
                allFinished = false;
            }
        }
        if (allFinished) {
            isAnimating = false;
        }
        return !allFinished;
    }

    /**
     * 描画オフセットを取得する
     * @return
     */
    public PointF getDrawOffset() {
        return null;
    }

}
