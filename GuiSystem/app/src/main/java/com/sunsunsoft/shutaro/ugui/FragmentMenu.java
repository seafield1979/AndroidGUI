package com.sunsunsoft.shutaro.ugui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;


public class FragmentMenu extends Fragment implements OnClickListener{
    public static final String FRAMGMENT_NAME = FragmentTop.class.getName();

    int[] buttonIds = {
        R.id.button,
        R.id.button2,
        R.id.button3,
        R.id.button4,
        R.id.button5
    };

    public FragmentMenu() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        for (int id : buttonIds) {
            ((Button)view.findViewById(id)).setOnClickListener(this);
        }

        return view;
    }

    public void onClick(View v) {
        // 描画オブジェクトをクリア
        DrawManager.getInstance().init();

        switch(v.getId()) {
            case R.id.button:
            {
                Fragment fragment = new FragmentTop();
                showFragment(fragment);
            }
                break;
            case R.id.button2:
            {
                Fragment fragment = new FragmentTestButton();
                showFragment(fragment);
            }
                break;
            case R.id.button3:
                break;
            case R.id.button4:
                break;
            case R.id.button5:
                break;
        }
    }

    private void showFragment(Fragment fragment) {
        // FragmentManagerからFragmentTransactionを作成
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        // Fragmentを組み込む
        transaction.replace(R.id.fragment_container, fragment);
        // backstackに追加
        transaction.addToBackStack(null);
        // 上記の変更を反映する
        transaction.commit();

    }
}
