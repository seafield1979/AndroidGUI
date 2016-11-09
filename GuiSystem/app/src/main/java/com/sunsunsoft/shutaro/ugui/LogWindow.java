package com.sunsunsoft.shutaro.ugui;


import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * メッセージを表示するWindow
 * メッセージをリストで保持する
 * 古いメッセージが一定時間で削除される
 */
public class LogWindow extends Window {
    public static final int SHOW_TIME = 3000;
    public static final int MESSAGE_MAX = 30;

    private LinkedList<LogData> logs = new LinkedList<>();
    private Timer timer;
    private View parentView;
    private Context context;
    private int count = 1;

    private LogWindow(float x, float y, int width, int height, int color)
    {
        super(0, 0, width, height, color);

    }

    public static LogWindow createInstance(Context context, View parentView, int width, int
            height, int bgColor)
    {
        LogWindow instance = new LogWindow(0, 0, width, height, bgColor);
        instance.parentView = parentView;
        instance.context = context;
        instance.isShow = false;
        instance.startTimer(SHOW_TIME);
        return instance;
    }

    /**
     * メッセージを追加する
     * @param text
     * @param color
     */
    public void addMessage(String text, int color) {
        LogData msg = new LogData("" + count + ": " + text, color);
        logs.push(msg);
        if (logs.size() > MESSAGE_MAX) {
            logs.removeLast();
        }
        isShow = true;
        startTimer(SHOW_TIME);
        count++;
    }

    /**
     * 毎フレーム行う処理
     *
     * @return true:描画を行う
     */
    public boolean doAction() {
        return false;
    }

    /**
     * 描画処理
     *
     * @param canvas
     * @param paint
     * @return trueなら描画継続
     */
    public boolean draw(Canvas canvas, Paint paint) {
        if (!isShow) return false;

        // 背景
        paint.setColor(bgColor);
        canvas.drawRect(rect, paint);

        // テキスト表示
        paint.setTextSize(30);
        paint.setAntiAlias(true);

        float drawX = 0;
        float drawY = 50;
        for (LogData msg : logs) {
            paint.setColor(msg.color);
            canvas.drawText(msg.text, pos.x + drawX, pos.y + drawY, paint);
            drawY += 30;
        }
        return false;
    }

    /**
     * タッチ処理
     * @param vt
     * @return trueならViewを再描画
     */
    public boolean touchEvent(ViewTouch vt) {
        if (!isShow) return false;

        // 範囲外なら除外
        if (!(rect.contains(vt.getX(), vt.getY()))) {
            return false;
        }

        switch (vt.type) {
            case Click:
                isShow = false;
                break;
            case Moving:
                if (vt.isMoveStart()) {
                }
                pos.x += vt.moveX;
                pos.y += vt.moveY;
                updateRect();
                break;
        }

        return true;
    }

    /**
     * ロングタッチ検出用のタイマーを開始
     */
    private void startTimer(int showTime) {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run() {
                if (isShow) {
                    // UIスレッドの処理
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            parentView.invalidate();
                        }
                    });
                    isShow = false;
                }
                timer.cancel();
            }
        }, showTime, showTime);
    }
}

/**
 * 表示メッセージ情報
 */
class LogData {
    String text;
    int color;

    public LogData(String text, int color) {
        this.text = text;
        this.color = color;
    }
}
