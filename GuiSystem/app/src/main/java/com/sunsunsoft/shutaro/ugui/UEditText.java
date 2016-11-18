package com.sunsunsoft.shutaro.ugui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import static android.view.View.VISIBLE;

/**
 * テキストを編集できる
 * 編集中のmiEditTextを表示する
 */

public class UEditText extends UDrawable {



    /**
     * Consts
     */
    public static final String TAG = "UEditText";

    /**
     * Member Variables
     */
    private Context mContext;
    private ViewGroup topLayout;
    private boolean isEditing;
    private EditText editText;


    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    public UEditText(Context context, EditText editText, int priority, float x, float y, int width,
                     int height) {
        super(priority, x, y, width, height);
        this.mContext = context;
        this.editText = editText;

        RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        layout.addRule(RelativeLayout.ALIGN_PARENT_TOP, R.id.view_container);
        layout.addRule(RelativeLayout.ALIGN_PARENT_LEFT, R.id.view_container);
        layout.setMargins((int)pos.x,(int)pos.y,0,0);
        editText.setLayoutParams(layout);
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
        // 編集中はEditTextを表示するので不要
        if (isEditing) {
            return;
        } else {
            Rect _rect = new Rect(rect);
            if (offset != null) {
                rect.left += offset.x;
                rect.top += offset.y;
                rect.right += offset.x;
                rect.bottom += offset.y;
            }
            UDraw.drawRectFill(canvas, paint, _rect, Color.BLUE);
        }
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
                    showEditView();
                    return true;
                }
            }
        }
        return false;
    }


    private void showEditView() {
        isEditing = true;
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.setVisibility(VISIBLE);


        InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context
                .INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }
}
