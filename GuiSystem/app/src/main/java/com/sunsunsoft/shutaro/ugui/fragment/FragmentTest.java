package com.sunsunsoft.shutaro.ugui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.sunsunsoft.shutaro.ugui.pageview.*;
import com.sunsunsoft.shutaro.ugui.R;
import com.sunsunsoft.shutaro.ugui.uview.UResourceManager;

/**
 * Created by shutaro on 2016/12/09.
 */

public class FragmentTest extends Fragment {

    /**
     * Constants
     */
    public static final String KEY_PAGE = "page";

    /**
     * Member variables
     */
    /**
     * Constructor
     */
    public static FragmentTest newInstance(PageView page)
    {
        FragmentTest fragment = new FragmentTest();

        // パラメターを設定
        Bundle b = new Bundle();
        b.putInt(KEY_PAGE, page.ordinal());
        fragment.setArguments(b);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public FragmentTest() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test, container, false);
        UResourceManager.getInstance().setView(view);

        // パラメータを取得
        Bundle bundle = getArguments();
        PageView pageView = PageView.toEnum(bundle.getInt(KEY_PAGE, 0));

        // SurfaceViewを追加
        TestView testView = new TestView(getContext(), pageView);
        LinearLayout containerView = (LinearLayout)view.findViewById(R.id.fragment_container);
        containerView.addView(testView);

        return view;
    }

}
