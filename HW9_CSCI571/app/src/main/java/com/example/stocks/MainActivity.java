package com.example.stocks;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.window.SplashScreen;

import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import androidx.appcompat.widget.SearchView;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    List<HomeSections> homeSectionList = new ArrayList<>();
    PortfolioRecyclerAdapter portRecAdaptor;
    RecyclerView portfolioRecView;
    FavoriteRecyclerAdapter favRecAdapter;
    RecyclerView favoriteRecView;
//    NestedScrollView mainActivityScroll;
    List<List<String>> portfolioItems1;
    List<List<String>> favoriteItems1;
    private ConstraintLayout mainConstraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Stocks);
        super.onCreate(savedInstanceState);
//        mainActivityScroll = (NestedScrollView) findViewById(R.id.main_activity_content);
        setContentView(R.layout.activity_main);
        mainConstraintLayout = findViewById(R.id.home_section_port_header);
        System.out.println("Inside onCreate");
        writeLocalStorage();
        readLocalStorage();
//        setSections();
        setHomeDateSection();
        portfolioRecycle();
        favoriteRecycle();
        setHomeFinnhubFooter();
        enableSwipeToDeleteAndUndoFavorite();
        enableSwipeToDeleteAndUndo();



    }

    //Lolly
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.search,menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                getInput(query);
                //Sending data to Info Activity
                Intent intent = new Intent(getBaseContext(), TradeActivity.class);
                intent.putExtra("SEARCH_ID", query);
                startActivity(intent);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    //Lolly

    private void setHomeDateSection(){
        TextView homeDate;
        homeDate = findViewById(R.id.home_date_sec);
        String timeStamp = new SimpleDateFormat("dd MMMM yyyy").format(Calendar.getInstance().getTime());
        homeDate.setText(timeStamp);
    }

    private void setHomeFinnhubFooter(){

        TextView finhubLink = findViewById(R.id.finhub_link);
        String value = "<html><a href=\"https://www.finnhub.io\" style=\"color: black;\">Powered by Finnhub</a> </html>";
        finhubLink.setText(Html.fromHtml(value));

    }

    private void writeLocalStorage(){
        SharedPreferences sharedPref = getSharedPreferences("LocalStorageValues",MODE_PRIVATE);
        SharedPreferences.Editor sharedEditor = sharedPref.edit();
        sharedEditor.putString("netWorthVal","25000");
        sharedEditor.putString("cashBalanceVal","15000");

        //Portfolio
        JSONObject jObj1 = new JSONObject();
        JSONObject jObj2 = new JSONObject();
        JSONArray jArr1 = new JSONArray();

        //Favorites
        JSONObject jObj3 = new JSONObject();
        JSONArray jArr2 = new JSONArray();
        JSONObject jObj4 = new JSONObject();

        try {
            //Portfolio Data
            jObj1.put("tickerName","AAPL");
            jObj1.put("numShares","3 shares");
            jArr1.put(jObj1);
            jObj2.put("tickerName","TSLA");
            jObj2.put("numShares","5 shares");
            jArr1.put(jObj2);

            //Favorites Data
            jObj3.put("tickerName","MSFT");
            jObj3.put("numShares","Microsoft");
            jArr2.put(jObj3);
            jObj4.put("tickerName","GS");
            jObj4.put("numShares","Goldman Sachs");
            jArr2.put(jObj4);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "writeLocalStorage: Portfolio>>>"+jArr1);
        Log.i(TAG, "writeLocalStorage: Favorite>>>"+jArr1);
        sharedEditor.putString("PortfolioJSONArray",jArr1.toString());
        sharedEditor.putString("FavoriteJSONArray",jArr2.toString());
        sharedEditor.commit();
    }

    private void readLocalStorage(){
        portfolioItems1 = new ArrayList<>();
        favoriteItems1 = new ArrayList<>();
        SharedPreferences sharedPref = getSharedPreferences("LocalStorageValues",MODE_PRIVATE);
        SharedPreferences.Editor sharedEditor = sharedPref.edit();
        String favoriteJSONArray = sharedPref.getString("FavoriteJSONArray","");
        String portfolioJSONArray = sharedPref.getString("PortfolioJSONArray","");

        JSONParser jParser = new JSONParser();
        try {
            org.json.simple.JSONArray jsonArrayPort = (org.json.simple.JSONArray)jParser.parse(portfolioJSONArray);
            org.json.simple.JSONArray jsonArrayFav = (org.json.simple.JSONArray)jParser.parse(favoriteJSONArray);

            for(Object item :jsonArrayPort){
                List<String> tempList = new ArrayList<>();
                org.json.simple.JSONObject tempItem = (org.json.simple.JSONObject)item;
                tempList.add((String) tempItem.get("tickerName"));
                tempList.add((String) tempItem.get("numShares"));
                portfolioItems1.add(tempList);

            }

            for(Object item :jsonArrayFav){
                List<String> tempList = new ArrayList<>();
                org.json.simple.JSONObject tempItem = (org.json.simple.JSONObject)item;
                tempList.add((String) tempItem.get("tickerName"));
                tempList.add((String) tempItem.get("numShares"));
                favoriteItems1.add(tempList);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    private void loadData(){
        //TBD: Get Data from Local Storage here
//        String sectionHeadPort = "Portfolio";
//        List<List<String>> secPortVals = new ArrayList<>();
//        List<String> tempList1 = new ArrayList<>();
//        tempList1.add("AAPL");
//        tempList1.add("2");
//
//        List<String> tempList2 = new ArrayList<>();
//        tempList2.add("TSLA");
//        tempList2.add("5");
//        secPortVals.add(tempList1);
//        secPortVals.add(tempList2);
//
//        String sectionHeadFav = "Favorites";
        //Items to be added for Favorite Section. Adding same items as Portfolio for now

//        homeSectionList.add(new HomeSections(sectionHeadPort,secPortVals));
//        homeSectionList.add(new HomeSections(sectionHeadFav,secPortVals));
//        Log.d(TAG, "loadData: "+homeSectionList);
    }

    private void portfolioRecycle(){
        setPortfolioHeadings();
        portfolioRecView = findViewById(R.id.portfolioRecyclerView);
//        portfolioRecView.setVisibility(View.GONE); To hide a view based on a condition
        portRecAdaptor = new PortfolioRecyclerAdapter(portfolioItems1);
        ItemTouchHelper.Callback callback =new ItemMoveCallback(portRecAdaptor);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(portfolioRecView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        portfolioRecView.setLayoutManager(layoutManager);
        portfolioRecView.setAdapter(portRecAdaptor);
        DividerItemDecoration dividerLine = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        portfolioRecView.addItemDecoration(dividerLine);

    }

    private void setPortfolioHeadings(){
        String netWorthValue = "$25000.00";
        String cashBalance = "$12000.00";
        TextView sectionHeading, netWorthHead, cashBalanceHead, netWorthVal, cashBalanceVal;
        sectionHeading = findViewById(R.id.portfolio_heading_text);
        netWorthHead = findViewById(R.id.portfolio_net_worth_head);
        netWorthVal = findViewById(R.id.portfolio_net_worth_val);
        cashBalanceHead = findViewById(R.id.portfolio_cash_bal_head);
        cashBalanceVal = findViewById(R.id.protfolio_cash_bal_val);

        sectionHeading.setText("PORTFOLIO");
        netWorthHead.setText("Net Worth");
        netWorthVal.setText(netWorthValue);
        cashBalanceHead.setText("Cash Balance");
        cashBalanceVal.setText(cashBalance);

    }

    private void favoriteRecycle(){
        //favoriteRecycler
        TextView sectionHeadingFav;
        sectionHeadingFav = findViewById(R.id.home_header_fav_text);
        sectionHeadingFav.setText("FAVORITE");
        favoriteRecView = findViewById(R.id.favoriteRecycler);
//        portfolioRecView.setVisibility(View.GONE); To hide a view based on a condition
        favRecAdapter = new FavoriteRecyclerAdapter(favoriteItems1);
        ItemTouchHelper.Callback callback =new ItemMoveCallbackFavorite(favRecAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(favoriteRecView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        favoriteRecView.setLayoutManager(layoutManager);
        favoriteRecView.setAdapter(favRecAdapter);
        DividerItemDecoration dividerLine = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        favoriteRecView.addItemDecoration(dividerLine);

    }
    private void enableSwipeToDeleteAndUndo() {
        Log.i(TAG, "Inside enableSwipeToDeleteAndUndo: ");
        HomeSwipeToDelete swipeToDeleteCallback = new HomeSwipeToDelete(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();
                final List<String> item = portRecAdaptor.getData().get(position);

                portRecAdaptor.removeItem(position);


                Snackbar snackbar = Snackbar
                        .make(mainConstraintLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        portRecAdaptor.restoreItem(item, position);
                        portfolioRecView.scrollToPosition(position);
                    }
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(portfolioRecView);
    }

    private void enableSwipeToDeleteAndUndoFavorite() {
        Log.i(TAG, "Inside enableSwipeToDeleteAndUndo: ");
        HomeSwipeToDelete swipeToDeleteCallback = new HomeSwipeToDelete(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();
                final List<String> item = favRecAdapter.getData().get(position);

                favRecAdapter.removeItem(position);


                Snackbar snackbar = Snackbar
                        .make(mainConstraintLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        favRecAdapter.restoreItem(item, position);
                        favoriteRecView.scrollToPosition(position);
                    }
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();

            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(favoriteRecView);
    }
}