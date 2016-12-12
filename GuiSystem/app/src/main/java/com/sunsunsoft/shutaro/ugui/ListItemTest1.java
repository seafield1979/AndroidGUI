package com.sunsunsoft.shutaro.ugui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

/**
 * Created by shutaro on 2016/12/09.
 */

public class ListItemTest1 extends UListItem {
    /**
     * Constants
     */
    public static final int ITEM_H = 150;
    public static final int TEXT_SIZE = 50;

    // colors
    private static final int TEXT_COLOR = Color.WHITE;

    /**
     * Member variables
     */
    private String mText;

    /**
     * Constructor
     */
    public ListItemTest1(UListItemCallbacks listItemCallbacks,
                         String text,
                         float x, int width, int color)
    {
        super(listItemCallbacks, x, width, ITEM_H);
        this.color = color;
        mText = text;
    }

    /**
     * Methods
     */

    /**
     * 描画処理
     * @param canvas
     * @param paint
     * @param offset 独自の座標系を持つオブジェクトをスクリーン座標系に変換するためのオフセット値
     */
    public void draw(Canvas canvas, Paint paint, PointF offset) {
        PointF _pos = new PointF(pos.x, pos.y);
        if (offset != null) {
            _pos.x += offset.x;
            _pos.y += offset.y;
        }

        // BG
        Rect _rect = new Rect((int)_pos.x, (int)_pos.y,
                (int)_pos.x + size.width, (int)_pos.y + size.height);
        UDraw.drawRectFill(canvas, paint, _rect, color, 4, Color.BLACK);

        // text
        String text = mText;
        if (UDebug.drawListItemIndex) {
            text += "" + mIndex;
        }
        UDraw.drawText(canvas, text, UAlignment.Center, TEXT_SIZE,
                _pos.x + size.width / 2, _pos.y + size.height / 2, TEXT_COLOR );
    }

    /**
     * このtouchEventは使用しない
     */
    public boolean touchEvent(ViewTouch vt) {
        return false;
    }

    /**
     * 高さを返す
     */
    public int getHeight() {
        return ITEM_H;
    }
}
