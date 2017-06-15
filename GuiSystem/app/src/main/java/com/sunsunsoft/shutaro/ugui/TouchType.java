package com.sunsunsoft.shutaro.ugui;

/**
 * Created by shutaro on 2017/06/15.
 */

public enum TouchType {
    None,
    Touch,        // タッチ開始
    LongPress,    // 長押し
    Click,        // ただのクリック（タップ)
    LongClick,    // 長クリック
    Moving,       // 移動
    MoveEnd,      // 移動終了
    MoveCancel    // 移動キャンセル
}
