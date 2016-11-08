package com.sunsunsoft.shutaro.udrawsystem;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.LinkedList;
import java.util.TreeMap;

/**
 * 描画オブジェクトを管理するクラス
 * 描画するオブジェクトを登録すると一括で描画を行ってくれる
 *
 */
public class DrawManager {
    public static final String TAG = "DrawManager";

    // 同じプライオリティーのDrawableリストを管理するリスト
    TreeMap<Integer, DrawList> lists = new TreeMap<>();

    public boolean addDrawable(int priority, Drawable obj) {
        // 挿入するリストを探す
        Integer _priority = new Integer(priority);
        DrawList list = lists.get(_priority);
        if (list == null) {
            // まだ存在していないのでリストを生成
            list = new DrawList(priority);
            lists.put(_priority, list);
        }
        list.add(obj);
        obj.setDrawList(list);
        return true;
    }


    /**
     * 追加済みのオブジェクトのプライオリティーを変更する
     * @param obj
     * @param priority
     */
    public void setPriority(Drawable obj, int priority) {
        // 探す
        for (Integer pri : lists.descendingKeySet()) {
            DrawList list = lists.get(pri);
            if (list.contains(obj)) {
                if (pri == priority) {
                    // すでに同じPriorityにいたら末尾に移動
                    list.toLast(obj);
                }
                else {
                    list.remove(obj);
                    addDrawable(priority, obj);
                    return;
                }
            }
        }
    }

    /**
     * 配下の描画オブジェクトを全て描画する
     * @param canvas
     * @param paint
     * @return true:再描画あり / false:再描画なし
     */
    public boolean draw(Canvas canvas, Paint paint) {
        boolean redraw = false;

        for (DrawList list : lists.descendingMap().values()) {
            if (list.draw(canvas, paint) ) {
                redraw = true;
            }
        }
        return redraw;
    }
}

/**
 * 描画オブジェクトのリストを管理するクラス
 * プライオリティーやクリップ領域を持つ
 */
class DrawList
{
    // 描画範囲 この範囲外には描画しない
    public Rect clipRect;
    private int priority;
    private LinkedList<Drawable> list = new LinkedList<>();

    public DrawList(int priority) {
        this.priority = priority;
    }

    // Get/Set

    public int getPriority() {
        return priority;
    }

    public Rect getClipRect() {
        return clipRect;
    }

    public void setClipRect(Rect clipRect) {
        this.clipRect = clipRect;
    }

    public void add(Drawable obj) {
        list.add(obj);
    }

    public void remove(Drawable obj) {
        list.remove(obj);
    }

    public void toLast(Drawable obj) {
        list.remove(obj);
        list.add(obj);
    }

    /**
     * Is contain in list
     * @param obj
     * @return
     */
    public boolean contains(Drawable obj) {
        for (Drawable _obj : list) {
            if (obj == _obj) {
                return true;
            }
        }
        return false;
    }

    /**
     * リストの描画オブジェクトを描画する
     * @param canvas
     * @param paint
     * @return true:再描画あり (まだアニメーション中のオブジェクトあり)
     */
    public boolean draw(Canvas canvas, Paint paint) {
        if (clipRect != null) {
            // クリッピング領域を設定
            canvas.save();
            canvas.clipRect(clipRect);
        }

        // 分けるのが面倒なのでアニメーションと描画を同時に処理する
        boolean allDone = true;

        for (Drawable obj : list) {
            Rect objRect = obj.getRect();

            boolean isDraw = true;

            // rectが設定されていたらクリッピング処理を行う
            if (clipRect != null && !(clipRect.intersect(objRect))) {
                // 全く重なっていなかったら描画しない
                isDraw = false;
            }
            if (isDraw) {
                if (obj.animate()) {
                    allDone = false;
                }
                obj.draw(canvas, paint);
            }
        }
        if (clipRect != null) {
            // クリッピング解除
            canvas.restore();
        }
        return !allDone;
    }
}