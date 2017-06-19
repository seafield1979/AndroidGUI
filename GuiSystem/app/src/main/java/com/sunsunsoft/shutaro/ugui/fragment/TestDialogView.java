package com.sunsunsoft.shutaro.ugui.fragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.sunsunsoft.shutaro.ugui.uview.*;
import com.sunsunsoft.shutaro.ugui.uview.button.*;
import com.sunsunsoft.shutaro.ugui.R;
import com.sunsunsoft.shutaro.ugui.util.UColor;
import com.sunsunsoft.shutaro.ugui.util.ULog;
import com.sunsunsoft.shutaro.ugui.uview.window.ULogWindow;
import com.sunsunsoft.shutaro.ugui.ViewTouch;

/**
 * ダイアログWindowのテスト
 */
public class TestDialogView extends View implements View.OnTouchListener, UButtonCallbacks, UDialogCallbacks {
    enum DialogTest{
        Test1,
        Test2,
        Test3,
        Test4,
        Test5
    }

    // Dialogに配置されるボタンのID
    private static final int DialogButton1 = 100;
    private static final int DialogButton2 = 101;
    private static final int DialogButton3 = 102;

    // Viewに配置されるボタンのID
    private static final int TestButton1 = 200;
    private static final int TestButton2 = 201;
    private static final int TestButton3 = 202;
    private static final int TestButton4 = 203;
    private static final int TestButton5 = 204;

    private static final int[] dialogButtonIds = {
            DialogButton1,
            DialogButton2,
            DialogButton3
    };
    private static final int[] testButtonIds = {
            TestButton1,
            TestButton2,
            TestButton3,
            TestButton4,
            TestButton5
    };

    public static final String TAG = "TestButtonView";
    private static final int BUTTON_PRIORITY = 100;

    // サイズ更新用
    private boolean isFirst = true;

    // タッチ情報
    private ViewTouch viewTouch = new ViewTouch();

    private Paint paint = new Paint();

    // UButton
    private UButtonText[] buttons = new UButtonText[testButtonIds.length];

    // Dialog
    private UDialogWindow dialogWindow;

    // ULogWindow
    private ULogWindow logWindow;

    // get/set
    public TestDialogView(Context context) {
        this(context, null);
    }

    public TestDialogView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnTouchListener(this);
    }

    /**
     * 画面に表示するオブジェクトを生成
     * @param width
     * @param height
     */
    private void initDrawables(int width, int height) {
        // 描画オブジェクトクリア
        UDrawManager.getInstance().init();

        // Buttons
        float y = 100;
        for (int i=0; i<testButtonIds.length; i++) {
            int id = testButtonIds[i];
            buttons[i] = new UButtonText(this, UButtonType.Press, id, BUTTON_PRIORITY,
                    "Dialog" + (i+1),
                    100, y, width - 100*2, 120,
                    50, Color.WHITE, Color.rgb(0,128,0));
            if (buttons[i] != null) {
                UDrawManager.getInstance().addDrawable(buttons[i]);
            }
            y += 150;
        }

        // LogWindow
        logWindow = ULogWindow.createInstance(getContext(), this, ULogWindow.LogWindowType.AutoDisappear,
                0, 500, getWidth(), getHeight() - 500);
    }

    /**
     * ダイアログを生成
     * @param id
     * @param width
     * @param height
     */
    private void initDialog(DialogTest id, int width, int height) {
        if (dialogWindow != null) {
            return;
        }

        switch (id) {
            case Test1: {
                // 横にボタンが並ぶダイアログ
                dialogWindow = UDialogWindow.createInstance(UDialogWindow.DialogType.Mordal,
                        this, this,
                        UDialogWindow.ButtonDir.Horizontal,
                        UDialogWindow.DialogPosType.Center, false,
                        width, height, UColor.getRandomColor(), UColor.getRandomColor());
                dialogWindow.addToDrawManager();
                dialogWindow.setTitle("hoge\nhoge");

                for (int i=0; i<3; i++) {
                    dialogWindow.addTextView("hoge" + (i + 1), UAlignment.Center.CenterX,
                            false, false,
                            50, Color.rgb(50, 50, 0), 0);
                }
                for (int i = 0; i < dialogButtonIds.length; i++) {
                    int buttonId = dialogButtonIds[i];
                    dialogWindow.addButton(buttonId, "Test" + (i + 1), Color.BLACK, Color
                            .YELLOW);
                }
                dialogWindow.addCloseButton(null);
                dialogWindow.setDrawPriority(DrawPriority.Dialog.p());
            }
                break;
            case Test2:
            {
                // 縦にボタンが並ぶダイアログ
                dialogWindow = UDialogWindow.createInstance(UDialogWindow.DialogType.Mordal,
                        this, this,
                        UDialogWindow.ButtonDir.Vertical,
                        UDialogWindow.DialogPosType.Center, true,
                        width, height, UColor.getRandomColor(), UColor.getRandomColor());
                dialogWindow.addToDrawManager();

                dialogWindow.setTitle("hoge\nhoge");
                for (int i=0; i<3; i++) {
                    dialogWindow.addTextView("hoge" + (i + 1), UAlignment.Center.CenterX,
                            false, false,
                            50, Color.rgb(50, 50, 0), 0);
                }
                for (int i=0; i<dialogButtonIds.length; i++) {
                    int buttonId = dialogButtonIds[i];
                    dialogWindow.addButton(buttonId, "Test" + (i+1), Color.BLACK, Color
                            .YELLOW);
                }
                dialogWindow.addCloseButton(null);
                dialogWindow.setDrawPriority(DrawPriority.Dialog.p());
            }
                break;
            case Test3:
            {
                // 縦にボタンが並ぶダイアログ
                // ノーマルタイプ
                dialogWindow = UDialogWindow.createInstance(UDialogWindow.DialogType.Normal,
                        this, this,
                        UDialogWindow.ButtonDir.Vertical,
                        UDialogWindow.DialogPosType.Center,
                        true,
                        width, height, UColor.getRandomColor(), UColor.getRandomColor());
                dialogWindow.addToDrawManager();
                dialogWindow.setTitle("hoge\nhoge");
                for (int i=0; i<3; i++) {
                    dialogWindow.addTextView("hoge" + (i + 1), UAlignment.Center.CenterX,
                            false, false,
                            50, Color.rgb(50, 50, 0), 0);
                }
                for (int i=0; i<dialogButtonIds.length; i++) {
                    int buttonId = dialogButtonIds[i];
                    dialogWindow.addButton(buttonId, "Test" + (i+1), Color.BLACK, Color
                            .WHITE);
                }
                dialogWindow.addCloseButton(null);
                dialogWindow.setDrawPriority(DrawPriority.Dialog.p());
            }
                break;
            case Test4:
            {
                dialogWindow = UDialogWindow.createInstance(UDialogWindow.DialogType.Normal,
                        this, this,
                        UDialogWindow.ButtonDir.Horizontal, UDialogWindow.DialogPosType.Center,
                        true,
                        width, height, UColor.getRandomColor(), UColor.getRandomColor());
                dialogWindow.addToDrawManager();

                for (int i=0; i<dialogButtonIds.length; i++) {
                    int buttonId = dialogButtonIds[i];
                    dialogWindow.addImageButton(buttonId, R.drawable.hogeman, R.drawable.hogeman2, 150, 150);
                }
                dialogWindow.addButton(100, "Test" + 1, Color.BLACK, Color
                        .WHITE);
                dialogWindow.setDrawPriority(DrawPriority.Dialog.p());
            }
                break;
            case Test5:
            {
                dialogWindow = UDialogWindow.createInstance(UDialogWindow.DialogType.Normal,
                        this, this,
                        UDialogWindow.ButtonDir.Vertical,
                        UDialogWindow.DialogPosType.Center,
                        true,
                        width, height, UColor.getRandomColor(), UColor.getRandomColor());
                dialogWindow.addToDrawManager();

                for (int i=0; i<dialogButtonIds.length; i++) {
                    int buttonId = dialogButtonIds[i];
                    dialogWindow.addImageButton(buttonId, R.drawable.hogeman, R.drawable.hogeman2, 150, 150);
                }
                dialogWindow.addButton(100, "Test" + 1, Color.BLACK, Color
                        .WHITE);
                dialogWindow.setDrawPriority(DrawPriority.Dialog.p());
            }
                break;
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (isFirst) {
            isFirst = false;
            initDrawables(getWidth(), getHeight());
        }
        // 毎フレームの処理
        if (logWindow.doAction() == DoActionRet.Redraw) {
            invalidate();
        }

        // 背景塗りつぶし
        canvas.drawColor(Color.WHITE);

        // アンチエリアシング(境界のぼかし)
        paint.setAntiAlias(true);

        // マネージャに登録した描画オブジェクトをまとめて描画
        if (UDrawManager.getInstance().draw(canvas, paint)){
            invalidate();
        }
    }

    /**
     * タッチイベント処理
     * @param v
     * @param e
     * @return
     */
    public boolean onTouch(View v, MotionEvent e) {
        boolean ret = true;
        boolean refresh = false;

        viewTouch.checkTouchType(e);

        // Windows
        if (UDrawManager.getInstance().touchEvent(viewTouch)) {
            refresh = true;
        }

        if (refresh) {
            invalidate();
        }

        switch(e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // trueを返す。こうしないと以降のMoveイベントが発生しなくなる。
                ret = true;
                break;
            case MotionEvent.ACTION_UP:
                ret = true;
                break;
            case MotionEvent.ACTION_MOVE:
                ret = true;
                break;
            default:
        }

        return ret;
    }

    /**
     * UButtonCallbacks
     */
    public boolean UButtonClicked(int id, boolean pressedOn) {
        ULog.print(TAG, "button click:" + id);

        switch (id) {
            case DialogButton1:
                return true;
            case DialogButton2:
                return true;
            case DialogButton3:
                return true;
            case UDialogWindow.CloseDialogId:
                if (dialogWindow != null) {
                    dialogWindow.closeDialog();
                    dialogWindow = null;
                }
                return true;
            case TestButton1:
                initDialog(DialogTest.Test1, getWidth(), getHeight());
                return true;
            case TestButton2:
                initDialog(DialogTest.Test2, getWidth(), getHeight());
                return true;
            case TestButton3:
                initDialog(DialogTest.Test3, getWidth(), getHeight());
                return true;
            case TestButton4:
                initDialog(DialogTest.Test4, getWidth(), getHeight());
                return true;
            case TestButton5:
                initDialog(DialogTest.Test5, getWidth(), getHeight());
                return true;
        }
        return false;
    }

    /**
     * UDialogCallbacks
     */
    public void dialogClosed(UDialogWindow dialog) {
        if (dialogWindow == dialog) {
            dialogWindow = null;
        }
    }
}
