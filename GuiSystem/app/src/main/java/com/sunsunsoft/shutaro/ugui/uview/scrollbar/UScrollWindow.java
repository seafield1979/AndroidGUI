package com.sunsunsoft.shutaro.ugui.uview.scrollbar;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import com.sunsunsoft.shutaro.ugui.TouchType;
import com.sunsunsoft.shutaro.ugui.ViewTouch;
import com.sunsunsoft.shutaro.ugui.uview.DoActionRet;
import com.sunsunsoft.shutaro.ugui.uview.window.*;

/**
 * Created by shutaro on 2016/12/09.
 *
 * client領域をスワイプでスクロールできるWindow
 */

public class UScrollWindow extends UWindow {
    /**
     * Enums
     */
    /**
     * Consts
     */

    /**
     * Member Variables
     */

    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    public UScrollWindow(UWindowCallbacks callbacks, int priority, float x, float y, int width, int
            height, int color, int topBarH, int frameW, int frameH)
    {
        super(callbacks, priority, x, y, width, height, color, topBarH, frameW, frameH);
    }

    /**
     * Methods
     */
    public DoActionRet doAction() {
        return DoActionRet.None;
    }

    public void drawContent(Canvas canvas, Paint paint, PointF offset) {

    }

    public boolean touchEvent(ViewTouch vt, PointF point) {
        if (super.touchEvent(vt, point)) {
            return true;
        }
        // スクロール処理
        boolean isDraw = false;
        if (vt.type == TouchType.Moving) {
            if (contentSize.width > clientSize.width) {
                if (vt.getMoveX() != 0) {
                    contentTop.x -= vt.getMoveX();
                    if (contentTop.x < 0) contentTop.x = 0;
                    if (contentTop.x + clientSize.width > contentSize.width) {
                        contentTop.x = contentSize.width - clientSize.width;
                    }
                    mScrollBarH.updateScroll((long)contentTop.x);
                    isDraw = true;

                }
            }
            if (contentSize.height > clientSize.height) {
                if (vt.getMoveY() != 0) {
                    contentTop.y -= vt.getMoveY();
                    if (contentTop.y < 0) contentTop.y = 0;
                    if (contentTop.y + clientSize.height > contentSize.height) {
                        contentTop.y = contentSize.height - clientSize.height;
                    }
                    mScrollBarV.updateScroll((long)contentTop.y);
                    isDraw = true;
                }
            }
        }
        return isDraw;
    }

    /**
     * Callbacks
     */
}
