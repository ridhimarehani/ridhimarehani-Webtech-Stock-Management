package com.example.stocks;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChildRecyclerAdapter extends RecyclerView.Adapter<ChildRecyclerAdapter.ChildViewHolder> {

    List<List<String>> secItems;

    public ChildRecyclerAdapter(List<List<String>> secItems) {
        this.secItems = secItems;
    }

    @NonNull
    @Override
    public ChildViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater childLayoutInflator = LayoutInflater.from(parent.getContext());
        View childView = childLayoutInflator.inflate(R.layout.ticker_item,parent,false);
        return new ChildViewHolder(childView);
    }

    @Override
    public void onBindViewHolder(@NonNull ChildViewHolder holder, int position) {
        holder.tickerName.setText(secItems.get(position).get(0));
        holder.numShares.setText(secItems.get(position).get(1));

    }

    @Override
    public int getItemCount() {
        return secItems.size();
    }

    public class ChildViewHolder extends RecyclerView.ViewHolder{

        TextView tickerName,numShares;

        public ChildViewHolder(@NonNull View itemView) {
            super(itemView);
            tickerName = itemView.findViewById(R.id.tickerSym);
            numShares = itemView.findViewById(R.id.numShares);
        }
    }
}
