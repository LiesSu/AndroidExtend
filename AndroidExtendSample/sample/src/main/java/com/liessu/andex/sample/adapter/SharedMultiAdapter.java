package com.liessu.andex.sample.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.liessu.andex.sample.R;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class SharedMultiAdapter extends RecyclerView.Adapter<SharedMultiAdapter.SharedViewHolder> {
    private Context context;
    private List<Map.Entry<String , ?>> valueList;

    public SharedMultiAdapter() {
    }

    public SharedMultiAdapter(Context context, Map<String, ?> valueMap) {
        this.context = context;
        valueList = new LinkedList<>();
        valueList.addAll(valueMap.entrySet());
    }

    @Override
    public SharedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SharedViewHolder(LayoutInflater.from(context).inflate(R.layout.item_shared_multi_process, parent, false));
    }

    @Override
    public void onBindViewHolder(SharedViewHolder holder, int position) {
        holder.keyTextView.setText(valueList.get(position).getKey());
        holder.valueTextView.setText((CharSequence) valueList.get(position).getValue());
    }

    @Override
    public int getItemCount() {
        return valueList.size();
    }

    public class SharedViewHolder extends RecyclerView.ViewHolder {
        protected TextView keyTextView;
        protected TextView valueTextView;

        public SharedViewHolder(View itemView) {
            super(itemView);
            keyTextView = (TextView) itemView.findViewById(R.id.shared_key_tv);
            valueTextView  = (TextView) itemView.findViewById(R.id.shared_value_tv);
        }
    }
}
