package com.example.emanuel.myapplication.slack;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.example.emanuel.myapplication.R;

/**
 * Created by emanuel on 25/9/16.
 */
public class SlackActivity extends AppCompatActivity implements RealTimeFragment.Listener {

    private RTMService service;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slack);
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainer, new RealTimeFragment());
            transaction.commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, RTMService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (service != null) {
            unbindService(serviceConnection);
        }
    }

    @Override
    public void sendMessage(String channel, String text) {
        service.sendMessage(channel, text);
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder serviceBinder) {
            RTMService.Binder binder = (RTMService.Binder) serviceBinder;
            service = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            service = null;
        }
    };
}
