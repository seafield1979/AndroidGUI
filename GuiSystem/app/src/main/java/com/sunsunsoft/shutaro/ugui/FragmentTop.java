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
public class FragmentTop extends MyFragment implements View.OnTouchListener {
    public static final String FRAMGMENT_NAME = FragmentTop.class.getName();
    private final static String BACKGROUND_COLOR = "background_color";
    private final static String KEY_PAGE = "page";

    private TopView topView;

    public static FragmentTop newInstance(@ColorRes int IdRes, PageView page) {
        FragmentTop frag = new FragmentTop();
        Bundle b = new Bundle();
        b.putInt(BACKGROUND_COLOR, IdRes);
        b.putInt(KEY_PAGE, page.ordinal());
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
        Bundle bundle = getArguments();
        String url = bundle.getString("URL");

        // Viewを追加
        topView = new TopView(getContext());
        LinearLayout containerView = (LinearLayout)view.findViewById(R.id.view_container);
        containerView.addView(topView);

        return view;
    }

    public boolean onTouch(View v, MotionEvent e) {
        return true;
    }


    /**
     * MyFragment
     */
    public boolean onBackKeyDown() {
        if (topView.onBackKeyDown()) {
            topView.invalidate();
            return true;
        }
        return false;
    }
}