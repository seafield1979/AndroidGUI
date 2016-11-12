package com.sunsunsoft.shutaro.ugui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentTestButton extends Fragment {


    public FragmentTestButton() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_button, container, false);

        // SurfaceViewを追加
        TestButtonView testView = new TestButtonView(getContext());
        LinearLayout containerView = (LinearLayout)view.findViewById(R.id.view_container);
        containerView.addView(testView);


        return view;
    }

}
