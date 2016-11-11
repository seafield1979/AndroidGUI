package com.sunsunsoft.shutaro.ugui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
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
public class UIconManager {

    private View mParentView;
    private UIconWindow mParentWindow;
    private LinkedList<UIcon> icons;
    private UIconsBlockManager mBlockManager;

    // Get/Set
    public LinkedList<UIcon> getIcons() {
        return icons;
    }

    public void setIcons(LinkedList<UIcon> icons) {
        this.icons = icons;
    }

    public UIconWindow getParentWindow() {
        return mParentWindow;
    }

    public UIconsBlockManager getBlockManager() {
        return mBlockManager;
    }

    public static UIconManager createInstance(View parentView, UIconWindow parentWindow) {
        UIconManager instance = new UIconManager();
        instance.mParentView = parentView;
        instance.mParentWindow = parentWindow;
        instance.icons = new LinkedList<>();
        instance.mBlockManager = UIconsBlockManager.createInstance(instance.icons);
        return instance;
    }

    /**
     * 指定タイプのアイコンを追加
     * @param type
     * @param addPos
     * @return
     */
    public UIcon addIcon(IconType type, AddPos addPos) {

        UIcon icon = null;
        switch (type) {
            case RECT:
                icon = new UIconRect(mParentWindow);
                break;
            case CIRCLE:
                icon = new UIconCircle(mParentWindow);
                break;
            case IMAGE: {
                Bitmap bmp = BitmapFactory.decodeResource(mParentView.getResources(), R.drawable.hogeman);
                icon = new UIconBmp(mParentWindow, bmp);
                break;
            }
            case BOX:
                icon = new UIconBox(mParentView, mParentWindow);
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
    public boolean addIcon(UIcon icon) {
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
    public void removeIcon(UIcon icon) {
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
    public UIcon getOverlappedIcon(Point pos, UIcon exceptIcon) {
        return mBlockManager.getOverlapedIcon(pos, exceptIcon);
    }
}
