package com.sunsunsoft.shutaro.ugui;

import android.content.Intent;
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
        R.id.buttonHome,
        R.id.buttonButton,
        R.id.buttonTextview,
        R.id.buttonMenuBar,
        R.id.buttonLog,
        R.id.buttonWindow,
        R.id.buttonDialog,
            R.id.buttonTouch,
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

        // Bitmapマネージャー生成
        UResourceManager.createInstance(getContext(), view);

        return view;
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.buttonHome:
            {
                Intent i = new Intent(getContext().getApplicationContext(),SubActivity.class);
                i.putExtra("testMode", 1);
                startActivity(i);
            }
                break;
            case R.id.buttonButton:
            {
                Fragment fragment = new FragmentTestButton();
                showFragment(fragment);
            }
                break;
            case R.id.buttonTextview:
            {
                Intent i = new Intent(getContext().getApplicationContext(),SubActivity.class);
                i.putExtra("testMode", 3);
                startActivity(i);
            }
            break;
            case R.id.buttonMenuBar:
            {
                Fragment fragment = new FragmentTestMenubar();
                showFragment(fragment);
            }
                break;
            case R.id.buttonLog:
            {
                Fragment fragment = new FragmentTestLogWindow();
                showFragment(fragment);
            }
            break;
            case R.id.buttonWindow:
            {
                Fragment fragment = new FragmentTestWindow();
                showFragment(fragment);
            }
            break;
            case R.id.buttonDialog:
            {
                Fragment fragment = new FragmentTestDialog();
                showFragment(fragment);
            }
            break;
            case R.id.buttonTouch:
            {
                Fragment fragment = new FragmentTestTouch();
                showFragment(fragment);
            }
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
