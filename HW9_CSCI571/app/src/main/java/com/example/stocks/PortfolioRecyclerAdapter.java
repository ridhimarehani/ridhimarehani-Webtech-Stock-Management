
package com.example.stocks;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import java.util.List;
import androidx.recyclerview.widget.RecyclerView;


public class PortfolioRecyclerAdapter extends RecyclerView.Adapter<PortfolioRecyclerAdapter.PortfolioViewHolder>{
    List<String> portItems;

    public PortfolioRecyclerAdapter(List<String> portItems) {
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
//        holder.portShareCount.setText(portItems.get(position).get(1));
        holder.portTickerText.setText(portItems.get(position)); // 0  for ticker Symbol, 1 for share count

    }

    public List<String> getData() {
        return portItems;
    }

    public void removeItem(int position) {
        portItems.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(String item, int position) {
        portItems.add(position, item);
        notifyItemInserted(position);
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
//            portShareCount = itemView.findViewById(R.id.portNumShares);
        }
    }
}