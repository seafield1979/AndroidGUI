package com.sunsunsoft.shutaro.ugui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

/**
 * 開いたり閉じたりできるTextView
 *
 * タップで開閉を切り替えできる
 */

public class UTextViewOpenClose extends UTextView {

    /**
     * Consts
     */

    /**
     * Member Variables
     */
    private boolean isOpened;
    private Size baseSize = new Size();
    private Size openedSize = new Size();

    /**
     * Get/Set
     */
    public Size getSize() {
        if (isOpened) {
            return openedSize;
        } else {
            return baseSize;
        }
    }

    public void updateRect() {
        if (baseSize == null || openedSize == null) return;

        rect.left = (int)pos.x;
        rect.top = (int)pos.y;
        if (isOpened) {
            rect.right = (int)pos.x + openedSize.width;
            rect.bottom = (int)pos.y + openedSize.height;
        } else {
            rect.right = (int)pos.x + baseSize.width;
            rect.bottom = (int)pos.y + baseSize.height;
        }
    }

    public Rect getRect() {
        return rect;
    }

    public int getWidth() {
        return getSize().width;
    }
    public int getHeight() {
        return getSize().height;
    }

    /**
     * Constructor
     */


    public UTextViewOpenClose(String text, int textSize, int priority,
                              UDraw.UAlignment alignment, int canvasW,
                              float x, float y,
                              int width,
                              int color, int bgColor)
    {
        super(text, textSize, priority, alignment, canvasW, false, true, x, y, width, color,
                bgColor);

        Size _size = getTextSize(canvasW);
        baseSize = addBGPadding(new Size(width, textSize));

        int _width = (width > _size.width) ? width : _size.width;
        openedSize = addBGPadding(new Size(_width, _size.height));

        updateRect();
    }

    public static UTextViewOpenClose createInstance(String text, int textSize, int priority,
                                                    UDraw.UAlignment alignment, int canvasW,
                                                    float x, float y,
                                                    int width,
                                                    int color, int bgColor)
    {
        UTextViewOpenClose instance = new UTextViewOpenClose(
                text, textSize, priority, alignment, canvasW,
                x, y, width, color, bgColor);

        // テキストを描画した時のサイズを取得

        return instance;
    }


    /**
     * Methods
     */

    public void setText(String text) {
        this.text = text;

        // サイズを更新
        Size size = getTextSize(canvasW);

        openedSize = addBGPadding(new Size(size));
    }

    /**
     * 描画処理
     * @param canvas
     * @param paint
     * @param offset 独自の座標系を持つオブジェクトをスクリーン座標系に変換するためのオフセット値
     */
    void draw(Canvas canvas, Paint paint, PointF offset) {
        if (isOpened) {

            super.draw(canvas, paint, offset);
        } else {
            super.draw(canvas, paint, offset);
        }
//        Size _size = getSize();
//        UDraw.UAlignment _alignment = alignment;
//
//        if (isDrawBG) {
//            PointF bgPos = new PointF(_pos.x, _pos.y);
//            switch (alignment) {
//                case CenterX:
//                    bgPos.x -= size.width / 2;
//                    _pos.y += MARGIN_V;
//                    break;
//                case CenterY:
//                    bgPos.y -= size.height / 2;
//                    _pos.x += MARGIN_H;
//                    break;
//                case Center:
//                    bgPos.x -= size.width / 2;
//                    bgPos.y -= size.height / 2;
//                    break;
//                case None:
//                    _pos.x += MARGIN_H;
//                    _pos.y += MARGIN_V;
//                    break;
//            }
//            drawBG(canvas, paint, bgPos);
//
//            // BGの中央にテキストを表示したいため、aligmentを書き換える
//            switch (alignment) {
//                case CenterX:
//                    _alignment = UDraw.UAlignment.Center;
//                    break;
//                case None:
//                    _alignment = UDraw.UAlignment.CenterY;
//                    break;
//            }
//        }

//        if (text != null) {
//            if (isOpened) {
//            } else {
//                // １行だけ描画する
//                UDraw.drawTextOneLine(canvas, paint, text, UDraw.UAlignment.None, textSize,
//                        _pos.x + MARGIN_H,
//                        _pos.y + textSize + MARGIN_V,
//                        color);
//            }
//        }
    }

    /**
     * タッチ処理
     * @param vt
     * @return
     */
    public boolean touchEvent(ViewTouch vt) {
        return this.touchEvent(vt, null);
    }

    public boolean touchEvent(ViewTouch vt, PointF offset) {
        if (vt.type == TouchType.Touch) {
            if (offset == null) {
                offset = new PointF();
            }
            if (getRect().contains((int)vt.touchX(offset.x), (int)vt.touchY(offset.y))) {
                isOpened = !isOpened;
                multiLine = isOpened;
                updateSize();
                return true;
            }
        }
        return false;
    }

    /**
     * 背景色を描画する
     * @param canvas
     * @param paint
     */
//    protected void drawBG(Canvas canvas, Paint paint, PointF pos) {
//        RectF drawRect;
//        if (isOpened) {
//            drawRect = new RectF(pos.x, pos.y, pos.x + openedSize.width, pos.y + openedSize.height);
//        } else {
//            drawRect = new RectF(pos.x, pos.y, pos.x + baseSize.width, pos.y + baseSize.height);
//        }
//        UDraw.drawRoundRectFill(canvas, paint,
//                drawRect, 20, bgColor, 0, 0);
//    }
}
