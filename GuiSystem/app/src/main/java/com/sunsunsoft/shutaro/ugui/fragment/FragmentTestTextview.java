package com.sunsunsoft.shutaro.ugui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sunsunsoft.shutaro.ugui.DetectableKeyboardEventLayout;
import com.sunsunsoft.shutaro.ugui.MyFragment;
import com.sunsunsoft.shutaro.ugui.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentTestTextview extends MyFragment {
    public static final String TAG = "FragmentTestTextview";

    public FragmentTestTextview() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_test_textview, container, false);

        TestTextView textView = ((TestTextView)view.findViewById(R.id.textView));

        // キーボードのON/OFFイベントの検出
        DetectableKeyboardEventLayout root = (DetectableKeyboardEventLayout)view.findViewById(R.id
                .top_layout);
        root.setKeyboardListener( new DetectableKeyboardEventLayout.KeyboardListener() {
            @Override
            public void onKeyboardShown() {
                Log.d(TAG, "keyboard shown");
            }
            @Override
            public void onKeyboardHidden() {
                Log.d(TAG, "keyboard hidden");
            }
        });
        return view;
    }

    /**
     * MyFragment
     */

    /**
     * 戻るボタンが押された時の処理
     * @return false:デフォルトの処理を行う
     */
    public boolean onBackKeyDown() {
        return false;
    }
}
