package com.example.stocks;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String TAG1 = "homeRecycle";
    List<HomeSections> homeSectionList = new ArrayList<>();
    RecyclerView homeRecView;
    RecyclerView portfolioRecView;
//    PortfolioRecyclerAdapter portRecAdaptor;
    List<List<String>> portfolioItems;
    List<List<String>> favoriteItems;
//    SectionedRecyclerViewAdapter sectionAdapter = new SectionedRecyclerViewAdapter();
    private SectionedRecyclerViewAdapter sectionedAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("Inside onCreate");
        loadData();
        setSections();

//        portfolioRecycle();


    }

    private void setSections(){
        portfolioItems = new ArrayList<>();
        portfolioItems = homeSectionList.get(0).getSecItems();
        favoriteItems = new ArrayList<>();
        favoriteItems = homeSectionList.get(1).getSecItems();

        sectionedAdapter = new SectionedRecyclerViewAdapter();

        //Setting Sections Start
        sectionedAdapter.addSection(new HomeDateSection());
        sectionedAdapter.addSection(new HomePortfolioSection(portfolioItems));
        sectionedAdapter.addSection(new HomeFavoriteSection(favoriteItems));
        //Setting Sections End

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.home_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(sectionedAdapter);
    }
    private void setHomeDateSection(){

    }


    private void portfolioRecycle(){

//        portfolioItems = new ArrayList<>();
//        portfolioItems = homeSectionList.get(0).getSecItems();
//
//        portfolioRecView = findViewById(R.id.portfolioRecyclerView);
////        portfolioRecView.setVisibility(View.GONE); To hide a view based on a condition
//        portRecAdaptor = new PortfolioRecyclerAdapter(portfolioItems);
//        portfolioRecView.setAdapter(portRecAdaptor);
//        DividerItemDecoration dividerLine = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
//        portfolioRecView.addItemDecoration(dividerLine);

    }

    private void loadData(){
        //TBD: Get Data from API here
        String sectionHeadPort = "Portfolio";
        List<List<String>> secPortVals = new ArrayList<>();
        List<String> tempList1 = new ArrayList<>();
        tempList1.add("AAPL");
        tempList1.add("2");

        List<String> tempList2 = new ArrayList<>();
        tempList2.add("TSLA");
        tempList2.add("5");
        secPortVals.add(tempList1);
        secPortVals.add(tempList2);

        String sectionHeadFav = "Favorites";
        //Items to be added for Favorite Section. Adding same items as Portfolio for now

        homeSectionList.add(new HomeSections(sectionHeadPort,secPortVals));
        homeSectionList.add(new HomeSections(sectionHeadFav,secPortVals));
        Log.d(TAG, "loadData: "+homeSectionList);
    }
}