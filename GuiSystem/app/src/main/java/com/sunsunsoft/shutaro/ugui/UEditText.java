package com.sunsunsoft.shutaro.ugui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;


interface UEditTextCallbacks {
    void showDialog(UEditText edit, boolean isShow);
}

/**
 * テキストを編集できる
 * 編集中のmiEditTextを表示する
 */
public class UEditText extends UTextView {
    /**
     * Consts
     */
    public static final String TAG = "UEditText";

    public static final int DEFAULT_H = 70;

    /**
     * Member Variables
     */
    private View parentView;
    private UEditTextCallbacks editTextCallbacks;
    private boolean isEditing;

    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    public UEditText(View parentView, UEditTextCallbacks editTextCallbacks,
                     int textSize, int priority, UAlignment alignment, int canvasW,
                     boolean isDrawBG,
                     float x, float y, int width,
                     int color, int bgColor)
    {
        super(priority, x, y, width, textSize + MARGIN_V * 2);
        this.parentView = parentView;
        this.alignment = alignment;
        this.editTextCallbacks = editTextCallbacks;
        this.textSize = textSize;
        this.color = color;
        this.bgColor = bgColor;
        this.isDrawBG = isDrawBG;
        this.canvasW = canvasW;

        // サイズを更新
        // サイズは元々のサイズ(size)とテキストを内包するサイズ(_size)で大きい方を使用する

    }

    /**
     * Methods
     */

    /**
     * UDrawable
     */
    /**
     * 描画処理
     * @param canvas
     * @param paint
     * @param offset 独自の座標系を持つオブジェクトをスクリーン座標系に変換するためのオフセット値
     */
    void draw(Canvas canvas, Paint paint, PointF offset) {
        super.draw(canvas, paint, offset);
    }
//    void draw(Canvas canvas, Paint paint, PointF offset) {
//        // 編集中はEditTextを表示するので不要
//        if (isEditing) {
//            return;
//        } else {
//            Rect _rect = new Rect(rect);
//            if (offset != null) {
//                rect.left += offset.x;
//                rect.top += offset.y;
//                rect.right += offset.x;
//                rect.bottom += offset.y;
//            }
//
//            Size _size = UDraw.drawText(canvas, paint, text, UDraw.UAlignment.None, 50, pos.x,
//                    pos.y,
//                    textColor);
//            if (_size != null) {
//                size.width = _size.width;
//                size.height = _size.height;
//                updateRect();
//            }
//            if (color != 0) {
//                UDraw.drawRectFill(canvas, paint, _rect, color);
//            }
//        }
//    }

    /**
     * タッチ処理
     * @param vt
     * @return
     */
    public boolean touchEvent(ViewTouch vt) {
        return touchEvent(vt, null);
    }

    public boolean touchEvent(ViewTouch vt, PointF offset) {
        if (offset == null) {
            offset = new PointF();
        }
        if (!isEditing) {
            if (vt.type == TouchType.Touch) {
                if (rect.contains((int)vt.touchX(offset.x), (int)vt.touchY(offset.y))) {
                    showEditView(true);
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 表示状態を切り替える
     * @param isShow
     */
    public void showEditView(boolean isShow) {
        if (isShow) {
            isEditing = true;
            editTextCallbacks.showDialog(this, true);

        } else {
            isEditing = false;
        }
    }


    /**
     * ダイアログで設定された値を渡してもらう
     * @param args
     */
    public void setEditDialogValues(Bundle args) {
        if (args != null) {
            String retStr = args.getString(UEditDialogFragment.KEY_RET);
            // 末尾の改行を除去
            retStr = retStr.trim();
            text = String.copyValueOf(retStr.toCharArray());
        }
    }

    public void closeDialog() {
        showEditView(false);
        parentView.invalidate();
    }
}
