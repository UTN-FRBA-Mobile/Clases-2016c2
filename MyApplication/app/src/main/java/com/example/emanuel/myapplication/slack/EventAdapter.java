package com.example.emanuel.myapplication.slack;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.emanuel.myapplication.Forecast;
import com.example.emanuel.myapplication.R;
import com.example.emanuel.myapplication.slack.response.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by emanuel on 25/9/16.
 */
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    List<Event> items = new ArrayList<>();

    public void addItem(Event item) {
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView)itemView.findViewById(R.id.textView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.celda, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Event item = items.get(position);
        holder.textView.setText(String.valueOf(item.toString()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
