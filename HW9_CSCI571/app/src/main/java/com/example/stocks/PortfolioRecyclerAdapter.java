package com.example.stocks;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PortfolioRecyclerAdapter extends RecyclerView.Adapter<PortfolioRecyclerAdapter.PortfolioViewHolder>{
    List<List<String>> portItems;

    public PortfolioRecyclerAdapter(List<List<String>> portItems) {
        this.portItems = portItems;
    }

    @NonNull
    @Override
    public PortfolioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Create the individual rows inside the Recycler container
        LayoutInflater layInflator = LayoutInflater.from(parent.getContext());
        View portView = layInflator.inflate(R.layout.portfolio_row, parent, false);
        PortfolioViewHolder portViewHolder = new PortfolioViewHolder(portView);
        return portViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PortfolioViewHolder holder, int position) {
        holder.portShareCount.setText(portItems.get(position).get(1));
        holder.portTickerText.setText(portItems.get(position).get(0)); // 0  for ticker Symbol, 1 for share count

    }

    @Override
    public int getItemCount() {
        return portItems.size();
    }

    class PortfolioViewHolder extends RecyclerView.ViewHolder {
        TextView portTickerText, portShareCount;

        public PortfolioViewHolder(@NonNull View itemView) {
            //Keeps track of all the different views that are present in the row
            super(itemView);

            portTickerText = itemView.findViewById(R.id.portTicker);
            portShareCount = itemView.findViewById(R.id.portNumShares);
        }
    }
}
