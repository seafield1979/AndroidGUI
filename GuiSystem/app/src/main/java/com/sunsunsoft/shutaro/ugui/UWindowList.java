package com.sunsunsoft.shutaro.ugui;

import java.util.LinkedList;

/**
 * UWindowのサブクラスを管理するクラス
 * 機能
 *   描画順を管理(同じ優先度のWindowを登録した場合のみ)
 *   処理順を管理(手前のWindowからタッチ処理を行う)
 *   他のWindowで隠れていたら描画をスキップ
 */

public class UWindowList {
    public static final String TAG = "UWindowList";

    // シングルトン
    private static UWindowList singleton = new UWindowList();


    /**
     * メンバ変数
     */
    // UWindowのリスト
    // 後のリストの方が描画優先度が高い
    LinkedList<UWindow> lists = new LinkedList<>();


    /**
     * Get/Set
     */
    public LinkedList<UWindow> getLists() {
        return lists;
    }

    /**
     * コンストラクタ
     */
    private UWindowList() {
    }

    /**
     * シングルトンのインスタンスを取得
     * @return
     */
    public static UWindowList getInstance() {
        return singleton;
    }

    /**
     * リストに追加
     * @param window
     */
    public void add(UWindow window) {
        lists.remove(window);
        lists.add(window);
        // 描画優先度を変更する
        UDrawManager.getInstance().addDrawable(window);
    }

    /**
     * リストから削除
     * @param window
     */
    public void remove(UWindow window) {
        lists.remove(window);
    }

    /**
     * アクション
     * @return true:再描画
     */
    public boolean doAction() {
        boolean redraw = false;
        for (UWindow window : lists) {
            if (window.doAction()) {
                redraw = true;
            }
        }
        return redraw;
    }
}
