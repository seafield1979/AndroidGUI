package com.sunsunsoft.shutaro.ugui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import java.util.LinkedList;

/**
 * ダイアログ(画面の最前面に表示されるWindow)
 */

public class UDialogWindow extends UWindow implements UButtonCallbacks {

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

    public static final int TEXT_MARGIN_V = 50;
    public static final int BUTTON_H = 100;
    public static final int BUTTON_MARGIN_H = 50;


    protected DialogType type;
    protected ButtonDir buttonDir;
    protected PointF dialogPos;
    protected Size dialogSize;

    protected String title;
    protected String message;

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
    public UDialogWindow createInstance(DialogType type, ButtonDir dir, int width, int height, int
            color)
    {
        UDialogWindow instance = new UDialogWindow(0, 0, width, height, color);
        // ダミーのサイズ
        instance.dialogSize = new Size(width - 200, height - 200);
        instance.dialogPos = new PointF(100, 100);
        instance.type = type;
        instance.buttonDir = dir;
        instance.paint = new Paint();

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
     * ボタンを追加
     * ボタンを追加した時点では座標は設定しない
     * @param text
     * @param color
     */
    public UButton addButton(int id, String text, int color) {
        UButton button = new UButton(this, UButtonType.Press, id, 0, text, 0, 0, 0, 0, color);
        buttons.add(button);
        isUpdate = true;
        return button;
    }

    /**
     * レイアウトを更新
     * ボタンの数によってレイアウトは自動で変わる
     */
    private void udpateLayout() {
        // タイトル、テキストのサイズを計算する
        Rect bounds = new Rect();
        paint.setTextSize(30);
        paint.getTextBounds(title, 0, title.length(), bounds);

        if (buttonDir == ButtonDir.Horizontal) {
            // ボタンを横に並べる
            int num = buttons.size();
            int buttonW = (size.width - ((num + 1) * BUTTON_MARGIN_H)) / num;
            for (int i=0; i<num; i++) {

            }
        }
        else {
        }
    }

    /**
     * 描画処理
     * @param canvas
     * @param paint
     * @param offset 独自の座標系を持つオブジェクトをスクリーン座標系に変換するためのオフセット値
     */
    public void draw(Canvas canvas, Paint paint, PointF offset) {
        if (isUpdate) {
        }
    }

    /**
     * タッチ処理
     * @param vt
     * @return
     */
    public boolean touchEvent(ViewTouch vt) {
        return false;
    }


    /**
     * UButtonCallbacks
     */
    public void click(UButton button) {

    }

    public void longClick(UButton button) {

    }
}
