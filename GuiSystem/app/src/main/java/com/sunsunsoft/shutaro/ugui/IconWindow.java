package com.sunsunsoft.shutaro.ugui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.View;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * アイコンのリストを表示するWindow
 */

public class IconWindow extends Window implements AutoMovable{
    enum viewState {
        none,
        drag,               // アイコンのドラッグ中
        icon_moving,        // アイコンの一変更後の移動中
    }

    // アイコンウィンドウのメイン、サブ
    // ホームはトップのウィンドウ
    // サブはトップウィンドウ以下のBoxアイコンを開いた時のウィンドウ
    enum WindowType {
        Home,
        Sub
    }

    public static final String TAG = "IconWindow";

    public static final int DRAW_PRIORITY = 100;
    public static final int DRAG_ICON_PRIORITY = 10;

    private static final int RECT_ICON_NUM = 10;
    private static final int CIRCLE_ICON_NUM = 10;
    private static final int BOX_ICON_NUM = 10;

    private static final int ICON_W = 200;
    private static final int ICON_H = 150;
    private static final int MARGIN_D = MenuBar.MENU_BAR_H;

    private static final int MOVING_TIME = 10;

    // ホームWindowを作成したかどうか
    // ホームWindowは１つしか存在できないため、最初のインスタンスはホーム、それ以降はサブになる
    private static boolean createdHome;

    // メンバ変数
    private WindowType type;
    private View mParentView;
    private IconCallbacks mIconCallbacks;
    private IconManager mIconManager;
    private DrawList mDrawList;

    // 他のIconWindow
    // ドラッグで他のWindowにアイコンを移動するのに使用する
    private IconWindow[] windows;

    // ドラッグ中のアイコン
    private Icon dragIcon;
    // ドロップ中のアイコン
    private Icon dropIcon;

    // アニメーション用
    private viewState state = viewState.none;

    // Iconのアニメーション中
    private boolean isAnimating;

    private int skipFrame = 3;  // n回に1回描画
    private int skipCount;

    // Get/Set
    public WindowType getType() {
        return type;
    }

    public void setType(WindowType type) {
        this.type = type;
    }
    public IconManager getIconManager() {
        return mIconManager;
    }
    public void setIconManager(IconManager mIconManager) {
        this.mIconManager = mIconManager;
    }

    public LinkedList<Icon> getIcons() {
        if (mIconManager == null) return null;
        return mIconManager.getIcons();
    }

    public void setWindows(IconWindow[] windows) {
        this.windows = windows;
    }

    public IconWindow[] getWindows() { return this.windows; }

    public void setParentView(View mParentView) {
        this.mParentView = mParentView;
    }

    public void setAnimating(boolean animating) {
        isAnimating = animating;
    }

    public IconCallbacks getIconCallbacks() {
        return mIconCallbacks;
    }

    public void setDragIcon(Icon dragIcon) {
        if (dragIcon == null) {
            if (this.dragIcon != null) {
                DrawManager.getInstance().removeDrawable(DRAG_ICON_PRIORITY, this.dragIcon);
            }
        }
        else {
            DrawManager.getInstance().addDrawable(DRAG_ICON_PRIORITY, dragIcon);
        }
        this.dragIcon = dragIcon;
    }

    private IconWindow(View parent, IconCallbacks iconCallbacks, float x, float y, int width, int height, int color) {
        super(x, y, width, height, color);
        this.mParentView = parent;
        this.mIconCallbacks = iconCallbacks;
    }
    /**
     * インスタンスを生成する
     * Homeタイプが２つできないように自動でHome、Subのタイプ分けがされる
     * @return
     */
    public static IconWindow createInstance(View parent, IconCallbacks iconCallbacks, float x, float y, int width, int height, int bgColor)
    {
        IconWindow instance = new IconWindow(parent, iconCallbacks, x, y, width, height, bgColor);
        if (!createdHome) {
            createdHome = true;
            instance.type = WindowType.Home;
            instance.mIconManager = IconManager.createInstance(parent, instance);
        } else {
            instance.type = WindowType.Sub;
        }
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

                Icon icon = mIconManager.addIcon(IconShape.RECT, AddPos.Tail);
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
                Icon icon = mIconManager.addIcon(IconShape.CIRCLE, AddPos.Tail);
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
                Icon icon = mIconManager.addIcon(IconShape.BOX, AddPos.Tail);
            }
        }
        // 描画はDrawManagerに任せるのでDrawManagerに登録
        mDrawList = DrawManager.getInstance().addDrawable(DRAW_PRIORITY, this);

        sortRects(false);
    }

    /**
     * 毎フレーム行う処理
     * @return true:再描画を行う(まだ処理が終わっていない)
     */
    public boolean doAction() {
        boolean redraw = false;
        boolean allFinished = true;
        List<Icon> icons = getIcons();

        if (isMoving) {
            // 移動処理
            if (isMoving) {
                if (!move()) {
                    isMoving = false;
                }
            }
        }
        if (isAnimating) {
            if (icons != null) {
                allFinished = true;
                for (Icon icon : icons) {
                    if (icon.animate()) {
                        allFinished = false;
                    }
                }
                if (allFinished) {
                    isAnimating = false;
                } else {
                    redraw = true;
                }
            }
        }

        // アイコンの移動
        if (icons != null) {
            if (state == viewState.icon_moving) {
                allFinished = true;
                for (Icon icon : icons) {
                    if (icon.move()) {
                        allFinished = false;
                    }
                }
                if (allFinished) {
                    state = viewState.none;
                    setDragIcon(null);
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
        List<Icon> icons = getIcons();
        if (icons == null) return;

        // ウィンドウの座標とスクロールの座標を求める
        PointF _offset = new PointF(pos.x - contentTop.x, pos.y - contentTop.y);
        Rect windowRect = new Rect((int)contentTop.x, (int)contentTop.y, (int)contentTop.x + size.width, (int)contentTop.y + size.height);

        // クリッピング領域を設定
        canvas.save();
        canvas.clipRect(rect);

        int clipCount = 0;
        for (Icon icon : mIconManager.getIcons()) {
            if (icon == dragIcon) continue;
            // 矩形範囲外なら描画しない
            if (MyRect.intersect(windowRect, icon.getRect())) {
                icon.draw(canvas, paint, _offset);
            } else {
                clipCount++;
            }
        }

        // スクロールバー
        mScrollBar.draw(canvas, paint);

        if (MyDebug.DRAW_ICON_BLOCK_RECT) {
            mIconManager.getBlockManager().draw(canvas, paint, getWin2ScreenPos());
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
        sortRects(false);

        // スクロールバー
        mScrollBar.updateSize(width, height);
        mScrollBar.updateContent(contentSize);
    }

    /**
     * アイコンを整列する
     * Viewのサイズが確定した時点で呼び出す
     */
    public void sortRects(boolean animate) {
        List<Icon> icons = getIcons();
        if (icons == null) return;

        int column = size.width / (ICON_W + 20);
        if (column <= 0) {
            return;
        }

        int maxHeight = 0;
        if (animate) {
            int i=0;
            for (Icon icon : icons) {
                int x = (i%column) * (ICON_W + 20);
                int y = (i/column) * (ICON_H + 20);
                int height = y + (ICON_H + 20);
                if ( height >= maxHeight ) {
                    maxHeight = height;
                }
                icon.startMove(x,y,MOVING_TIME);
                i++;
            }
            state = viewState.icon_moving;
        }
        else {
            int i=0;
            for (Icon icon : icons) {
                int x = (i%column) * (ICON_W + 20);
                int y = (i/column) * (ICON_H + 20);
                int height = y + (ICON_H + 20);
                if ( height >= maxHeight ) {
                    maxHeight = height;
                }
                icon.setPos(x, y);
                i++;
            }
            IconsPosFixed();
        }
        // メニューバーに重ならないように下にマージンを設ける
        setContentSize(size.width, maxHeight + MARGIN_D);

        mScrollBar.updateContent(contentSize);

        mParentView.invalidate();
    }

    /**
     * アイコンの座標が確定
     * アイコンの再配置完了時(アニメーションありの場合はそれが終わったタイミング)
     */
    private void IconsPosFixed() {
        mIconManager.updateBlockRect();
    }

    /**
     * アイコンをタッチする処理
     * @param vt
     * @return
     */
    private boolean touchIcons(ViewTouch vt) {
        List<Icon> icons = getIcons();
        if (icons == null) return false;

        for (Icon icon : icons) {
            if (icon.checkTouch(toWinX(vt.touchX()), toWinY(vt.touchY()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * アイコンをクリックする処理
     * @param vt
     * @return アイコンがクリックされたらtrue
     */
    private boolean clickIcons(ViewTouch vt) {
        List<Icon> icons = getIcons();
        if (icons == null) return false;

        // どのアイコンがクリックされたかを判定
        for (Icon icon : icons) {
            if (icon.checkClick(toWinX(vt.touchX()), toWinY(vt.touchY()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * アイコンをロングクリックする処理
     * @param vt
     */
    private void longClickIcons(ViewTouch vt) {

    }

    /**
     * アイコンをドラッグ開始
     * @param vt
     */
    private boolean dragStart(ViewTouch vt) {
        List<Icon> icons = getIcons();
        if (icons == null) return false;

        // タッチされたアイコンを選択する
        // 一番上のアイコンからタッチ判定したいのでリストを逆順（一番手前から）で参照する
        boolean ret = false;
        Collections.reverse(icons);
        for (Icon icon : icons) {
            // 座標判定
            if (icon.checkTouch(toWinX(vt.touchX()), toWinY(vt.touchY()))) {
                setDragIcon(icon);
                ret = true;
                break;
            }
        }

        Collections.reverse(icons);

        if (ret) {
            state = viewState.drag;
            return true;
        }
        return ret;
    }

    private boolean dragMove(ViewTouch vt) {
        // ドラッグ中のアイコンを移動
        boolean ret = false;
        if (dragIcon != null) {
            dragIcon.move((int)vt.moveX, (int)vt.moveY);
            ret = true;

            boolean isDone = false;

            // 現在のドロップフラグをクリア
            if (dropIcon != null) {
                dropIcon.isDroping = false;
            }

            for (IconWindow window : windows) {
                // ドラッグ中のアイコンが別のアイコンの上にあるかをチェック
                Point dragPos = new Point((int)window.toWinX(vt.getX()), (int) window.toWinY(vt.getY()));

                IconManager manager = window.getIconManager();
                if (manager == null) continue;

                Icon icon = manager.getOverlappedIcon(dragPos, dragIcon);
                if (icon != null) {
                    isDone = true;
                    dropIcon = icon;
                    dropIcon.isDroping = true;
                }
                if (isDone) break;
            }
        }

        skipCount++;
        if (skipCount >= skipFrame) {
            skipCount = 0;
        }
        return ret;
    }

    /**
     * ドラッグ終了時の処理
     * @param vt
     * @return trueならViewを再描画
     */
    private boolean dragEnd(ViewTouch vt) {
        // ドロップ処理
        // 他のアイコンの上にドロップされたらドロップ処理を呼び出す
        if (dragIcon == null) return false;
        boolean ret = false;

        boolean isDroped = false;

        if (dropIcon != null) {
            dropIcon.isDroping = false;
            dropIcon = null;
        }

        // 全てのWindowの全ての
        for (IconWindow window : windows) {
            // Windowの領域外ならスキップ
            if (!(window.rect.contains((int)vt.getX(),(int)vt.getY()))){
                continue;
            }

            LinkedList<Icon> srcIcons = getIcons();
            LinkedList<Icon> dstIcons = window.getIcons();

            if (dstIcons == null) continue;

            // スクリーン座標系からWindow座標系に変換
            float winX = window.toWinX(vt.getX());
            float winY = window.toWinY(vt.getY());

            for (Icon icon : dstIcons) {
                if (icon == dragIcon) continue;

                if (icon.checkDrop(winX, winY)) {
                    switch (icon.getShape()) {
                        case CIRCLE:
                            // ドラッグ位置のアイコンと場所を交換する
                            changeIcons(srcIcons, dstIcons, dragIcon, icon, window);
                            isDroped = true;
                            break;
                        case RECT:
                        case IMAGE:
                            // ドラッグ位置にアイコンを挿入する
                            insertIcons(srcIcons, dstIcons, dragIcon, icon, window, true);
                            isDroped = true;
                            break;
                        case BOX:
                            if (dragIcon.shape != IconShape.BOX) {
                                IconBox box = (IconBox) icon;
                                if (box.getIcons() != null) {
                                    moveIconIntoBox(srcIcons, box.getIcons(), dragIcon, icon);
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
                if (dstIcons.size() > 0) {
                    Icon lastIcon = dstIcons.getLast();
                    if ((lastIcon.getY() <= winY &&
                            winY <= lastIcon.getBottom() &&
                            lastIcon.getRight() <= winX) ||
                            (lastIcon.getBottom() <= winY)) {
                        // 最後のアイコンの後の空きスペースにドロップされた場合
                        // ドラッグ中のアイコンをリストの最後に移動
                        srcIcons.remove(dragIcon);
                        dstIcons.add(dragIcon);
                        isDroped = true;
                    }
                } else {
                    // ドラッグ中のアイコンをリストの最後に移動
                    srcIcons.remove(dragIcon);
                    dstIcons.add(dragIcon);
                }
            }
            // 再配置
            if (srcIcons != dstIcons) {
                // 座標系変換(移動元Windowから移動先Window)
                if (isDroped) {
                    dragIcon.setPos(win1ToWin2X(dragIcon.pos.x, this, window), win1ToWin2Y(dragIcon.pos.y, this, window));
                }
                window.sortRects(true);
            }
            if (isDroped) break;
        }
        this.sortRects(true);

        return true;
    }



    /**
     * タッチ処理
     * @param vt
     * @return trueならViewを再描画
     */
    public boolean touchEvent(ViewTouch vt) {
        if (!isShow) return false;
        if (state == viewState.icon_moving) return false;
        boolean done = false;

        // スクロールバーのタッチ処理
        if (mScrollBar.touchEvent(vt)) {
            contentTop.y = mScrollBar.getTopPos();
            return true;
        }

        // 範囲外なら除外
        if (!(rect.contains((int)vt.touchX(), (int)vt.touchY()))) {
            return false;
        }

        if (!done) {
            switch (vt.type) {
                case Touch:
                    if (touchIcons(vt)) {
                        done = true;
                    }
                    break;
                case Click:
                    if (clickIcons(vt)) {
                        done = true;
                    }
                    break;
                case LongClick:
                    longClickIcons(vt);
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
                    if (dragEnd(vt)) {
                        done = true;
                    }
                    break;
                case MoveCancel:
                    sortRects(false);
                    setDragIcon(null);
                    break;
            }
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
     * @param srcIcons
     * @param dstIcons
     * @param icon1
     * @param icon2
     * @param window
     */
    private void changeIcons(List<Icon> srcIcons, List<Icon> dstIcons, Icon
            icon1, Icon icon2, IconWindow window )
    {
        // アイコンの位置を交換
        // 並び順も重要！
        int index = dstIcons.indexOf(icon2);
        int index2 = srcIcons.indexOf(icon1);
        if (index == -1 || index2 == -1) return;

        srcIcons.remove(icon1);
        dstIcons.add(index, icon1);
        dstIcons.remove(icon2);
        srcIcons.add(index2, icon2);

        // 再配置
        if (srcIcons != dstIcons) {
            // ドロップアイコンの座標系を変換
            // アイコン1 Window -> アイコン2 Window
            dragIcon.setPos(dragIcon.pos.x + this.pos.x - window.pos.x,
                    dragIcon.pos.y + this.pos.y - window.pos.y);

            // アイコン2 Window -> アイコン1 Window
            icon2.setPos(icon2.pos.x + window.pos.x - this.pos.x,
                    icon2.pos.y + window.pos.y - this.pos.y);
            window.sortRects(true);
        }
        this.sortRects(true);
    }

    /**
     * アイコンを挿入する
     * @param srcIcons
     * @param dstIcons
     * @param srcIcon  挿入元のアイコン
     * @param dstIcon  挿入先のアイコン
     * @param window
     */
    private void insertIcons(List<Icon> srcIcons, List<Icon> dstIcons, Icon srcIcon, Icon dstIcon, IconWindow window, boolean animate)
    {
        int index = dstIcons.indexOf(dstIcon);
        if (index == -1) return;

        srcIcons.remove(srcIcon);
        dstIcons.add(index, srcIcon);

        // 再配置
        if (animate) {
            if (srcIcons != dstIcons) {
                // ドロップアイコンの座標系を変換
                dragIcon.setPos(srcIcon.pos.x + this.pos.x - window.pos.x,
                        srcIcon.pos.y + this.pos.y - window.pos.y);
                window.sortRects(animate);
            }
            sortRects(animate);
        }
    }

    /**
     * アイコンを移動する
     * アイコンを別のボックスタイプのアイコンにドロップした時に使用する
     * @param srcIcons ドラッグ元のIcons
     * @param dstIcons ドロップ先のIcons
     * @param srcIcon ドロップ元のIcon
     * @param dstIcon ドロップ先のIcon
     */
    private void moveIconIntoBox(List<Icon> srcIcons, List<Icon> dstIcons, Icon srcIcon, Icon dstIcon)
    {
        srcIcons.remove(srcIcon);
        dstIcons.add(srcIcon);

        if (dstIcon instanceof IconBox) {
            IconBox box = (IconBox)dstIcon;
            if (box.getSubWindow() != null) {
                box.getSubWindow().sortRects(false);
            }
        }

        sortRects(true);
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
        return false;
    }

    /**
     * アニメーション中かどうか
     * @return
     */
    public boolean isAnimating() {
        return false;
    }
}
