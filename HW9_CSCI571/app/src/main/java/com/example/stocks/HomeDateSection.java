package com.example.stocks;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class HomeDateSection extends Section {
    public HomeDateSection() {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.home_date_section)
//                .headerResourceId(R.layout.home_date_section)
                .build());
    }

    @Override
    public int getContentItemsTotal() {
        return 1;
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new HomeDateSectionView(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        HomeDateSectionView dateHolder = (HomeDateSectionView) holder;
        dateHolder.homeDate.setText("31 March 2022");

    }

    class HomeDateSectionView extends RecyclerView.ViewHolder{
        private TextView homeDate;

        public HomeDateSectionView(View itemView){
            super(itemView);
            homeDate = (TextView) itemView.findViewById(R.id.home_date_sec);
        }
    }
}
