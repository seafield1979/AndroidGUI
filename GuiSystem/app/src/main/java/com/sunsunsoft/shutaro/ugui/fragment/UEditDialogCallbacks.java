package com.sunsunsoft.shutaro.ugui.fragment;

import android.os.Bundle;

/**
 * Created by shutaro on 2017/06/15.
 * UEditDialogのコールバック処理
 */
public interface UEditDialogCallbacks {
    void submit(Bundle args);
    void cancel();
}
