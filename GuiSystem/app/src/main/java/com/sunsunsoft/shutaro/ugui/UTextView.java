package com.sunsunsoft.shutaro.ugui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

/**
 * テキストを表示する
 */

public class UTextView extends UDrawable {
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
            alignment, int canvasW, float x, float y, int color, int bgColor)
    {
        UTextView instance = new UTextView(priority, x, y);
        instance.text = text;
        instance.alignment = alignment;
        instance.textSize = textSize;
        instance.color = color;
        instance.bgColor = bgColor;

        // テキストを描画した時のサイズを取得

        Size size = instance.getTextRect(canvasW);
        instance.setSize(size.width, size.height);

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
                _pos.y = _pos.y - size.height / 2;
                break;
            case Center:
                _pos.x = _pos.x - size.width / 2;
                _pos.y = _pos.y - size.height / 2;
                break;
        }


        // 改行ができるようにTextPaintとStaticLayoutを使用する
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(textSize);
        textPaint.setColor(color);

        StaticLayout mTextLayout = new StaticLayout(text, textPaint,
                canvas.getWidth() * 4 / 5, Layout.Alignment.ALIGN_NORMAL,
                1.0f, 0.0f, false);

        canvas.save();
        canvas.translate(_pos.x, _pos.y);

        drawBG(canvas, textPaint);

        ///テキストの描画位置の指定
        textPaint.setColor(color);
        mTextLayout.draw(canvas);
        canvas.restore();

    }

    /**
     * 背景色を描画する
     * @param canvas
     * @param paint
     */
    private void drawBG(Canvas canvas, TextPaint paint) {
        paint.setColor(bgColor);
        canvas.drawRect(0, 0, size.width, size.height, paint);
    }

    /**
     * テキストのサイズを取得する（マルチライン対応）
     * @param canvasW
     * @return
     */
    public Size getTextRect(int canvasW) {
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(textSize);
        StaticLayout textLayout = new StaticLayout(text, textPaint,
                canvasW, Layout.Alignment.ALIGN_NORMAL,
                1.0f, 0.0f, false);

        int height = textLayout.getHeight();
        int maxWidth = 0;
        int _width;

        // 各行の最大の幅を計算する
        for (int i = 0; i < textLayout.getLineCount(); i++) {
            _width = (int)textLayout.getLineWidth(i);
            if (_width > maxWidth) {
                maxWidth = _width;
            }
        }

        return new Size(maxWidth, height);
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
