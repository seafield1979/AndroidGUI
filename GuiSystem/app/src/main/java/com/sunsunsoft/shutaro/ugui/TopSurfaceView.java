package com.sunsunsoft.shutaro.ugui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 単語帳トップ SurfaceView版
 */

public class TopSurfaceView extends SurfaceView implements Runnable,SurfaceHolder.Callback, UMenuItemCallbacks, UIconCallbacks, ViewTouchCallbacks {
    enum WindowType {
        Icon1,
        Icon2,
        MenuBar,
        Log
    }

    public static final String TAG = "TopView";
    public static final int SUB_WINDOW_MOVE_FRAME = 10;

    // SurfaceView用
    SurfaceHolder surfaceHolder;
    Thread thread;
    Context mContext;
    private boolean isInvalidate;
    int screen_width, screen_height;

    // Windows
    private UWindow[] mWindows = new UWindow[TopView.WindowType.values().length];
    // UIconWindow
    private UIconWindows mIconWindows;

    // MessageWindow
    private ULogWindow mLogWin;

    // メニューバー
    private UMenuBar mMenuBar;

    // サイズ更新用
    private boolean isFirst = true;

    // クリック判定の仕組み
    private ViewTouch viewTouch = new ViewTouch(this);

    private Paint paint = new Paint();

    // get/set
    public TopSurfaceView(Context context) {
        this(context, null);
    }

    public TopSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
    }

    private void initWindows(int width, int height) {
        // 描画オブジェクトクリア
        UDrawManager.getInstance().init();

        ULog.print(TAG, "w:" + width + " h:" + height);

        // UIconWindow
        PointF pos1, pos2;
        Size size1, size2;
        UIconWindow.WindowDir winDir;
        if (width <= height) {
            pos1 = new PointF(0, 0);
            size1 = new Size(width, height/2);
            pos2 = new PointF(0, height/2);
            size2 = new Size(width, height/2);
            winDir = UIconWindow.WindowDir.Vertical;
        } else {
            pos1 = new PointF(0, 0);
            size1 = new Size(width / 2, height);
            pos2 = new PointF(width / 2, 0);
            size2 = new Size(width / 2, height);
            winDir = UIconWindow.WindowDir.Horizontal;
        }

        // Main
        UIconWindow mainWindow = UIconWindow.createInstance(this, this, true, winDir, pos1.x, pos1.y, size1.width, size1.height, Color.WHITE);
        mWindows[TopView.WindowType.Icon1.ordinal()] = mainWindow;

        // Sub
        UIconWindow subWindow = UIconWindow.createInstance(this, this, false, winDir, pos2.x, pos2.y, size2.width, size2.height, Color.LTGRAY);
        mWindows[TopView.WindowType.Icon2.ordinal()] = subWindow;

        mIconWindows = UIconWindows.createInstance(mainWindow, subWindow);
        mainWindow.setWindows(mIconWindows);
        subWindow.setWindows(mIconWindows);

        // アイコンの登録はMainとSubのWindowを作成後に行う必要がある
        mainWindow.init();
        subWindow.init();

        // UMenuBar
        if (mMenuBar == null) {
            mMenuBar = UMenuBar.createInstance(this, this, width, height,
                    Color.BLACK);
            mWindows[TopView.WindowType.MenuBar.ordinal()] = mMenuBar;
        }

        // ULogWindow
        if (mLogWin == null) {
            mLogWin = ULogWindow.createInstance(getContext(), this, LogWindowType.AutoDisappear,
                    0, 0, width / 2, height);
            mWindows[TopView.WindowType.Log.ordinal()] = mLogWin;
        }
    }

    // Surfaceが作られた時のイベント処理
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        initWindows(getWidth(), getHeight());
        isInvalidate = true;

        thread = new Thread(this);
        thread.start();
    }

    // Surfaceが変更された時の処理
    @Override
    public void surfaceChanged(
            SurfaceHolder holder,
            int format,
            int width,
            int height) {
        screen_width = width;
        screen_height = height;

        Log.i("myLog", "surfaceChanged");
    }

    // Surfaceが破棄された時の処理
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        thread = null;
        Log.i("myLog", "surfaceDestroyed");
    }

    @Override
    public void run() {
        Canvas canvas = null;

        long loopCount = 0;
        while(thread != null){
            try{
                synchronized(this) {
                    loopCount++;

                    if (isInvalidate) {
                        isInvalidate = false;

                        canvas = surfaceHolder.lockCanvas();
                        myDraw(canvas);
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }

                       // 次のinvalidateが実行されるまで待ち(すでにinvalidate済みなら待たない)
                    if (!isInvalidate) {
                        wait();
                    }
                }
            }
            catch(Exception e){
                Log.d(TAG, "exception:" + e.getMessage());
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    public synchronized void invalidate() {
        if (isInvalidate == false) {
            isInvalidate = true;
            notify();
        }
    }


    public void myDraw(Canvas canvas) {
        // 背景塗りつぶし
        canvas.drawColor(Color.WHITE);

        // アンチエリアシング(境界のぼかし)
        paint.setAntiAlias(true);

        // アイコンWindow
        // アクション(手前から順に処理する)
        for (int i=mWindows.length - 1; i >= 0; i--) {
            UWindow win = mWindows[i];
            if (win == null) continue;
            if (win.doAction()) {
                invalidate();
            }
        }

        // マネージャに登録した描画オブジェクトをまとめて描画
        if (UDrawManager.getInstance().draw(canvas, paint)){
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
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
     * UMenuItemCallbacks
     */
    /**
     * メニューアイテムをタップした時のコールバック
     */
    public void menuItemClicked(MenuItemId id) {
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
                    UIconWindow window = mIconWindows.getSubWindow();
                    UIconBox box = (UIconBox)icon;
                    window.setIconManager(box.getIconManager());
                    window.sortIcons(false);

                    // SubWindowを画面外から移動させる
                    float sx, sy, dx, dy;
                    if (window.getDir() == UIconWindow.WindowDir.Vertical) {
                        sx = 0;
                        sy = getHeight();
                        dx = 0;
                        dy = window.getBasePos().y;
                    } else {
                        sx = getWidth();
                        sy = 0;
                        dx = window.getBasePos().x;
                        dy = 0;
                    }
                    window.setPos(sx, sy, true);
                    window.startMovingPos(dx, dy, SUB_WINDOW_MOVE_FRAME);
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
     * ViewTouchCallbacks
     */
    public void longPressed() {
        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WindoTouchEvent(viewTouch);
                invalidate();
            }
        });
    }
}
