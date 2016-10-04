package com.example.emanuel.myapplication;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.example.emanuel.myapplication.slack.SlackActivity;

public class MainActivity extends AppCompatActivity implements MainFragment.Listener {

    boolean splitScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.acaVaElFragment, new MainFragment(), "Fragment");
            transaction.commit();
        }
        splitScreen = findViewById(R.id.segundoFragment) != null;
    }

    @Override
    public void navigateToDetails(int tapCount) {
        RecyclerFragment fragment = new RecyclerFragment();
        /*
        Bundle arguments = new Bundle();
        arguments.putInt(MainFragment.TAP_COUNT, tapCount);
        fragment.setArguments(arguments);
        */
        if (splitScreen) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.segundoFragment, fragment)
                    .commit();
        }
        else {
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.push_show, R.anim.push_hide, R.anim.pop_show, R.anim.pop_hide)
                    .replace(R.id.acaVaElFragment, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void showOtroFragment() {
        Intent intent = new Intent(this, SlackActivity.class);
        startActivity(intent);
    }

    @Override
    public void showTermsAndConditions() {
        Intent intent = new Intent(this, TermsAndConditionsActivity.class);
        startActivity(intent);
    }
}
