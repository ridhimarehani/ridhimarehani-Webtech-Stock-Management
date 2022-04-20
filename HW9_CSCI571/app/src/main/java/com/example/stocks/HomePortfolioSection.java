package com.example.stocks;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class HomePortfolioSection extends Section {
    public List<List<String >> portfolioItems;
    public String netWorthVal;
    public String cashBalVal;

    public HomePortfolioSection(List<List<String>> portItems, String netWorth, String cashBalance) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.ticker_item)
                .headerResourceId(R.layout.home_section_port_header)
                .build());
        portfolioItems = portItems;
        netWorthVal = netWorth;
        cashBalVal = cashBalance;
    }

    @Override
    public int getContentItemsTotal() {

        return portfolioItems.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new HomePortfolioSectionView(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        HomePortfolioSection.HomePortfolioSectionView portfolioHolder = (HomePortfolioSection.HomePortfolioSectionView)holder;
        portfolioHolder.tickerName.setText(portfolioItems.get(position).get(0));
        portfolioHolder.numShares.setText(portfolioItems.get(position).get(1));
        portfolioHolder.currentPrice.setText("$3279.69");
        portfolioHolder.priceChange.setText("$0.45 (0.09%)");

    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(final View view) {
        return new HomeHeadingsViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HomeHeadingsViewHolder headingHolder = (HomeHeadingsViewHolder) holder;
        headingHolder.sectionHeading.setText("PORTFOLIO");
        headingHolder.netWorthHead.setText("Net Worth");
        headingHolder.cashBalanceHead.setText("Cash Balance");
        headingHolder.netWorthVal.setText(netWorthVal);
        headingHolder.cashBalanceVal.setText(cashBalVal);
    }

    class HomePortfolioSectionView extends RecyclerView.ViewHolder{
        private TextView tickerName, numShares, currentPrice, priceChange;

        public HomePortfolioSectionView(View itemView){
            super(itemView);
            tickerName = (TextView) itemView.findViewById(R.id.ticker_Sym);
            numShares = (TextView) itemView.findViewById(R.id.num_Shares);
            currentPrice = (TextView) itemView.findViewById(R.id.current_price);
            priceChange = (TextView) itemView.findViewById(R.id.change_in_price);
        }
    }

}
