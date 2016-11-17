package com.sunsunsoft.shutaro.ugui;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            FragmentMenu fragment = new FragmentMenu();

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            // コンテナにMainFragmentを格納
            transaction.add(R.id.fragment_container, fragment, FragmentMenu.FRAMGMENT_NAME);
            // 画面に表示
            transaction.commit();

            UDrawManager.getInstance().init();
            ULog.init();
        }
    }
}
