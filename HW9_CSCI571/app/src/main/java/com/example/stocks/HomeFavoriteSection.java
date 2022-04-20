package com.example.stocks;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class HomeFavoriteSection extends Section {
    public List<List<String >> favoriteItems;
    public HomeFavoriteSection(List<List<String>> favItems) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.ticker_item)
                .headerResourceId(R.layout.home_section_headers)
                .build());
        favoriteItems = favItems;
    }

    @Override
    public int getContentItemsTotal() {
        return favoriteItems.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new HomeFavoriteSectionView(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        HomeFavoriteSection.HomeFavoriteSectionView portfolioHolder = (HomeFavoriteSection.HomeFavoriteSectionView)holder;
        portfolioHolder.tickerName.setText(favoriteItems.get(position).get(0));
        portfolioHolder.numShares.setText(favoriteItems.get(position).get(1));
        portfolioHolder.currentPrice.setText("$3279.69");
        portfolioHolder.priceChange.setText("$0.45 (0.09%)");

    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(final View view) {
        return new HomeHeadingsFavViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HomeHeadingsFavViewHolder headingHolder = (HomeHeadingsFavViewHolder) holder;
        headingHolder.sectionHeading.setText("FAVORITES");
    }

    class HomeFavoriteSectionView extends RecyclerView.ViewHolder{
        private TextView tickerName, numShares, currentPrice, priceChange;

        public HomeFavoriteSectionView(View itemView){
            super(itemView);
            tickerName = (TextView) itemView.findViewById(R.id.ticker_Sym);
            numShares = (TextView) itemView.findViewById(R.id.num_Shares);
            currentPrice = (TextView) itemView.findViewById(R.id.current_price);
            priceChange = (TextView) itemView.findViewById(R.id.change_in_price);
        }
    }

    class HomeHeadingsFavViewHolder extends RecyclerView.ViewHolder {
        public TextView sectionHeading;

        public HomeHeadingsFavViewHolder(@NonNull View itemView) {
            super(itemView);
            sectionHeading = itemView.findViewById(R.id.home_header_text);
        }
    }
}
