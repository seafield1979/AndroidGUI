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

    // 同じプライオリティーのDrawableリストを管理するリスト
    TreeMap<Integer, DrawList> lists = new TreeMap<>();

    public boolean add(int priority, Drawable obj) {
        // 挿入するリストを探す
        Integer _priority = new Integer(priority);
        DrawList list = lists.get(_priority);
        if (list == null) {
            // まだ存在していないのでリストを生成
            list = new DrawList();
            lists.put(_priority, list);
        }
        list.add(obj);
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
                    add(priority, obj);
                    return;
                }
            }
        }
    }

    public void draw(Canvas canvas, Paint paint) {
        for (DrawList list : lists.descendingMap().values()) {
            list.draw(canvas, paint);
        }
    }
}

/**
 * 描画オブジェクトのリストを管理するクラス
 * プライオリティーやクリップ領域を持つ
 */
class DrawList
{
    // 描画範囲 この範囲外には描画しない
    public Rect rect;
    private LinkedList<Drawable> list = new LinkedList<>();

    // Get/Set
    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect clipRect) {
        this.rect = clipRect;
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

    public void draw(Canvas canvas, Paint paint) {
        if (rect != null) {
            // クリッピング領域を設定
            canvas.save();
            canvas.clipRect(rect);
        }
        for (Drawable obj : list) {
            Rect objRect = obj.getRect();
            if (rect != null) {
                if (rect.intersect(objRect)) {
                    obj.draw(canvas, paint);
                }
            } else {
                obj.draw(canvas, paint);
            }
        }
        if (rect != null) {
            // クリッピング解除
            canvas.restore();
        }
    }
}