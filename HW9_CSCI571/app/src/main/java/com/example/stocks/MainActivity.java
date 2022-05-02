package com.example.stocks;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.window.SplashScreen;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.widget.SearchView;
//import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MainActivity extends AppCompatActivity implements FavoriteRecyclerAdapter.OnChevronClickListener{
    private static final String TAG = "MainActivity";
    List<HomeSections> homeSectionList = new ArrayList<>();

    PortfolioRecyclerAdapter portRecAdaptor;
    RecyclerView portfolioRecView;
    FavoriteRecyclerAdapter favRecAdapter;
    RecyclerView favoriteRecView;
    //    NestedScrollView mainActivityScroll;
    List<List<String>> portfolioItems1 = null;
    List<List<String>> favoriteItems1 = null;
    private ConstraintLayout mainConstraintLayout;
    private RequestQueue requestQueue;
    String currentStockPrice1;
    private int count = 0;
    private String baseUrl = "https://csci571hw8-backend-346006.wl.r.appspot.com/";
    String latestStock_url = baseUrl + "stockPrice?ticker=";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Stocks);
        super.onCreate(savedInstanceState);
//        mainActivityScroll = (NestedScrollView) findViewById(R.id.main_activity_content);
        setContentView(R.layout.activity_main);
        mainConstraintLayout = findViewById(R.id.home_section_port_header);
        System.out.println("Inside onCreate");
        requestQueue = Volley.newRequestQueue(this);
        writeLocalStorage();
        setHomeDateSection();
        setPortfolioHeader();
        readLocalStorage();
//        setSections();
        getCurrentStockPriceVals();

//        portfolioRecycle();
        favoriteRecycle();
        setHomeFinnhubFooter();
        enableSwipeToDeleteAndUndoFavorite();


    }

    @Override
    protected void onResume() {
        setHomeDateSection();
//        portfolioRecycle();
//        favoriteRecycle();
        setHomeFinnhubFooter();
//        enableSwipeToDeleteAndUndoFavorite();
//        enableSwipeToDeleteAndUndo();
//        readLocalStorage();
        super.onResume();
    }
    //AddTextChangeListener
    //Adapter.setData

    //Lolly
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search...");

        //Autocomplete
        SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        String dataArr[]  = {"TS.MX | TS.MX", "TSE | TSE", "TSP | TSP", "TSC | TSC", "TSQ | TSQ", "TSN | TSN", "TSLA | TSLA", "TSLA.MI | TSLA.MI", "TSLA.VI | TSLA.VI", "TSLA.SW | TSLA.SW", "TSLA.MX | TSLA.MX", "TRIS3.SA | TRIS3.SA", "0R0X.L | 0R0X.L", "TL0.HM | TL0.HM", "TL0.F | TL0.F", "TSLAUSD.SW | TSLAUSD.SW", "TL0.SG | TL0.SG", "TL0.MU | TL0.MU", "TSLA-RM.ME | TSLA-RM.ME", "TL0.DU | TL0.DU", "TL0.DE | TL0.DE", "TL0.BE | TL0.BE", "T | T", "T.TO | T.TO", "T.NE | T.NE", "T.SN | T.SN", "T.MX | T.MX", "T.BC | T.BC", "TD | TD", "TU | TU", "TH | TH", "TW | TW", "TG | TG", "TR | TR", "TT | TT", "TK | TK", "TA | TA", "4Y5.F | 4Y5.F", "4Y5.MU | 4Y5.MU", "4Y5.SG | 4Y5.SG", "G | G", "G.TO | G.TO", "G.MI | G.MI", "G.NE | G.NE", "GS | GS", "GP | GP", "GT | GT", "GH | GH", "GD | GD", "GB | GB", "GL | GL", "GM | GM", "GO | GO", "GE | GE", "GS.MX | GS.MX", "GS.BC | GS.BC", "GS.VI | GS.VI", "GSV | GSV", "GSM | GSM", "GSL | GSL", "AGESF | AGESF", "AGSN.MX | AGSN.MX", "AGS.SW | AGS.SW", "AGS.BR | AGS.BR", "FO4N.HA | FO4N.HA", "0Q99.L | 0Q99.L", "FO4N.F | FO4N.F", "FO4N.DU | FO4N.DU", "M | M", "M.BK | M.BK", "M.MX | M.MX", "MG | MG", "MD | MD", "MA | MA", "MQ | MQ", "MC | MC", "MS | MS", "MU | MU", "ME | ME", "MO | MO", "MX | MX", "MN | MN", "MP | MP", "ML | ML", "ALMND.PA | ALMND.PA", "MS | MS", "MS.SN | MS.SN", "MS.MX | MS.MX", "MS.SW | MS.SW", "MSN | MSN", "MSM | MSM", "MSI | MSI", "MSP | MSP", "MSA | MSA", "MSF.MU | MSF.MU", "MSF.HM | MSF.HM", "MSF.DU | MSF.DU", "MSF.HA | MSF.HA", "MSF.SG | MSF.SG", "MSF.BE | MSF.BE", "MSF.BR | MSF.BR", "MSF.F | MSF.F", "MSF.DE | MSF.DE", "MSFN | MSFN", "MSFT | MSFT", "MSFT.MI | MSFT.MI", "MSFT.VI | MSFT.VI", "MSFT.SN | MSFT.SN", "MSFT.MX | MSFT.MX", "MSFT.BC | MSFT.BC", "MSFT.SW | MSFT.SW"};
        ArrayAdapter<String> newsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, dataArr);
        searchAutoComplete.setAdapter(newsAdapter);
        searchAutoComplete.setBackgroundColor(Color.WHITE);
        searchAutoComplete.setTextColor(Color.BLACK);
        //Autocomplete
        // Listen to search view item on click event.
        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long id) {
                String queryString = (String) adapterView.getItemAtPosition(itemIndex);
                searchAutoComplete.setText("" + queryString);
//                Toast.makeText(MainActivity.this, "you clicked " + queryString, Toast.LENGTH_LONG).show();
                navigateToTradeActivity(queryString);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Navigate to Trade Activity
                navigateToTradeActivity(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

//        EditText search=(EditText) findViewById(R.id.searchbar);
        return super.onCreateOptionsMenu(menu);
    }
    //Lolly

    private void navigateToTradeActivity(String query) {
        Intent intent = new Intent(getBaseContext(), TradeActivity.class);
        intent.putExtra("SEARCH_ID", query);
        startActivity(intent);
    }

    private void setPortfolioHeader() {
        SharedPreferences sharedPref = getSharedPreferences("LocalStorageValues", MODE_PRIVATE);
        String netWorth = sharedPref.getString("net_worth", "");
        String cashBalance = sharedPref.getString("cash_balance", "");
        TextView net_worth, cash_balance, netWorthHead, cashBalanceHead;
        netWorthHead = findViewById(R.id.portfolio_net_worth_head);
        cashBalanceHead = findViewById(R.id.portfolio_cash_bal_head);
        net_worth = findViewById(R.id.portfolio_net_worth_val);
        cash_balance = findViewById(R.id.protfolio_cash_bal_val);
        net_worth.setText(netWorth);
        cash_balance.setText(cashBalance);
        netWorthHead.setText("Net Worth");
        cashBalanceHead.setText("Cash Balance");


    }

    private void setHomeDateSection() {
        TextView homeDate;
        homeDate = findViewById(R.id.home_date_sec);
        String timeStamp = new SimpleDateFormat("dd MMMM yyyy").format(Calendar.getInstance().getTime());
        homeDate.setText(timeStamp);
    }

    private void setHomeFinnhubFooter() {

        TextView finhubLink = findViewById(R.id.finhub_link);
        String value = "<html><a href=\"https://www.finnhub.io\" style=\"color: black;\">Powered by Finnhub</a> </html>";
        finhubLink.setText(Html.fromHtml(value));
        finhubLink.setLinkTextColor(Color.parseColor("#a4a4a3"));
        finhubLink.setMovementMethod(LinkMovementMethod.getInstance());

    }

    private void writeLocalStorage() {
        SharedPreferences sharedPref = getSharedPreferences("LocalStorageValues", MODE_PRIVATE);
        SharedPreferences.Editor sharedEditor = sharedPref.edit();
        String net_worth = sharedPref.getString("net_worth", "");
        String cash_balance = sharedPref.getString("cash_balance", "");
        if (net_worth.isEmpty()) {
            sharedEditor.putString("net_worth", "25000");
            sharedEditor.commit();
        }
        if (cash_balance.isEmpty()) {
            sharedEditor.putString("cash_balance", "25000");
            sharedEditor.commit();
        }
        try {
            //Favorites
            JSONObject jObj3 = new JSONObject();
            JSONArray jArr2 = new JSONArray();
            JSONObject jObj4 = new JSONObject();

            //Favorites Data
            jObj3.put("tickerName", "MSFT");
            jObj3.put("compName", "Microsoft");
            jObj3.put("latestPrice", "100.00");
            jArr2.put(jObj3);
            jObj4.put("tickerName", "GS");
            jObj4.put("compName", "Goldman Sachs");
            jObj4.put("latestPrice", "200.00");
            jArr2.put(jObj4);
            sharedEditor.putString("Favorite", jArr2.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        sharedEditor.commit();
    }

    private void readLocalStorage() {
        portfolioItems1 = new ArrayList<>();
        favoriteItems1 = new ArrayList<>();
        SharedPreferences sharedPref = getSharedPreferences("LocalStorageValues", MODE_PRIVATE);
        SharedPreferences.Editor sharedEditor = sharedPref.edit();
        String favoriteJSONArray = sharedPref.getString("Favorite", "");
        String portfolioJSONArray = sharedPref.getString("Portfolio", "");

        JSONParser jParser = new JSONParser();
        try {
            org.json.simple.JSONArray jsonArrayPort = (org.json.simple.JSONArray) jParser.parse(portfolioJSONArray);
            org.json.simple.JSONArray jsonArrayFav = (org.json.simple.JSONArray) jParser.parse(favoriteJSONArray);
            Log.i(TAG, "readLocalStorage: jsonArrayPort> "+jsonArrayPort);
            for (Object item : jsonArrayPort) {
                List<String> tempList = new ArrayList<>();
                org.json.simple.JSONObject tempItem = (org.json.simple.JSONObject) item;
                tempList.add((String) tempItem.get("tickerSymbol")); //0 for ticker
                tempList.add((String) tempItem.get("numOfShares")); //1 for number of shares
                portfolioItems1.add(tempList);
            }
            Log.i(TAG, "readLocalStorage: portfolioItems1>> "+ portfolioItems1);
            Log.i(TAG, "readLocalStorage: jsonArrayFav>> "+jsonArrayFav);

            for (Object item : jsonArrayFav) {
                List<String> tempList = new ArrayList<>();
                org.json.simple.JSONObject tempItem = (org.json.simple.JSONObject) item;
                tempList.add((String) tempItem.get("tickerName"));
                tempList.add((String) tempItem.get("compName")); // 1 for current price
                tempList.add((String) tempItem.get("latestPrice")); // 2 for latest price
                tempList.add("150.00"); //3 for price since last closed
                favoriteItems1.add(tempList);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    private void getCurrentStockPriceVals() {
        Log.i(TAG, "Inside getCurrentStockPriceVals: ");
        int count1 = 0;

        JSONParser jParser = new JSONParser();
        SharedPreferences sharedPref = getSharedPreferences("LocalStorageValues", MODE_PRIVATE);
        String favoriteJSONArray = sharedPref.getString("Favorite", "");
        String portfolioJSONArray = sharedPref.getString("Portfolio", "");
        Log.i(TAG, "getCurrentStockPriceVals: portfolioJSONArray"+portfolioJSONArray);
        Log.i(TAG, "getCurrentStockPriceVals: favoriteJSONArray"+favoriteJSONArray);

        try {
            org.json.simple.JSONArray jsonArrayPort = (org.json.simple.JSONArray) jParser.parse(portfolioJSONArray);
            org.json.simple.JSONArray jsonArrayFav = (org.json.simple.JSONArray) jParser.parse(favoriteJSONArray);
            int jsonArrayPortLength = jsonArrayPort.size();
            int jsonArrayFavoriteLength = jsonArrayFav.size();
            //Portfolio Values Start
//            int count = 0;
            for (Object item : jsonArrayPort) {
                fetchPortfolioValue((org.json.simple.JSONObject)item, jsonArrayPortLength);
            }
            for (Object item : jsonArrayFav) {

                //fetchFavoriteValue((org.json.simple.JSONObject)item, jsonArrayFavoriteLength);
            }
            //Portfolio Values End

            //Favorite Values End
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void fetchPortfolioValue(org.json.simple.JSONObject portObject, int jsonArrayPortLength){

        String tickerSymbol = "";
        String latestStockUrl1 = "";
        org.json.simple.JSONObject tempItem = portObject;
        Log.i(TAG, "getCurrentStockPriceVals tempItem: "+tempItem);
        tickerSymbol = (String) tempItem.get("tickerSymbol");
//        latestStock_url = latestStock_url + tickerSymbol;
        latestStockUrl1 = latestStock_url + tickerSymbol;
        Log.i(TAG, "getCurrentStockPriceVals tickerSymbol: "+tickerSymbol);
        Log.i(TAG, "getCurrentStockPriceVals latestStock_url: "+latestStockUrl1);
        String totalCostStocks = (String) tempItem.get("totalCostStocks");
        List<String> tempList = new ArrayList<>();

        //Fetch from API starts
        JsonObjectRequest latestStockPrice = new JsonObjectRequest(Request.Method.GET, latestStockUrl1, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject latest_stock_response) {
                try {
                    Log.i("latest_response", "onResponse123: " + latest_stock_response.toString());

                    currentStockPrice1 = latest_stock_response.getString("c");
//                            double dp = Double.parseDouble(latest_stock_response.getString("dp"));
                    Log.i(TAG, "onResponse tempItem: "+tempItem);
                    Log.i(TAG, "onResponse: portfolioItems1>>"+portfolioItems1);
                    portfolioItems1.get(count).add(currentStockPrice1); // Adding current price on index 2
                    portfolioItems1.get(count).add(totalCostStocks); // Adding market Value from Local Storage on index 3

                    Log.i(TAG, "getCurrentStockPriceVals11: " + portfolioItems1);
                    count += 1;
                    if (count == jsonArrayPortLength) {
                        Log.i(TAG, "getCurrentStockPriceVals: check1 " + count + ' '+ jsonArrayPortLength);
                        portfolioRecycle();
                        enableSwipeToDeleteAndUndo();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }

                });
        requestQueue.add(latestStockPrice);

        //Fetch from API ends

    }

    private void fetchFavoriteValue(org.json.simple.JSONObject favoriteObject, int jsonArrayFavoriteLength){

        org.json.simple.JSONObject tempItem = favoriteObject;
        String tickerSymbol = (String) tempItem.get("tickerSymbol");
        latestStock_url = latestStock_url + tickerSymbol;
        List<String> tempList = new ArrayList<>();
        JsonObjectRequest latestStockPrice = new JsonObjectRequest(Request.Method.GET, latestStock_url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject latest_stock_response) {
                try {
                    Log.i("latest_response", "onResponse123: " + latest_stock_response.toString());
                    currentStockPrice1 = latest_stock_response.getString("c");
                    favoriteItems1.get(count).add(currentStockPrice1);
                    count += 1;
                    if (count == jsonArrayFavoriteLength) {
                        favoriteRecycle();
                        enableSwipeToDeleteAndUndo();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }

                });
        requestQueue.add(latestStockPrice);

    }


    private void portfolioRecycle() {
        setPortfolioHeadings();
        portfolioRecView = findViewById(R.id.portfolioRecyclerView);
//        portfolioRecView.setVisibility(View.GONE); To hide a view based on a condition
        portRecAdaptor = new PortfolioRecyclerAdapter(portfolioItems1, this);
        ItemTouchHelper.Callback callback = new ItemMoveCallback(portRecAdaptor);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(portfolioRecView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        portfolioRecView.setLayoutManager(layoutManager);
        portfolioRecView.setAdapter(portRecAdaptor);
        DividerItemDecoration dividerLine = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        portfolioRecView.addItemDecoration(dividerLine);

    }

    private void setPortfolioHeadings() {
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

    private void favoriteRecycle() {
        //favoriteRecycler
        TextView sectionHeadingFav;
        sectionHeadingFav = findViewById(R.id.home_header_fav_text);
        sectionHeadingFav.setText("FAVORITE");
        favoriteRecView = findViewById(R.id.favoriteRecycler);
//        portfolioRecView.setVisibility(View.GONE); To hide a view based on a condition
        favRecAdapter = new FavoriteRecyclerAdapter(favoriteItems1, this,this);
        ItemTouchHelper.Callback callback = new ItemMoveCallbackFavorite(favRecAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(favoriteRecView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        favoriteRecView.setLayoutManager(layoutManager);
        favoriteRecView.setAdapter(favRecAdapter);
        DividerItemDecoration dividerLine = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        favoriteRecView.addItemDecoration(dividerLine);

    }

    private void clickChevronRight(){
        ImageButton chevronButton = findViewById(R.id.chevron_right_button);
        chevronButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String p = peer.getText().toString();
                Intent intent = new Intent(getBaseContext(), TradeActivity.class);
//                intent.putExtra("SEARCH_ID", p);
                startActivity(intent);
            }
        });
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

    @Override
    public void onChevronClick(int position) {
        favoriteItems1.get(position);
        Intent intent = new Intent(this,TradeActivity.class);
        String searchId = favoriteItems1.get(position).get(0);
        Log.i(TAG, "onChevronClick: searchId"+searchId);
        intent.putExtra("SEARCH_ID", searchId);
        startActivity(intent);
    }
}