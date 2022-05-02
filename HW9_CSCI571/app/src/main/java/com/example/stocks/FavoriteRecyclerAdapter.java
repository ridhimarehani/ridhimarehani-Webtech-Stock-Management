package com.example.stocks;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;

public class FavoriteRecyclerAdapter extends RecyclerView.Adapter<FavoriteRecyclerAdapter.FavoriteViewHolder> implements ItemMoveCallbackFavorite.ItemTouchHelperContract{
    public List<List<String >> favItems;
    private static final String TAG = "FavoriteRecyclerAdapter";
    private static final DecimalFormat df = new DecimalFormat("0.00");
    private Context context;
    private OnChevronClickListener mChevronClickListener;

    public FavoriteRecyclerAdapter(List<List<String>> favItems, Context context, OnChevronClickListener onChevronClickListener) {
        Log.i(TAG, "FavoriteRecyclerAdapter Cons: "+favItems);
        this.favItems = favItems;
        this.context = context;
        this.mChevronClickListener = onChevronClickListener;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.i(TAG, "onCreateViewHolder: ");
        LayoutInflater layInflator = LayoutInflater.from(parent.getContext());
        View favView = layInflator.inflate(R.layout.ticker_item, parent, false);
        FavoriteRecyclerAdapter.FavoriteViewHolder favViewHolder = new FavoriteRecyclerAdapter.FavoriteViewHolder(favView,mChevronClickListener );
        return favViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        holder.portTickerText.setText(favItems.get(position).get(0)); // 0  for ticker Symbol, 1 for share count
        holder.numShares.setText(favItems.get(position).get(1)); //1 for company name

        holder.priceChange.setText("$0.45 (0.09%)");

        //Calculations for Portfolio
        String currentPrice = favItems.get(position).get(2); // 2 for current price //3 for price since last closed
        String priceSinceLastClose = favItems.get(position).get(3);
        holder.currentPrice.setText(currentPrice);
        Double currentPriceDouble = Double.parseDouble(currentPrice);
        Double priceSinceLastCloseDouble = Double.parseDouble(priceSinceLastClose);
        Double priceChange = currentPriceDouble - priceSinceLastCloseDouble;
        String priceChangeString = df.format(priceChange);
//        priceChange = df.format(priceChange);
        Double priceChangePercentage = (priceChange/priceSinceLastCloseDouble) * 100;
        String priceChangePercentageString = df.format(priceChange);
        String changeString = "$"+ String.valueOf(priceChangeString)+ " (" + String.valueOf(priceChangePercentageString) + "%)";
        holder.priceChange.setText(changeString);
        //Calculation End

        //Trending Symbol
        if(priceChangeString.equals("-0.00")){
            Log.i(TAG, "onBindViewHolder: inside if ch");
            priceChange = 0.00;
        }

        if(priceChange > 0){
            Drawable unwrappedDrawable = ContextCompat.getDrawable(context, R.drawable.trending_up).mutate();
            unwrappedDrawable.setColorFilter(Color.GREEN, PorterDuff.Mode.SRC_IN);
            holder.imageView.setImageDrawable(unwrappedDrawable);
//            Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.trending_up).mutate();
//            Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
//            DrawableCompat.setTint(wrappedDrawable, Color.GREEN);
//            holder.trendingSymbol.setCompoundDrawablesWithIntrinsicBounds(R.drawable.trending_up,0 , 0, 0);
            //holder.trendingSymbol.setTextColor(Color.GREEN);

        }
        else if (priceChange < 0){
            Drawable unwrappedDrawable = ContextCompat.getDrawable(context, R.drawable.trending_down).mutate();
            unwrappedDrawable.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN);
            holder.imageView.setImageDrawable(unwrappedDrawable);
//            holder.trendingSymbol.setCompoundDrawablesWithIntrinsicBounds(R.drawable.trending_down,0 , 0, 0);;
//            setTextViewDrawableColor(holder.trendingSymbol, Color.RED);
        }


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

    public class FavoriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView portTickerText, numShares, currentPrice, priceChange, trendingSymbol;
        View rowView;
        ImageView imageView;
        ImageButton chevronRight;
        OnChevronClickListener onChevronClickListener;

        public FavoriteViewHolder(@NonNull View itemView, OnChevronClickListener onChevronClickListener) {
            super(itemView);
            //Chevron Click
            this.onChevronClickListener = onChevronClickListener;
            rowView = itemView;
            portTickerText = itemView.findViewById(R.id.ticker_Sym);
            numShares = (TextView) itemView.findViewById(R.id.num_Shares);
            currentPrice = (TextView) itemView.findViewById(R.id.current_price);
            priceChange = (TextView) itemView.findViewById(R.id.change_in_price);
            trendingSymbol = itemView.findViewById(R.id.change_symbol);
            imageView = itemView.findViewById(R.id.imageView);
            chevronRight = itemView.findViewById(R.id.chevron_right_button);
            //Chevron Click
            chevronRight.setOnClickListener(this::onClick);
        }

        @Override
        public void onClick(View view) {
            onChevronClickListener.onChevronClick(getAdapterPosition());
        }
    }

    public interface OnChevronClickListener{
        void onChevronClick(int position);

    }


}
