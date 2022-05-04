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
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
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
    double netWorthDouble = 0;
    int helloCount = 0;
    private static final DecimalFormat df = new DecimalFormat("0.00");


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
        writeLocalStorage();
        readLocalStorage();
        Log.i(TAG, "onCreateAAAA: >"+portfolioItems1);
        Log.i(TAG, "onCreateCCC: >"+favoriteItems1);
        if (portfolioItems1.size() !=0 || favoriteItems1.size() !=0){
            Log.i(TAG, "onCreate: Inside If Gone");
            progressBarArea.setVisibility(View.VISIBLE);
            mainConstraint.setVisibility(View.GONE);
        }
        Log.i(TAG, "onCreate: helloCount");
//        if (helloCount != 0){
//            progressBarArea.setVisibility(View.VISIBLE);
//            mainConstraint.setVisibility(View.GONE);
//        }
//        progressBarArea.setVisibility(View.VISIBLE);
//        mainConstraint.setVisibility(View.GONE);

//        writeLocalStorage();
        setHomeDateSection();


        setPortfolioHeadings();
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
//        progressBarArea.setVisibility(View.VISIBLE);
//        mainConstraint.setVisibility(View.GONE);
        setHomeDateSection();
        setHomeFinnhubFooter();
        readLocalStorage();
//        count = 0;
//        count1 = 0;
//        getCurrentStockPriceVals();
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
//                Log.i(TAG, "run: Autorefresh> " + countAutoRefresh);
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
        String dataArr[] = {"TSE | TRINSEO PLC", "TSP | TUSIMPLE HOLDINGS INC - A", "TSC | TRISTATE CAPITAL HLDGS INC", "TSQ | TOWNSQUARE MEDIA INC - CL A", "TSN | TYSON FOODS INC-CL A", "TSLA | TESLA INC", "T | AT&T INC", "TD | TORONTO-DOMINION BANK", "TU | TELUS CORP", "TH | TARGET HOSPITALITY CORP", "TW | TRADEWEB MARKETS INC-CLASS A", "TG | TREDEGAR CORP", "TR | TOOTSIE ROLL INDS", "TT | TRANE TECHNOLOGIES PLC", "TK | TEEKAY CORP", "TA | TRAVELCENTERS OF AMERICA INC", "G | GENPACT LTD", "GS | GOLDMAN SACHS GROUP INC", "GP | GREENPOWER MOTOR CO INC", "GT | GOODYEAR TIRE & RUBBER CO", "GH | GUARDANT HEALTH INC", "GD | GENERAL DYNAMICS CORP", "GB | GLOBAL BLUE GROUP HOLDING AG", "GL | GLOBE LIFE INC", "GM | GENERAL MOTORS CO", "GO | GROCERY OUTLET HOLDING CORP", "GE | GENERAL ELECTRIC CO", "GSV | GOLD STANDARD VENTURES CORP", "GSM | FERROGLOBE PLC", "GSL | GLOBAL SHIP LEASE INC-CL A", "AGESF | AGEAS", "M | MACY'S INC", "MG | MISTRAS GROUP INC", "MD | MEDNAX INC", "MA | MASTERCARD INC - A", "MQ | MARQETA INC-A", "MC | MOELIS & CO - CLASS A", "MS | MORGAN STANLEY", "MU | MICRON TECHNOLOGY INC", "ME | 23ANDME HOLDING CO -CLASS A", "MO | ALTRIA GROUP INC", "MX | MAGNACHIP SEMICONDUCTOR CORP", "MN | MANNING & NAPIER INC", "MP | MP MATERIALS CORP", "ML | MONEYLION INC", "MS | MORGAN STANLEY", "MSN | EMERSON RADIO CORP", "MSM | MSC INDUSTRIAL DIRECT CO-A", "MSI | MOTOROLA SOLUTIONS INC", "MSP | DATTO HOLDING CORP", "MSA | MSA SAFETY INC", "MSFT | MICROSOFT CORP", "MSFN | MAINSTREET FINANCIAL CORP"};
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
                searchAutoComplete.setText("" + queryStringArray[0]);
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

//    private void setPortfolioHeader() {
//        SharedPreferences sharedPref = getSharedPreferences("LocalStorageValues", MODE_PRIVATE);
//        String netWorth = sharedPref.getString("net_worth", "");
//        String cashBalance = sharedPref.getString("cash_balance", "");
//        TextView net_worth, cash_balance, netWorthHead, cashBalanceHead;
//        netWorthHead = findViewById(R.id.portfolio_net_worth_head);
//        cashBalanceHead = findViewById(R.id.portfolio_cash_bal_head);
//        net_worth = findViewById(R.id.portfolio_net_worth_val);
//        cash_balance = findViewById(R.id.protfolio_cash_bal_val);
//        net_worth.setText(netWorth);
//        cash_balance.setText(cashBalance);
//        netWorthHead.setText("Net Worth");
//        cashBalanceHead.setText("Cash Balance");
//
//
//    }

    private void setHomeDateSection() {
        TextView homeDate;
        homeDate = findViewById(R.id.home_date_sec);
        String timeStamp = new SimpleDateFormat("dd MMMM yyyy").format(Calendar.getInstance().getTime());
        homeDate.setText(timeStamp);
    }

    private void setHomeFinnhubFooter() {
        String value = "<html><a class=test href=\"https://www.finnhub.io\" style=\"color: black; text-decoration:none;\">Powered by Finnhub</a> </html>";
        TextView finhubLink = findViewById(R.id.finhub_link);
        Spannable s = (Spannable) Html.fromHtml(value);
        for (URLSpan u: s.getSpans(0, s.length(), URLSpan.class)) {
            s.setSpan(new UnderlineSpan() {
                public void updateDrawState(TextPaint tp) {
                    tp.setUnderlineText(false);
                }
            }, s.getSpanStart(u), s.getSpanEnd(u), 0);
        }
        finhubLink.setText(s);



//        finhubLink.setText(Html.fromHtml(value));
        finhubLink.setLinkTextColor(Color.parseColor("#a4a4a3"));
        finhubLink.setMovementMethod(LinkMovementMethod.getInstance());

    }

    private void writeLocalStorage() {
        SharedPreferences sharedPref = getSharedPreferences("LocalStorageValues", MODE_PRIVATE);
        SharedPreferences.Editor sharedEditor = sharedPref.edit();
        String net_worth = sharedPref.getString("net_worth", "");
        String cash_balance = sharedPref.getString("cash_balance", "");
        if (net_worth.isEmpty()) {
            sharedEditor.putString("net_worth", "25000.00");
            sharedEditor.commit();
        }
        if (cash_balance.isEmpty()) {
            sharedEditor.putString("cash_balance", "25000.00");
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
        //sharedEditor.commit();
    }

    private void readLocalStorage() {
        portfolioItems1 = new ArrayList<>();
        favoriteItems1 = new ArrayList<>();
        SharedPreferences sharedPref = getSharedPreferences("LocalStorageValues", MODE_PRIVATE);
        SharedPreferences.Editor sharedEditor = sharedPref.edit();
        String favoriteJSONArray = sharedPref.getString("Favorite", "");
        String portfolioJSONArray = sharedPref.getString("Portfolio", "");
        Log.i(TAG, "readLocalStorage: yo> "+favoriteJSONArray);
        Double tempTotalCost = 0.00;

        JSONParser jParser = new JSONParser();
        try {
            if(!portfolioJSONArray.isEmpty()){
                org.json.simple.JSONArray jsonArrayPort = (org.json.simple.JSONArray) jParser.parse(portfolioJSONArray);
                for (Object item : jsonArrayPort) {
                    List<String> tempList = new ArrayList<>();
                    org.json.simple.JSONObject tempItem = (org.json.simple.JSONObject) item;
                    tempList.add((String) tempItem.get("tickerSymbol")); //0 for ticker
                    tempList.add((String) tempItem.get("numOfShares")); //1 for number of shares
                    String totalCostStocks = (String) tempItem.get("totalCostStocks");
                    Double totalCostStocksDouble = Double.parseDouble(totalCostStocks);
                    tempTotalCost += totalCostStocksDouble;
                    portfolioItems1.add(tempList);
                }
            }
            netWorthDouble = tempTotalCost;



            Log.i(TAG, "readLocalStorage: portfolioItems1>> " + portfolioItems1);


            if(!favoriteJSONArray.isEmpty()){
                org.json.simple.JSONArray jsonArrayFav = (org.json.simple.JSONArray) jParser.parse(favoriteJSONArray);
                for (Object item : jsonArrayFav) {
                    List<String> tempList = new ArrayList<>();
                    org.json.simple.JSONObject tempItem = (org.json.simple.JSONObject) item;
                    tempList.add((String) tempItem.get("tickerSymbol"));
                    tempList.add((String) tempItem.get("compName")); // 1 for current price
                    favoriteItems1.add(tempList);
                }
                Log.i(TAG, "readLocalStorage: jsonArrayFav>> " + jsonArrayFav);
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    private void getCurrentStockPriceVals() {
        Log.i(TAG, "Inside getCurrentStockPriceVals: ");
//        int count1 = 0;

        JSONParser jParser = new JSONParser();
        SharedPreferences sharedPref = getSharedPreferences("LocalStorageValues", MODE_PRIVATE);
        String favoriteJSONArray = sharedPref.getString("Favorite", "");
        String portfolioJSONArray = sharedPref.getString("Portfolio", "");
        Log.i(TAG, "getCurrentStockPriceVals: portfolioJSONArray" + portfolioJSONArray);
        Log.i(TAG, "getCurrentStockPriceVals: favoriteJSONArray11" + favoriteJSONArray);

        try {
            org.json.simple.JSONArray jsonArrayPort = null;
            org.json.simple.JSONArray jsonArrayFav = null;
            if(!portfolioJSONArray.isEmpty()){
                jsonArrayPort = (org.json.simple.JSONArray) jParser.parse(portfolioJSONArray);
                int jsonArrayPortLength = jsonArrayPort.size();
                for (Object item : jsonArrayPort) {
                    fetchPortfolioValue((org.json.simple.JSONObject) item, jsonArrayPortLength);
                }
            }
            if(!favoriteJSONArray.isEmpty()){
                jsonArrayFav = (org.json.simple.JSONArray) jParser.parse(favoriteJSONArray);
                Log.i(TAG, "getCurrentStockPriceVals: jsonArrayFav1234> "+jsonArrayFav);
                int jsonArrayFavoriteLength = jsonArrayFav.size();
                for (Object item : jsonArrayFav) {
                    fetchFavoriteValue((org.json.simple.JSONObject) item, jsonArrayFavoriteLength);
                }
            }

            Log.i(TAG, "getCurrentStockPriceVals: jsonArrayFav1122> " + jsonArrayFav);


            //Portfolio Values Start
//            int count = 0;


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
        String tickerSymbol = (String) tempItem.get("tickerSymbol");
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
        //setPortfolioHeadings();
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

        //Calculate Net Worth
        Double cashBalanceFloat = Double.parseDouble(cashBalance);
        Double netWorthFinal = cashBalanceFloat + netWorthDouble;
        String netWorthFinalString = df.format(netWorthFinal);

        TextView sectionHeading, netWorthHead, cashBalanceHead, netWorthVal, cashBalanceVal;
        sectionHeading = findViewById(R.id.portfolio_heading_text);
        netWorthHead = findViewById(R.id.portfolio_net_worth_head);
        netWorthVal = findViewById(R.id.portfolio_net_worth_val);
        cashBalanceHead = findViewById(R.id.portfolio_cash_bal_head);
        cashBalanceVal = findViewById(R.id.protfolio_cash_bal_val);
        Log.i(TAG, "setPortfolioHeadings: netWorthFinalString> "+netWorthFinalString);

        sectionHeading.setText("PORTFOLIO");
        netWorthHead.setText("Net Worth");
        netWorthVal.setText("$"+netWorthFinalString);
        cashBalanceHead.setText("Cash Balance");
        cashBalanceVal.setText("$"+cashBalance);

    }

    private void favoriteRecycle() {
        //favoriteRecycler
        TextView sectionHeadingFav;
        sectionHeadingFav = findViewById(R.id.home_header_fav_text);
        sectionHeadingFav.setText("FAVORITE");
        favoriteRecView = findViewById(R.id.favoriteRecycler);
//        portfolioRecView.setVisibility(View.GONE); To hide a view based on a condition
        Log.i(TAG, "favoriteRecycle: favoriteItems1>> "+favoriteItems1);
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
                listToJsonPortfolio(updatedPortfolioList, true);
                Snackbar snackbar = Snackbar
                        .make(mainConstraintLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        List<List<String>> updatedPortfolio = portRecAdaptor.restoreItem(item, position);
                        listToJsonPortfolio(updatedPortfolio, false);
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

    public void listToJsonPortfolio(List<List<String>> updatedPortfolioList, Boolean isDelete) {
        SharedPreferences sharedPref = getSharedPreferences("LocalStorageValues", MODE_PRIVATE);
        SharedPreferences.Editor sharedEditor = sharedPref.edit();
        JSONArray tempJsonArray = new JSONArray();
        String costOfItem = "";
        String oldCashBalance = sharedPref.getString("cash_balance","");
        Double newCashBalance = 0.00;
        String newCashBalanceString = "";
        for (int j = 0; j < updatedPortfolioList.size(); j++) {
            JSONObject tempObj = new JSONObject();
            try {
                tempObj.put("tickerSymbol", updatedPortfolioList.get(j).get(0));
                tempObj.put("numOfShares", updatedPortfolioList.get(j).get(1));
                tempObj.put("totalCostStocks", updatedPortfolioList.get(j).get(3));
                costOfItem = updatedPortfolioList.get(j).get(3);

                tempJsonArray.put(tempObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Double costOfItemDouble = Double.parseDouble(costOfItem);

        if(isDelete == true){
            newCashBalance = Double.parseDouble(oldCashBalance) + costOfItemDouble;
            newCashBalanceString = df.format(newCashBalance);
            sharedEditor.putString("cash_balance", newCashBalanceString);
        }
        else {
            newCashBalance = Double.parseDouble(oldCashBalance) - costOfItemDouble;
            newCashBalanceString = df.format(newCashBalance);
            sharedEditor.putString("cash_balance", newCashBalanceString);
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
                tempObj.put("tickerSymbol", updatedFavoriteList.get(j).get(0));
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
        String searchId = favoriteItems1.get(position).get(0);
        navigateToTradeActivity(searchId);
    }

    @Override
    public void onChevronClickPort(int position) {
        portfolioItems1.get(position);
        String searchId = portfolioItems1.get(position).get(0);
        navigateToTradeActivity(searchId);
    }
}