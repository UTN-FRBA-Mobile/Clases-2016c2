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

    public static final String TAP_COUNT = "tapCount";

    public interface Listener {

        void navigateToDetails(int tapCount);
        void showOtroFragment();
        void showTermsAndConditions();
    }

    TextView textView;
    View navigateButton;
    int tapCount = 0;
    Listener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            tapCount = arguments.getInt(TAP_COUNT, tapCount);
        }
        if (savedInstanceState != null) {
            tapCount = savedInstanceState.getInt(TAP_COUNT, tapCount);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listener = (Listener) getActivity();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textView = (TextView) view.findViewById(R.id.elTextView);
        if (tapCount != 0) {
            textView.setText(getString(R.string.tap_n_times, tapCount));
        }
        else {
            textView.setText(R.string.hola_mundo);
        }
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tapCount++;
                textView.setText(getString(R.string.tap_n_times, tapCount));
            }
        });
        navigateButton = view.findViewById(R.id.navigateButton);
        navigateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.navigateToDetails(tapCount);
            }
        });
        view.findViewById(R.id.otroFragmentButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.showOtroFragment();
            }
        });
        view.findViewById(R.id.termsAndConditionsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.showTermsAndConditions();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(TAP_COUNT, tapCount);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }
}
