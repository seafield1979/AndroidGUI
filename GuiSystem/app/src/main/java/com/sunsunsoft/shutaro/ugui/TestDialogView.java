package com.sunsunsoft.shutaro.ugui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * ダイアログWindowのテスト
 */
public class TestDialogView extends View implements View.OnTouchListener, UButtonCallbacks, UDialogCallbacks{
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
                    100, y,
                    width - 100*2, 120,
                    Color.WHITE,
                    Color.rgb(0,128,0));
            if (buttons[i] != null) {
                UDrawManager.getInstance().addDrawable(buttons[i]);
            }
            y += 150;
        }

        // LogWindow
        logWindow = ULogWindow.createInstance(getContext(), this,LogWindowType.AutoDisappear,
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
                        UDialogWindow.ButtonDir.Horizontal, false,
                        width, height, UColor.getRandomColor(), UColor.getRandomColor());
                dialogWindow.title = "hoge\nhoge";
                dialogWindow.message = "message";
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
                        UDialogWindow.ButtonDir.Vertical, true,
                        width, height, UColor.getRandomColor(), UColor.getRandomColor());
                dialogWindow.title = "hoge\nhoge";
                dialogWindow.message = "message";
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
                        UDialogWindow.ButtonDir.Vertical, true,
                        width, height, UColor.getRandomColor(), UColor.getRandomColor());
                dialogWindow.title = "hoge\nhoge";
                dialogWindow.message = "message";
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
                        UDialogWindow.ButtonDir.Horizontal, true,
                        width, height, UColor.getRandomColor(), UColor.getRandomColor());

                Bitmap image1 = BitmapFactory.decodeResource(getResources(), R.drawable.hogeman);
                Bitmap image2 = BitmapFactory.decodeResource(getResources(), R.drawable.hogeman2);

                for (int i=0; i<dialogButtonIds.length; i++) {
                    int buttonId = dialogButtonIds[i];
                    dialogWindow.addImageButton(buttonId, image1, image2, 150, 150);
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
                        UDialogWindow.ButtonDir.Vertical, true,
                        width, height, UColor.getRandomColor(), UColor.getRandomColor());

                Bitmap image1 = BitmapFactory.decodeResource(getResources(), R.drawable.hogeman);
                Bitmap image2 = BitmapFactory.decodeResource(getResources(), R.drawable.hogeman2);

                for (int i=0; i<dialogButtonIds.length; i++) {
                    int buttonId = dialogButtonIds[i];
                    dialogWindow.addImageButton(buttonId, image1, image2, 150, 150);
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
        if (logWindow.doAction()) {
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
    public void click(int id) {
        ULog.print(TAG, "button click:" + id);

        switch (id) {
            case DialogButton1:
                break;
            case DialogButton2:
                break;
            case DialogButton3:
                break;
            case UDialogWindow.CloseDialogId:
                if (dialogWindow != null) {
                    dialogWindow.close();
                    dialogWindow = null;
                }
                break;
            case TestButton1:
                initDialog(DialogTest.Test1, getWidth(), getHeight());
                break;
            case TestButton2:
                initDialog(DialogTest.Test2, getWidth(), getHeight());
                break;
            case TestButton3:
                initDialog(DialogTest.Test3, getWidth(), getHeight());
                break;
            case TestButton4:
                initDialog(DialogTest.Test4, getWidth(), getHeight());
                break;
            case TestButton5:
                initDialog(DialogTest.Test5, getWidth(), getHeight());
                break;
        }
    }
    public void longClick(int id) {

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
