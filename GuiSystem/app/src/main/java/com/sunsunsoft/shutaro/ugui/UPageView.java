package com.sunsunsoft.shutaro.ugui;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by shutaro on 2016/12/05.
 *
 * UViewPageManager配下のページの基底クラス
 */

abstract public class UPageView {
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

    /**
     * Methods
     */
    abstract boolean draw(Canvas canvas, Paint paint);

    abstract boolean touchEvent(ViewTouch vt);

    abstract void initDrawables(int width, int height);

    abstract boolean onBackKeyDown();

    /**
     * Callbacks
     */
}
