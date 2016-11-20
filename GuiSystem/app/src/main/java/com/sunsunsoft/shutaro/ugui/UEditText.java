package com.sunsunsoft.shutaro.ugui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;


interface UEditTextCallbacks {
    void showDialog(UEditText edit, String title, String text);
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
    private Size baseSize;

    /**
     * Get/Set
     */
    public void setText(String text) {
        this.text = text;

        // サイズを更新
        updateSize();
    }

    private void updateSize() {
        // サイズは元々のサイズ(size)とテキストを内包するサイズ(_size)で大きい方を使用する
        Size _size = getTextRect(canvasW);
        int _width = (_size.width > baseSize.width) ? _size.width : baseSize.width;
        int _height = (_size.height > baseSize.height) ? _size.height : baseSize.height;
        if (isDrawBG) {
            setSize(_width + MARGIN_H * 2, _height + MARGIN_V * 2);
        } else {
            setSize(_width, _height);
        }
    }

    /**
     * Constructor
     */
    public UEditText(View parentView, UEditTextCallbacks editTextCallbacks,
                     String text, int textSize, int priority, UAlignment alignment, int canvasW,
                     boolean isDrawBG,
                     float x, float y, int width,
                     int color, int bgColor)
    {
        super(priority, x, y, width, textSize + MARGIN_V * 2);
        this.parentView = parentView;
        this.alignment = alignment;
        this.editTextCallbacks = editTextCallbacks;
        this.baseSize = new Size(size);
        this.text = text;
        this.textSize = textSize;
        this.color = color;
        this.bgColor = bgColor;
        this.isDrawBG = isDrawBG;
        this.canvasW = canvasW;

        updateSize();
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
            editTextCallbacks.showDialog(this, "たいとる", text);

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
            setText(String.copyValueOf(retStr.toCharArray()));
        }
    }

    public void closeDialog() {
        showEditView(false);
        parentView.invalidate();
    }
}
