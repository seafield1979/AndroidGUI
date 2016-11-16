package com.sunsunsoft.shutaro.ugui;

import java.util.LinkedList;

/**
 * 複数のUIconWindowを管理する
 * Window間でアイコンのやり取りを行ったりするのに使用する
 * 想定はメインWindowが１つにサブWindowが１つ
 */

public class UIconWindows {
    /**
     * Consts
     */

    /**
     * Member Variables
     */

    private LinkedList<UIconWindow> windows = new LinkedList<>();
    private UIconWindow mainWindow;
    private UIconWindow subWindow;

    /**
     * Get/Set
     */
    public UIconWindow getMainWindow() {
        return mainWindow;
    }

    public UIconWindow getSubWindow() {
        return subWindow;
    }

    public LinkedList<UIconWindow> getWindows() {
        return windows;
    }

    /**
     * Constructor
     * インスタンスの生成はcreateInstanceを使用すること
     */
    private UIconWindows() {
    }

    public static UIconWindows createInstance(UIconWindow mainWindow, UIconWindow subWindow) {
        UIconWindows instance = new UIconWindows();
        instance.mainWindow = mainWindow;
        instance.subWindow = subWindow;

        instance.windows.add(mainWindow);
        instance.windows.add(subWindow);
        return instance;
    }

    /**
     * Methods
     */

    public void showWindow(UIconWindow window, boolean animation) {
        if (animation) {

        } else {

        }
    }

    public void hideWindow(UIconWindow window) {

    }

    /**
     * 毎フレームの処理
     * @return true:処理中
     */
    public boolean doAction() {
        boolean isFinished = true;


        for (UIconWindow window : windows) {
            if (window.autoMoving()) {
                isFinished = false;
            }
        }
        return !isFinished;
    }

}
