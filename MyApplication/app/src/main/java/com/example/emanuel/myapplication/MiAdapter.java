package com.example.emanuel.myapplication;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by emanuel on 5/9/16.
 */
public class MiAdapter extends RecyclerView.Adapter<MiAdapter.ViewHolder> {

    List<Forecast> items = new ArrayList<>();

    public void setItems(List<Forecast> items) {
        this.items = items;
        notifyDataSetChanged();
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
        Forecast item = items.get(position);
        holder.textView.setText(String.valueOf(item.getRain()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
