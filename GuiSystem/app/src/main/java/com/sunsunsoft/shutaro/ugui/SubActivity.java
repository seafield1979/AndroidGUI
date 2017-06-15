package com.sunsunsoft.shutaro.ugui;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.sunsunsoft.shutaro.ugui.fragment.FragmentTestTextview;
import com.sunsunsoft.shutaro.ugui.fragment.FragmentTop;
import com.sunsunsoft.shutaro.ugui.util.ULog;
import com.sunsunsoft.shutaro.ugui.uview.UDrawManager;

public class SubActivity extends AppCompatActivity {

    private MyFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        // 引数を受け取る
        Intent i = getIntent();
        int testMode = i.getIntExtra("testMode", 1);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            // コンテナにMainFragmentを格納
            switch(testMode) {
                case 1: {
                    fragment = new FragmentTop();
                    transaction.add(R.id.fragment_container, fragment, FragmentTop.FRAMGMENT_NAME);
                }
                    break;
                case 3: {
                    fragment = new FragmentTestTextview();
                    transaction.add(R.id.fragment_container, fragment, FragmentTestTextview
                            .TAG);
                }
                    break;
            }
            // 画面に表示
            transaction.commit();

            UDrawManager.getInstance().init();
            ULog.init();
        }
    }

    /**
     *  Androidのキーイベント
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode != KeyEvent.KEYCODE_BACK){
            return super.onKeyDown(keyCode, event);
        } else {
            // 戻るボタン
            if (fragment.onBackKeyDown()) {
                return true;
            }
            return super.onKeyDown(keyCode, event);
        }
    }

}
