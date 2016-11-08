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
            // データを渡す為のBundleを生成し、渡すデータを内包させる
            Bundle bundle = new Bundle();
            bundle.putString("URL", "http://hogehoge.com");

            // Fragmentを生成し、setArgumentsで先ほどのbundleをセットする
            FragmentTop fragment = new FragmentTop();
            fragment.setArguments(bundle);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            // コンテナにMainFragmentを格納
            transaction.add(R.id.fragment_container, fragment, FragmentTop.FRAMGMENT_NAME);
            // 画面に表示
            transaction.commit();
        }
    }
}
