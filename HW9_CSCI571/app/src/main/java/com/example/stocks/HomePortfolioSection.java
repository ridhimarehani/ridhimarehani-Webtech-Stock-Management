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

    public HomePortfolioSection(List<List<String>> portItems) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.ticker_item)
                .headerResourceId(R.layout.home_section_port_header)
                .build());
        portfolioItems = portItems;
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
    }

    class HomePortfolioSectionView extends RecyclerView.ViewHolder{
        private TextView tickerName,numShares;

        public HomePortfolioSectionView(View itemView){
            super(itemView);
            tickerName = (TextView) itemView.findViewById(R.id.ticker_Sym);
            numShares = (TextView) itemView.findViewById(R.id.num_Shares);
        }
    }

}
