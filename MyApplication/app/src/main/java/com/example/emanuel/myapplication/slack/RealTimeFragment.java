package com.example.emanuel.myapplication.slack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.emanuel.myapplication.R;
import com.example.emanuel.myapplication.slack.response.Event;
import com.example.emanuel.myapplication.slack.response.MessageEvent;

/**
 * Created by emanuel on 25/9/16.
 */
public class RealTimeFragment extends Fragment {

    public interface Listener {
        void sendMessage(String channel, String text);
    }

    RecyclerView recyclerView;
    EditText messageField;
    EventAdapter adapter = new EventAdapter();
    Listener listener;
    String channel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_slack, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new EventItemAnimator());
        messageField = (EditText)view.findViewById(R.id.messageField);
        view.findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (Listener)context;
        context.registerReceiver(eventReceiver, new IntentFilter(RTMService.NewEventIntentAction));
    }

    @Override
    public void onDetach() {
        super.onDetach();
        getContext().unregisterReceiver(eventReceiver);
    }

    private void sendMessage() {
        if (channel != null) {
            String message = messageField.getText().toString();
            messageField.setText("");
            listener.sendMessage(channel, message);
        }
        else {
            Toast.makeText(getContext(), R.string.required_channel, Toast.LENGTH_LONG).show();
        }
    }

    private BroadcastReceiver eventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Event event = (Event) intent.getSerializableExtra(RTMService.EventExtraKey);
            adapter.addItem(event);
            if (event instanceof MessageEvent) {
                channel = ((MessageEvent)event).getChannel();
            }
            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
        }
    };
}
