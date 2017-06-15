package com.sunsunsoft.shutaro.ugui.fragment;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.sunsunsoft.shutaro.ugui.uview.*;
import com.sunsunsoft.shutaro.ugui.uview.button.*;
import com.sunsunsoft.shutaro.ugui.uview.text.*;
import com.sunsunsoft.shutaro.ugui.util.UColor;
import com.sunsunsoft.shutaro.ugui.util.ULog;
import com.sunsunsoft.shutaro.ugui.uview.window.ULogWindow;
import com.sunsunsoft.shutaro.ugui.uview.text.UTextView;
import com.sunsunsoft.shutaro.ugui.uview.text.UTextViewOpenClose;
import com.sunsunsoft.shutaro.ugui.ViewTouch;

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
    private static final int PAGE_NUM = 5;

    // UDrawManagerの描画ページID
    private static final int PAGE1 = 0;
    private static final int PAGE2 = 2;
    private static final int PAGE3 = 3;
    private static final int PAGE4 = 4;
    private static final int PAGE5 = 5;

    /**
     * Member variables
     */
    private Context mContext;

    // サイズ更新用
    private boolean isFirst = true;

    // タッチ情報
    private ViewTouch viewTouch = new ViewTouch();

    private Paint paint = new Paint();

    // Page mButtons
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
                                  UAlignment alignment, boolean multiLine,
                                  boolean drawBG,
                                  float x, float y, int color, int bgColor) {
        UTextView textView = UTextView.createInstance(text, textSize, priority,
                alignment,
                getWidth(), multiLine, drawBG,
                x, y,
                0, color, bgColor);
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

        // pageButtons
        pageButtons = new UButtons(this, UButtonType.Press, 100, Color.rgb(200,100,100),
                Color.BLACK, PAGE_NUM, 1, 100, y, getWidth() - 200, 120);
        pageButtons.addFull( PageButtonId, "p");

        // 各ページの描画登録
        initDrawablesPage(PAGE1, y);
        initDrawablesPage(PAGE2, y);
        initDrawablesPage(PAGE3, y);
        initDrawablesPage(PAGE4, y);
        initDrawablesPage(PAGE5, y);

        UDrawManager.getInstance().setCurrentPage(PAGE1);

        // LogWindow
        logWindow = ULogWindow.createInstance(getContext(), this, ULogWindow.LogWindowType.AutoDisappear,
                0, 500, getWidth(), getHeight() - 500);
    }

    /**
     * 各ページの描画オブジェクトを登録する
     * @param page
     */
    private void initDrawablesPage(int page, float topY) {
        float y = topY;

        UDrawManager drawManager = UDrawManager.getInstance();

        // 登録先のページを切り替える
        drawManager.setCurrentPage(page);

        // 描画オブジェクトクリア
        drawManager.initPage(page);

        // 全てのページに表示するページ切り替えボタンを登録
        drawManager.addDrawable(pageButtons);

        y += 150;

        switch (page) {
            case PAGE1:
            {
                // TextView
                for (int i=0; i<6; i++) {
                    UAlignment alignment = UAlignment.None;
                    switch (i) {
                        case 1:
                            alignment = UAlignment.CenterX;
                            break;
                        case 2:
                            alignment = UAlignment.CenterY;
                            break;
                        case 3:
                            alignment = UAlignment.Center;
                            break;
                        case 4:
                            alignment = UAlignment.Right;
                            break;
                        case 5:
                            alignment = UAlignment.Right_CenterY;
                            break;
                    }
                    String text = "hoge" + (i + 1);

                    addTextView(text, TEXT_SIZE, TEXT_PRIORITY, alignment, false, false,
                            getWidth() / 2, y, UColor.getRandomColor(), UColor
                                    .getRandomColor());
                    y += 150;
                }
            }
                break;
            case PAGE2:
            {
                // UButton
                button1 = new UButtonText(this, UButtonType.Press, ButtonId.Edit.ordinal(), BUTTON_PRIORITY,
                        "edit", 100, y,
                        getWidth() - 100*2, 120,
                        50, Color.WHITE, Color.rgb(0,128,0));
                drawManager.addDrawable(button1);
                y += 120 + 50;

                // TextView
                for (int i=0; i<6; i++) {
                    UAlignment alignment = UAlignment.None;
                    switch (i) {
                        case 1:
                            alignment = UAlignment.CenterX;
                            break;
                        case 2:
                            alignment = UAlignment.CenterY;
                            break;
                        case 3:
                            alignment = UAlignment.Center;
                            break;
                        case 4:
                            alignment = UAlignment.Right;
                            break;
                        case 5:
                            alignment = UAlignment.Right_CenterY;
                            break;
                    }
                    String text = "hoge" + (i + 1);

                    addTextView(text, TEXT_SIZE, TEXT_PRIORITY, alignment, false, true,
                            getWidth() / 2, y, UColor.getRandomColor(), UColor
                                    .getRandomColor());
                    y += 150;
                }

            }

                break;
            case PAGE3:
            {
                // Multi line
                addTextView("abl\nbbb", TEXT_SIZE, TEXT_PRIORITY,
                        UAlignment.None, true, false,
                        getWidth() / 2, y, UColor.getRandomColor(), UColor
                                .getRandomColor());
                y += 200;

                addTextView("abl\nbbb", TEXT_SIZE, TEXT_PRIORITY,
                        UAlignment.CenterX, true, false,
                        getWidth() / 2, y, UColor.getRandomColor(), UColor
                                .getRandomColor());
                y += 200;
                addTextView("abl\nbbb", TEXT_SIZE, TEXT_PRIORITY,
                        UAlignment.CenterY, true, false,
                        getWidth() / 2, y, UColor.getRandomColor(), UColor
                                .getRandomColor());
                y += 200;

                addTextView("abl\nbbb", TEXT_SIZE, TEXT_PRIORITY,
                        UAlignment.Center, true, false,
                        getWidth() / 2, y, UColor.getRandomColor(), UColor
                                .getRandomColor());
                y += 200;

            }
                break;
            case PAGE4:
            {
                // Multi line
                addTextView("aaa\nbbb", TEXT_SIZE, TEXT_PRIORITY,
                        UAlignment.None, true, true,
                        getWidth() / 2, y, UColor.getRandomColor(), UColor
                                .getRandomColor());
                y += 200;

                addTextView("aaa\nbbb", TEXT_SIZE, TEXT_PRIORITY,
                        UAlignment.CenterX, true, true,
                        getWidth() / 2, y, UColor.getRandomColor(), UColor
                                .getRandomColor());
                y += 200;
                addTextView("aaa\nbbb", TEXT_SIZE, TEXT_PRIORITY,
                        UAlignment.CenterY, true, true,
                        getWidth() / 2, y, UColor.getRandomColor(), UColor
                                .getRandomColor());
                y += 200;

                addTextView("aaa\nbbb", TEXT_SIZE, TEXT_PRIORITY,
                        UAlignment.Center, true, true,
                        getWidth() / 2, y, UColor.getRandomColor(), UColor
                                .getRandomColor());
                y += 200;

            }
                break;
            case PAGE5:
            {
                // EditText
                editText = UEditText.createInstance(this, this, "aaa", TEXT_SIZE, 71,
                        UAlignment.None, getWidth(), false,
                        100, y, 300, Color.GREEN, Color.argb(128,0,
                                0,0));
                drawManager.addDrawable(editText);
                y += editText.getHeight() + 50;

                // UTextViewOpenClose
                textView2 = UTextViewOpenClose.createInstance(
                        "aaa\naaa\naaa\naaa", 71, TEXT_SIZE, UAlignment.None,
                        getWidth(),
                        100, y, 300, Color.GREEN, Color.argb(128,0,
                                0,0));
                drawManager.addDrawable(textView2);
                y += textView2.getHeight() + 50;
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
    public boolean UButtonClicked(int id, boolean pressedOn) {
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
                UDrawManager.getInstance().setCurrentPage(PAGE1);
                break;
            case PageButtonId + 1:
                UDrawManager.getInstance().setCurrentPage(PAGE2);
                break;
            case PageButtonId + 2:
                UDrawManager.getInstance().setCurrentPage(PAGE3);
                break;
            case PageButtonId + 3:
                UDrawManager.getInstance().setCurrentPage(PAGE4);
                break;
            case PageButtonId + 4:
                UDrawManager.getInstance().setCurrentPage(PAGE5);
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
