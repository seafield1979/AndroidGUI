package com.sunsunsoft.shutaro.udrawsystem;

import android.graphics.PointF;

import java.util.LinkedList;

/**
 * アイコンを管理するクラス
 */

public class IconManager {
    DrawManager mDrawManager;

    private LinkedList<Icon> icons = new LinkedList<>();
    private PointF parentPos;
    private PointF parentScroll;

    // Get/Set
    public LinkedList<Icon> getIcons() {
        return icons;
    }

    public PointF getParentPos() {
        return parentPos;
    }

    public void setParentPos(PointF parentPos) {
        this.parentPos = parentPos;
    }

    public PointF getParentScroll() {
        return parentScroll;
    }

    public void setParentScroll(PointF parentScroll) {
        this.parentScroll = parentScroll;
    }

    // Constructor
    public IconManager() {
        mDrawManager = DrawManager.getInstance();
    }

    /**
     * アイコンを追加
     * @param type
     * @param x
     * @param y
     * @param color
     * @param isDraw  trueなら描画マネージャーに追加する
     */
    public Icon addIcon(IconType type, float x, float y, int color, boolean isDraw) {
        Icon icon = null;
        int priority = 100;

        switch (type) {
            case Rect:
                icon = new IconRect(x, y, color);
                priority = 10;
                break;
            case Circle:
                icon = new IconCircle(x, y, color);
                priority = 9;
                break;
        }
        if (icon != null) {
            icons.add(icon);
        }
        if (isDraw) {
            mDrawManager.addDrawable(priority, icon);
        }
        return icon;
    }

    /**
     * タッチ処理1
     * @param vt
     * @return trueならViewを再描画
     */
    public boolean touchEvent(ViewTouch vt) {
        boolean done = false;

        switch (vt.type) {
            case Touch:
                break;
            case Click:
            {
            Icon icon = checkClick((int)vt.touchX(), (int)vt.touchY());
                if (icon != null) {
                    mDrawManager.setPriority(icon, 1);
                    icon.startAnim();
                    done = true;
                }
            }
                break;
            case LongClick:
                break;
            case Moving:
                break;
            case MoveEnd:
                break;
            case MoveCancel:
                break;
        }
        return done;
    }

    /**
     * クリックしたアイコンを取得する
     * @param clickX
     * @param clickY
     * @return
     */
    private Icon checkClick(int clickX, int clickY) {
        for (Icon icon : icons) {
            if (icon.rect.contains(clickX, clickY)) {
                return icon;
            }
        }
        return null;
    }
}
