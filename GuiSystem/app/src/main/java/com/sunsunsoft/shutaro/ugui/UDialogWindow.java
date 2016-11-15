package com.sunsunsoft.shutaro.ugui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.LinkedList;

/**
 * ダイアログ(画面の最前面に表示されるWindow)
 */
interface UDialogCallbacks {
    void dialogClosed(UDialogWindow dialog);
}

public class UDialogWindow extends UWindow implements UButtonCallbacks{

    enum DialogType {
        Normal,     // 移動可能、下にあるWindowをタッチできる
        Mordal      // 移動不可、下にあるWindowをタッチできない
    }

    enum DialogPos {
        Point,      // 指定した座標に表示
        Center      // 中央に表示
    }

    // ボタンの並ぶ方向
    enum ButtonDir {
        Horizontal,     // 横に並ぶ
        Vertical        // 縦に並ぶ
    }

    enum AnimationType {
        Opening,        // ダイアログが開くときのアニメーション
        Closing         // ダイアログが閉じる時のアニメーション
    }

    public static final int CloseDialogId = 10000123;

    public static final int ANIMATION_FRAME = 10;

    public static final int TEXT_SIZE = 70;
    public static final int MESSAGE_TEXT_SIZE = 50;
    public static final int TEXT_MARGIN_V = 50;
    public static final int BUTTON_H = 100;
    public static final int BUTTON_MARGIN_H = 50;
    public static final int BUTTON_MARGIN_V = 30;

    protected DialogType type;
    protected ButtonDir buttonDir;
    protected PointF dialogPos;
    protected Size dialogSize;

    protected String title = "";
    protected String message = "";

    protected UTextView titleView;
    protected UTextView messageView;

    protected int textColor;
    protected int dialogColor;

    protected UButtonCallbacks buttonCallbacks;
    protected UDialogCallbacks dialogCallbacks;
    protected AnimationType animationType;
    protected boolean isAnimation;
    protected Paint paint;

    protected boolean isUpdate = true;     // ボタンを追加するなどしてレイアウトが変更された

    // ボタン
    LinkedList<UButton> buttons = new LinkedList<>();

    /**
     * Constructor
     */
    public UDialogWindow(int x, int y, int width, int height, int color) {
        super(DrawPriority.Dialog.p(), 0, 0, width, height, color);

    }
    public static UDialogWindow createInstance(DialogType type, UButtonCallbacks buttonCallbacks,
                                               UDialogCallbacks dialogCallbacks,
                                               ButtonDir dir,
                                               boolean isAnimation,
                                               int width, int height,
                                               int textColor, int dialogColor)
    {
        UDialogWindow instance = new UDialogWindow(0, 0, width, height, 0);
        // ダミーのサイズ
        instance.dialogSize = new Size(width - 200, height - 200);
        instance.dialogPos = new PointF(
                (width - instance.dialogSize.width) / 2,
                (height - instance.dialogSize.height) / 2 );
        instance.type = type;
        instance.buttonDir = dir;
        instance.paint = new Paint();
        instance.textColor = textColor;
        instance.dialogColor = dialogColor;
        instance.buttonCallbacks = buttonCallbacks;
        instance.dialogCallbacks = dialogCallbacks;
        instance.isAnimation = isAnimation;
        if (isAnimation) {
            instance.startAnimation(AnimationType.Opening);
        }

        // 描画はDrawManagerに任せるのでDrawManagerに登録
        UDrawManager.getInstance().addDrawable(instance);

        return instance;
    }

    public void setDialogPos(float x, float y) {
        pos.x = x;
        pos.y = y;
    }

    public void setDialogPosCenter() {
        pos.x = (size.width - dialogSize.width) / 2;
        pos.y = (size.height - dialogSize.height) / 2;
    }

    public boolean doAction() {
        return false;
    }

    /**
     * ボタンを全削除
     */
    public void clearButtons() {
        buttons.clear();
    }

    /**
     * ダイアログを閉じる
     */
    public void close() {
        isShow = false;
        UDrawManager.getInstance().removeDrawable(this);
        dialogCallbacks.dialogClosed(this);
    }

    /**
     * 閉じるアニメーション開始
     */
    public void startClosing() {
        startAnimation(AnimationType.Closing);
    }

    /**
     * ボタンを追加
     * ボタンを追加した時点では座標は設定しない
     * @param text
     * @param color
     */
    public UButton addButton(int id, String text, int textColor, int color) {
        UButton button = new UButton(buttonCallbacks, UButtonType.Press, id, 0, text, 0, 0, 0, 0,
                textColor, color);
        buttons.add(button);
        isUpdate = true;
        return button;
    }

    /**
     * ダイアログを閉じるボタンを追加する
     * @param text
     */
    public void addCloseButton(String text) {
        if (text == null) {
            text = "Close";
        }
        UButton button = new UButton(this, UButtonType.Press, CloseDialogId, 0, text, 0, 0,
                0, 0,
                Color.WHITE, Color.RED);
        buttons.add(button);
        isUpdate = true;
    }

    /**
     * レイアウトを更新
     * ボタンの数によってレイアウトは自動で変わる
     */
    private void udpateLayout(Canvas canvas) {
        // タイトル、メッセージ
        int y = TEXT_MARGIN_V;
        if (titleView == null) {
            titleView = UTextView.createInstance(title, 70, 0, UTextView.UAlignment.CenterX,
                    canvas.getWidth(), dialogSize.width / 2, y, color, 0);
        }

        Size titleSize = titleView.getTextRect(getWidth());
        y += titleSize.height + BUTTON_MARGIN_H;

        if (messageView == null) {
            messageView = UTextView.createInstance(message, MESSAGE_TEXT_SIZE, 0, UTextView
                    .UAlignment.CenterX,
                    canvas.getWidth(), dialogSize.width / 2, y, color, 0);
        }

        Size messageSize = messageView.getTextRect(getWidth());
        y += messageSize.height + BUTTON_MARGIN_H;

        if (buttonDir == ButtonDir.Horizontal) {
            // ボタンを横に並べる
            int num = buttons.size();
            int buttonW = (dialogSize.width - ((num + 1) * BUTTON_MARGIN_H)) / num;
            for (int i=0; i<num; i++) {
                UButton button = buttons.get(i);
                button.setPos(BUTTON_MARGIN_H + (buttonW + BUTTON_MARGIN_H) * i, y);
                button.setSize(buttonW, BUTTON_H);
            }
            y += BUTTON_H + BUTTON_MARGIN_H;
        }
        else {
            // ボタンを縦に並べる
            int num = buttons.size();

            for (int i=0; i<num; i++) {
                UButton button = buttons.get(i);
                button.setPos(BUTTON_MARGIN_H, y);
                button.setSize(dialogSize.width - BUTTON_MARGIN_H * 2, BUTTON_H);
                y += BUTTON_H + BUTTON_MARGIN_V;
            }
        }
        dialogSize.height = y;

        // センタリング
        dialogPos.x = (size.width - dialogSize.width) / 2;
        dialogPos.y = (size.height - dialogSize.height) / 2;
    }

    /**
     * 描画処理
     * @param canvas
     * @param paint
     * @param offset 独自の座標系を持つオブジェクトをスクリーン座標系に変換するためのオフセット値
     */
    public void draw(Canvas canvas, Paint paint, PointF offset) {
        if (!isShow) return;
        if (isUpdate) {
            isUpdate = false;
            udpateLayout(canvas);
        }

        if (isAnimating) {
            float ratio;
            if (animationType == AnimationType.Opening) {
                ratio = (float)Math.sin(animeRatio * 90 * RAD);
                ULog.print(TAG, "ratio:" + ratio);
            } else {
                ratio = (float)Math.cos(animeRatio * 90 * RAD);
                ULog.print(TAG, "cos ratio:" + ratio);
            }
            float width = dialogSize.width * ratio;
            float height = dialogSize.height * ratio;
            float x = (size.width - width) / 2;
            float y = (size.height - height) / 2;
            RectF _rect = new RectF(x, y, x + width, y + height);
            UDraw.drawRoundRectFill(canvas, paint, _rect, 20, dialogColor);

        } else {
            // BG
            UDraw.drawRoundRectFill(canvas, paint, getDialogRect(), 20, dialogColor);

            // Title, Message
            PointF _offset = dialogPos;
            titleView.draw(canvas, paint, _offset);
            messageView.draw(canvas, paint, _offset);

            // Buttons
            for (UButton button : buttons) {
                button.draw(canvas, paint, _offset);
            }
        }
    }

    public RectF getDialogRect() {
        return new RectF(dialogPos.x, dialogPos.y, dialogPos.x + dialogSize.width, dialogPos
                .y + dialogSize.height);
    }

    /**
     * タッチ処理
     * @param vt
     * @return
     */
    public boolean touchEvent(ViewTouch vt) {
        PointF offset = dialogPos;

        for (UButton button : buttons) {
            if (button.touchEvent(vt, offset)) {
                return true;
            }
        }

        // 範囲外をタッチしたら閉じる
        if (vt.type == TouchType.Touch) {
            if (getDialogRect().contains(vt.touchX(), vt.touchY())) {

            } else {
                if (type == DialogType.Mordal) {
                    return false;
                } else {
                    startClosing();
                    return true;
                }
            }
        }

        return false;
    }

    public void startAnimation(AnimationType type) {
        animationType = type;
        startAnimation(ANIMATION_FRAME);
    }

    public void endAnimation() {
        if (animationType == AnimationType.Closing) {
            close();
        }
    }

    /**
     * UButtonCallbacks
     */
    public void click(int id) {
        switch(id) {
            case CloseDialogId:
                if (isAnimation) {
                    startClosing();
                } else {
                    close();
                }
                break;
        }
    }
    public void longClick(int id) {

    }
}
