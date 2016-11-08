package com.sunsunsoft.shutaro.udrawsystem;

import java.util.LinkedList;

/**
 * アイコンを管理するクラス
 */

public class IconManager {
    DrawManager mDrawManager;

    LinkedList<Icon> icons = new LinkedList<>();

    public IconManager(DrawManager drawManager) {
        mDrawManager = drawManager;
    }

    public void addIcon(IconType type, float x, float y, int color) {
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
        mDrawManager.add(priority, icon);
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
