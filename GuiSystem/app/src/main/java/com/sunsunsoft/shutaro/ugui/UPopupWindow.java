package com.sunsunsoft.shutaro.ugui;

import android.graphics.Color;

/**
 * Created by shutaro on 2016/12/04.
 *
 * OKかキャンセルを押すまで消えないポップアップWindow
 */

enum UPopupType {
    OK,
    OKCancel
}

public class UPopupWindow extends UDialogWindow {

    /**
     * Constants
     */
    public static final int OKButtonId = 10005000;

    /**
     * Member variables
     */

    /**
     * Constructor
     */
    public UPopupWindow(UButtonCallbacks buttonCallbacks,
            UPopupType popupType,
                        String title,
                        boolean isAnimation,
                        int screenW, int screenH)
    {
        super(DialogType.Mordal, buttonCallbacks, null,
                ButtonDir.Horizontal, DialogPosType.Center,
                isAnimation, 0, 0, screenW, screenH,
                Color.BLACK, Color.WHITE);

        this.frameColor = Color.BLACK;
        this.title = title;

        if (popupType == UPopupType.OK) {
            addButton(OKButtonId, "OK", Color.BLACK, Color.LTGRAY);
        } else {
            addButton(OKButtonId, "OK", Color.BLACK, Color.LTGRAY);
            addCloseButton("cancel");
        }
    }

    /**
     * UButtonCallbacks
     */
    public boolean UButtonClicked(int id, boolean pressedOn) {
        if (super.UButtonClicked(id, pressedOn)) {
            return true;
        }

        switch(id) {
            case OKButtonId:
                if (buttonCallbacks != null) {
                    buttonCallbacks.UButtonClicked(id, pressedOn);
                }
                startClosing();
                return true;
            case CloseDialogId:
                startClosing();
                return true;
        }
        return false;
    }
}
