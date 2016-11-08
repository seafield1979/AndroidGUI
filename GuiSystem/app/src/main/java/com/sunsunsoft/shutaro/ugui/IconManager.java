package com.sunsunsoft.shutaro.ugui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.View;

import java.util.LinkedList;

// アイコンの挿入位置
enum AddPos {
    Top,
    Tail
}

/**
 * IconWindowに表示するアイコンを管理するクラス
 *
 * Rect判定の高速化のためにいくつかのアイコンをまとめたブロックのRectを作成し、個々のアイコンのRect判定前に
 * ブロックのRectと判定を行う
 */
public class IconManager {

    private View mParentView;
    private IconWindow mParentWindow;
    private LinkedList<IconBase> icons;
    private IconsBlockManager mBlockManager;

    // Get/Set
    public LinkedList<IconBase> getIcons() {
        return icons;
    }

    public void setIcons(LinkedList<IconBase> icons) {
        this.icons = icons;
    }

    public IconWindow getParentWindow() {
        return mParentWindow;
    }

    public IconsBlockManager getBlockManager() {
        return mBlockManager;
    }

    public static IconManager createInstance(View parentView, IconWindow parentWindow) {
        IconManager instance = new IconManager();
        instance.mParentView = parentView;
        instance.mParentWindow = parentWindow;
        instance.icons = new LinkedList<>();
        instance.mBlockManager = IconsBlockManager.createInstance(instance.icons);
        return instance;
    }

    /**
     * 指定タイプのアイコンを追加
     * @param type
     * @param addPos
     * @return
     */
    public IconBase addIcon(IconShape type, AddPos addPos) {

        IconBase icon = null;
        switch (type) {
            case RECT:
                icon = new IconRect(mParentWindow);
                break;
            case CIRCLE:
                icon = new IconCircle(mParentWindow);
                break;
            case IMAGE: {
                Bitmap bmp = BitmapFactory.decodeResource(mParentView.getResources(), R.drawable.hogeman);
                icon = new IconBmp(mParentWindow, bmp);
                break;
            }
            case BOX:
                icon = new IconBox(mParentView, mParentWindow);
                break;
        }
        if (icon == null) return null;

        if (addPos == AddPos.Top) {
            icons.push(icon);
        } else {
            icons.add(icon);
        }

        return icon;
    }

    /**
     * すでに作成済みのアイコンを追加
     * ※べつのWindowにアイコンを移動するのに使用する
     * @param icon
     * @return
     */
    public boolean addIcon(IconBase icon) {
        // すでに追加されている場合は追加しない
        if (!icons.contains(icon)) {
            icons.add(icon);
            return true;
        }
        return false;
    }

    /**
     * アイコンを削除
     * @param icon
     */
    public void removeIcon(IconBase icon) {
        icons.remove(icon);
    }


    /**
     * アイコンを内包するRectを求める
     * アイコンの座標確定時に呼ぶ
     */
    public void updateBlockRect() {
        mBlockManager.update();
    }

    /**
     * 指定座標下にあるアイコンを取得する
     * @param pos
     * @param exceptIcon
     * @return
     */
    public IconBase getOverlappedIcon(Point pos, IconBase exceptIcon) {
        return mBlockManager.getOverlapedIcon(pos, exceptIcon);
    }
}
