
package com.example.stocks;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


public class PortfolioRecyclerAdapter extends RecyclerView.Adapter<PortfolioRecyclerAdapter.PortfolioViewHolder> implements ItemMoveCallback.ItemTouchHelperContract{
    public List<List<String >> portfolioItems;
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private static final String TAG = "PortfolioRecyclerAdapter";
    private Context context;
    private OnChevronClickListenerPort mOnChevronClickListenerPort;

    public PortfolioRecyclerAdapter(List<List<String>> portfolioItems, Context context, OnChevronClickListenerPort onChevronClickListenerPort) {
        this.portfolioItems = portfolioItems;
        this.context = context;
        this.mOnChevronClickListenerPort = onChevronClickListenerPort;
    }
//    public PortfolioRecyclerAdapter(List<List<String>> portItems, String netWorth, String cashBalance) {
//        this.portItems = portItems;
//    }

    @NonNull
    @Override
    public PortfolioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Create the individual rows inside the Recycler container
        LayoutInflater layInflator = LayoutInflater.from(parent.getContext());
        View portView;
        portView = layInflator.inflate(R.layout.ticker_item, parent, false);


        PortfolioViewHolder portViewHolder = new PortfolioViewHolder(portView, mOnChevronClickListenerPort);
        return portViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PortfolioViewHolder holder, int position) {
//        holder.portShareCount.setText(portItems.get(position).get(1));
        String numOfShares = portfolioItems.get(position).get(1);
        //Binding values for Portfolio Row
        holder.portTickerText.setText(portfolioItems.get(position).get(0)); // 0  for ticker Symbol, 1 for share count
        holder.numShares.setText(numOfShares+ " shares");

        //Calculation Start
        //Calculation for market Value
        String currentPrice = portfolioItems.get(position).get(2); // 2 for current price
        Double currentPriceDouble = Double.parseDouble(currentPrice);
        Integer numOfSharesInt = Integer.parseInt(numOfShares);
        Double marketValue = currentPriceDouble * numOfSharesInt;
        String marketValueString = df.format(marketValue);
        holder.currentPrice.setText("$"+String.valueOf(marketValueString));

        //Calculation for Change and Percentage
        String totalCostStocks = portfolioItems.get(position).get(3); //3 for money spent on stocks i.e. total cost so far
        Double totalCostStocksDouble = Double.parseDouble(totalCostStocks);
        Double changeInPrice = marketValue - totalCostStocksDouble; // totalCostStocksDouble is the purchase cost
        Double changeInPricePercentage = ((changeInPrice/totalCostStocksDouble) * 100);
        String changeInPriceString = df.format(changeInPrice);

        String changeInPricePercentageString = df.format(changeInPricePercentage);
        String changeString = "$"+ String.valueOf(changeInPriceString)+ " (" +String.valueOf(changeInPricePercentageString) + "%)";
        holder.priceChange.setText(changeString);
        String zeroChange = "$0.00 (0.00%)";
        //Calculation End
        if(changeInPriceString.equals("-0.00") || changeInPriceString.equals("0.00")){
            changeInPrice = 0.00;
            holder.priceChange.setText(zeroChange);
        }

        //Trending Symbol
        if(changeInPrice > 0){
            Drawable unwrappedDrawable = ContextCompat.getDrawable(context, R.drawable.trending_up).mutate();
            unwrappedDrawable.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
            holder.imageView.setImageDrawable(unwrappedDrawable);
        }
        else if (changeInPrice < 0){
            Drawable unwrappedDrawable = ContextCompat.getDrawable(context, R.drawable.trending_down).mutate();
            unwrappedDrawable.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            holder.imageView.setImageDrawable(unwrappedDrawable);
        }


    }

    @Override
    public int getItemCount() {
        return portfolioItems.size();
    }


    //Methods for Swipe to Delete Start
    public List<List<String>> getData() {
        return portfolioItems;
    }

    public List<List<String>> removeItem(int position) {
        portfolioItems.remove(position);
        notifyItemRemoved(position);
        return portfolioItems;
    }

    public List<List<String>> restoreItem(List<String> item, int position) {
        portfolioItems.add(position, item);
        notifyItemInserted(position);
        return portfolioItems;
    }
    //Methods for Swipe to Delete End

    //Methods for Reodering Start
    @Override
    public void onRowMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(portfolioItems, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(portfolioItems, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);

    }

    @Override
    public void onRowSelected(PortfolioViewHolder myViewHolder) {
        //myViewHolder.rowView.setBackgroundColor(Color.GRAY);
    }

    @Override
    public void onRowClear(PortfolioViewHolder myViewHolder) {
        myViewHolder.rowView.setBackgroundColor(Color.WHITE);
    }
    //Methods for Reodering End

    class PortfolioViewHolder extends RecyclerView.ViewHolder {
        TextView portTickerText, numShares, currentPrice, priceChange, trendingSymbol;
        View rowView;
        ImageView imageView;
        ImageButton chevronRight;
        OnChevronClickListenerPort onChevronClickListenerPort;

        public PortfolioViewHolder(@NonNull View itemView, OnChevronClickListenerPort onChevronClickListenerPort) {
            //Keeps track of all the different views that are present in the row
            super(itemView);
            //For Reordering
            rowView = itemView;
            this.onChevronClickListenerPort = onChevronClickListenerPort;

            //For portfolio Row
            portTickerText = itemView.findViewById(R.id.ticker_Sym);
            numShares = (TextView) itemView.findViewById(R.id.num_Shares);
            currentPrice = (TextView) itemView.findViewById(R.id.current_price);
            priceChange = (TextView) itemView.findViewById(R.id.change_in_price);
            trendingSymbol = itemView.findViewById(R.id.change_symbol);
            imageView = itemView.findViewById(R.id.imageView);

            //Chevron Click
            chevronRight = itemView.findViewById(R.id.chevron_right_button);
            chevronRight.setOnClickListener(this::onClick);

        }

        private void onClick(View view) {
            onChevronClickListenerPort.onChevronClickPort(getAdapterPosition());
        }
    }
    public interface OnChevronClickListenerPort{
        void onChevronClickPort(int position);

    }
}