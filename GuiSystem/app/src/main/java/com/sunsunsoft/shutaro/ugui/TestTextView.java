package com.sunsunsoft.shutaro.ugui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.LinkedList;

/**
 * UTextViewのテスト用View
 */

public class TestTextView extends View implements View.OnTouchListener, UButtonCallbacks,
        UEditTextCallbacks, UTest1DialogCallbacks {
    // ボタンのID
    enum ButtonId {
        Edit("edit text");

        private final String text;

        ButtonId(final String text) {
            this.text = text;
        }

        public String getString() {
            return this.text;
        }
    }

    /**
     * Constants
     */
    public static final String TAG = "TestButtonView";
    private static final int TEXT_PRIORITY = 100;
    private static final int BUTTON_PRIORITY = 90;
    private static final int TEXT_SIZE = 70;
    private static final int PageButtonId = 100;
    private static final int PAGE_NUM = 4;

    /**
     * Member variables
     */
    private Context mContext;

    // サイズ更新用
    private boolean isFirst = true;

    // タッチ情報
    private ViewTouch viewTouch = new ViewTouch();

    private Paint paint = new Paint();

    // Page buttons
    private UButtons pageButtons;

    // UButton
    private UButton button1;

    // UTextView
    private LinkedList<UTextView> textViews = new LinkedList<>();
    private UEditText editText;
    private UTextViewOpenClose textView2;

    // ULogWindow
    private ULogWindow logWindow;

    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    public TestTextView(Context context) {
        this(context, null);
    }

    public TestTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnTouchListener(this);
        mContext = context;
    }

    /**
     * UTextViewを追加
     *
     * @return
     */
    private UTextView addTextView(String text, int textSize, int priority,
                                  UDraw.UAlignment alignment, boolean multiLine,
                                  float x, float y, int color, int bgColor) {
        UTextView textView = UTextView.createInstance(text, textSize, priority,
                alignment,
                getWidth(), multiLine, true,
                x, y,
                getWidth() - 100, color, bgColor);
        textViews.add(textView);
        UDrawManager.getInstance().addDrawable(textView);


        UDrawableRect rectObj = UDrawableRect.createInstance(priority - 1, 0, y, getWidth(), 2,
                color);
        UDrawManager.getInstance().addDrawable(rectObj);

        return textView;
    }

    /**
     * 画面に表示するオブジェクトを生成
     * @param width
     * @param height
     */
    private void initDrawables(int width, int height) {
        float y = 50;

        // 描画オブジェクトクリア
        UDrawManager.getInstance().init();

        // pageButtons
        pageButtons = new UButtons(this, UButtonType.Press, 100, Color.rgb(200,100,100),
                Color.BLACK, PAGE_NUM, 1, 100, y, getWidth() - 200, 120);
        pageButtons.addFull( PageButtonId, "p");

        y += 150;

        // 描画マネージャに登録
        UDrawManager.getInstance().addDrawable(pageButtons);


        // UButton
        button1 = new UButtonText(this, UButtonType.Press, ButtonId.Edit.ordinal(), BUTTON_PRIORITY,
                "edit", 100, y,
                width - 100*2, 120,
                Color.WHITE,
                Color.rgb(0,128,0));
        UDrawManager.getInstance().addDrawable(button1);
        y += 120 + 50;

        // TextView
        for (int i=0; i<4; i++) {
            UDraw.UAlignment alignment = UDraw.UAlignment.None;
            switch (i) {
                case 1:
                    alignment = UDraw.UAlignment.CenterX;
                    break;
                case 2:
                    alignment = UDraw.UAlignment.CenterY;
                    break;
                case 3:
                    alignment = UDraw.UAlignment.Center;
                    break;
            }
            String text = "hoge" + (i + 1);

            addTextView(text, TEXT_SIZE, TEXT_PRIORITY, alignment, false,
                    width / 2, y, UColor.getRandomColor(), UColor
                    .getRandomColor());
            y += 100;
        }

        // Multi line
        addTextView("aaa\nbbb\nccc", TEXT_SIZE, TEXT_PRIORITY,
                UDraw.UAlignment.CenterX, true,
                width / 2, y, UColor.getRandomColor(), UColor
                .getRandomColor());
        y += 200;

        // EditText
        editText = UEditText.createInstance(this, this, "aaa", TEXT_SIZE, 71,
                UDraw.UAlignment.None, width, false,
                100, y, 300, Color.GREEN, Color.argb(128,0,
                0,0));
        UDrawManager.getInstance().addDrawable(editText);
        y += editText.getHeight() + 50;

        // UTextViewOpenClose
        textView2 = UTextViewOpenClose.createInstance(
                "aaa\naaa\naaa\naaa", 71, TEXT_SIZE, UDraw.UAlignment.None,
                width,
                100, y, 300, Color.GREEN, Color.argb(128,0,
                        0,0));
        UDrawManager.getInstance().addDrawable(textView2);
        y += textView2.getHeight() + 50;


        // LogWindow
        logWindow = ULogWindow.createInstance(getContext(), this,LogWindowType.AutoDisappear,
                0, 500, getWidth(), getHeight() - 500);
    }

    private void initDrawablesPage() {

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

        for (UTextView textView : textViews) {
            if (textView.getRect().contains((int)viewTouch.touchX(), (int)viewTouch.touchY())) {
                textView.setText("hogehoge\nhogehoge");
                refresh = true;
            }
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
     * UEditText上のEditTextを非表示にする
     */
    public void hideEditTexts() {
        editText.showEditView(false);
    }


    /**
     * UButtonCallbacks
     */
    public boolean UButtonClick(int id) {
        ULog.print(TAG, "button click:" + id);

        if (id < ButtonId.values().length) {
            ButtonId buttonId = ButtonId.values()[id];
            switch (buttonId) {
                case Edit:
                {
                    LinkedList<String> args = new LinkedList();
                    for (UTextView textView : textViews) {
                        args.add(textView.getText());
                    }
                    UTest1DialogFragment dialogFragment = UTest1DialogFragment.createInstance
                            (this, args.toArray(new String[0]));

                    dialogFragment.show(((AppCompatActivity)mContext).getSupportFragmentManager(),
                            "fragment_dialog");
                }
                    break;
            }
        }

        switch(id) {
            case PageButtonId:
                break;
            case PageButtonId + 1:
                break;
            case PageButtonId + 2:
                break;
            case PageButtonId + 3:
                break;

        }
        return false;
    }

    public boolean UButtonLongClick(int id) {
        return false;
    }


    /**
     * UEditTextCallbacks
     */
    /**
     * ダイアログを表示する
     * @param edit
     */
    public void showDialog(UEditText edit, String title, String text) {
        UEditDialogFragment dialogFragment = UEditDialogFragment.createInstance(edit, title, text);

        dialogFragment.show(((AppCompatActivity)mContext).getSupportFragmentManager(),
                "fragment_dialog");
    }

    /**
     * UTest1DialogCallbacks
     */
    public void submit(Bundle args) {
        String[] texts = args.getStringArray(UTest1DialogFragment.KEY_RETS);
        if (texts != null) {
            for (int i = 0; i < texts.length; i++) {
                textViews.get(i).setText(texts[i]);
            }
        }
        invalidate();
    }

    public void cancel() {

    }
}
