package com.sunsunsoft.shutaro.ugui.uview;

import android.graphics.Bitmap;
import android.view.View;

import com.sunsunsoft.shutaro.ugui.R;

/**
 * Created by shutaro on 2016/12/05.
 */

public class MenuBarIconWindow extends UMenuBar {
    /**
     * Enums
     */

    // メニューをタッチした時に返されるID
    public enum MenuItemId {
        AddTop,
        AddCard,
        AddCard1,
        AddCard2,
        AddCard3,
        AddBook,
        AddBox,
        SortTop,
        Sort1,
        Sort2,
        Sort3,
        ListTypeTop,
        ListType1,
        ListType2,
        ListType3,
        DebugTop,
        Debug1,
        Debug2,
        Debug3
        ;
        public static MenuItemId toEnum(int value) {
            if (value >= values().length) return AddTop;
            return values()[value];
        }
    }

    /**
     * Constructor
     */
    public MenuBarIconWindow(View parentView, UMenuItemCallbacks callbackClass, int parentW, int parentH, int bgColor) {
        super(callbackClass, parentW, parentH, bgColor);
    }

    /**
     * メニューバーを生成する
     * @param parentView
     * @param callbackClass
     * @param parentW     親Viewのwidth
     * @param parentH    親Viewのheight
     * @param bgColor
     * @return
     */
    public static MenuBarIconWindow createInstance(View parentView, UMenuItemCallbacks callbackClass, int parentW, int parentH, int bgColor)
    {
        MenuBarIconWindow instance = new MenuBarIconWindow(parentView, callbackClass, parentW, parentH, bgColor);
        instance.initMenuBar();
        return instance;
    }

    /**
     * Methods
     */
    protected void initMenuBar() {
        UMenuItem item;
        UMenuItem item2;

        // Add
        Bitmap image = UResourceManager.getBitmapById(R.drawable.hogeman);
        item = addTopMenuItem(MenuItemId.AddTop.ordinal(), image);
        item2 = addMenuItem(item, MenuItemId.AddCard.ordinal(), image);
        addMenuItem(item2, MenuItemId.AddCard1.ordinal(), image);
        addMenuItem(item2, MenuItemId.AddCard2.ordinal(), image);
        addMenuItem(item2, MenuItemId.AddCard3.ordinal(), image);

        addMenuItem(item, MenuItemId.AddBook.ordinal(), image);
        addMenuItem(item, MenuItemId.AddBox.ordinal(), image);

        // Sort
        item = addTopMenuItem(MenuItemId.SortTop.ordinal(), image);
        addMenuItem(item, MenuItemId.Sort1.ordinal(), image);
        addMenuItem(item, MenuItemId.Sort2.ordinal(), image);
        addMenuItem(item, MenuItemId.Sort3.ordinal(), image);
        // ListType
        item = addTopMenuItem(MenuItemId.ListTypeTop.ordinal(), image);
        addMenuItem(item, MenuItemId.ListType1.ordinal(), image);
        addMenuItem(item, MenuItemId.ListType2.ordinal(), image);
        addMenuItem(item, MenuItemId.ListType3.ordinal(), image);
        // Debug
        Bitmap image2 = UResourceManager.getBitmapById(R.drawable.debug);
        item = addTopMenuItem(MenuItemId.DebugTop.ordinal(), image2);
        addMenuItem(item, MenuItemId.Debug1.ordinal(), image2);
        addMenuItem(item, MenuItemId.Debug2.ordinal(), image2);
        addMenuItem(item, MenuItemId.Debug3.ordinal(), image2);

        mDrawList = UDrawManager.getInstance().addDrawable(this);
        updateBGSize();
    }
}
