package com.example.stocks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

public class PortfolioConstraintHeader extends ConstraintLayout {
    public PortfolioConstraintHeader(@NonNull Context context) {
        super(context);

        setId(View.generateViewId());
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(1100,200);
        setLayoutParams(layoutParams );
        setBackgroundColor(getResources().getColor(R.color.purple_200));

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this);
        addValsPortHeader(context,constraintSet);
        constraintSet.applyTo(this) ;

//        TextView netWorth, cashBalance;
//        LayoutInflater portHeaderLayout = getLayoutInflater();
//        View portHeaderView = portHeaderLayout.inflate(R.layout.portfolio_header, null);
//
//        netWorth = portHeaderView.findViewById(R.id.net_worth);
//        cashBalance = portHeaderView.findViewById(R.id.cash_balance);
//        netWorth.setText("Net Worth");
//        cashBalance.setText("Cash Balance");
//        portHeaderView.buildLayer();
//        portHeaderView.setVisibility(View.VISIBLE);
//        homeRecView.setText
//        ConstraintLayout progressBarArea;
//        progressBarArea = findViewById(R.id.portfolio_header1);
//        progressBarArea.setVisibility(View.GONE);
    }

    void addValsPortHeader(Context context,ConstraintSet constraintSet){
        TextView tView1 = new TextView(context);
        TextView tView2 = new TextView(context);
        tView1.setId(View.generateViewId());
        tView1.setText("Test");
        addView(tView1);

        constraintSet.constrainWidth(tView1.getId(),ConstraintSet.WRAP_CONTENT);
        constraintSet.constrainHeight(tView1.getId(),ConstraintSet.WRAP_CONTENT);

        constraintSet.connect(tView1.getId(),ConstraintSet.START,ConstraintSet.PARENT_ID,constraintSet.START);
        constraintSet.connect(tView1.getId(),ConstraintSet.TOP,ConstraintSet.PARENT_ID,constraintSet.TOP);
    }
}
