package com.sunsunsoft.shutaro.ugui.uview.icon;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

import com.sunsunsoft.shutaro.ugui.util.ULog;
import com.sunsunsoft.shutaro.ugui.util.URect;
import com.sunsunsoft.shutaro.ugui.ViewTouch;
import com.sunsunsoft.shutaro.ugui.uview.*;
import com.sunsunsoft.shutaro.ugui.uview.window.*;

import java.util.LinkedList;
import java.util.List;


public class UIconWindow extends UWindow {
    /**
     * Windows for Icons
     * Window can have many icons
     */
    enum WindowState {
        none,
        drag,               // single icon draging
        icon_moving,        // icons moving (icons sort animation)
        icon_selecting      // icons can be selected
    }

    /**
     * Type of icon window
     * Home is a window that shows desktop icons
     * Sub is a window that shows icons which in a box
     */
    public enum WindowType {
        Home,
        Sub
    }

    /**
     * Window directions
     * If screen width is longer than height, it is Horizontal
     * If screen height is longer than width, it is Vertical
     */
    public enum WindowDir {
        Horizontal,
        Vertical
    }

    public enum IconMovingType {
        Exchange,
        Insert
    }

    /**
     * Consts
     */
    public static final String TAG = "UIconWindow";

    public static final int DRAW_PRIORITY = 100;
    public static final int DRAG_ICON_PRIORITY = 11;

    public static final int ICON_MARGIN = 30;

    private static final int RECT_ICON_NUM = 10;
    private static final int CIRCLE_ICON_NUM = 10;
    private static final int BOX_ICON_NUM = 10;

    private static final int ICON_W = 200;
    private static final int ICON_H = 150;
    private static final int MARGIN_D = UMenuBar.MENU_BAR_H;

    private static final int MOVING_TIME = 10;

    /**
     * Member veriables
     */
    private WindowType type;
    private UIconManager mIconManager;
    private PointF basePos;
    private WindowDir dir;

    // 他のIconWindow
    // ドラッグで他のWindowにアイコンを移動するのに使用する
    private UIconWindows windows;

    // ドラッグ中のアイコン
    private UIcon dragedIcon;

    private WindowState state = WindowState.none;
    private WindowState nextState = WindowState.none;

    private boolean isDragMove;
    private boolean isDropInBox;
    private boolean isAnimating;
    private boolean isAppearance = true;       // true:出現中 / false:退出中

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

    public void setWindows(UIconWindows windows) {
        this.windows = windows;
    }

    public UIconWindows getWindows() { return this.windows; }

    public void setAnimating(boolean animating) {
        isAnimating = animating;
    }

    public void setDragedIcon(UIcon dragedIcon) {
        if (dragedIcon == null) {
            if (this.dragedIcon != null) {
                UDrawManager.getInstance().removeDrawable(this.dragedIcon);
            }
        }
        else {
            UDrawManager.getInstance().addWithNewPriority(dragedIcon, DrawPriority.DragIcon.p());
        }
        this.dragedIcon = dragedIcon;
    }

    public void setPos(float x, float y) {
        super.setPos(x,y);
        ULog.print(TAG, "x:" + x + " y:" + y);
    }

    public boolean isAppearance() {
        return isAppearance;
    }

    public void setAppearance(boolean appearance) {
        isAppearance = appearance;
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
            case icon_moving:
            {
                // ドラッグアイコンの描画オブジェクトをクリア
                UDrawManager.getInstance().removeWithPriority(DRAG_ICON_PRIORITY);
            }
                break;
            case icon_selecting:
            {
                if (state == WindowState.none) {
                    List<UIcon> icons = getIcons();
                    for (UIcon icon : icons) {
                        icon.isChecking = false;
                        if (icon.isChecked) {
                            icon.isChecked = false;
                        }
                    }
                    UDrawManager.getInstance().removeWithPriority(DRAG_ICON_PRIORITY);
                }
            }
                break;
        }

        // 前処理
        switch(state){
            case icon_moving:
            {
                List<UIcon> icons = mIconManager.getCheckedIcons();
                for (UIcon icon : icons) {
                    UDrawManager.getInstance().addDrawable(icon);
                }
            }
                break;
        }

        this.state = state;
    }














    /**
     * Constructor
     */
    private UIconWindow(float x, float y, int width, int height, int color) {
        super(null, DRAW_PRIORITY, x, y, width, height, color);
        basePos = new PointF(x,y);
    }
    /**
     * Create class instance
     * It doesn't allow creating multi Home windows.
     * @return
     */
    public static UIconWindow createInstance(View parent, UWindowCallbacks windowCallbacks,
                                             UIconCallbacks iconCallbacks,
                                             boolean isHome, WindowDir dir,
                                             int width, int height,
                                             int bgColor)
    {
        UIconWindow instance = new UIconWindow(0, 0, width, height, bgColor);
        if (isHome) {
            instance.type = WindowType.Home;
            instance.mIconManager = UIconManager.createInstance(parent, instance, iconCallbacks);
        } else {
            instance.type = WindowType.Sub;
            instance.addCloseIcon();
        }
        instance.windowCallbacks = windowCallbacks;
        instance.dir = dir;

        // 描画はDrawManagerに任せるのでDrawManagerに登録
        instance.drawList = UDrawManager.getInstance().addDrawable(instance);

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

                UIcon icon = mIconManager.addIcon(IconType.RECT, UIconManager.AddPos.Tail);
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
                UIcon icon = mIconManager.addIcon(IconType.CIRCLE, UIconManager.AddPos.Tail);
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
                mIconManager.addIcon(IconType.BOX, UIconManager.AddPos.Tail);
            }
        }

        sortIcons(false);
    }

    /**
     * 毎フレーム行う処理
     * @return true:再描画を行う(まだ処理が終わっていない)
     */
    public DoActionRet doAction() {
        DoActionRet ret = DoActionRet.None;
        boolean allFinished;
        List<UIcon> icons = getIcons();

        // Windowの移動
        if (isMoving) {
            if (!autoMoving()) {
                isMoving = false;
            }
        }

        // アイコンの移動
        if (icons != null) {
            if (state == WindowState.icon_moving) {
                allFinished = true;
                for (UIcon icon : icons) {
                    if (icon.autoMoving()) {
                        allFinished = false;
                    }
                }
                if (allFinished) {
                    endIconMoving();
                }
                ret = DoActionRet.Redraw;
            }
        }

        return ret;
    }

    /**
     * 描画処理
     * UIconManagerに登録されたIconを描画する
     * @param canvas
     * @param paint
     * @return trueなら描画継続
     */
    public void drawContent(Canvas canvas, Paint paint, PointF offset)
    {
        if (!isShow) return;

        List<UIcon> icons = getIcons();
        if (icons == null) return;

        // 背景を描画
        drawBG(canvas, paint);

        // ウィンドウの座標とスクロールの座標を求める
        PointF _offset = new PointF(pos.x - contentTop.x, pos.y - contentTop.y);
        Rect windowRect = new Rect((int)contentTop.x, (int)contentTop.y, (int)contentTop.x + size.width, (int)contentTop.y + size.height);

        // クリッピング領域を設定
        canvas.save();
        canvas.clipRect(rect);

        // 選択中のアイコンに枠を表示する
        if (mIconManager.getSelectedIcon() != null) {
            UDraw.drawRoundRectFill(canvas, paint,
                    new RectF(mIconManager.getSelectedIcon().getRectWithOffset
                    (_offset, 5)), 10.0f, Color.argb(160, 255, 0, 0), 0, 0);
        }
        for (UIcon icon : mIconManager.getIcons()) {
            if (icon == dragedIcon) continue;
            // 矩形範囲外なら描画しない
            if (URect.intersect(windowRect, icon.getRect())) {
                icon.draw(canvas, paint, _offset);

            } else {
            }
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
    public void setSize(int width, int height) {
        super.setSize(width, height);
        // アイコンの整列
        sortIcons(false);
    }

    /**
     * アイコンを整列する
     * Viewのサイズが確定した時点で呼び出す
     */
    public void sortIcons(boolean animate) {
        List<UIcon> icons = getIcons();
        if (icons == null) return;

        int maxSize = 0;

        int i=0;
        if (dir == WindowDir.Vertical) {
            int column = (clientSize.width - ICON_MARGIN) / (ICON_W + ICON_MARGIN);
            if (column <= 0) {
                return;
            }
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
            int column = ((int)clientSize.height - ICON_MARGIN) / (ICON_H + ICON_MARGIN);
            if (column <= 0) {
                return;
            }
            int margin = ((int)clientSize.height - ICON_H * column) / (column + 1);
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

        if (state == WindowState.icon_selecting) {
            if (isDropInBox) {
                nextState = WindowState.none;
            } else {
                nextState = WindowState.icon_selecting;
            }
        } else {
            nextState = WindowState.none;
        }

        setState(WindowState.icon_moving);

        // メニューバーに重ならないように下にマージンを設ける
        if (dir == WindowDir.Vertical) {
            setContentSize(size.width, maxSize + MARGIN_D, true);
            mScrollBarV.setPageLen(getHeight());
            contentTop.y = mScrollBarV.updateContent(contentSize.height);
        } else {
            setContentSize(maxSize + MARGIN_D, size.height, true);
            mScrollBarH.setPageLen(getWidth());
            contentTop.x = mScrollBarH.updateContent(contentSize.width);
        }

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
        isDragMove = false;

        List<UIcon> checkedIcons = mIconManager.getCheckedIcons();
        if (checkedIcons.size() > 0) {
            setState(WindowState.icon_selecting);
        }

        if (state == WindowState.none) {
            // ドラッグ中のアイコンが１つでもあればドラッグ開始
            for (UIcon icon : icons) {
                if (icon.isDraging) {
                    setDragedIcon(icon);
                    ret = true;
                    isDragMove = true;
                    break;
                }
            }

            if (ret) {
                setState(WindowState.drag);
                return true;
            }
        } else if (state == WindowState.icon_selecting) {
            // チェックしたアイコンをまとめてドラッグ
            PointF offset = getToWinPos();

            // チェックされたアイコンが最前面に表示されるように描画優先度をあげる
            for (UIcon icon : checkedIcons) {
                icon.isDraging = true;
                UDrawManager.getInstance().addWithNewPriority(icon, DrawPriority.DragIcon.p());
            }
            // チェックアイコンのどれかをタッチしていたらドラッグ開始
            for (UIcon icon : checkedIcons) {
                if (icon.getRect().contains((int) vt.touchX(offset.x), (int) vt.touchY(offset.y))) {
                    ret = true;
                    isDragMove = true;
                    break;
                }
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
        if (!isDragMove) return false;

        if (state == WindowState.drag) {
            if (dragedIcon == null) return false;
            // ドラッグ中のアイコンを移動する
            dragedIcon.move(vt.getMoveX(), vt.getMoveY());
        } else if (state == WindowState.icon_selecting){
            // チェックしたアイコンをまとめて移動する
            List<UIcon> icons = mIconManager.getCheckedIcons();
            if (icons != null) {
                for (UIcon icon : icons) {
                    icon.move(vt.getMoveX(), vt.getMoveY());
                }
            }
        } else {
            return false;
        }

        // 現在のドロップフラグをクリア
        mIconManager.setDropedIcon(null);

        for (UIconWindow window : windows.getWindows()) {
            // ドラッグ中のアイコンが別のアイコンの上にあるかをチェック
            Point dragPos = new Point((int) window.toWinX(vt.getX()), (int) window.toWinY(vt.getY()));

                UIconManager manager = window.getIconManager();
                if (manager == null) continue;

            // ドラッグ先のアイコンと重なっているアイコンを取得する
            UIcon dropIcon;
            if (state == WindowState.drag) {
                LinkedList<UIcon> exceptIcons = new LinkedList<>();
                exceptIcons.add(dragedIcon);
                dropIcon = manager.getOverlappedIcon(dragPos, exceptIcons);
            } else {
                List<UIcon> checkedIcons = mIconManager.getCheckedIcons();
                dropIcon = manager.getOverlappedIcon(dragPos, checkedIcons);
                // 箱以外のアイコンにドロップできない
                if (dropIcon != null &&
                        dropIcon.type != IconType.BOX)
                {
                    dropIcon = null;
                }
            }
            if (dropIcon != null) {
                isDone = true;
                mIconManager.setDropedIcon(dropIcon);
            }
        }

        return isDone;
    }


    /**
     * ドラッグ終了時の処理（通常時)
     * @param vt
     * @return trueならViewを再描画
     */
    private boolean dragEnd(ViewTouch vt) {
        if (state == WindowState.drag) {
            if (dragEndNormal(vt)) {
                return true;
            }
        } else {
            if (dragEndChecking(vt)) {
                return true;
            }
        }
        return false;
    }

    /**
     * ドラッグ終了時の処理（通常時)
     * @param vt
     * @return trueならViewを再描画
     */
    private boolean dragEndNormal(ViewTouch vt) {

        // 他のアイコンの上にドロップされたらドロップ処理を呼び出す
        if (dragedIcon == null) return false;
        boolean isDroped = false;

        mIconManager.setDropedIcon(null);

        List<UIcon> srcIcons = getIcons();
        for (UIconWindow window : windows.getWindows()) {
            // Windowの領域外ならスキップ
            if (!(window.rect.contains((int)vt.getX(),(int)vt.getY()))){
                continue;
            }

            List<UIcon> dstIcons = window.getIcons();
            UIcon dropedIcon = null;
            IconMovingType moving = IconMovingType.Insert;

            if (dstIcons == null) continue;

            // スクリーン座標系からWindow座標系に変換
            float winX = window.toWinX(vt.getX());
            float winY = window.toWinY(vt.getY());

            for (int i=0; i<dstIcons.size(); i++) {
                UIcon dropIcon = dstIcons.get(i);
                if (dropIcon == dragedIcon) continue;

                // ドラッグアイコンが画面外ならスキップ or break
                if (dir == WindowDir.Vertical) {
                    if (contentTop.y > dropIcon.getBottom()) {
                        continue;
                    } else if (contentTop.y + size.height < dropIcon.getY()){
                        // これ以降は画面外に表示されるアイコンなので処理を中止
                        break;
                    }
                } else {
                    if (contentTop.x > dropIcon.getRight()) {
                        continue;
                    } else if (contentTop.x + size.width < dropIcon.getX()){
                        break;
                    }
                }

                if (dropIcon.checkDrop(winX, winY)) {
                    switch (dropIcon.getType()) {
                        case CIRCLE:
                        case RECT:
                        case IMAGE:
                            // ドラッグ位置のアイコンと場所を交換する
                            dropedIcon = dropIcon;
                            moving = IconMovingType.Exchange;
                            isDroped = true;
                            break;
                        case BOX:
                            // 箱の中に挿入する　
                            if (dragedIcon.type != IconType.BOX) {
                                UIconBox box = (UIconBox) dropIcon;
                                if (box.getIcons() != null) {
                                    moveIconIntoBox(dragedIcon, dropIcon);
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
                } else {
                    // アイコンのマージン部分にドロップされたかのチェック
                    if (dir == WindowDir.Vertical) {
                        // 縦画面
                        if (dropIcon.getX() - ICON_MARGIN*2 <= winX &&
                                winX <= dropIcon.getX() + ICON_MARGIN &&
                                dropIcon.getY() <= winY &&
                                winY <= dropIcon.getY() + dropIcon.getHeight() )
                        {
                            // ドラッグ位置（アイコンの左側)にアイコンを挿入する
                            dropedIcon = dropIcon;
                            isDroped = true;
                            break;
                        } else if (dropIcon.getX() + (ICON_MARGIN + ICON_W) * 2 > size.width ) {
                            // 右端のアイコンは右側に挿入できる
                            if (winX > dropIcon.getRight() &&
                                    dropIcon.getY() <= winY &&
                                    winY <= dropIcon.getY() + dropIcon.getHeight() )
                            {
                                // 右側の場合は次のアイコンの次の位置に挿入
                                if (i < dstIcons.size() - 1) {
                                    dropIcon = dstIcons.get(i+1);
                                }
                                dropedIcon = dropIcon;
                                isDroped = true;
                                break;
                            }
                        }
                    } else {
                        // 横画面
                        if (dropIcon.getY() - ICON_MARGIN * 2 <= winY &&
                                winY <= dropIcon.getY() + ICON_MARGIN &&
                                dropIcon.getX() <= winX && winX <= dropIcon.getX() + dropIcon.getWidth())
                        {
                            dropedIcon = dropIcon;
                            isDroped = true;
                            break;
                        } else if (dropIcon.getY() + (ICON_MARGIN + ICON_H) * 2 > size.height ) {
                            // 下端のアイコンは下側に挿入できる
                            if (winY > dropIcon.getBottom() &&
                                    dropIcon.getX() <= winX &&
                                    winX <= dropIcon.getX() + dropIcon.getWidth() )
                            {
                                // 右側の場合は次のアイコンの次の位置に挿入
                                if (i < dstIcons.size() - 1) {
                                    dropIcon = dstIcons.get(i+1);
                                }
                                dropedIcon = dropIcon;
                                isDroped = true;
                                break;
                            }
                        }
                    }
                }
            }
            // 移動あり
            if (isDroped && dropedIcon != null) {
                if (moving == IconMovingType.Insert) {
                    insertIcons(dragedIcon, dropedIcon, true);
                } else {
                    changeIcons(dragedIcon, dropedIcon);
                }
            }

            // その他の場所にドロップされた場合
            if (!isDroped && dstIcons != null ) {
                boolean isMoved = false;
                if (dstIcons.size() > 0) {
                    // アイコンとアイコンの隙間のスペース


                    // 最後のアイコン以降のスペース
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
                    // ボックスタイプのアイコンをサブWindowに移動できない
                    if (dragedIcon.type == IconType.BOX && window == windows.getSubWindow()) {
                        continue;
                    }
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
                    dragedIcon.setPos(win1ToWin2X(dragedIcon.getX(), this, window), win1ToWin2Y(dragedIcon.getY(), this, window));
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

        mIconManager.setDropedIcon(null);

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

            UIcon dropIcon = null;
            for (UIcon icon : dstIcons) {
                if (checkedIcons.contains(icon) || icon.getType() != IconType.BOX) {
                    continue;
                }

                if (icon.getRect().contains((int) winX, (int) winY)) {
                    dropIcon = icon;
                    break;
                }
            }
            if (dropIcon != null) {
                UIconBox box = (UIconBox) dropIcon;
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
                    isDropInBox = true;
                }
            }
            // 再配置
            if (isDroped && srcIcons != dstIcons) {
                // 座標系変換(移動元Windowから移動先Window)

                for (UIcon icon : checkedIcons) {
                    icon.setPos(win1ToWin2X(icon.getX(), this, window), win1ToWin2Y(icon.getY(), this, window));
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
    public boolean touchEvent(ViewTouch vt, PointF offset) {
        if (!isShow) return false;
        if (state == WindowState.icon_moving) return false;

        if (super.touchEvent(vt, offset)) {
            return true;
        }
        boolean done = false;

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
                } else if (state != WindowState.icon_selecting) {
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
     * アイコンの移動が完了
     */
    private void endIconMoving() {
        setState(nextState);
        mIconManager.updateBlockRect();
        changeIconCheckingAll(false);
        setDragedIcon(null);
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
            icon1.setPos(icon1.getX() + this.pos.x - window2.pos.x,
                    icon1.getY() + this.pos.y - window2.pos.y);

            // アイコン2 UWindow -> アイコン1 UWindow
            icon2.setPos(icon2.getX() + window2.pos.x - this.pos.x,
                    icon2.getY() + window2.pos.y - this.pos.y);
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

        int index1 = icons1.indexOf(icon1);
        int index2 = icons2.indexOf(icon2);

        if (index1 == -1 || index2 == -1) return;

        // 挿入元と先の位置関係で追加と削除の順番が前後する
        if (index1 < index2) {
            icons2.add(index2, icon1);
            icons1.remove(icon1);
        } else {
            icons1.remove(icon1);
            icons2.add(index2, icon1);
        }

        // 再配置
        if (animate) {
            if (window1 != window2) {
                // 親の付け替え
                icon1.setParentWindow(window2);
                icon2.setParentWindow(window1);

                // ドロップアイコンの座標系を変換
                dragedIcon.setPos(icon1.getX() + window2.pos.x - window1.pos.x,
                        icon1.getY() + window2.pos.y - window1.pos.y);
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
        List<UIcon> icons2 = dropedIcon.getIcons();

        icons1.removeAll(checkedIcons);
        if (icons2 != null) {
            icons2.addAll(checkedIcons);

            window2.sortIcons(false);
            for (UIcon icon : checkedIcons) {
                icon.isChecking = false;
                icon.setParentWindow(window2);
            }
        }
        // 箱の中に入れた後のアイコン整列後にチェックを解除したいのでフラグを持っておく
        isDropInBox = true;
    }


    /**
     * アイコンの選択状態を変更する
     */
    private void changeIconChecking(List<UIcon> icons, boolean isChecking) {
        if (icons == null) return;

        for (UIcon icon : icons) {
            icon.isChecking = isChecking;
            if (!isChecking) {
                icon.isChecked = false;
            }
        }
    }

    /**
     * 全てのウィンドウのアイコンの選択状態を変更する
     * @param isChecking
     */
    private void changeIconCheckingAll(boolean isChecking) {
        for (UIconWindow window : windows.getWindows()) {
            List<UIcon> icons = window.getIcons();
            changeIconChecking(icons, isChecking);
        }
    }

    /**
     * 以下Drawableインターフェースのメソッド
     */
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
     * 移動が完了した時の処理
     */
    public void endMoving() {
        super.endMoving();

        if (isAppearance) {

        } else {
            isShow = false;
        }
        mScrollBarH.setShow(true);
        mScrollBarV.setShow(true);
    }


    public void startMoving() {
        super.startMoving();

        mScrollBarH.setShow(false);
        mScrollBarV.setShow(false);
    }

}
