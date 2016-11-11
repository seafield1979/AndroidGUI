package com.sunsunsoft.shutaro.ugui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * メニューバー、サブViewのサンプル

 */
public class TopView extends View implements OnTouchListener, UMenuItemCallbacks, UIconCallbacks {
    enum WindowType {
        Icon1,
        Icon2,
        MenuBar,
        Log
    }

    public static final String TAG = "TopView";

    // Windows
    private UWindow[] mWindows = new UWindow[WindowType.values().length];
    // UIconWindow
    private UIconWindow[] mIconWindows = new UIconWindow[2];

    // MessageWindow
    private ULogWindow mLogWin;

    // メニューバー
    private UMenuBar mMenuBar;

    // サイズ更新用
    private boolean isFirst = true;

    // クリック判定の仕組み
    private ViewTouch viewTouch = new ViewTouch();

    private Paint paint = new Paint();

    // get/set
    public TopView(Context context) {
        this(context, null);
    }

    public TopView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnTouchListener(this);
    }

    private void initWindows(int width, int height) {
        ULog.print(TAG, "w:" + width + " h:" + height);

        // UIconWindow
        PointF pos1, pos2;
        Size size1, size2;
        if (width <= height) {
            pos1 = new PointF(0, 0);
            size1 = new Size(width, height/2);
            pos2 = new PointF(0, height/2);
            size2 = new Size(width, height/2);
        } else {
            pos1 = new PointF(0, 0);
            size1 = new Size(width / 2, height);
            pos2 = new PointF(width / 2, 0);
            size2 = new Size(width / 2, height);
        }

        if (mIconWindows[0] == null) {
            mIconWindows[0] = UIconWindow.createInstance(this, this, true, pos1.x, pos1.y, size1.width, size1.height, Color.WHITE);
            mIconWindows[0].setWindows(mIconWindows);
            mWindows[WindowType.Icon1.ordinal()] = mIconWindows[0];
        }

        if (mIconWindows[1] == null) {
            mIconWindows[1] = UIconWindow.createInstance(this, this, false, pos2.x, pos2.y, size2.width, size2.height, Color.LTGRAY);
            mIconWindows[1].setWindows(mIconWindows);
            mWindows[WindowType.Icon2.ordinal()] = mIconWindows[1];
        }
        // アイコンの登録はMainとSubのWindowを作成後に行う必要がある
        mIconWindows[0].init();
        mIconWindows[1].init();

        // UMenuBar
        if (mMenuBar == null) {
            mMenuBar = UMenuBar.createInstance(this, this, width, height,
                    Color.BLACK);
            mWindows[WindowType.MenuBar.ordinal()] = mMenuBar;
        }

        // ULogWindow
        if (mLogWin == null) {
            mLogWin = ULogWindow.createInstance(getContext(), this,
                    0, 0, width / 2, height);
            mWindows[WindowType.Log.ordinal()] = mLogWin;
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (isFirst) {
            isFirst = false;
            initWindows(getWidth(), getHeight());
        }

        // 背景塗りつぶし
        canvas.drawColor(Color.WHITE);

        // アンチエリアシング(境界のぼかし)
        paint.setAntiAlias(true);

        // アイコンWindow
        // アクション(手前から順に処理する)
        for (int i=mWindows.length - 1; i >= 0; i--) {
            UWindow win = mWindows[i];
            if (win.doAction()) {
                invalidate();
            }
        }

        // マネージャに登録した描画オブジェクトをまとめて描画
        if (DrawManager.getInstance().draw(canvas, paint)){
            invalidate();
        }
    }

    /**
     * タッチイベント処理
     * @param v
     * @param e
     * @return
     */
    public boolean onTouch(View v, MotionEvent e) {
        boolean ret = true;

        viewTouch.checkTouchType(e);
        if (WindoTouchEvent(viewTouch)) {
            invalidate();
        }

        switch(e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // trueを返す。こうしないと以降のMoveイベントが発生しなくなる。
                ret = true;
                break;
            case MotionEvent.ACTION_UP:
                ret = true;
                break;
            case MotionEvent.ACTION_MOVE:
                ret = true;
                break;
            default:
        }

        // コールバック
        return ret;
    }

    /**
     * 各Windowのタッチ処理を変更する
     * @param vt
     * @return
     */
    private boolean WindoTouchEvent(ViewTouch vt) {
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
     * @param windowId
     * @param shape
     * @param menuItemId
     */
    private void addIcon(int windowId, IconType shape, MenuItemId menuItemId) {
        UIconWindow iconWindow = mIconWindows[windowId];
        UIconManager manager = iconWindow.getIconManager();
        UIcon icon = manager.addIcon(shape, AddPos.Top);

        // アイコンの初期座標は追加メニューアイコンの位置
        PointF menuPos = mMenuBar.getItemPos(menuItemId);
        icon.setPos(iconWindow.toWinX(menuPos.x), iconWindow.toWinY(menuPos.y));
        mIconWindows[windowId].sortRects(true);
    }

    /**
     * UMenuItemCallbacks
     */
    /**
     * メニューアイテムをタップした時のコールバック
     */
    public void menuItemCallback1(MenuItemId id) {
        switch (id) {
            case AddTop:
                break;
            case AddCard:
                addIcon(0, IconType.CIRCLE, id);
                break;
            case AddBook:
                addIcon(0, IconType.RECT, id);
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
                invalidate();
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
                    UIconBox box = (UIconBox)icon;
                    mIconWindows[1].setIconManager(box.getIconManager());
                    mIconWindows[1].sortRects(false);
                    float posY = mIconWindows[1].pos.y;
                    mIconWindows[1].setPos(0, getHeight(), true);
                    mIconWindows[1].startMove(0, posY, 10);
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

}
