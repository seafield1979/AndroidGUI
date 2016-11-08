package com.sunsunsoft.shutaro.udrawsystem;


import android.graphics.PointF;
import android.text.method.Touch;
import android.util.Log;
import android.view.MotionEvent;

import java.util.Timer;
import java.util.TimerTask;

enum TouchType {
    None,
    Touch,        // タッチ開始
    Click,        // ただのクリック（タップ)
    LongClick,    // 長クリック
    Moving,       // 移動
    MoveEnd,      // 移動終了
    MoveCancel    // 移動キャンセル
}
/**
 * View上のタッチ処理を判定する
 *
 */
public class ViewTouch {
    private static final String TAG = "ViewTouch";

    // クリック判定するためのタッチ座標誤差
    public static final int CLICK_DISTANCE = 30;

    // ロングクリックの時間(ms)
    public static final int LONG_CLICK_TIME = 300;

    // 移動前の待機時間(ms)
    public static final int MOVE_START_TIME = 100;

    // 長押しまでの時間(ms)
    public static final int LONG_TOUCH_TIME = 1000;

    public TouchType type;

    private Timer timer;

    // タッチ中にtrueになる
    private boolean isTouching;
    private boolean isLongTouch;

    // タッチ開始した座標
    private float touchX, touchY;

    protected float x, y;       // スクリーン座標
    float moveX, moveY;
    private boolean isMoveStart;

    // タッチ開始した時間
    long touchTime;

    // get/set
    public float getX() { return x; }
    public float getY() { return y; }
    public float getX(float offset) { return x + offset; }
    public float getY(float offset) { return y + offset; }
    public float touchX() {return this.touchX;}
    public float touchY() {return this.touchY;}
    public boolean isMoveStart() { return isMoveStart; }

    public ViewTouch() {
        type = TouchType.None;
    }

    /**
     * ロングタッチがあったかどうかを取得する
     * このメソッドを呼ぶと内部のフラグをクリア
     * @return true:ロングタッチ
     */
    public boolean checkLongTouch() {
        // ロングタッチが検出済みならそれを返す
        if (isLongTouch) {
            MyLog.print("viewtouch", "Long Touch");
            isLongTouch = false;
            return true;
        }
        return false;
    }

    public TouchType checkTouchType(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
            {
                MyLog.print("viewtouch", "Touch Down");

                isTouching = true;
                touchX = e.getX();
                touchY = e.getY();
                type = TouchType.Touch;
                touchTime = System.currentTimeMillis();
                startLongTouchTimer();
            }
            break;
            case MotionEvent.ACTION_UP:
            {
                MyLog.print("viewtouch", "Up");
                isTouching = false;

                timer.cancel();

                if (type == TouchType.Moving) {
                    MyLog.print("viewtouch", "MoveEnd");
                    type = TouchType.MoveEnd;
                    return type;
                } else {
                    float x = (e.getX() - touchX);
                    float y = (e.getY() - touchY);
                    float dist = (float) Math.sqrt(x * x + y * y);

                    if (dist <= CLICK_DISTANCE) {
                        long time = System.currentTimeMillis() - touchTime;

                        if (time <= LONG_CLICK_TIME) {
                            type = TouchType.Click;
                            MyLog.print("viewtouch", "SingleClick");
                        } else {
                            type = TouchType.LongClick;
                            MyLog.print("viewtouch", "LongClick");
                        }
                    } else {
                        type = TouchType.None;
                    }
                }
            }
            break;
            case MotionEvent.ACTION_MOVE:
                // クリックが判定できるようにタッチ時間が一定時間以上、かつ移動距離が一定時間以上で移動判定される
                isMoveStart = false;
                if ( type != TouchType.Moving) {
                    float dx = (e.getX() - touchX);
                    float dy = (e.getY() - touchY);
                    float dist = (float) Math.sqrt(dx * dx + dy * dy);

                    if (dist >= CLICK_DISTANCE) {
                        long time = System.currentTimeMillis() - touchTime;
                        if (time >= MOVE_START_TIME) {
                            type = TouchType.Moving;
                            isMoveStart = true;
                            x = touchX;
                            y = touchY;
                        }
                    }
                }
                if ( type == TouchType.Moving) {
                    moveX = e.getX() - x;
                    moveY = e.getY() - y;
                }
                x = e.getX();
                y = e.getY();

                break;
            case MotionEvent.ACTION_CANCEL:
                MyLog.print("viewtouch", "Cancel");
                if (type == TouchType.Moving) {
                    type = TouchType.None;
                    return TouchType.MoveCancel;
                }
                break;
        }

        return type;
    }

    /**
     * ロングタッチ検出用のタイマーを開始
     */
    private void startLongTouchTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask(){
            @Override
            public void run() {
                timer.cancel();
                if (isTouching) {
                    // ロングタッチを検出する
                    isLongTouch = true;
                    MyLog.print("viewtouch", "timer Long Touch");
                }
            }
        }, LONG_TOUCH_TIME, 1000);
    }
}
