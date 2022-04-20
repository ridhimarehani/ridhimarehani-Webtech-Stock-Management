package com.example.stocks;

import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class HomeFinhubSection extends Section {
    private static final String finLink= "Powered by Finnhub";
    public HomeFinhubSection() {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.home_finhub_footer)
//                .headerResourceId(R.layout.home_date_section)
                .build());
    }

    @Override
    public int getContentItemsTotal() {
        return 1;
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new HomeFinhubSectionView(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        HomeFinhubSection.HomeFinhubSectionView finhubHolder = (HomeFinhubSection.HomeFinhubSectionView) holder;
        String value = "<html><a href=\"https://www.finnhub.io\" style=\"color: black;\">Powered by Finnhub</a> </html>";
//        finhubHolder.finhubLink.setText(finLink);

        finhubHolder.finhubLink.setText(Html.fromHtml(value));
        finhubHolder.finhubLink.setTextColor(R.color.black);
        finhubHolder.finhubLink.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public class HomeFinhubSectionView extends RecyclerView.ViewHolder{
        private TextView finhubLink;
        public HomeFinhubSectionView(@NonNull View itemView) {
            super(itemView);
            finhubLink = (TextView) itemView.findViewById(R.id.finhub_link);
        }
    }
}
