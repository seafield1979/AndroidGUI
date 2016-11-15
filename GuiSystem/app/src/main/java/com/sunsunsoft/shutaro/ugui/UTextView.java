package com.sunsunsoft.shutaro.ugui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

/**
 * テキストを表示する
 */

public class UTextView extends Drawable{
    enum UAlignment {
        None,
        CenterX,
        CenterY,
        Center
    }

    /**
     * Member variables
     */
    protected String text;
    protected UAlignment alignment;
    protected int textSize;
    protected int bgColor;

    /**
     * Constructor
     */
    public UTextView(int priority, float x, float y) {
        super( priority, x, y, 100, 100);       // 100はダミー

    }
    public static UTextView createInstance(String text, int textSize, int priority, UAlignment
            alignment, float x, float y, int color, int bgColor)
    {
        UTextView instance = new UTextView(priority, x, y);
        instance.text = text;
        instance.alignment = alignment;
        instance.textSize = textSize;
        instance.color = color;
        instance.bgColor = bgColor;

        // テキストを描画した時のサイズを取得
        Rect bounds = new Rect();
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        paint.getTextBounds(text, 0, text.length(), bounds);
        instance.size.width = bounds.width();
        instance.size.height = bounds.height();

        return instance;
    }

    /**
     * 描画処理
     * @param canvas
     * @param paint
     * @param offset 独自の座標系を持つオブジェクトをスクリーン座標系に変換するためのオフセット値
     */
    void draw(Canvas canvas, Paint paint, PointF offset) {
        PointF _pos = new PointF(pos.x, pos.y);
        if (offset != null) {
            _pos.x = pos.x + offset.x;
            _pos.y = pos.y + offset.y;
        }
        switch (alignment) {
            case CenterX:
                _pos.x = _pos.x - size.width / 2;
                break;
            case CenterY:
                _pos.y = _pos.y + size.height / 2;
                break;
            case Center:
                _pos.x = _pos.x - size.width / 2;
                _pos.y = _pos.y + size.height / 2;
                break;
        }

//        paint.setColor(color);
//        paint.setTextSize(textSize);
//        paint.setFakeBoldText(true);
//        canvas.drawText(text, _pos.x, _pos.y, paint);
        TextPaint mTextPaint= new TextPaint();
        mTextPaint.setTextSize(textSize);
        mTextPaint.setColor(color);

        StaticLayout mTextLayout = new StaticLayout(text, mTextPaint,
                canvas.getWidth() * 4 / 5, Layout.Alignment.ALIGN_NORMAL,
                1.0f, 0.0f, false);

        canvas.save();
        canvas.translate(_pos.x, _pos.y);
        ///テキストの描画位置の指定
        mTextLayout.draw(canvas);
        canvas.restore();
    }

    /**
     * タッチ処理
     * @param vt
     * @return
     */
    public boolean touchEvent(ViewTouch vt) {
        return false;
    }
}
