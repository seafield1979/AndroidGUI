package com.sunsunsoft.shutaro.udrawsystem;

import android.graphics.PointF;
import android.graphics.Rect;

/**
 * Created by shutaro on 2016/11/09.
 */

abstract public class UWindow implements Drawable{

    protected static final int SCROLL_BAR_W = 100;

    // メンバ変数
    protected boolean isShow = true;
    protected PointF pos = new PointF();
    protected Size size = new Size();
    protected Rect rect = new Rect();       // Windowの移動に連動して変化する
    protected int bgColor;

    // Window移動用
    protected boolean isMoving;
    protected int movingFrame;
    protected int movingFrameMax;
    protected PointF srcPos = new PointF();
    protected PointF dstPos = new PointF();

    // スクロール用
    protected Size contentSize = new Size();     // 領域全体のサイズ
    protected PointF contentTop = new PointF();  // 画面に表示する領域の左上の座標

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public void setPos(float x, float y, boolean update) {
        pos.x = x;
        pos.y = y;
        if (update) {
            updateRect();
        }
    }

    protected void updateRect() {
        rect.left = (int)pos.x;
        rect.right = (int)pos.x + size.width;
        rect.top = (int)pos.y;
        rect.bottom = (int)pos.y + size.height;
    }

    protected void setSize(int width, int height) {
        size.width = width;
        size.height = height;
        MyLog.print("Window", "size:" + size.width + " " + size.height);
    }

    public PointF getContentTop() {
        return contentTop;
    }

    public void setContentTop(PointF contentTop) {
        this.contentTop = contentTop;
    }

    public void setContentTop(float x, float y) {
        contentTop.x = x;
        contentTop.y = y;
    }

    // 座標系を変換する
    // 座標系は以下の３つある
    // 1.Screen座標系  画面上の左上原点
    // 2.Window座標系  ウィンドウ左上原点 + スクロールして表示されている左上が原点

    // Screen座標系 -> Window座標系
    public float toWinX(float screenX) {
        return screenX + contentTop.x - pos.x;
    }

    public float toWinY(float screenY) {
        return screenY + contentTop.y - pos.y;
    }

    // Windows座標系 -> Screen座標系
    public float toScreenX(float winX) {
        return winX - contentTop.x + pos.x;
    }

    public float toScreenY(float winY) {
        return winY - contentTop.y + pos.y;
    }

    // Window2座標系 -> Screen座標系に変換するための値
    // Window内のオブジェクトを描画する際にこの値を加算する
    public PointF getWin2ScreenPos() {
        return new PointF(pos.x - contentTop.x, pos.y - contentTop.y);
    }

    /**
     * 外部からインスタンスを生成できないようにprivateでコンストラクタを定義する
     * インスタンス生成には createWindow を使うべし
     */
    protected UWindow(float x, float y, int width, int height, int color) {
        pos.x = x;
        pos.y = y;
        size.width = width;
        size.height = height;
        this.bgColor = color;
        updateRect();
    }

    public void setContentSize(int width, int height) {
        contentSize.width = width;
        contentSize.height = height;
    }

    /**
     * Windowのサイズを更新する
     * サイズ変更に合わせて中のアイコンを再配置する
     * @param width
     * @param height
     */
    public void updateSize(int width, int height) {
        setSize(width, height);
        updateRect();

        // スクロールをクリア
        setContentTop(0, 0);
    }

    /**
     * タッチ処理
     * @param vt
     * @return trueならViewを再描画
     */
    abstract public boolean touchEvent(ViewTouch vt);

    /**
     * Viewをスクロールする処理
     * sizeよりcontentSizeが大きい場合にのみスクロール可能
     * @param vt
     * @return
     */
    protected boolean scrollView(ViewTouch vt) {
        if (vt.type != TouchType.Moving) return false;

        // タッチの移動とスクロール方向は逆
        float moveX = vt.moveX * (-1);
        float moveY = vt.moveY * (-1);

        // 横
        if (size.width < contentSize.width) {
            contentTop.x += moveX;
            if (contentTop.x < 0) {
                contentTop.x = 0;
            } else if (contentTop.x + size.width > contentSize.width) {
                contentTop.x = contentSize.width - size.width;
            }
        }

        // 縦
        if (size.height < contentSize.height) {
            contentTop.y += moveY;
            if (contentTop.y < 0) {
                contentTop.y = 0;
            } else if (contentTop.y + size.height > contentSize.height) {
                contentTop.y = contentSize.height - size.height;
            }
        }

        return true;
    }

    /**
     * 指定の移動量でウィンドウを動かす
     * @param moveX
     * @param moveY
     */
    public void movePos(float moveX, float moveY) {
        pos.x += moveX;
        pos.y += moveY;
        updateRect();
    }

    /**
     * 自動移動開始
     * @param dstX  目的位置x
     * @param dstY  目的位置y
     * @param frame  移動にかかるフレーム数
     */
    public void startMove(float dstX, float dstY, int frame) {
        if (pos.x == dstX && pos.y == dstY) {
            return;
        }
        srcPos.x = pos.x;
        srcPos.y = pos.y;
        dstPos.x = dstX;
        dstPos.y = dstY;
        movingFrame = 0;
        movingFrameMax = frame;
        isMoving = true;
    }

    /**
     * 移動
     * 移動開始位置、終了位置、経過フレームから現在位置を計算する
     * @return 移動完了したらtrue
     */
    public boolean move() {
        if (!isMoving) return true;

        boolean ret = false;

        float ratio = (float)movingFrame / (float)movingFrameMax;
        pos.x = srcPos.x + ((dstPos.x - srcPos.x) * ratio);
        pos.y = srcPos.y + ((dstPos.y - srcPos.y) * ratio);

        movingFrame++;
        if (movingFrame >= movingFrameMax) {
            isMoving = false;
            pos.x = dstPos.x;
            pos.y = dstPos.y;

            ret = true;
        }
        updateRect();
        return ret;
    }
}
