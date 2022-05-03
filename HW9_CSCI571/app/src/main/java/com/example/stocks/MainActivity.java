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
import android.os.Handler;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
import java.util.concurrent.atomic.AtomicInteger;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MainActivity extends AppCompatActivity implements FavoriteRecyclerAdapter.OnChevronClickListener, PortfolioRecyclerAdapter.OnChevronClickListenerPort {
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
    String changeInPriceFav;
    String getChangeInPriceFavPercentage;
    private int count = 0;
    private int count1 = 0;
    private String baseUrl = "https://csci571hw8-backend-346006.wl.r.appspot.com/";
    String latestStock_url = baseUrl + "stockPrice?ticker=";
    //Lolly
    private LinearLayout progressBarArea;
    AtomicInteger requestsCounter;
    ConstraintLayout mainConstraint;
    Handler handler = new Handler();
    int countAutoRefresh = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Stocks);
        super.onCreate(savedInstanceState);
//        mainActivityScroll = (NestedScrollView) findViewById(R.id.main_activity_content);
        setContentView(R.layout.activity_main);
        mainConstraintLayout = findViewById(R.id.home_section_port_header);
        System.out.println("Inside onCreate");

        //Lolly
        requestQueue = Volley.newRequestQueue(this);
        mainConstraint = (ConstraintLayout) findViewById(R.id.home_section_port_header);  // detail contents
        progressBarArea = (LinearLayout) findViewById(R.id.lProgressBarArea);


        if (portfolioItems1 != null && favoriteItems1 !=null){
            progressBarArea.setVisibility(View.VISIBLE);
            mainConstraint.setVisibility(View.GONE);
        }

        writeLocalStorage();
        setHomeDateSection();
        setPortfolioHeader();
        readLocalStorage();
        getCurrentStockPriceVals();
        setHomeFinnhubFooter();
        int totalCount = portfolioItems1.size() + favoriteItems1.size();
        Log.i(TAG, "onCreate: totalCount" + totalCount);
        requestsCounter = new AtomicInteger(totalCount);

        //Lolly
        requestQueue.addRequestFinishedListener(req -> {
            requestsCounter.decrementAndGet();
            Log.i(TAG, "onCreate: ");
            Log.d(String.valueOf(requestsCounter.get()), "-----------i am called on on resume------");
            if (requestsCounter.get() == 0) {
//                getCompanyDescription();
                Log.d(String.valueOf(requestsCounter.get()), "-----------i loadin p in resume------");

                // set visibility GONE for progress bar, show nestedScrollView
                progressBarArea.setVisibility(View.GONE);
                mainConstraintLayout.setVisibility(View.VISIBLE);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        setHomeDateSection();
        setHomeFinnhubFooter();
//        doTheAutoRefresh();
//        readLocalStorage();

    }

    //Lolly
    private void doTheAutoRefresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Write code for your refresh logic

                count = 0;
                count1 = 0;
                countAutoRefresh += 1;
                getCurrentStockPriceVals();
                Log.i(TAG, "run: Autorefresh> " + countAutoRefresh);
                doTheAutoRefresh();
            }
        }, 15000);
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
        String dataArr[] = {"TS.MX | TENARIS SA", "TSE | TRINSEO PLC", "TSP | TUSIMPLE HOLDINGS INC - A", "TSC | TRISTATE CAPITAL HLDGS INC", "TSQ | TOWNSQUARE MEDIA INC - CL A", "TSN | TYSON FOODS INC-CL A", "TSLA | TESLA INC", "TSLA.MI | TESLA INC", "TSLA.VI | TESLA INC", "TSLA.SW | TESLA INC", "TSLA.MX | TESLA INC", "TRIS3.SA | TRISUL SA", "0R0X.L | TESLA INC", "TL0.HM | TESLA INC", "TL0.F | TESLA INC", "TSLAUSD.SW | TESLA INC", "TL0.SG | TESLA INC", "TL0.MU | TESLA INC", "TSLA-RM.ME | TESLA INC", "TL0.DU | TESLA INC", "TL0.DE | TESLA INC", "TL0.BE | TESLA INC", "T | AT&T INC", "T.TO | TELUS CORP", "T.NE | TELUS CORP", "T.SN | AT&T INC", "T.MX | AT&T INC", "T.BC | AT&T INC", "TD | TORONTO-DOMINION BANK", "TU | TELUS CORP", "TH | TARGET HOSPITALITY CORP", "TW | TRADEWEB MARKETS INC-CLASS A", "TG | TREDEGAR CORP", "TR | TOOTSIE ROLL INDS", "TT | TRANE TECHNOLOGIES PLC", "TK | TEEKAY CORP", "TA | TRAVELCENTERS OF AMERICA INC", "4Y5.F | DBT", "4Y5.MU | DBT", "4Y5.SG | DBT", "G | GENPACT LTD", "G.TO | AUGUSTA GOLD CORP", "G.MI | ASSICURAZIONI GENERALI", "G.NE | AUGUSTA GOLD CORP", "GS | GOLDMAN SACHS GROUP INC", "GP | GREENPOWER MOTOR CO INC", "GT | GOODYEAR TIRE & RUBBER CO", "GH | GUARDANT HEALTH INC", "GD | GENERAL DYNAMICS CORP", "GB | GLOBAL BLUE GROUP HOLDING AG", "GL | GLOBE LIFE INC", "GM | GENERAL MOTORS CO", "GO | GROCERY OUTLET HOLDING CORP", "GE | GENERAL ELECTRIC CO", "GS.MX | GOLDMAN SACHS GROUP INC", "GS.BC | GOLDMAN SACHS GROUP INC", "GS.VI | GOLDMAN SACHS GROUP INC", "GSV | GOLD STANDARD VENTURES CORP", "GSM | FERROGLOBE PLC", "GSL | GLOBAL SHIP LEASE INC-CL A", "AGESF | AGEAS", "AGSN.MX | AGEAS", "AGS.SW | AGEAS", "AGS.BR | AGEAS", "FO4N.HA | AGEAS", "0Q99.L | AGEAS", "FO4N.F | AGEAS", "FO4N.DU | AGEAS", "M | MACY'S INC", "M.BK | MK RESTAURANTS GROUP PCL", "M.MX | MACY'S INC", "MG | MISTRAS GROUP INC", "MD | MEDNAX INC", "MA | MASTERCARD INC - A", "MQ | MARQETA INC-A", "MC | MOELIS & CO - CLASS A", "MS | MORGAN STANLEY", "MU | MICRON TECHNOLOGY INC", "ME | 23ANDME HOLDING CO -CLASS A", "MO | ALTRIA GROUP INC", "MX | MAGNACHIP SEMICONDUCTOR CORP", "MN | MANNING & NAPIER INC", "MP | MP MATERIALS CORP", "ML | MONEYLION INC", "ALMND.PA | MND", "MS | MORGAN STANLEY", "MS.SN | MORGAN STANLEY", "MS.MX | MORGAN STANLEY", "MS.SW | MORGAN STANLEY", "MSN | EMERSON RADIO CORP", "MSM | MSC INDUSTRIAL DIRECT CO-A", "MSI | MOTOROLA SOLUTIONS INC", "MSP | DATTO HOLDING CORP", "MSA | MSA SAFETY INC", "MSF.MU | MICROSOFT CORP", "MSF.HM | MICROSOFT CORP", "MSF.DU | MICROSOFT CORP", "MSF.HA | MICROSOFT CORP", "MSF.SG | MICROSOFT CORP", "MSF.BE | MICROSOFT CORP", "MSF.BR | MICROSOFT CORP", "MSF.F | MICROSOFT CORP", "MSF.DE | MICROSOFT CORP", "MSFN | MAINSTREET FINANCIAL CORP", "MSFT | MICROSOFT CORP", "MSFT.MI | MICROSOFT CORP", "MSFT.VI | MICROSOFT CORP", "MSFT.SN | MICROSOFT CORP", "MSFT.MX | MICROSOFT CORP", "MSFT.BC | MICROSOFT CORP", "MSFT.SW | MICROSOFT CORP"};
        ArrayAdapter<String> newsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, dataArr);
        //ArrayAdapter<String> newsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, dataArr);
        searchAutoComplete.setBackgroundColor(Color.WHITE);
        searchAutoComplete.setTextColor(Color.BLACK);
        searchAutoComplete.setAdapter(newsAdapter);

        //Autocomplete
        // Listen to search view item on click event.
        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int itemIndex, long id) {
                String queryString = (String) adapterView.getItemAtPosition(itemIndex);
                String[] queryStringArray = queryString.split(" ");
                Log.i(TAG, "onItemClick: queryStringArray> " + queryString);
                Log.i(TAG, "onItemClick: queryStringArray length> " + queryStringArray[0]);
                searchAutoComplete.setText("" + "TSLA");
//                Toast.makeText(MainActivity.this, "you clicked " + queryString, Toast.LENGTH_LONG).show();
                navigateToTradeActivity(queryStringArray[0]);
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
        String value = "<html><a class=test href=\"https://www.finnhub.io\" style=\"color: black; text-decoration:none;\">Powered by Finnhub</a> </html>";
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
//        try {
//            //Favorites
//            JSONObject jObj3 = new JSONObject();
//            JSONArray jArr2 = new JSONArray();
//            JSONObject jObj4 = new JSONObject();
//
//            //Favorites Data
//            jObj3.put("tickerName", "MSFT");
//            jObj3.put("compName", "Microsoft");
//            //jObj3.put("latestPrice", "100.00");
//            jArr2.put(jObj3);
//            jObj4.put("tickerName", "GS");
//            jObj4.put("compName", "Goldman Sachs");
//            //jObj4.put("latestPrice", "200.00");
//            jArr2.put(jObj4);
//            sharedEditor.putString("Favorite", jArr2.toString());
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        sharedEditor.commit();
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
            Log.i(TAG, "readLocalStorage: jsonArrayPort> " + jsonArrayPort);
            for (Object item : jsonArrayPort) {
                List<String> tempList = new ArrayList<>();
                org.json.simple.JSONObject tempItem = (org.json.simple.JSONObject) item;
                tempList.add((String) tempItem.get("tickerSymbol")); //0 for ticker
                tempList.add((String) tempItem.get("numOfShares")); //1 for number of shares
                portfolioItems1.add(tempList);
            }
            Log.i(TAG, "readLocalStorage: portfolioItems1>> " + portfolioItems1);
            Log.i(TAG, "readLocalStorage: jsonArrayFav>> " + jsonArrayFav);

            for (Object item : jsonArrayFav) {
                List<String> tempList = new ArrayList<>();
                org.json.simple.JSONObject tempItem = (org.json.simple.JSONObject) item;
                tempList.add((String) tempItem.get("tickerName"));
                tempList.add((String) tempItem.get("compName")); // 1 for current price
//                tempList.add((String) tempItem.get("latestPrice")); // 2 for latest price
//                tempList.add("150.00"); //3 for price since last closed
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
        Log.i(TAG, "getCurrentStockPriceVals: portfolioJSONArray" + portfolioJSONArray);
        Log.i(TAG, "getCurrentStockPriceVals: favoriteJSONArray" + favoriteJSONArray);

        try {
            org.json.simple.JSONArray jsonArrayPort = (org.json.simple.JSONArray) jParser.parse(portfolioJSONArray);
            org.json.simple.JSONArray jsonArrayFav = (org.json.simple.JSONArray) jParser.parse(favoriteJSONArray);
            Log.i(TAG, "getCurrentStockPriceVals: jsonArrayFav1122> " + jsonArrayFav);
            int jsonArrayPortLength = jsonArrayPort.size();
            int jsonArrayFavoriteLength = jsonArrayFav.size();
            //Portfolio Values Start
//            int count = 0;
            for (Object item : jsonArrayPort) {
                fetchPortfolioValue((org.json.simple.JSONObject) item, jsonArrayPortLength);
            }
            for (Object item : jsonArrayFav) {

                fetchFavoriteValue((org.json.simple.JSONObject) item, jsonArrayFavoriteLength);
            }
            //Portfolio Values End

            //Favorite Values End
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void fetchPortfolioValue(org.json.simple.JSONObject portObject, int jsonArrayPortLength) {

        String tickerSymbol = "";
        String latestStockUrl1 = "";
        org.json.simple.JSONObject tempItem = portObject;
        Log.i(TAG, "getCurrentStockPriceVals tempItem: " + tempItem);
        tickerSymbol = (String) tempItem.get("tickerSymbol");
//        latestStock_url = latestStock_url + tickerSymbol;
        latestStockUrl1 = latestStock_url + tickerSymbol;
        Log.i(TAG, "getCurrentStockPriceVals tickerSymbol: " + tickerSymbol);
        Log.i(TAG, "getCurrentStockPriceVals latestStock_url: " + latestStockUrl1);
        String totalCostStocks = (String) tempItem.get("totalCostStocks");
        List<String> tempList = new ArrayList<>();

        //Fetch from API starts
        String finalTickerSymbol = tickerSymbol;
        JsonObjectRequest latestStockPrice = new JsonObjectRequest(Request.Method.GET, latestStockUrl1, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject latest_stock_response) {
                try {
                    Log.i("latest_response", "onResponse123: " + latest_stock_response.toString());

                    currentStockPrice1 = latest_stock_response.getString("c");
//                            double dp = Double.parseDouble(latest_stock_response.getString("dp"));
                    Log.i(TAG, "onResponse tempItem: " + tempItem);
                    Log.i(TAG, "onResponse: portfolioItems1>>" + portfolioItems1);
                    for (int k = 0; k < portfolioItems1.size(); k++) {
                        if (finalTickerSymbol.equals(portfolioItems1.get(k).get(0))) {
                            portfolioItems1.get(k).add(currentStockPrice1); // Adding current price on index 2
                            portfolioItems1.get(k).add(totalCostStocks);
                        }
                    }
//                    portfolioItems1.get(count).add(currentStockPrice1); // Adding current price on index 2
//                    portfolioItems1.get(count).add(totalCostStocks); // Adding market Value from Local Storage on index 3

                    Log.i(TAG, "getCurrentStockPriceVals11: " + portfolioItems1);
                    count += 1;
                    if (count == jsonArrayPortLength) {
                        Log.i(TAG, "getCurrentStockPriceVals: check1 " + count + ' ' + jsonArrayPortLength);
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

    private void fetchFavoriteValue(org.json.simple.JSONObject favoriteObject, int jsonArrayFavoriteLength) {

        org.json.simple.JSONObject tempItem = favoriteObject;
        String tickerSymbol = (String) tempItem.get("tickerName");
        Log.i(TAG, "fetchFavoriteValue: tickerSymbol> " + tickerSymbol);
//        latestStock_url = latestStock_url + tickerSymbol;
        String latestStockUrl1 = "";
        latestStockUrl1 = latestStock_url + tickerSymbol;
        Log.i(TAG, "fetchFavoriteValue: latestStock_url> " + latestStock_url);
        List<String> tempList = new ArrayList<>();
        String finalTickerSymbol = tickerSymbol;
        JsonObjectRequest latestStockPrice = new JsonObjectRequest(Request.Method.GET, latestStockUrl1, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject latest_stock_response) {
                try {

                    Log.i(TAG, "onResponse: count1> " + count1);
                    currentStockPrice1 = latest_stock_response.getString("c");
                    changeInPriceFav = latest_stock_response.getString("d");
                    getChangeInPriceFavPercentage = latest_stock_response.getString("dp");
                    for (int k = 0; k < favoriteItems1.size(); k++) {
                        if (finalTickerSymbol.equals(favoriteItems1.get(k).get(0))) {
                            favoriteItems1.get(k).add(currentStockPrice1); //Position 2
                            favoriteItems1.get(k).add(changeInPriceFav); //Position 3
                            favoriteItems1.get(k).add(getChangeInPriceFavPercentage); //Position 4
                        }
                    }
//                    favoriteItems1.get(count1).add(currentStockPrice1); //Position 2
//                    favoriteItems1.get(count1).add(changeInPriceFav); //Position 3
//                    favoriteItems1.get(count1).add(getChangeInPriceFavPercentage); //Position 4
                    count1 += 1;
                    Log.i("latest_response", "onResponse123 Fav: " + latest_stock_response.toString() + "    " + countAutoRefresh);
                    Log.i(TAG, "onResponse: Check refresh> " + count1 + " autoCount> " + countAutoRefresh);
                    Log.i(TAG, "onResponse: Check refresh> " + jsonArrayFavoriteLength);
                    if (count1 == jsonArrayFavoriteLength) {
                        favoriteRecycle();
                        enableSwipeToDeleteAndUndoFavorite();
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
        portRecAdaptor = new PortfolioRecyclerAdapter(portfolioItems1, this, this);
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
        SharedPreferences sharedPref = getSharedPreferences("LocalStorageValues", MODE_PRIVATE);
        String netWorthValue = sharedPref.getString("net_worth", "");
        String cashBalance = sharedPref.getString("cash_balance", "");
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
        favRecAdapter = new FavoriteRecyclerAdapter(favoriteItems1, this, this);
        ItemTouchHelper.Callback callback = new ItemMoveCallbackFavorite(favRecAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(favoriteRecView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        favoriteRecView.setLayoutManager(layoutManager);
        favoriteRecView.setAdapter(favRecAdapter);
        DividerItemDecoration dividerLine = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        favoriteRecView.addItemDecoration(dividerLine);

    }

    private void enableSwipeToDeleteAndUndo() {
        HomeSwipeToDelete swipeToDeleteCallback = new HomeSwipeToDelete(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                final List<String> item = portRecAdaptor.getData().get(position);

                List<List<String>> updatedPortfolioList = portRecAdaptor.removeItem(position);
                //Update Local Storage with this new List
                listToJsonPortfolio(updatedPortfolioList);
                Snackbar snackbar = Snackbar
                        .make(mainConstraintLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        List<List<String>> updatedPortfolio = portRecAdaptor.restoreItem(item, position);
                        listToJsonPortfolio(updatedPortfolio);
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

    public void listToJsonPortfolio(List<List<String>> updatedPortfolioList) {
        SharedPreferences sharedPref = getSharedPreferences("LocalStorageValues", MODE_PRIVATE);
        SharedPreferences.Editor sharedEditor = sharedPref.edit();
        JSONArray tempJsonArray = new JSONArray();
        for (int j = 0; j < updatedPortfolioList.size(); j++) {
            JSONObject tempObj = new JSONObject();
            try {
                tempObj.put("tickerSymbol", updatedPortfolioList.get(j).get(0));
                tempObj.put("numOfShares", updatedPortfolioList.get(j).get(1));
                tempObj.put("totalCostStocks", updatedPortfolioList.get(j).get(3));

                tempJsonArray.put(tempObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        sharedEditor.putString("Portfolio", tempJsonArray.toString());
        sharedEditor.commit();
        Log.i(TAG, "onSwiped: sharedEditor Portfolio" + sharedPref.getString("Portfolio", ""));
    }

    private void enableSwipeToDeleteAndUndoFavorite() {
        HomeSwipeToDelete swipeToDeleteCallback = new HomeSwipeToDelete(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                final List<String> item = favRecAdapter.getData().get(position);

                //Update Local Storage with this new List
                List<List<String>> updatedFavoriteList = favRecAdapter.removeItem(position);
                listToJsonFavorite(updatedFavoriteList);

                Snackbar snackbar = Snackbar
                        .make(mainConstraintLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        List<List<String>> updateFavorite = favRecAdapter.restoreItem(item, position);
                        //Add values back to local storage
                        listToJsonFavorite(updateFavorite);
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

    public void listToJsonFavorite(List<List<String>> updatedFavoriteList) {
        SharedPreferences sharedPref = getSharedPreferences("LocalStorageValues", MODE_PRIVATE);
        SharedPreferences.Editor sharedEditor = sharedPref.edit();
        JSONArray tempJsonArray = new JSONArray();
        for (int j = 0; j < updatedFavoriteList.size(); j++) {
            JSONObject tempObj = new JSONObject();
            try {
                tempObj.put("tickerName", updatedFavoriteList.get(j).get(0));
                tempObj.put("compName", updatedFavoriteList.get(j).get(1));

                tempJsonArray.put(tempObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        sharedEditor.putString("Favorite", tempJsonArray.toString());
        sharedEditor.commit();
        Log.i(TAG, "onSwiped: sharedEditor " + sharedPref.getString("Favorite", ""));
    }

    @Override
    public void onChevronClick(int position) {
        favoriteItems1.get(position);
        Intent intent = new Intent(this, TradeActivity.class);
        String searchId = favoriteItems1.get(position).get(0);
        intent.putExtra("SEARCH_ID", searchId);
        startActivity(intent);
    }

    @Override
    public void onChevronClickPort(int position) {
        portfolioItems1.get(position);
        Intent intent = new Intent(this, TradeActivity.class);
        String searchId = portfolioItems1.get(position).get(0);
        intent.putExtra("SEARCH_ID", searchId);
        startActivity(intent);
    }
}