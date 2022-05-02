
package com.example.stocks;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public PortfolioRecyclerAdapter(List<List<String>> portfolioItems, Context context) {
        this.portfolioItems = portfolioItems;
        this.context = context;
    }
//    public PortfolioRecyclerAdapter(List<List<String>> portItems, String netWorth, String cashBalance) {
//        this.portItems = portItems;
//    }

    @NonNull
    @Override
    public PortfolioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Create the individual rows inside the Recycler container
        Log.i(TAG, "onCreateViewHolder: ");
        LayoutInflater layInflator = LayoutInflater.from(parent.getContext());
        View portView;
        portView = layInflator.inflate(R.layout.ticker_item, parent, false);
        Log.i(TAG, "onCreateViewHolder viewType: "+viewType);


        PortfolioViewHolder portViewHolder = new PortfolioViewHolder(portView);
        return portViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PortfolioViewHolder holder, int position) {
//        holder.portShareCount.setText(portItems.get(position).get(1));
        String numOfShares = portfolioItems.get(position).get(1);
        Log.i(TAG, "onBindViewHolder: ");
        //Binding values for Portfolio Row
        holder.portTickerText.setText(portfolioItems.get(position).get(0)); // 0  for ticker Symbol, 1 for share count
        holder.numShares.setText(numOfShares);

        //Calculation Start
        //Calculation for market Value
        String currentPrice = portfolioItems.get(position).get(2); // 2 for current price
        Double currentPriceDouble = Double.parseDouble(currentPrice);
        Integer numOfSharesInt = Integer.parseInt(numOfShares);
        Double marketValue = currentPriceDouble * numOfSharesInt;
        String marketValueString = df.format(marketValue);
        holder.currentPrice.setText(String.valueOf(marketValueString));

        //Calculation for Change and Percentage
        String totalCostStocks = portfolioItems.get(position).get(3);
        Double totalCostStocksDouble = Double.parseDouble(totalCostStocks);
        Double changeInPrice = marketValue - totalCostStocksDouble; // totalCostStocksDouble is the purchase cost
        Log.i(TAG, "onBindViewHolder: changeInPrice> "+changeInPrice);
        Double changeInPricePercentage = ((changeInPrice/totalCostStocksDouble) * 100);
        String changeInPriceString = df.format(changeInPrice);

        String changeInPricePercentageString = df.format(changeInPricePercentage);
        String changeString = "$"+ String.valueOf(changeInPriceString)+ " (" +String.valueOf(changeInPricePercentageString) + "%)";
        holder.priceChange.setText(changeString);
        //Calculation End
        Log.i(TAG, "onBindViewHolder: changeInPriceString> "+changeInPriceString);
        if(changeInPriceString.equals("-0.00")){
            Log.i(TAG, "onBindViewHolder: inside if ch");
            changeInPrice = 0.00;
        }

        //Trending Symbol
        if(changeInPrice > 0){
            Drawable unwrappedDrawable = ContextCompat.getDrawable(context, R.drawable.trending_up).mutate();
            unwrappedDrawable.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
            holder.imageView.setImageDrawable(unwrappedDrawable);
//            holder.trendingSymbol.setCompoundDrawablesWithIntrinsicBounds(R.drawable.trending_up,0 , 0, 0);
        }
        else if (changeInPrice < 0){
            Log.i(TAG, "onBindViewHolder: if changeInPrice> "+changeInPrice);
            Drawable unwrappedDrawable = ContextCompat.getDrawable(context, R.drawable.trending_down).mutate();
            unwrappedDrawable.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            holder.imageView.setImageDrawable(unwrappedDrawable);
//            holder.trendingSymbol.setCompoundDrawablesWithIntrinsicBounds(R.drawable.trending_down,0 , 0, 0);
        }


//        holder.priceChange.setText("$0.45 (0.09%)"); // 3 for change, 4 for percent

        //Binding Values for Header
//        holder.sectionHeading.setText("PORTFOLIO");
//        holder.netWorthHead.setText("Net Worth");
//        holder.cashBalanceHead.setText("Cash Balance");
//        headingHolder.netWorthVal.setText(netWorthVal);
//        headingHolder.cashBalanceVal.setText(cashBalVal);

    }

    @Override
    public int getItemCount() {
        return portfolioItems.size();
    }

//    public int getItemViewType(int position){
//        if (position == 0){
//            return 0;
//        }
//        else {
//            return 1;
//        }
//    }

    //Methods for Swipe to Delete Start
    public List<List<String>> getData() {
        return portfolioItems;
    }

    public void removeItem(int position) {
        portfolioItems.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(List<String> item, int position) {
        portfolioItems.add(position, item);
        notifyItemInserted(position);
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
        myViewHolder.rowView.setBackgroundColor(Color.GRAY);
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

        public PortfolioViewHolder(@NonNull View itemView) {
            //Keeps track of all the different views that are present in the row
            super(itemView);
            Log.i(TAG, "PortfolioViewHolder: ");
            //For Reordering
            rowView = itemView;

            //For portfolio Row
            portTickerText = itemView.findViewById(R.id.ticker_Sym);
//            portShareCount = itemView.findViewById(R.id.portNumShares);
            numShares = (TextView) itemView.findViewById(R.id.num_Shares);
            currentPrice = (TextView) itemView.findViewById(R.id.current_price);
            priceChange = (TextView) itemView.findViewById(R.id.change_in_price);
            trendingSymbol = itemView.findViewById(R.id.change_symbol);
            imageView = itemView.findViewById(R.id.imageView);

        }
    }
}