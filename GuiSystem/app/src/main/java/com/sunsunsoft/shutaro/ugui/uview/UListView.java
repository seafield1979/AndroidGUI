package com.sunsunsoft.shutaro.ugui.uview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import com.sunsunsoft.shutaro.ugui.ViewTouch;
import com.sunsunsoft.shutaro.ugui.uview.scrollbar.UScrollWindow;
import com.sunsunsoft.shutaro.ugui.uview.window.UWindowCallbacks;

import java.util.LinkedList;

/**
 * Created by shutaro on 2016/12/09.
 *
 * ListView
 * UListItemを縦に並べて表示する
 */

public class UListView extends UScrollWindow
{
    /**
     * Enums
     */
    /**
     * Constants
     */
    public static final int MARGIN_V = 20;

    /**
     * Member variables
     */
    protected LinkedList<UListItem> mItems = new LinkedList<>();
    protected UListItemCallbacks mListItemCallbacks;
    protected Rect mClipRect;

    // リストの最後のアイテムの下端の座標
    protected float mBottomY;


    /**
     * Get/Set
     */

    /**
     * Constructor
     */
    public UListView(UWindowCallbacks callbacks,
                     UListItemCallbacks listItemCallbacks,
                     int priority, float x, float y, int width, int
                             height, int color)
    {
        super(callbacks, priority, x, y, width, height, color, 0, 0, 30);
        mListItemCallbacks = listItemCallbacks;
        mClipRect = new Rect();
    }

    /**
     * Methods
     */
    public UListItem get(int index) {
        return mItems.get(index);
    }


    public void add(UListItem item) {
        item.setPos(0, mBottomY);
        item.setIndex(mItems.size());
        item.setListItemCallbacks(mListItemCallbacks);

        mItems.add(item);
        mBottomY += item.size.height;

        contentSize.height = (int)mBottomY;
    }

    public void remove(UListItem item) {
        int index = mItems.indexOf(item);
        removeCore(item, index);
    }

    public void remove(int index) {
        UListItem item = mItems.get(index);
        removeCore(item, index);
    }

    protected void removeCore(UListItem item, int index) {
        if (index == -1 || item == null) return;

        float y = item.pos.y;
        mItems.remove(index);
        // 削除したアイテム以降のアイテムのIndexと座標を詰める
        for (int i = index; i< mItems.size(); i++) {
            UListItem _item = mItems.get(i);
            _item.setIndex(i);
            _item.pos.y = y;
            y = _item.getBottom();
        }
        mBottomY = y;
    }


    public void drawContent(Canvas canvas, Paint paint, PointF offset) {
        // BG
        drawBG(canvas, paint);

        // クリッピング前の状態を保存
        canvas.save();

        PointF _pos = new PointF(pos.x + offset.x, pos.y + offset.y);
        // クリッピングを設定
        mClipRect.left = (int)_pos.x;
        mClipRect.right = (int)_pos.x + clientSize.width;
        mClipRect.top = (int)_pos.y;
        mClipRect.bottom = (int)_pos.y + clientSize.height;

        canvas.clipRect(mClipRect);

        // アイテムを描画
        PointF _offset = new PointF(_pos.x, _pos.y - contentTop.y);
        for (UListItem item : mItems) {
            if (item.getBottom() < contentTop.y) continue;

            item.draw(canvas, paint, _offset);

            if (item.pos.y + item.size.height > contentTop.y + size.height) {
                // アイテムの下端が画面外にきたので以降のアイテムは表示されない
                break;
            }
        }

        // クリッピングを解除
        canvas.restore();
    }

    public boolean touchEvent(ViewTouch vt) {
        // アイテムのクリック判定処理
        PointF offset = new PointF(pos.x, pos.y - contentTop.y);
        boolean isDraw = false;

        for (UListItem item : mItems) {
            if (item.touchEvent(vt, offset)) {
                isDraw = true;
            }
        }

        if (super.touchEvent(vt, offset)) {
            isDraw = true;
        }
        return isDraw;
    }

    /**
     * for Debug
     */
    public void addDummyItems(int count) {

        for (int i=0; i<count; i++){
            ListItemTest1 item = new ListItemTest1(null, "hoge", 0, size.width, Color.BLUE);
            add(item);
        }
        updateWindow();
    }
}
