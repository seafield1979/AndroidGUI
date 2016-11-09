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
public class TopView extends View implements OnTouchListener, MenuItemCallbacks, IconCallbacks{
    enum WindowType {
        Icon1,
        Icon2,
        MenuBar,
        Log
    }

    public static final String TAG = "TopView";

    // Windows
    private Window[] mWindows = new Window[WindowType.values().length];
    // IconWindow
    private IconWindow[] mIconWindows = new IconWindow[2];

    // MessageWindow
    private LogWindow mLogWin;

    // メニューバー
    private MenuBar mMenuBar;

    // サイズ更新用
    private boolean resetSize;
    private int newWidth, newHeight;
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
        MyLog.print(TAG, "w:" + width + " h:" + height);

        // IconWindow
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
            mIconWindows[0] = IconWindow.createInstance(this, this, pos1.x, pos1.y, size1.width, size1.height, Color.WHITE);
            mIconWindows[0].setWindows(mIconWindows);
            mWindows[WindowType.Icon1.ordinal()] = mIconWindows[0];
        }

        if (mIconWindows[1] == null) {
            mIconWindows[1] = IconWindow.createInstance(this, this, pos2.x, pos2.y, size2.width, size2.height, Color.LTGRAY);
            mIconWindows[1].setWindows(mIconWindows);
            mWindows[WindowType.Icon2.ordinal()] = mIconWindows[1];
        }
        // MenuBar
        if (mMenuBar == null) {
            mMenuBar = MenuBar.createInstance(this, this, width, height, Color.BLACK);
            mWindows[WindowType.MenuBar.ordinal()] = mMenuBar;
        }

        // LogWindow
        if (mLogWin == null) {
            mLogWin = LogWindow.createInstance(getContext(), this,
                    width / 2, height,
                    Color.argb(128,0,0,0));
            mWindows[WindowType.Log.ordinal()] = mLogWin;
        }
    }

    /**
     * Viewのサイズを指定する
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int viewW = MeasureSpec.getSize(widthMeasureSpec);
        int viewH = MeasureSpec.getSize(heightMeasureSpec);
        int modeW = MeasureSpec.getMode(widthMeasureSpec);
        int modeH = MeasureSpec.getMode(heightMeasureSpec);
        MyLog.print(TAG, "measure w:" + viewW + " h:" + viewH + " wm:" + (modeW >> 30) + " wh:" + (modeH >> 30));

        if (resetSize) {
            int width = MeasureSpec.EXACTLY | newWidth;
            int height = MeasureSpec.EXACTLY | newHeight;
            setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

        // ここでとるサイズはViewのgetWidth(),getHeightと異なるので使用できない
//        if (isFirst) {
//            isFirst = false;
//            initWindows(viewW, viewH);
//        }
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
            Window win = mWindows[i];
            if (win.doAction()) {
                invalidate();
            }
        }

        DrawManager.getInstance().draw(canvas, paint);
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
            Window win = mWindows[i];
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
    private void addIcon(int windowId, IconShape shape, MenuItemId menuItemId) {
        IconWindow iconWindow = mIconWindows[windowId];
        IconManager manager = iconWindow.getIconManager();
        Icon icon = manager.addIcon(shape, AddPos.Top);

        // アイコンの初期座標は追加メニューアイコンの位置
        PointF menuPos = mMenuBar.getItemPos(menuItemId);
        icon.setPos(iconWindow.toWinX(menuPos.x), iconWindow.toWinY(menuPos.y));
        mIconWindows[windowId].sortRects(true);
    }

    /**
     * MenuItemCallbacks
     */
    /**
     * メニューアイテムをタップした時のコールバック
     */
    public void callback1(MenuItemId id) {
        switch (id) {
            case AddTop:
                break;
            case AddCard:
                addIcon(0, IconShape.CIRCLE, id);
                break;
            case AddBook:
                addIcon(0, IconShape.RECT, id);
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
        }
        MyLog.print(TAG, "menu item clicked " + id);
    }

    public void callback2() {
        MyLog.print(TAG, "menu item moved");
    }

    /**
     * IconCallbacks
     */
    public void clickIcon(Icon icon) {
        MyLog.print(TAG, "clickIcon");
        switch(icon.shape) {
            case CIRCLE:
                break;
            case RECT:
                break;
            case BOX: {
                // 配下のアイコンをSubWindowに表示する
                if (icon instanceof IconBox) {
                    IconBox box = (IconBox)icon;
                    mIconWindows[1].setIconManager(box.getIconManager());
                    mIconWindows[1].sortRects(false);
                    box.setSubWindow(mIconWindows[1]);
                }
            }
                break;
            case IMAGE:
                break;
        }
    }
    public void longClickIcon(Icon icon) {
        MyLog.print(TAG, "longClickIcon");
    }
    public void dropToIcon(Icon icon) {
        MyLog.print(TAG, "dropToIcon");
    }

}
