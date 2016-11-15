package com.sunsunsoft.shutaro.ugui;
import android.graphics.Color;
import android.graphics.PointF;



/**
 * Viewの中に表示できるWindow
 * 座標、サイズを持ち自由に配置が行える
 */
abstract public class UWindow extends Drawable implements AutoMovable {
    enum WindowType {
        Movable,        // ドラッグで移動可能(クリックで表示切り替え)
        Fixed,          // 固定位置に表示(ドラッグ移動不可、クリックで非表示にならない)
    }

    public static final String TAG = "UWindow";
    protected static final int SCROLL_BAR_W = 100;

    // メンバ変数
    protected boolean isShow = true;
    protected int bgColor;
    protected Size clientSize = new Size();       // ウィンドウの幅からスクロールバーのサイズを引いたサイズ

    // スクロール用
    protected Size contentSize = new Size();     // 領域全体のサイズ
    protected PointF contentTop = new PointF();  // 画面に表示する領域の左上の座標
    protected UScrollBar mScrollBarH;
    protected UScrollBar mScrollBarV;

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

    public PointF getToWinPos() {
        return new PointF(contentTop.x - pos.x, contentTop.y - pos.y);
    }

    // Windows座標系 -> Screen座標系
    public float toScreenX(float winX) {
        return winX - contentTop.x + pos.x;
    }

    public float toScreenY(float winY) {
        return winY - contentTop.y + pos.y;
    }

    public PointF getToScreenPos() {
        return new PointF(-contentTop.x + pos.x, -contentTop.y + pos.y);
    }

    // Window1の座標系から Window2の座標系に変換
    public float win1ToWin2X(float win1X, UWindow win1, UWindow win2) {
        return win1X + win1.pos.x - win1.contentTop.x - win2.pos.x + win2.contentTop.x;
    }

    public float win1ToWin2Y(float win1Y, UWindow win1, UWindow win2) {
        return win1Y + win1.pos.y - win1.contentTop.y - win2.pos.y + win2.contentTop.y;
    }

    public PointF getWin1ToWin2(UWindow win1, UWindow win2) {
        return new PointF(
                win1.pos.x - win1.contentTop.x - win2.pos.x + win2.contentTop.x,
                win1.pos.y - win1.contentTop.y - win2.pos.y + win2.contentTop.y
                );
    }

    /**
     * 外部からインスタンスを生成できないようにprivateでコンストラクタを定義する
     * インスタンス生成には createWindow を使うべし
     */
    protected UWindow(int priority, float x, float y, int width, int height, int color) {
        super(priority, x,y,width,height);
        this.bgColor = color;
        clientSize.width = size.width - SCROLL_BAR_W;
        clientSize.height = size.height - SCROLL_BAR_W;
        updateRect();

        // ScrollBar
        mScrollBarV = new UScrollBar(ScrollBarType.Right, ScrollBarInOut.In, this.pos, width, height, SCROLL_BAR_W, contentSize.height);
        mScrollBarV.setBgColor(Color.rgb(128, 128, 128));

        mScrollBarH = new UScrollBar(ScrollBarType.Bottom, ScrollBarInOut.In, this.pos, width, height, SCROLL_BAR_W, contentSize.height);
        mScrollBarH.setBgColor(Color.rgb(128, 128, 128));
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
     * 毎フレーム行う処理
     *
     * @return true:描画を行う
     */
    abstract public boolean doAction();

    /**
     * Viewをスクロールする処理
     * Viewの空きスペースをドラッグすると表示領域をスクロールすることができる
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
        // スクロールバーの表示を更新
        mScrollBarV.updateScroll(contentTop);

        return true;
    }

    /**
     * タッチイベント処理
     * @param vt
     * @return true:再描画
     */
    public boolean touchEvent(ViewTouch vt) {
        switch (vt.type) {
            case Touch:
                if (rect.contains((int)vt.touchX(), (int)vt.touchY())) {
                    UWindowList.getInstance().add(this);
                    return true;
                }
                break;
        }
        return false;
    }
}
