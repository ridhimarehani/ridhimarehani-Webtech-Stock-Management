package com.example.stocks;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class HomeHeadingsViewHolder extends RecyclerView.ViewHolder {
    public TextView sectionHeading, netWorthHead, cashBalanceHead;
    public HomeHeadingsViewHolder(@NonNull View itemView) {
        super(itemView);
        sectionHeading = itemView.findViewById(R.id.portfolio_heading_text);
        netWorthHead = itemView.findViewById(R.id.portfolio_net_worth_head);
        cashBalanceHead = itemView.findViewById(R.id.protfolio_cash_bal_head);
    }
}
