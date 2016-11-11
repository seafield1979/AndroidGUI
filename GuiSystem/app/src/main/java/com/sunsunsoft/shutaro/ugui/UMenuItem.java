package com.sunsunsoft.shutaro.ugui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

// メニューをタッチした時に返されるID
enum MenuItemId {
    AddTop,
    AddCard,
    AddBook,
    AddBox,
    SortTop,
    Sort1,
    Sort2,
    Sort3,
    ListTypeTop,
    ListType1,
    ListType2,
    ListType3,
    DebugTop,
    Debug1,
    Debug2,
    Debug3
};

/**
 * メニューに表示する項目
 * アイコンを表示してタップされたらIDを返すぐらいの機能しか持たない
 */
abstract public class UMenuItem extends Drawable implements Animatable{
    public static final int DRAW_PRIORITY = 200;
    public static final int ITEM_W = 120;
    public static final int ITEM_H = 120;
    public static final int ANIME_FRAME = 15;

    protected UMenuBar parent;
    protected UMenuItemCallbacks mCallbacks;
    protected MenuItemId id;

    // アイコン用画像
    protected Bitmap icon;
    protected int animeColor;

    // Get/Set
    public void setCallbacks(UMenuItemCallbacks callbacks){
        mCallbacks = callbacks;
    }

    public UMenuItem(UMenuBar parent, MenuItemId id, Bitmap icon) {
        super(DRAW_PRIORITY, 0,0,0,0);
        this.parent = parent;
        this.id = id;
        this.icon = icon;
    }

    public void draw(Canvas canvas, Paint paint, PointF parentPos) {
        // スタイル(内部を塗りつぶし)
        paint.setStyle(Paint.Style.FILL);
        // 色
        paint.setColor(0);

        PointF drawPos = new PointF();
        drawPos.x = pos.x + parentPos.x;
        drawPos.y = pos.y + parentPos.y;

        if (icon != null) {
            // アニメーション処理
            // フラッシュする
            if (isAnimating) {
                int alpha = getAnimeAlpha();
                paint.setColor((alpha << 24) | animeColor);
            } else {
                paint.setColor(0xff000000);
            }

            // 領域の幅に合わせて伸縮
            canvas.drawBitmap(icon, new Rect(0,0,icon.getWidth(), icon.getHeight()),
                    new Rect((int)drawPos.x, (int)drawPos.y, (int)drawPos.x + ITEM_W,(int)drawPos.y + ITEM_H),
                    paint);
        }
    }

    /**
     * アニメーション開始
     */
    public void startAnim() {
        parent.setAnimating(true);
        isAnimating = true;
        animeFrame = 0;
        animeFrameMax = ANIME_FRAME;
        animeColor = Color.argb(0,255,255,255);
    }

    /**
     * アニメーション処理
     * といいつつフレームのカウンタを増やしているだけ
     * @return true:アニメーション中
     */
    public boolean animate() {
        if (!isAnimating) return false;
        if (animeFrame >= animeFrameMax) {
            isAnimating = false;
            return false;
        }

        animeFrame++;
        return true;
    }

    /**
     * クリックをチェック
     * @param vt
     * @param clickX
     * @param clickY
     */
    abstract public boolean checkClick(ViewTouch vt, float clickX, float clickY);
}
