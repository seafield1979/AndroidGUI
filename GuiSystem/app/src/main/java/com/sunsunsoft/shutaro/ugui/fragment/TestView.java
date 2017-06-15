package com.sunsunsoft.shutaro.ugui.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.sunsunsoft.shutaro.ugui.pageview.*;
import com.sunsunsoft.shutaro.ugui.pageview.PageViewManager;
import com.sunsunsoft.shutaro.ugui.uview.button.*;
import com.sunsunsoft.shutaro.ugui.uview.UDrawManager;
import com.sunsunsoft.shutaro.ugui.util.ULog;
import com.sunsunsoft.shutaro.ugui.uview.UPageViewManager;
import com.sunsunsoft.shutaro.ugui.*;


/**
 * Created by shutaro on 2016/12/09.
 */

public class TestView extends View
        implements View.OnTouchListener, UButtonCallbacks, ViewTouchCallbacks
{
    public static final String TAG = "TestView";
    private static final int BUTTON_PRIORITY = 100;

    /**
     * Member variables
     */

    // サイズ更新用
    private boolean isFirst = true;

    private UPageViewManager mPageManager;
    private Context mContext;
    private Paint paint = new Paint();
    // クリック判定の仕組み
    private ViewTouch vt;

    /**
     * Get/Set
     */
    public TestView(Context context, PageView pageView) {
        this(context, null, pageView);
    }

    public TestView(Context context, AttributeSet attrs, PageView pageView) {
        super(context, attrs);
        this.setOnTouchListener(this);

        vt = new ViewTouch(this);
        mContext = context;

        mPageManager = PageViewManager.createInstance(context, this);

        mPageManager.stackPage(pageView);
    }

    /**
     * 画面に表示するオブジェクトを生成
     * @param width
     * @param height
     */
    private void initDrawables(int width, int height) {
        // 描画オブジェクトクリア
        UDrawManager.getInstance().init();


    }

    @Override
    public void onDraw(Canvas canvas) {
        mPageManager.draw(canvas, paint);

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
        ULog.print(TAG, "button click:" + id);

        return false;
    }

    /**
     * ViewTouchCallbacks
     */
    public void longPressed() {
        ((Activity)mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UDrawManager.getInstance().touchEvent(vt);
                invalidate();
            }
        });
    }
}
