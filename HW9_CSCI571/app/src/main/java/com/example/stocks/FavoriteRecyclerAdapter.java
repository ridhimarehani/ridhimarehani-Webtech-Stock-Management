package com.example.stocks;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class FavoriteRecyclerAdapter extends RecyclerView.Adapter<FavoriteRecyclerAdapter.FavoriteViewHolder> implements ItemMoveCallbackFavorite.ItemTouchHelperContract{
    public List<List<String >> favItems;
    private static final String TAG = "FavoriteRecyclerAdapter";

    public FavoriteRecyclerAdapter(List<List<String>> favItems) {
        Log.i(TAG, "FavoriteRecyclerAdapter Cons: "+favItems);
        this.favItems = favItems;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder: ");
        LayoutInflater layInflator = LayoutInflater.from(parent.getContext());
        View favView = layInflator.inflate(R.layout.ticker_item, parent, false);
        FavoriteRecyclerAdapter.FavoriteViewHolder favViewHolder = new FavoriteRecyclerAdapter.FavoriteViewHolder(favView);
        return favViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        holder.portTickerText.setText(favItems.get(position).get(0)); // 0  for ticker Symbol, 1 for share count
        holder.numShares.setText(favItems.get(position).get(1));
        holder.currentPrice.setText("$3279.69");
        holder.priceChange.setText("$0.45 (0.09%)");

    }

    @Override
    public int getItemCount() {
        return favItems.size();
    }


    //Methods for Swipe to Delete Start
    public List<List<String>> getData() {
        return favItems;
    }

    public void removeItem(int position) {
        favItems.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(List<String> item, int position) {
        favItems.add(position, item);
        notifyItemInserted(position);
    }
    //Methods for Swipe to Delete End

    //Methods for Reodering Start
    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(favItems, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(favItems, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);

    }

    @Override
    public void onRowSelected(FavoriteRecyclerAdapter.FavoriteViewHolder myViewHolder) {
        myViewHolder.rowView.setBackgroundColor(Color.GRAY);
    }

    @Override
    public void onRowClear(FavoriteRecyclerAdapter.FavoriteViewHolder myViewHolder) {
        myViewHolder.rowView.setBackgroundColor(Color.WHITE);
    }
    //Methods for Reodering End

    public class FavoriteViewHolder extends RecyclerView.ViewHolder{
        TextView portTickerText, numShares, currentPrice, priceChange;
        View rowView;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            rowView = itemView;
            portTickerText = itemView.findViewById(R.id.ticker_Sym);
            numShares = (TextView) itemView.findViewById(R.id.num_Shares);
            currentPrice = (TextView) itemView.findViewById(R.id.current_price);
            priceChange = (TextView) itemView.findViewById(R.id.change_in_price);
        }
    }
}
