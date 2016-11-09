package com.sunsunsoft.shutaro.ugui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.View;

// メニューバーのトップ項目
enum TopMenu {
    Add,            // 追加
    Sort,           // 並び替え
    ListType        // リストの表示方法
}

/**
 * メニューバー
 * メニューに表示する項目を管理する
 */
public class MenuBar extends Window {
    public static final int MENU_BAR_H = 150;
    private static final int MARGIN_L = 30;
    private static final int MARGIN_LR = 50;
    private static final int MARGIN_TOP = 15;
    public static final int TOP_MENU_MAX = TopMenu.values().length;


    private View mParentView;
    private MenuItemCallbacks mCallbackClass;
    MenuItemTop[] topItems = new MenuItemTop[TOP_MENU_MAX];
    MenuItem[] items = new MenuItem[MenuItemId.values().length];

    // Get/Set
    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    private MenuBar(View parentView, MenuItemCallbacks callbackClass, int parentW, int parentH, int bgColor)
    {
        super(0, parentH - MENU_BAR_H, parentW, MENU_BAR_H, bgColor);
        mParentView = parentView;
        mCallbackClass = callbackClass;
    }

    /**
     * メニューバーを生成する
     * @param parentView
     * @param callbackClass
     * @param parentW     親Viewのwidth
     * @param parentH    親Viewのheight
     * @param bgColor
     * @return
     */
    public static MenuBar createInstance(View parentView, MenuItemCallbacks callbackClass, int parentW, int parentH, int bgColor)
    {
        MenuBar instance = new MenuBar(parentView, callbackClass, parentW, parentH, bgColor);
        instance.initMenuBar();
        return instance;
    }

    /**
     * メニューバーを初期化
     */
    private void initMenuBar() {
        // トップ要素
        addTopMenuItem(TopMenu.Add, MenuItemId.AddTop, R.drawable.hogeman);
        addTopMenuItem(TopMenu.Sort, MenuItemId.SortTop, R.drawable.hogeman);
        addTopMenuItem(TopMenu.ListType, MenuItemId.ListTypeTop, R.drawable.hogeman);

        // 子要素
        // Add
        addChildMenuItem(TopMenu.Add, MenuItemId.AddCard, R.drawable.hogeman);
        addChildMenuItem(TopMenu.Add, MenuItemId.AddBook, R.drawable.hogeman);
        addChildMenuItem(TopMenu.Add, MenuItemId.AddBox, R.drawable.hogeman);
        // Sort
        addChildMenuItem(TopMenu.Sort, MenuItemId.Sort1, R.drawable.hogeman);
        addChildMenuItem(TopMenu.Sort, MenuItemId.Sort2, R.drawable.hogeman);
        addChildMenuItem(TopMenu.Sort, MenuItemId.Sort3, R.drawable.hogeman);
        // ListType
        addChildMenuItem(TopMenu.ListType, MenuItemId.ListType1, R.drawable.hogeman);
        addChildMenuItem(TopMenu.ListType, MenuItemId.ListType2, R.drawable.hogeman);
        addChildMenuItem(TopMenu.ListType, MenuItemId.ListType3, R.drawable.hogeman);

        updateBGSize();
    }

    private void updateBGSize() {
        size.width = MARGIN_L + TOP_MENU_MAX * (MenuItem.ITEM_W + MARGIN_LR);
    }

    /**
     * メニューのトップ項目を追加する
     * @param topId
     * @param menuId
     * @param bmpId
     */
    private void addTopMenuItem(TopMenu topId, MenuItemId menuId, int bmpId) {
        Bitmap bmp = BitmapFactory.decodeResource(mParentView.getResources(), bmpId);
        MenuItemTop item = new MenuItemTop(menuId, bmp);
        item.setCallbacks(mCallbackClass);
        addItem(topId, item);

        items[menuId.ordinal()] = item;
    }

    /**
     * メニューの子要素を追加する
     * @param topId
     * @param menuId
     * @param bmpId
     */
    private void addChildMenuItem(TopMenu topId, MenuItemId menuId, int bmpId) {
        Bitmap bmp = BitmapFactory.decodeResource(mParentView.getResources(), bmpId);
        MenuItemChild item = new MenuItemChild(menuId, bmp);
        item.setCallbacks(mCallbackClass);
        addChildItem(topId, item);

        items[menuId.ordinal()] = item;
    }

    /**
     * 項目を追加する
     * @param index   項目を追加する位置
     * @param item    追加する項目
     */
    public void addItem(TopMenu index, MenuItemTop item) {
        int pos = index.ordinal();
        if (pos >= TOP_MENU_MAX) return;

        // 項目の座標を設定
        // メニューバーの左上原点の相対座標、スクリーン座標は計算で求める
        item.setPos(MARGIN_L + pos * (MenuItem.ITEM_W + MARGIN_LR), MARGIN_TOP);

        topItems[pos] = item;
    }

    /**
     * 子要素を追加する
     * @param index
     * @param item
     */
    public void addChildItem(TopMenu index, MenuItemChild item) {
        MenuItemTop topItem = topItems[index.ordinal()];
        if (topItem == null) return;

        topItem.addItem(item);
    }

    /**
     * 表示する
     * @param canvas
     * @param paint
     */
    public boolean draw(Canvas canvas, Paint paint) {
        if (!isShow) return false;

        // bg
        // 内部を塗りつぶし
        paint.setStyle(Paint.Style.FILL);
        // 色
        paint.setColor(0xff000000);

        canvas.drawRect(pos.x,
                pos.y,
                pos.x + size.width,
                pos.y + size.height,
                paint);


        for (MenuItemTop item : topItems) {
            if (item != null) {
                item.draw(canvas, paint, pos);
            }
        }
        return false;
    }

    /**
     * メニューのアクション
     * メニューアイテムを含めて何かしらの処理を行う
     *
     * @return true:処理中 / false:完了
     */
    public boolean doAction() {
        if (!isShow) return false;

        boolean allFinished = true;
        for (MenuItemTop item : topItems) {
            // 移動
            if (item.moveChilds()) {
                allFinished = false;
            }

            // アニメーション
            if (item.animate()) {
                allFinished = false;
            }
        }

        return !allFinished;
    }

    /**
     * タッチ処理を行う
     * 現状はクリック以外は受け付けない
     * メニューバー以下の項目(メニューの子要素も含めて全て)のクリック判定
     */
    public boolean touchEvent(ViewTouch vt) {
        if (!isShow) return false;

        boolean done = false;
        float clickX = vt.touchX() - pos.x;
        float clickY = vt.touchY() - pos.y;

        // 渡されるクリック座標をメニューバーの座標系に変換
        for (int i = 0; i < topItems.length; i++) {
            MenuItemTop item = topItems[i];
            if (item == null) continue;

            if (item.checkClick(vt, clickX, clickY)) {
                done = true;
                if (item.isOpened()) {
                    // 他に開かれたメニューを閉じる
                    closeAllMenu(i);
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
                return true;
            }
        }
        return done;
    }


    /**
     * メニューを閉じる
     * @param excludedIndex
     */
    private void closeAllMenu(int excludedIndex) {
        for (int i = 0; i < topItems.length; i++) {
            if (i == excludedIndex) continue;
            MenuItemTop item = topItems[i];
            item.closeMenu();
        }
    }

    /**
     * メニュー項目の座標をスクリーン座標で取得する
     */
    public PointF getItemPos(MenuItemId itemId) {
        MenuItem item = items[itemId.ordinal()];
        if (item == null) {
            return new PointF();
        }
        PointF itemPos = item.getPos();
        return new PointF(toScreenX(itemPos.x), toScreenY(itemPos.y));
    }
}
