package com.sunsunsoft.shutaro.ugui;

import java.util.LinkedList;

/**
 * UWindowのサブクラスを管理するクラス
 * 機能
 *   描画順を管理
 *   処理順を管理(手前のWindowからタッチ処理を行う)
 *   他のWindowで隠れていたら描画をスキップ
 */

public class UWindowList {
    // UWindowのリスト
    // 後のリストの方が描画優先度が高い
    LinkedList<UWindow> lists = new LinkedList<>();

    public void add(UWindow window) {
        // すでにリストに登録されていたら最前面に移動
        lists.add(window);

        // 描画優先度も変更する
        int priority = window.getDrawPriority();
    }
}
