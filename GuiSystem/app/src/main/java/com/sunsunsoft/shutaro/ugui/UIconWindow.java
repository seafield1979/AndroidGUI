package com.sunsunsoft.shutaro.ugui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.SurfaceView;
import android.view.View;

import java.util.Collections;
import java.util.List;

/**
 * アイコンのリストを表示するWindow
 */
enum WindowState {
    none,
    drag,               // アイコンのドラッグ中
    icon_moving,        // アイコンの一変更後の移動中
    icon_selecting      // アイコンを選択中(チェックを入れる)
}

// アイコンウィンドウの種類
// Homeはデスクトップのアイコンを表示するウィンドウ
// サブはHome以下のBoxアイコンを開いた時のウィンドウ
enum WindowType {
    Home,
    Sub
}

// ウィンドウの向き
// 画面が横長の場合はHorizontal
// 画面が縦長の場合はVertical
enum WindowDir {
    Horizontal,
    Vertical
}

public class UIconWindow extends UWindow implements AutoMovable{

    public static final String TAG = "UIconWindow";

    public static final int DRAW_PRIORITY = 100;
    public static final int DRAG_ICON_PRIORITY = 10;

    public static final int ICON_MARGIN = 30;

    private static final int RECT_ICON_NUM = 10;
    private static final int CIRCLE_ICON_NUM = 10;
    private static final int BOX_ICON_NUM = 10;

    private static final int ICON_W = 200;
    private static final int ICON_H = 150;
    private static final int MARGIN_D = UMenuBar.MENU_BAR_H;

    private static final int MOVING_TIME = 10;

    // メンバ変数
    private WindowType type;
    private View mParentView;
    private UIconCallbacks mIconCallbacks;
    private UIconManager mIconManager;
    private DrawList mDrawList;
    private PointF basePos;
    private WindowDir dir;

    // 他のIconWindow
    // ドラッグで他のWindowにアイコンを移動するのに使用する
    private UIconWindows windows;

    // ドラッグ中のアイコン
    private UIcon dragedIcon;
    // ドロップ中のアイコン
    private UIcon dropedIcon;
    // 選択中のアイコン
    private UIcon selectedIcon;

    // アニメーション用
    private WindowState state = WindowState.none;

    // Iconのアニメーション中
    private boolean isAnimating;

    private int skipFrame = 3;  // n回に1回描画
    private int skipCount;


    /**
     * Get/Set
     */
    public WindowType getType() {
        return type;
    }
    public void setType(WindowType type) {
        this.type = type;
    }

    public PointF getBasePos() {
        return basePos;
    }

    public UIconManager getIconManager() {
        return mIconManager;
    }
    public void setIconManager(UIconManager mIconManager) {
        this.mIconManager = mIconManager;
    }

    public List<UIcon> getIcons() {
        if (mIconManager == null) return null;
        return mIconManager.getIcons();
    }

    public WindowDir getDir() {
        return dir;
    }

    public void setDir(WindowDir dir) {
        this.dir = dir;
    }

    public void setWindows(UIconWindows windows) {
        this.windows = windows;
    }

    public UIconWindows getWindows() { return this.windows; }

    public void setParentView(SurfaceView mParentView) {
        this.mParentView = mParentView;
    }

    public void setAnimating(boolean animating) {
        isAnimating = animating;
    }

    public UIconCallbacks getIconCallbacks() {
        return mIconCallbacks;
    }

    public void setDragedIcon(UIcon dragedIcon) {
        if (dragedIcon == null) {
            if (this.dragedIcon != null) {
                UDrawManager.getInstance().removeDrawable(this.dragedIcon);
            }
        }
        else {
            dragedIcon.setDrawPriority(DRAG_ICON_PRIORITY);
            UDrawManager.getInstance().addDrawable(dragedIcon);
        }
        this.dragedIcon = dragedIcon;
    }

    public void setSelectedIcon(UIcon selectedIcon) {
        this.selectedIcon = selectedIcon;
    }

    /**
     * 状態を設定する
     * 状態に移る時の前処理、後処理を実行できる
     * @param state
     */
    public void setState(WindowState state) {
        if (this.state == state) return;

        // 状態変化時の処理
        // 後処理
        switch (this.state) {
            case icon_selecting:
            {
                if (state == WindowState.none) {
                    List<UIcon> icons = getIcons();
                    for (UIcon icon : icons) {
                        icon.isChecking = false;
                        if (icon.isChecked) {
                            icon.isChecked = false;
                            UDrawManager.getInstance().removeDrawable(icon);
                        }
                    }
                }
            }
                break;
        }

        // 前処理
        switch(state){
            case icon_moving:
            {
//                List<UIcon> icons = mIconManager.getCheckedIcons();
//                for (UIcon icon : icons) {
//                    UDrawManager.getInstance().addDrawable(icon);
//                }
            }
                break;
        }

        this.state = state;
    }

    /**
     * コンストラクタ
     */
    private UIconWindow(float x, float y, int width, int height, int color) {
        super(DRAW_PRIORITY, x, y, width, height, color);
        basePos = new PointF(x,y);
    }
    /**
     * インスタンスを生成する
     * Homeタイプが２つできないように自動でHome、Subのタイプ分けがされる
     * @return
     */
    public static UIconWindow createInstance(View parent, UIconCallbacks iconCallbacks,
                                             boolean isHome, WindowDir dir,
                                             float x, float y, int width, int height, int bgColor)
    {
        UIconWindow instance = new UIconWindow(x, y, width, height, bgColor);
        if (isHome) {
            instance.type = WindowType.Home;
            instance.mIconManager = UIconManager.createInstance(parent, instance);
        } else {
            instance.type = WindowType.Sub;
        }
        instance.mParentView = parent;
        instance.mIconCallbacks = iconCallbacks;
        instance.dir = dir;
        return instance;
    }

    /**
     * Windowを生成する
     * インスタンス生成後に一度だけ呼ぶ
     */
    public void init() {
        // アイコンを追加
        if (type == WindowType.Home) {
            for (int i = 0; i < RECT_ICON_NUM; i++) {

                UIcon icon = mIconManager.addIcon(IconType.RECT, AddPos.Tail);
                int color = 0;
                switch (i % 3) {
                    case 0:
                        color = Color.rgb(255, 0, 0);
                        break;
                    case 1:
                        color = Color.rgb(0, 255, 0);
                        break;
                    case 2:
                        color = Color.rgb(0, 0, 255);
                        break;
                }
                icon.setColor(color);
            }

            for (int i = 0; i < CIRCLE_ICON_NUM; i++) {
                UIcon icon = mIconManager.addIcon(IconType.CIRCLE, AddPos.Tail);
                int color = 0;
                switch (i % 3) {
                    case 0:
                        color = Color.rgb(255, 0, 0);
                        break;
                    case 1:
                        color = Color.rgb(0, 255, 0);
                        break;
                    case 2:
                        color = Color.rgb(0, 0, 255);
                        break;
                }
                icon.setColor(color);
            }
            for (int i = 0; i < BOX_ICON_NUM; i++) {
                UIcon icon = mIconManager.addIcon(IconType.BOX, AddPos.Tail);
            }
        }
        // 描画はDrawManagerに任せるのでDrawManagerに登録
        mDrawList = UDrawManager.getInstance().addDrawable(this);

        sortIcons(false);
    }

    /**
     * 毎フレーム行う処理
     * @return true:再描画を行う(まだ処理が終わっていない)
     */
    public boolean doAction() {
        boolean redraw = false;
        boolean allFinished = true;
        List<UIcon> icons = getIcons();

        // Windowの移動
        if (isMoving) {
            if (isMoving) {
                if (!move()) {
                    isMoving = false;
                }
            }
        }

        // アイコンの移動
        if (icons != null) {
            if (state == WindowState.icon_moving) {
                allFinished = true;
                for (UIcon icon : icons) {
                    if (icon.move()) {
                        allFinished = false;
                    }
                }
                if (allFinished) {
                    List<UIcon> checkedIcons = mIconManager.getCheckedIcons();
                    if (checkedIcons.size() > 0) {
                        setState(WindowState.icon_selecting);
                    } else {
                        setState(WindowState.none);
                    }
                    mIconManager.updateBlockRect();
                    setDragedIcon(null);
                }
                else {
                    redraw = true;
                }
            }
        }

        return redraw;
    }

    /**
     * 描画処理
     * @param canvas
     * @param paint
     * @return trueなら描画継続
     */
    public void draw(Canvas canvas, Paint paint, PointF offset)
    {
        if (!isShow) return;
        List<UIcon> icons = getIcons();
        if (icons == null) return;

        // 背景を描画
        if (bgColor != 0) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(bgColor);
            canvas.drawRect(rect, paint);
        }

        // ウィンドウの座標とスクロールの座標を求める
        PointF _offset = new PointF(pos.x - contentTop.x, pos.y - contentTop.y);
        Rect windowRect = new Rect((int)contentTop.x, (int)contentTop.y, (int)contentTop.x + size.width, (int)contentTop.y + size.height);

        // クリッピング領域を設定
        canvas.save();
        canvas.clipRect(rect);

        int clipCount = 0;
        for (UIcon icon : mIconManager.getIcons()) {
            if (icon == dragedIcon) continue;
            // 矩形範囲外なら描画しない
            if (URect.intersect(windowRect, icon.getRect())) {
                icon.draw(canvas, paint, _offset);

                // 選択中のアイコンに枠を表示する
                if (icon == selectedIcon) {
                    UDraw.drawRect(canvas, paint, icon.getRectWithOffset(_offset), 5, Color.RED);
                }
            } else {
                clipCount++;
            }
        }

        // スクロールバー
        if (dir == WindowDir.Vertical) {
            mScrollBarV.draw(canvas, paint);
        } else {
            mScrollBarH.draw(canvas, paint);
        }

        if (UDebug.DRAW_ICON_BLOCK_RECT) {
            mIconManager.getBlockManager().draw(canvas, paint, getToScreenPos());
        }

        // クリッピング解除
        canvas.restore();
    }


    /**
     * 描画オフセットを取得する
     * @return
     */
    public PointF getDrawOffset() {
        return null;
    }


    /**
     * Windowのサイズを更新する
     * Windowのサイズを更新する
     * Windowのサイズを更新する
     * サイズ変更に合わせて中のアイコンを再配置する
     * @param width
     * @param height
     */
    @Override
    public void updateSize(int width, int height) {
        super.updateSize(width, height);
        // アイコンの整列
        sortIcons(false);

        // スクロールバー
        if (dir == WindowDir.Vertical) {
            mScrollBarV.updateSize(width, height);
            mScrollBarV.updateContent(contentSize);
        } else {
            mScrollBarH.updateSize(width, height);
            mScrollBarH.updateContent(contentSize);
        }
    }

    /**
     * アイコンを整列する
     * Viewのサイズが確定した時点で呼び出す
     */
    public void sortIcons(boolean animate) {
        List<UIcon> icons = getIcons();
        if (icons == null) return;

        int column = (clientSize.width - ICON_MARGIN) / (ICON_W + ICON_MARGIN);
        if (column <= 0) {
            return;
        }

        int maxSize = 0;

        int i=0;
        if (dir == WindowDir.Vertical) {
            int margin = (clientSize.width - ICON_W * column) / (column + 1);
            for (UIcon icon : icons) {
                int x = margin + (i % column) * (ICON_W + margin);
                int y = margin + (i / column) * (ICON_H + margin);
                int height = y + (ICON_H + margin);
                if (height >= maxSize) {
                    maxSize = height;
                }
                if (animate) {
                    icon.startMoving(x, y, MOVING_TIME);
                } else {
                    icon.setPos(x, y);
                }
                i++;
            }
        } else {
            int margin = (clientSize.height - ICON_H * column) / (column + 1);
            for (UIcon icon : icons) {
                int x = margin + (i / column) * (ICON_W + margin);
                int y = margin + (i % column) * (ICON_H + margin);
                int width = x + (ICON_W + margin);
                if (width >= maxSize) {
                    maxSize = width;
                }
                if (animate) {
                    icon.startMoving(x, y, MOVING_TIME);
                } else {
                    icon.setPos(x, y);
                }
                i++;
            }
        }

        if (!animate) {
            IconsPosFixed();
        }
        setState(WindowState.icon_moving);

        // メニューバーに重ならないように下にマージンを設ける
        if (dir == WindowDir.Vertical) {
            setContentSize(size.width, maxSize + MARGIN_D);
            mScrollBarV.updateContent(contentSize);
        } else {
            setContentSize(maxSize + MARGIN_D, size.height);
            mScrollBarH.updateContent(contentSize);
        }

//        mParentView.invalidate();
    }

    /**
     * アイコンの座標が確定
     * アイコンの再配置完了時(アニメーションありの場合はそれが終わったタイミング)
     */
    private void IconsPosFixed() {
        mIconManager.updateBlockRect();
    }

    /**
     * 長押しされた時の処理
     * @param vt
     */
    private boolean longPressed(ViewTouch vt) {
        List<UIcon> icons = getIcons();
        if (icons == null) return false;

        if (state == WindowState.none) {
            // チェック中のアイコンが１つでも存在していたら他のアイコンを全部チェック中に変更
            boolean isChecking = false;
            for (UIcon icon : icons) {
                if (icon.isChecking) {
                    isChecking = true;
                    break;
                }
            }
            if (isChecking) {
                changeIconChecking(icons, true);
            }
            setState(WindowState.icon_selecting);
        } else if (state == WindowState.icon_selecting) {
            changeIconChecking(icons, false);
            setState(WindowState.none);
        }
        return true;
    }

    /**
     * アイコンをドラッグ開始
     * @param vt
     */
    private boolean dragStart(ViewTouch vt) {
        List<UIcon> icons = getIcons();
        if (icons == null) return false;

        boolean ret = false;
        List<UIcon> checkedIcons = mIconManager.getCheckedIcons();
        if (checkedIcons.size() > 0) {
            setState(WindowState.icon_selecting);
        }

        if (state == WindowState.none) {
            // タッチされたアイコンを選択する
            // 一番上のアイコンからタッチ判定したいのでリストを逆順（一番手前から）で参照する
            Collections.reverse(icons);
            for (UIcon icon : icons) {
                if (icon.isDraging) {
                    setDragedIcon(icon);
                    ret = true;
                    break;
                }
            }

            Collections.reverse(icons);

            if (ret) {
                setState(WindowState.drag);
                return true;
            }
        } else if (state == WindowState.icon_selecting) {
            // チェックしたアイコンをまとめてドラッグ
            for (UIcon icon : icons) {
                icon.isDraging = true;
                ret = true;
            }
        }
        return ret;
    }

    /**
     * ドラッグ中の移動処理
     * @param vt
     * @return
     */
    private boolean dragMove(ViewTouch vt) {
        // ドラッグ中のアイコンを移動
        boolean isDone = false;

        if (state == WindowState.drag) {
            if (dragedIcon == null) return false;
            // ドラッグ中のアイコンを移動する
            dragedIcon.move(vt.moveX, vt.moveY);

            // 現在のドロップフラグをクリア
            if (dropedIcon != null) {
                dropedIcon.isDroping = false;
            }

            for (UIconWindow window : windows.getWindows()) {
                // ドラッグ中のアイコンが別のアイコンの上にあるかをチェック
                Point dragPos = new Point((int) window.toWinX(vt.getX()), (int) window.toWinY(vt.getY()));

                UIconManager manager = window.getIconManager();
                if (manager == null) continue;

                UIcon icon = manager.getOverlappedIcon(dragPos, dragedIcon);
                if (icon != null) {
                    isDone = true;
                    dropedIcon = icon;
                    dropedIcon.isDroping = true;
                }
                if (isDone) break;
            }
        } else if (state == WindowState.icon_selecting){
            // チェックしたアイコンをまとめて移動する
            List<UIcon> icons = mIconManager.getCheckedIcons();
            if (icons != null) {
                for (UIcon icon : icons) {
                    icon.move(vt.moveX, vt.moveY);
                }
            }
        }

        return isDone;
    }

    /**
     * ドラッグ終了時の処理（通常時)
     * @param vt
     * @return trueならViewを再描画
     */
    private boolean dragEndNormal(ViewTouch vt) {
        // ドロップ処理
        // 他のアイコンの上にドロップされたらドロップ処理を呼び出す
        if (dragedIcon == null) return false;
        boolean ret = false;

        boolean isDroped = false;

        if (dropedIcon != null) {
            dropedIcon.isDroping = false;
            dropedIcon = null;
        }

        List<UIcon> srcIcons = getIcons();
        for (UIconWindow window : windows.getWindows()) {
            // Windowの領域外ならスキップ
            if (!(window.rect.contains((int)vt.getX(),(int)vt.getY()))){
                continue;
            }

            List<UIcon> dstIcons = window.getIcons();

            if (dstIcons == null) continue;

            // スクリーン座標系からWindow座標系に変換
            float winX = window.toWinX(vt.getX());
            float winY = window.toWinY(vt.getY());

            for (UIcon icon : dstIcons) {
                if (icon == dragedIcon) continue;

                if (icon.checkDrop(winX, winY)) {
                    switch (icon.getType()) {
                        case CIRCLE:
                            // ドラッグ位置のアイコンと場所を交換する
                            changeIcons(dragedIcon, icon);
                            isDroped = true;
                            break;
                        case RECT:
                        case IMAGE:
                            // ドラッグ位置にアイコンを挿入する
                            insertIcons(dragedIcon, icon, true);
                            isDroped = true;
                            break;
                        case BOX:
                            if (dragedIcon.type != IconType.BOX) {
                                UIconBox box = (UIconBox) icon;
                                if (box.getIcons() != null) {
                                    moveIconIntoBox(dragedIcon, icon);
                                    mIconManager.updateBlockRect();
                                    for (UIconWindow win : windows.getWindows()) {
                                        UIconManager manager = win.getIconManager();
                                        if (manager != null) {
                                            manager.updateBlockRect();
                                        }
                                    }
                                    isDroped = true;
                                }
                            }
                            break;
                    }
                    break;
                }
            }

            // その他の場所にドロップされた場合
            if (!isDroped && dstIcons != null ) {
                boolean isMoved = false;
                if (dstIcons.size() > 0) {
                    UIcon lastIcon = dstIcons.get(dstIcons.size() - 1);
                    if ((lastIcon.getY() <= winY &&
                            winY <= lastIcon.getBottom() &&
                            lastIcon.getRight() <= winX) ||
                            (lastIcon.getBottom() <= winY))
                    {
                        isMoved = true;
                        isDroped = true;
                    }
                } else {
                    isMoved = true;
                }

                if (isMoved) {
                    // 最後のアイコンの後の空きスペースにドロップされた場合
                    // ドラッグ中のアイコンをリストの最後に移動
                    srcIcons.remove(dragedIcon);
                    dstIcons.add(dragedIcon);
                    // 親の付け替え
                    dragedIcon.setParentWindow(window);
                }
            }
            // 再配置
            if (srcIcons != dstIcons) {
                // 座標系変換(移動元Windowから移動先Window)
                if (isDroped) {
                    dragedIcon.setPos(win1ToWin2X(dragedIcon.pos.x, this, window), win1ToWin2Y(dragedIcon.pos.y, this, window));
                }
                window.sortIcons(true);
            }
            if (isDroped) break;
        }
        this.sortIcons(true);

        return true;
    }

    /**
     * ドラッグ終了時の処理（アイコン選択時)
     * @param vt
     * @return trueならViewを再描画
     */
    private boolean dragEndChecking(ViewTouch vt) {
        // ドロップ処理
        // 他のアイコンの上にドロップされたらドロップ処理を呼び出す
        boolean isDroped = false;

        if (dropedIcon != null) {
            dropedIcon.isDroping = false;
            dropedIcon = null;
        }

        List<UIcon> srcIcons = getIcons();
        List<UIcon> checkedIcons = mIconManager.getCheckedIcons();

        for (UIconWindow window : windows.getWindows()) {
            // Windowの領域外ならスキップ
            if (!(window.rect.contains((int)vt.getX(),(int)vt.getY()))){
                continue;
            }

            List<UIcon> dstIcons = window.getIcons();
            if (dstIcons == null) continue;

            // スクリーン座標系からWindow座標系に変換
            float winX = window.toWinX(vt.getX());
            float winY = window.toWinY(vt.getY());

            for (UIcon icon : dstIcons) {
                if (icon == dragedIcon ||
                 icon.getType() != IconType.BOX )
                {
                    continue;
                }

                if (icon.checkDrop(winX, winY)) {
                    UIconBox box = (UIconBox) icon;
                    if (box.getIcons() != null) {
                        moveIconsIntoBox(box);

                        for (UIconWindow win : windows.getWindows()) {
                            UIconManager manager = win.getIconManager();
                            if (manager != null) {
                                manager.updateBlockRect();
                            }
                        }
                        isDroped = true;
                    }
                }
            }

            // その他の場所にドロップされた場合
            if (!isDroped && dstIcons != null ) {
                boolean isMoved = false;
                if (dstIcons.size() > 0) {
                    UIcon lastIcon = dstIcons.get(dstIcons.size() - 1);
                    if ((lastIcon.getY() <= winY &&
                            winY <= lastIcon.getBottom() &&
                            lastIcon.getRight() <= winX) ||
                            (lastIcon.getBottom() <= winY))
                    {
                        isMoved = true;
                        isDroped = true;
                    }
                } else {
                    isMoved = true;
                }

                if (isMoved) {
                    // 最後のアイコンの後の空きスペースにドロップされた場合
                    // ドラッグ中のアイコンをリストの最後に移動
                    srcIcons.removeAll(checkedIcons);
                    dstIcons.addAll(checkedIcons);
                    // 親の付け替え
                    for (UIcon icon : checkedIcons) {
                        icon.setParentWindow(window);
                    }
                }
            }
            // 再配置
            if (srcIcons != dstIcons) {
                // 座標系変換(移動元Windowから移動先Window)
                if (isDroped) {
                    for (UIcon icon : checkedIcons) {
                        icon.setPos(win1ToWin2X(icon.pos.x, this, window), win1ToWin2Y(icon.pos.y, this, window));
                    }
                }
                window.sortIcons(true);
            }
            if (isDroped) break;
        }
        this.sortIcons(true);

        return true;
    }

    /**
     * タッチ処理
     * @param vt
     * @return trueならViewを再描画
     */
    public boolean touchEvent(ViewTouch vt) {
        if (!isShow) return false;
        if (state == WindowState.icon_moving) return false;
        boolean done = false;

        // スクロールバーのタッチ処理
        if (mScrollBarV.touchEvent(vt)) {
            if (dir == WindowDir.Vertical) {
                contentTop.y = mScrollBarV.getTopPos();
            } else {
                contentTop.x = mScrollBarH.getTopPos();
            }
            return true;
        }

        // 範囲外なら除外
        if (!(rect.contains((int)vt.touchX(), (int)vt.touchY()))) {
            return false;
        }


        List<UIcon> icons = getIcons();
        if (icons != null) {
            for (UIcon icon : icons) {
                if (icon.touchEvent(vt, getToWinPos())) {
                    done = true;
                    break;
                }
            }
        }

        switch (vt.type) {
            case Click:
                if (state == WindowState.icon_selecting) {
                    // 選択されたアイコンがなくなったら選択状態を解除
                    List<UIcon> checkedIcons = mIconManager.getCheckedIcons();
                    if (checkedIcons.size() <= 0) {
                        setState(WindowState.none);
                        done = true;
                    }
                }
                break;
            case LongPress:
                longPressed(vt);
                done = true;
                break;
            case Moving:
                if (vt.isMoveStart()) {
                    if (dragStart(vt)) {
                        done = true;
                    }
                }
                if (dragMove(vt)) {
                    done = true;
                }
                break;
            case MoveEnd:
                if (state == WindowState.drag) {
                    if (dragEndNormal(vt)) {
                        done = true;
                    }
                } else {
                    if (dragEndChecking(vt)) {
                        done = true;
                    }
                }
                break;
            case MoveCancel:
                sortIcons(false);
                setDragedIcon(null);
                break;
        }

        if (!done) {
            // 画面のスクロール処理
            if (scrollView(vt)){
                done = true;
            }
        }
        return done;
    }

    /**
     * ２つのアイコンの位置を交換する
     * @param icon1
     * @param icon2
     */
    private void changeIcons(UIcon icon1, UIcon icon2 )
    {
        // アイコンの位置を交換
        // 並び順も重要！
        UIconWindow window1 = icon1.parentWindow;
        UIconWindow window2 = icon2.parentWindow;
        List<UIcon> icons1 = window1.getIcons();
        List<UIcon> icons2 = window2.getIcons();

        int index = icons2.indexOf(icon2);
        int index2 = icons1.indexOf(icon1);
        if (index == -1 || index2 == -1) return;


        icons1.remove(icon1);
        icons2.add(index, icon1);
        icons2.remove(icon2);
        icons1.add(index2, icon2);

        // 再配置
        if (window1 != window2) {
            // 親の付け替え
            icon1.setParentWindow(window2);
            icon2.setParentWindow(window1);

            // ドロップアイコンの座標系を変換
            // アイコン1 UWindow -> アイコン2 UWindow
            icon1.setPos(icon1.pos.x + this.pos.x - window2.pos.x,
                    icon1.pos.y + this.pos.y - window2.pos.y);

            // アイコン2 UWindow -> アイコン1 UWindow
            icon2.setPos(icon2.pos.x + window2.pos.x - this.pos.x,
                    icon2.pos.y + window2.pos.y - this.pos.y);
            window2.sortIcons(true);
        }
        window1.sortIcons(true);
    }

    /**
     * アイコンを挿入する
     * @param icon1  挿入元のアイコン
     * @param icon2  挿入先のアイコン
     * @param animate
     */
    private void insertIcons(UIcon icon1, UIcon icon2, boolean animate)
    {
        UIconWindow window1 = icon1.parentWindow;
        UIconWindow window2 = icon2.parentWindow;
        List<UIcon> icons1 = window1.getIcons();
        List<UIcon> icons2 = window2.getIcons();

        int index = icons2.indexOf(icon2);
        if (index == -1) return;

        icons1.remove(icon1);
        icons2.add(index, icon1);

        // 再配置
        if (animate) {
            if (window1 != window2) {
                // 親の付け替え
                icon1.setParentWindow(window2);
                icon2.setParentWindow(window1);

                // ドロップアイコンの座標系を変換
                dragedIcon.setPos(icon1.pos.x + window2.pos.x - window1.pos.x,
                        icon1.pos.y + window2.pos.y - window1.pos.y);
                window2.sortIcons(animate);
            }
        }
        window1.sortIcons(animate);
    }

    /**
     * アイコンを移動する
     * アイコンを別のボックスタイプのアイコンにドロップした時に使用する
     * @param icon1 ドロップ元のIcon
     * @param icon2 ドロップ先のIcon
     */
    private void moveIconIntoBox(UIcon icon1, UIcon icon2)
    {

        if (icon2 instanceof UIconBox) {
            UIconBox box = (UIconBox)icon2;

            UIconWindow window1 = icon1.parentWindow;
            UIconWindow window2 = box.getSubWindow();
            List<UIcon> icons1 = window1.getIcons();
            List<UIcon> icons2 = box.getIcons();

            icons1.remove(icon1);
            icons2.add(icon1);

            if (window2 != null) {
                window2.sortIcons(false);
                icon1.setParentWindow(window2);
            }
        }

        sortIcons(true);
    }

    /**
     * チェックされた複数のアイコンをボックスの中に移動する
     * @param dropedIcon
     */
    private void moveIconsIntoBox(UIconBox dropedIcon) {
        // チェックされたアイコンのリストを作成
        List<UIcon> checkedIcons = mIconManager.getCheckedIcons();
        if (checkedIcons.size() <= 0) return;

        UIcon dragIcon = checkedIcons.get(0);

        UIconWindow window1 = dragIcon.parentWindow;
        UIconWindow window2 = dropedIcon.getSubWindow();
        List<UIcon> icons1 = window1.getIcons();
        List<UIcon> icons2 = window2.getIcons();

        icons1.removeAll(checkedIcons);
        icons2.addAll(checkedIcons);

        if (window2 != null) {
            window2.sortIcons(false);
            for (UIcon icon : checkedIcons) {
                icon.setParentWindow(window2);
            }
        }
    }


    /**
     * アイコンの選択状態を変更する
     */
    private void changeIconChecking(List<UIcon> icons, boolean isChecking) {
        for (UIcon icon : icons) {
            icon.isChecking = isChecking;
            if (!isChecking) {
                icon.isChecked = false;
            }
        }
    }

    /**
     * 以下Drawableインターフェースのメソッド
     */
    public void setDrawList(DrawList drawList) {
        mDrawList = drawList;
    }

    public DrawList getDrawList() {
        return mDrawList;
    }

    /**
     * 描画範囲の矩形を取得
     * @return
     */
    public Rect getRect() {
        return rect;
    }

    /**
     * アニメーション開始
     */
    public void startAnim() {

    }

    /**
     * アニメーション処理
     * onDrawからの描画処理で呼ばれる
     * @return true:アニメーション中
     */
    public boolean animate() {
        boolean allFinished = true;

        List<UIcon> icons = getIcons();
        if (isAnimating) {
            if (icons != null) {
                allFinished = true;
                for (UIcon icon : icons) {
                    if (icon.animate()) {
                        allFinished = false;
                    }
                }
                if (allFinished) {
                    isAnimating = false;
                }
            }
        }
        return !allFinished;
    }

    /**
     * アニメーション中かどうか
     * @return
     */
    public boolean isAnimating() {
        return isAnimating;
    }
}
