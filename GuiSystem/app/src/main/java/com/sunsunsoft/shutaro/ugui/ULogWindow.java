package com.sunsunsoft.shutaro.ugui;


import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.view.View;

import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * メッセージを表示するWindow
 * メッセージをリストで保持する
 * 古いメッセージが一定時間で削除される
 */
public class ULogWindow extends UWindow {
    public static final int SHOW_TIME = 3000;
    public static final int MESSAGE_MAX = 30;
    public static final int DRAW_PRIORITY = 5;

    private LinkedList<LogData> logs = new LinkedList<>();
    private DrawList mDrawList;
    private Timer timer;
    private View parentView;
    private Context context;
    private int count = 1;

    private ULogWindow(Context context, View parentView, float x, float y, int width, int height, int color)
    {
        super(x, y, width, height, color);
        this.parentView = parentView;
        this.context = context;
        setShow(false);
        startTimer(SHOW_TIME);
    }

    /**
     * インスタンスを生成する
     * @param context
     * @param parentView
     * @param width
     * @param height
     * @return
     */
    public static ULogWindow createInstance(Context context, View parentView,
                                            float x, float y, int width, int height)
    {
        ULogWindow instance = new ULogWindow(context, parentView, x, y, width, height, Color.argb(128,0,0,0));
        instance.init();

        return instance;
    }

    private void init() {
        // 描画はDrawManagerに任せるのでDrawManagerに登録
        mDrawList = DrawManager.getInstance().addDrawable(DRAW_PRIORITY, this);
    }

    /**
     * メッセージを追加する
     * @param text
     * @param color
     */
    public void addLog(String text, int color) {
        LogData msg = new LogData("" + count + ": " + text, color);
        logs.push(msg);
        if (logs.size() > MESSAGE_MAX) {
            logs.removeLast();
        }
        setShow(true);
        startTimer(SHOW_TIME);
        count++;
    }

    /**
     * ログをクリアする
     */
    public void clear() {
        logs.clear();
    }

    /**
     * 表示のON/OFFを切り替える
     */
    public boolean toggle() {
        isShow = !isShow;
        return isShow;
    }

    /**
     * 毎フレーム行う処理
     *
     * @return true:描画を行う
     */
    public boolean doAction() {
        // 自動移動
        if (isMoving) {
            if (move()) {
                return true;
            }
        }
        return false;
    }


    /**
     * 描画オフセットを取得する
     * @return
     */
    public PointF getDrawOffset() {
        return null;
    }

    /**
     * タッチ処理
     * @param vt
     * @return trueならViewを再描画
     */
    public boolean touchEvent(ViewTouch vt) {
        if (!isShow) return false;

        // 範囲外なら除外
        if (!(rect.contains((int)vt.getX(), (int)vt.getY()))) {
            return false;
        }

        switch (vt.type) {
            case Click:
                setShow(false);
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

    /*
        Drawableインターフェースのメソッド
     */
    /**
     * 描画処理
     * @param canvas
     * @param paint
     */
    public void draw(Canvas canvas, Paint paint, PointF offset ) {
        if (!isShow) return;

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
    }

    /**
     * 描画範囲の矩形を取得
     * @return
     */
    public Rect getRect() {
        return rect;
    }

    public void setDrawList(DrawList drawList) {
        mDrawList = drawList;
    }
    public DrawList getDrawList() {
        return mDrawList;
    }

    /**
     * アニメーション開始
     */
    public void startAnim() {

    }

    /**
     * アニメーション処理
     * onDrawからの描画処理で呼ばれる
     * @return true:アニメーション中
     */
    public boolean animate() {
        return false;
    }

    /**
     * アニメーション中かどうか
     * @return
     */
    public boolean isAnimating() {
        return false;
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
