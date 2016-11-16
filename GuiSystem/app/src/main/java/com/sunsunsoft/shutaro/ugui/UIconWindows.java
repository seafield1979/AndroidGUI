package com.sunsunsoft.shutaro.ugui;

import java.util.LinkedList;

/**
 * 複数のUIconWindowを管理する
 * Window間でアイコンのやり取りを行ったりするのに使用する
 * 想定はメインWindowが１つにサブWindowが１つ
 */

public class UIconWindows {
    enum DirectionType {
        Landscape,      // 横長
        Portlait,       // 縦長
    }
    /**
     * Consts
     */
    public static final int MOVING_FRAME = 10;

    /**
     * Member Variables
     */

    private LinkedList<UIconWindow> windows = new LinkedList<>();
    private UIconWindow mainWindow;
    private UIconWindow subWindow;
    private Size size;
    private DirectionType directionType;

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

    public static UIconWindows createInstance(UIconWindow mainWindow, UIconWindow subWindow,
                                              int screenW, int screenH) {
        UIconWindows instance = new UIconWindows();
        instance.size = new Size(screenW, screenH);
        instance.directionType = (screenW > screenH) ? DirectionType.Landscape : DirectionType
                .Portlait;
        instance.mainWindow = mainWindow;
        instance.subWindow = subWindow;

        instance.windows.add(mainWindow);
        instance.windows.add(subWindow);

        // 初期配置(HomeWindowで画面が占有されている)
        mainWindow.setPos(0, 0);
        mainWindow.setSize(screenW, screenH);
        if (instance.directionType == DirectionType.Landscape) {
            subWindow.setPos(screenW, 0);
        } else {
            subWindow.setPos(0, screenH);
        }
        return instance;
    }

    /**
     * Methods
     */

    /**
     * Windowを表示する
     * @param window
     * @param animation
     */
    public void showWindow(UIconWindow window, boolean animation) {
        window.isShow = true;
        LinkedList<UIconWindow> showWindows = new LinkedList<>();
        for (UIconWindow _window : windows) {
            if (_window.isShow) {
                showWindows.add(_window);
            }
        }
        if (showWindows.size() == 0) return;

        // 各ウィンドウが同じサイズになるように並べる
        int width = 0;
        int height = 0;
        if (directionType == DirectionType.Landscape) {
            width = size.width / showWindows.size();
            height = size.height;
        } else {
            width = size.width;
            height = size.height / showWindows.size();
        }

        // 座標を設定する
        if (animation) {
            // Main
            mainWindow.setPos(0, 0);
            mainWindow.startMovingSize(width, height, MOVING_FRAME);

            // Sub
            if (directionType == DirectionType.Landscape) {
                subWindow.setPos(size.width, 0);
                subWindow.startMoving(width, 0, width, height, MOVING_FRAME);
            } else {
                subWindow.setPos(0, size.height);
                subWindow.startMoving(0, height, width, height, MOVING_FRAME);
            }

        } else {
            float x = 0, y = 0;
            for (UIconWindow _window : showWindows) {
                _window.setPos(x, y);
                _window.setSize(width, height);
                if (directionType == DirectionType.Landscape) {
                    x += width;
                } else {
                    y += height;
                }
            }
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
