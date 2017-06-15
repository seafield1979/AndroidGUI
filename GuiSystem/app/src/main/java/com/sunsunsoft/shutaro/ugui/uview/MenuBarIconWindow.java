package com.sunsunsoft.shutaro.ugui.uview;

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
        super(parentView, callbackClass, parentW, parentH, bgColor);
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
        item = addTopMenuItem(MenuItemId.AddTop.ordinal(), R.drawable.hogeman);
        item2 = addMenuItem(item, MenuItemId.AddCard.ordinal(), R.drawable.hogeman);
        addMenuItem(item2, MenuItemId.AddCard1.ordinal(), R.drawable.hogeman);
        addMenuItem(item2, MenuItemId.AddCard2.ordinal(), R.drawable.hogeman);
        addMenuItem(item2, MenuItemId.AddCard3.ordinal(), R.drawable.hogeman);

        addMenuItem(item, MenuItemId.AddBook.ordinal(), R.drawable.hogeman);
        addMenuItem(item, MenuItemId.AddBox.ordinal(), R.drawable.hogeman);

        // Sort
        item = addTopMenuItem(MenuItemId.SortTop.ordinal(), R.drawable.hogeman);
        addMenuItem(item, MenuItemId.Sort1.ordinal(), R.drawable.hogeman);
        addMenuItem(item, MenuItemId.Sort2.ordinal(), R.drawable.hogeman);
        addMenuItem(item, MenuItemId.Sort3.ordinal(), R.drawable.hogeman);
        // ListType
        item = addTopMenuItem(MenuItemId.ListTypeTop.ordinal(), R.drawable.hogeman);
        addMenuItem(item, MenuItemId.ListType1.ordinal(), R.drawable.hogeman);
        addMenuItem(item, MenuItemId.ListType2.ordinal(), R.drawable.hogeman);
        addMenuItem(item, MenuItemId.ListType3.ordinal(), R.drawable.hogeman);
        // Debug
        item = addTopMenuItem(MenuItemId.DebugTop.ordinal(), R.drawable.debug);
        addMenuItem(item, MenuItemId.Debug1.ordinal(), R.drawable.debug);
        addMenuItem(item, MenuItemId.Debug2.ordinal(), R.drawable.debug);
        addMenuItem(item, MenuItemId.Debug3.ordinal(), R.drawable.debug);

        mDrawList = UDrawManager.getInstance().addDrawable(this);
        updateBGSize();
    }
}
