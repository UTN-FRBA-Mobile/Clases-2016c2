package com.example.emanuel.myapplication;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by emanuel on 22/8/16.
 */
public class MainFragment extends Fragment {

    TextView textView;
    int tapCount = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textView = (TextView) view.findViewById(R.id.elTextView);
        textView.setText(R.string.hola_mundo);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tapCount++;
                textView.setText(getString(R.string.tap_n_times, tapCount));
            }
        });
    }
}
