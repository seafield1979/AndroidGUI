package com.sunsunsoft.shutaro.ugui.fragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.sunsunsoft.shutaro.ugui.R;
import com.sunsunsoft.shutaro.ugui.uview.button.*;
import com.sunsunsoft.shutaro.ugui.uview.button.UButtons;
import com.sunsunsoft.shutaro.ugui.uview.UDrawManager;
import com.sunsunsoft.shutaro.ugui.util.ULog;
import com.sunsunsoft.shutaro.ugui.ViewTouch;

/**
 * UButtonのテスト
 */
public class TestButtonView extends SurfaceView implements Runnable,SurfaceHolder.Callback, UButtonCallbacks {
    enum ButtonId {
        Test1,
        Test2,
        Test3,
        Test4,
        Test5,
        Test6
    }

    public static final String TAG = "TestButtonView";
    private static final int BUTTON_PRIORITY = 100;
    private static final int IMAGE_BUTTON_ID = 10;

    // SurfaceView用
    SurfaceHolder surfaceHolder;
    Thread thread;
    private boolean isInvalidate = true;
    int screen_width, screen_height;

    // サイズ更新用
    private boolean isFirst = true;

    // タッチ情報
    private ViewTouch vt = new ViewTouch();

    private Paint paint = new Paint();

    // UButton
    private UButtonText[] buttons = new UButtonText[ButtonId.values().length];

    // UButtonClose
    private UButtonClose closingButton;

    // UButtons
    private UButtons buttons2;

    // get/set
    public TestButtonView(Context context) {
        this(context, null);
    }

    public TestButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
    }
    /**
     * 画面に表示するオブジェクトを生成
     * @param width
     * @param height
     */
    private void initDrawables(int width, int height) {
        // 描画オブジェクトクリア
        UDrawManager.getInstance().init();

        // UButton
        float y = 100;
        UButtonType buttonType = UButtonType.BGColor;

        for (int i=0; i<buttons.length; i++) {
            ButtonId id = ButtonId.values()[i];

            switch (id) {
                case Test1:
                    buttonType = UButtonType.BGColor;
                    break;
                case Test2:
                    buttonType = UButtonType.BGColor;
                    break;
                case Test3:
                    buttonType = UButtonType.Press;
                    break;
                case Test4:
                    buttonType = UButtonType.Press2;
                    break;
                case Test5:
                    buttonType = UButtonType.Press3;
                    break;
                case Test6:
                    buttonType = UButtonType.Press3;
                    break;
            }

            buttons[i] = new UButtonText(this, buttonType, id.ordinal(), BUTTON_PRIORITY, "test" +
                    (i+1), 100, y,
                    width - 100*2, 120,
                    50, Color.WHITE, Color.rgb(0,128,0));

            UDrawManager.getInstance().addDrawable(buttons[i]);
            y += 150;
        }

        // UButtonClose
        closingButton = new UButtonClose(this, UButtonType.Press, 100, BUTTON_PRIORITY,
                100, y,
                Color.rgb(255,0,0));


        UDrawManager.getInstance().addDrawable(closingButton);
        y += 50;

        // UButtons
        int row = 2;
        int column = 2;
        buttons2 = new UButtons(this, UButtonType.Press, BUTTON_PRIORITY, Color.BLUE,
                Color.WHITE, row, column, 0, y, width, 300);
        for (int i=0; i < row * column; i++) {
            buttons2.add(100 + i, "button " + (100+i));
        }
        UDrawManager.getInstance().addDrawable(buttons2);
        y += 200 + 50;

        // UButtonImage
        UButtonImage imageButton = UButtonImage.createButton(this,
                IMAGE_BUTTON_ID,
                BUTTON_PRIORITY, 100, y, 150, 150, R.drawable.hogeman, R.drawable.hogeman2);
        UDrawManager.getInstance().addDrawable(imageButton);

        UButtonImage imageButton2 = UButtonImage.createButton(this,
                IMAGE_BUTTON_ID + 1,
                BUTTON_PRIORITY, 300, y, 150, 150, R.drawable.hogeman, -1);
        UDrawManager.getInstance().addDrawable(imageButton2);

    }

    // Surfaceが作られた時のイベント処理
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        initDrawables(getWidth(), getHeight());

        thread = new Thread(this);
        thread.start();
    }

    // Surfaceが変更された時の処理
    @Override
    public void surfaceChanged(
            SurfaceHolder holder,
            int format,
            int width,
            int height) {
        screen_width = width;
        screen_height = height;

        Log.i("myLog", "surfaceChanged");
    }

    // Surfaceが破棄された時の処理
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        thread = null;
        Log.i("myLog", "surfaceDestroyed");
    }

    @Override
    public void run() {
        Canvas canvas = null;

        long loopCount = 0;
        while(thread != null){
            try{
                synchronized(this) {
                    loopCount++;

                    if (isInvalidate) {
                        isInvalidate = false;

                        canvas = surfaceHolder.lockCanvas();
                        myDraw(canvas);
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }

                    // 次のinvalidateが実行されるまで待ち(すでにinvalidate済みなら待たない)
                    if (!isInvalidate) {
                        wait();
                    }
                }
            }
            catch(Exception e){
                Log.d(TAG, "exception:" + e.getMessage());
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    public synchronized void invalidate() {
        isInvalidate = true;
        notify();
    }

    public void myDraw(Canvas canvas) {
        if (isFirst) {
            isFirst = false;
            initDrawables(getWidth(), getHeight());
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
     * @param e
     * @return
     */
    public boolean onTouchEvent(MotionEvent e) {
        boolean ret = true;

        vt.checkTouchType(e);

        if (UDrawManager.getInstance().touchEvent(vt)) {
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
        ULog.print(TAG, "button click:" + (id + 1) + " pressedOn:" + pressedOn);

        if (id < ButtonId.values().length) {
            ButtonId buttonId = ButtonId.values()[id];
            switch (buttonId) {
                case Test1:
                    break;
                case Test2:
                    break;
                case Test3:
                    break;
                case Test4:
                    break;
                case Test5:
                    // セットのボタンの片方の押された状態をキャンセル
                    buttons[ButtonId.Test6.ordinal()].setPressedOn(false);
                    break;
                case Test6:
                    // セットのボタンの片方の押された状態をキャンセル
                    buttons[ButtonId.Test5.ordinal()].setPressedOn(false);
                    break;
            }
            return true;
        } else {
            switch(id) {
                case IMAGE_BUTTON_ID:

                    break;
            }
        }
        return false;
    }
}
