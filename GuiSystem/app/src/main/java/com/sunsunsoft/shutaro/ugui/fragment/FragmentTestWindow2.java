package com.sunsunsoft.shutaro.ugui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sunsunsoft.shutaro.ugui.R;

public class FragmentTestWindow2 extends Fragment {


    public FragmentTestWindow2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_test_window2, container, false);
    }

}
