package com.sunsunsoft.shutaro.udrawsystem;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

/**
 * Created by shutaro on 2016/11/09.
 */

public class UWindowTest1 extends UWindow {
    public static final String TAG = "UWindowTest1";
    private static final int ICON_NUM = 50;
    private static final int DRAW_PRIORITY = 100;
    private static final int ICON_MARGIN_H = 30;
    private static final int ICON_MARGIN_V = 50;


    private IconManager iconManager = new IconManager();
    private boolean initFlag = false;
    private DrawList drawList;

    private UWindowTest1(float x, float y, int width, int height, int color) {
        super(x, y, width, height, color);
    }

    /**
     * Windowを生成する
     * インスタンス生成後に一度だけ呼ぶ
     *
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public static UWindowTest1 createWindow(float x, float y, int width, int height, int color) {
        UWindowTest1 instance = new UWindowTest1(x, y, width, height, color);
        instance.init();
        return instance;
    }

    public void init() {
        if (initFlag) return;
        initFlag = true;

        // アイコンを追加
        // アイコンが収まるcontentSizeも計算する
        float x = 0, y = 0;
        int maxH = 0;

        for (int i=0; i<ICON_NUM; i++) {
            Icon icon = iconManager.addIcon(IconType.Rect, x, y, MyColor.getRandom(), false);
            x += icon.size.width + ICON_MARGIN_H;
            if (x + icon.size.width + ICON_MARGIN_H > size.width) {
                x = 0;
                y += icon.size.height + ICON_MARGIN_V;
                maxH = (int)y + icon.size.height + ICON_MARGIN_V;
            }
        }
        contentSize.width = size.width;
        contentSize.height = maxH;

        // 描画はDrawManagerに任せるのでDrawManagerに登録
        drawList = DrawManager.getInstance().addDrawable(DRAW_PRIORITY, this);
        drawList.setClipRect(rect);
    }

    public boolean touchEvent(ViewTouch vt) {
        boolean done = false;
        switch(vt.type) {
            case Click:
                break;
            case Moving:
//                movePos(vt.moveX, vt.moveY);
                scrollView(vt);
                done = true;
                break;
        }
        return done;
    }

    /**
     * 描画処理
     * 配下のアイコンを全描画
     * @param canvas
     * @param paint
     */
    public void draw(Canvas canvas, Paint paint, PointF offset ) {
        // ウィンドウの座標とスクロールの座標を求める
        PointF _offset = new PointF(pos.x - contentTop.x, pos.y - contentTop.y);
        Rect windowRect = new Rect((int)contentTop.x, (int)contentTop.y, (int)contentTop.x + size.width, (int)contentTop.y + size.height);

        if (offset != null) {
            _offset.x += offset.x;
            _offset.y += offset.y;
        }

        MyLog.print(TAG, " - u:" + windowRect.top + " d:" + windowRect.bottom);

        int clipCount = 0;
        for (Icon icon : iconManager.getIcons()) {
            // クリッピング
            if (MyRect.intersect(windowRect, icon.getRect())) {
                icon.draw(canvas, paint, _offset);
            } else {
                clipCount++;
            }
        }
        MyLog.print(TAG, "clipCount:" + clipCount);
    }

    /**
     * 描画範囲の矩形を取得
     * @return
     */
    public Rect getRect() {
        return rect;
    }

    public void setDrawList(DrawList drawList) {
        this.drawList = drawList;
    }

    public DrawList getDrawList() {
        return drawList;
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