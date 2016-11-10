package com.sunsunsoft.shutaro.ugui;

import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * SubViewのテスト
 * 今までViewがアイコンのWindowを管理していたが、２つに増やすためにIconWindowクラスを作成する
 */
public class FragmentTop extends Fragment implements View.OnTouchListener {
    public static final String FRAMGMENT_NAME = FragmentTop.class.getName();
    private final static String BACKGROUND_COLOR = "background_color";
    private TopView myView;

    public static FragmentTop newInstance(@ColorRes int IdRes) {
        FragmentTop frag = new FragmentTop();
        Bundle b = new Bundle();
        b.putInt(BACKGROUND_COLOR, IdRes);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top, null);

        myView = (TopView)view.findViewById(R.id.TopView);

        return view;
    }

    public boolean onTouch(View v, MotionEvent e) {
        return true;
    }

    //TouchEventCallbacks
    // 子Viewをタッチしている最中はスクロールしないようにする
    public void touchCallback(int action) {
    }
}