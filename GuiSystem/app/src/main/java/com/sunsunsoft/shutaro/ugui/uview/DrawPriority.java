package com.sunsunsoft.shutaro.ugui.uview;

/**
 * Created by shutaro on 2017/06/19.
 *
 * 描画優先度
 */
public enum DrawPriority {
    Dialog(5),
    DragIcon(11),
    IconWindow(100),
    ;

    private final int priority;

    DrawPriority(final int priority) {
        this.priority = priority;
    }

    public int p() {
        return this.priority;
    }
}
