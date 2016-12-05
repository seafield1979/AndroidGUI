package com.sunsunsoft.shutaro.ugui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.view.View;

/**
 * Created by shutaro on 2016/12/05.
 */

public class PageViewIconWindow extends UPageView implements UMenuItemCallbacks, UIconCallbacks, UWindowCallbacks {

    /**
     * Enums
     */
    enum WindowType {
        Icon1,
        Icon2,
        MenuBar,
        Log
    }

    /**
     * Consts
     */
    public static final String TAG = "PageViewIconWindow";

    /**
     * Member Variables
     */

    // Windows
    private UWindow[] mWindows = new UWindow[WindowType.values().length];
    // UIconWindow
    private UIconWindows mIconWindows;

    // MessageWindow
    private ULogWindow mLogWin;

    // メニューバー
    private UMenuBar mMenuBar;

    private Paint paint = new Paint();

    /**
     * Get/Set
     */

    /**
     * Constructor
     */


    public PageViewIconWindow(Context context, View parentView) {
        super(context, parentView, PageView.IconWindow.getDrawId());
    }

    public void initDrawables() {
        int width = mParentView.getWidth();
        int height = mParentView.getHeight();

        // 描画オブジェクトクリア
        UDrawManager.getInstance().initPage(drawPageId);

        // UIconWindow
        UIconWindow.WindowDir winDir;

        if (width <= height) {
            winDir = UIconWindow.WindowDir.Vertical;
        } else {
            winDir = UIconWindow.WindowDir.Horizontal;
        }

        // Main
        UIconWindow mainWindow = UIconWindow.createInstance(mParentView, this, this, true, winDir,
                width, height, Color.WHITE);
        mWindows[WindowType.Icon1.ordinal()] = mainWindow;

        // Sub
        UIconWindow subWindow = UIconWindow.createInstance(mParentView, this, this, false, winDir,
                width, height, Color.LTGRAY);
        subWindow.isShow = false;
        mWindows[WindowType.Icon2.ordinal()] = subWindow;

        mIconWindows = UIconWindows.createInstance(mainWindow, subWindow, width, height);
        mainWindow.setWindows(mIconWindows);
        subWindow.setWindows(mIconWindows);
        mIconWindows.updateLayout(false);

        // アイコンの登録はMainとSubのWindowを作成後に行う必要がある
        mainWindow.init();
        subWindow.init();

        // UMenuBar
        if (mMenuBar == null) {
            mMenuBar = UMenuBar.createInstance(mParentView, this, width, height,
                    Color.BLACK);
            mWindows[WindowType.MenuBar.ordinal()] = mMenuBar;
        }

        // ULogWindow
        if (mLogWin == null) {
            mLogWin = ULogWindow.createInstance(mContext, mParentView, LogWindowType.AutoDisappear,
                    0, 0, width / 2, height);
            mWindows[WindowType.Log.ordinal()] = mLogWin;
        }
    }

    /**
     * スタックの先頭になって表示され始める前に呼ばれる
     */
    public void onShow() {
        super.onShow();
    }

    /**
     * スタックの先頭でなくなって表示されなくなる前に呼ばれる
     */
    public void onHide() {
    }

    /**
     * 描画処理
     * @param canvas
     * @param paint
     * @return true:再描画が必要
     */
    public boolean draw(Canvas canvas, Paint paint) {
        super.draw(canvas, paint);

        // Windowの処理
        // アクション(手前から順に処理する)
        for (int i=mWindows.length - 1; i >= 0; i--) {
            UWindow win = mWindows[i];
            if (win == null) continue;
            if (win.doAction()) {
                return true;
            }
        }
        return false;
    }

    public boolean touchEvent(ViewTouch vt) {
        // 手前から順に処理する
        for (int i=mWindows.length - 1; i >= 0; i--) {
            UWindow win = mWindows[i];
            if (!win.isShow()) continue;

            if (win.touchEvent(vt)) {
                return true;
            }
        }
        return false;
    }

    /**
     * メニューアイテムをタップしてアイコンを追加する
     * @param window
     * @param shape
     * @param menuItemId
     */
    private void addIcon(UIconWindow window, IconType shape, MenuItemId menuItemId) {
        UIconManager manager = window.getIconManager();
        UIcon icon = manager.addIcon(shape, AddPos.Top);

        // アイコンの初期座標は追加メニューアイコンの位置
        PointF menuPos = mMenuBar.getItemPos(menuItemId);
        icon.setPos(window.toWinX(menuPos.x), window.toWinY(menuPos.y));
        window.sortIcons(true);
    }

    /**
     * Androidのバックキーが押された時の処理
     * @return
     */
    public boolean onBackKeyDown() {
        // サブウィンドウが表示されていたら閉じる
        UIconWindow subWindow = mIconWindows.getSubWindow();
        if (subWindow.isShow()) {
            if (mIconWindows.hideWindow(subWindow, true)) {
                return true;
            }
        }
        return false;
    }

    /**
     * UMenuItemCallbacks
     */
    /**
     * メニューアイテムをタップした時のコールバック
     */
    public void menuItemClicked(MenuItemId id, int stateId) {
        switch (id) {
            case AddTop:
                break;
            case AddCard:
                addIcon(mIconWindows.getMainWindow(), IconType.CIRCLE, id);
                break;
            case AddBook:
                addIcon(mIconWindows.getMainWindow(), IconType.RECT, id);
                break;
            case AddBox:
                break;
            case SortTop:
                break;
            case Sort1:
                break;
            case Sort2:
                break;
            case Sort3:
                break;
            case ListTypeTop:
                break;
            case ListType1:
                break;
            case ListType2:
                break;
            case ListType3:
                break;
            case Debug1:
                // ログウィンドウの表示切り替え
                mLogWin.toggle();
                mParentView.invalidate();
                break;
            case Debug2:
                mLogWin.addLog("hoge", UColor.getRandomColor());
                break;
            case Debug3:
                break;
        }
        ULog.print(TAG, "menu item clicked " + id);
    }

    public void menuItemCallback2() {
        ULog.print(TAG, "menu item moved");
    }

    /**
     * Methods
     */

    /**
     * Callbacks
     */
    /**
     * UIconCallbacks
     */
    public void clickIcon(UIcon icon) {
        ULog.print(TAG, "clickIcon");
        switch(icon.type) {
            case CIRCLE:
                break;
            case RECT:
                break;
            case BOX: {
                // 配下のアイコンをSubWindowに表示する
                if (icon instanceof UIconBox) {
                    UIconWindow window = mIconWindows.getSubWindow();
                    UIconBox box = (UIconBox)icon;
                    window.setIconManager(box.getIconManager());
                    window.sortIcons(false);

                    // SubWindowを画面外から移動させる
                    mIconWindows.showWindow(window, true);
                    mParentView.invalidate();
                }
            }
            break;
            case IMAGE:
                break;
        }
    }
    public void longClickIcon(UIcon icon) {
        ULog.print(TAG, "longClickIcon");
    }
    public void dropToIcon(UIcon icon) {
        ULog.print(TAG, "dropToIcon");
    }

    /**
     * UWindowCallbacks
     */
    public void windowClose(UWindow window) {
        // Windowを閉じる
        for (UIconWindow _window : mIconWindows.getWindows()) {
            if (window == _window) {
                mIconWindows.hideWindow(_window, true);
                break;
            }
        }
    }

}
