package com.example.stocks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
//For highcharts
import com.highsoft.highcharts.common.hichartsclasses.*;
import com.highsoft.highcharts.common.HIColor;
import com.highsoft.highcharts.core.HIChartView;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

public class TradeActivity extends AppCompatActivity {
    private Menu menu;
    private String search_id;
    private RequestQueue requestQueue;
    //    private String baseUrl = "http://10.0.2.2:8080/";
    private String baseUrl = "https://csci571hw8-backend-346006.wl.r.appspot.com/";
    private DecimalFormat df = new DecimalFormat("0.00");
    private List<String> companyDescription = new ArrayList<String>();
    private List<String> historySummary = new ArrayList<String>();
    private List<String> historyCharts = new ArrayList<String>();
    //    private List<String> companyPeers = new ArrayList<String>();
    //    private List<JSONObject> companyNews = new ArrayList<JSONObject>();
    JSONArray companyNews = new JSONArray();
    private List<JSONObject> companyRecommendation = new ArrayList<JSONObject>();
    private List<JSONObject> companyEarning = new ArrayList<JSONObject>();
    private java.util.Date latest_stock_time;
    private java.util.Date date = new Date();
    private boolean market_open;
    private long unix_from_6, unix_to;
    private static boolean initializedPicasso = false;
    private NestedScrollView page_content;
    private Context context;
    String comp_name;
    NewsAdapter newsAdapter;
    RecyclerView newsRecycleView;

    ViewPager ViewPager;
    TabLayout TabLayout;
    Toolbar toolbar;
    MenuItem starred;
    String latest_price_main;
    public boolean isFavorite;
    private LinearLayout progressBarArea;
    private NestedScrollView nestedScrollView;
    AtomicInteger requestsCounter;

    private static final String TAG = "TradeActivity";
    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_Stocks);
        context = this;
        page_content = (NestedScrollView) findViewById(R.id.page_content);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trade);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        search_id = getIntent().getStringExtra("SEARCH_ID");
        actionBar.setTitle(Html.fromHtml("<font color='#000000'>"+search_id+"</font>"));

        if (!initializedPicasso) {
            //Defining Picasso
            Picasso.setSingletonInstance(new Picasso.Builder(this).build());
            initializedPicasso = true;
        }
        nestedScrollView = (NestedScrollView) findViewById(R.id.page_content);  // detail contents
        progressBarArea = (LinearLayout) findViewById(R.id.lProgressBarArea);   // progress bar area
        // show progress bar area
        requestsCounter = new AtomicInteger(5);
        progressBarArea.setVisibility(View.VISIBLE);
        nestedScrollView.setVisibility(View.GONE);

        requestQueue = Volley.newRequestQueue(this);

//        requestsCounter = new AtomicInteger(3);
        getCompanyDescription();
        getLatestStockPrice();
        getCompanyPeers();
        newsRecycleView = findViewById(R.id.new_details_recycle_view);
        newsRecycleView.setLayoutManager(new LinearLayoutManager(this));
        getCompanyNews();
        setValuesForCompanyRecommendation();
        setValuesForEPSCharts();
        getCompanySocialSentiments();
//        progressBarArea.setVisibility(View.GONE);
//        setProgressBarIndeterminateVisibility(false);
//        doTheAutoRefresh();

        requestQueue.addRequestFinishedListener(req -> {
            requestsCounter.decrementAndGet();
            Log.i(TAG, "onCreate: ");
            Log.d(String.valueOf(requestsCounter.get()),"-----------i am called on on resume------");
            if (requestsCounter.get() == 0) {
//                getCompanyDescription();
                Log.d(String.valueOf(requestsCounter.get()),"-----------i loadin p in resume------");

                // set visibility GONE for progress bar, show nestedScrollView
                progressBarArea.setVisibility(View.GONE);
                nestedScrollView.setVisibility(View.VISIBLE);
            }
        });





    }
//    private void doTheAutoRefresh() {
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // Write code for your refresh logic
//                doTheAutoRefresh();
//            }
//        }, 15000);
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Favourite color setting
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.favorite, menu);
        this.menu = menu;
        starred = this.menu.findItem(R.id.action_favorite);
        addRemoveFavorite();

        return true;

//        return super.onCreateOptionsMenu(menu);
    }

    public boolean addRemoveFavorite(){

        SharedPreferences sharedPref = getSharedPreferences("LocalStorageValues", MODE_PRIVATE);
        SharedPreferences.Editor sharedEditor = sharedPref.edit();
        String favourite= sharedPref.getString("Favorite","");
//        Log.i(TAG, "addRemoveFavorite: "+favourite);
        if(!favourite.isEmpty()){
//            Log.i(TAG, "addRemoveFavorite: ");
            JSONParser Parser = new JSONParser();
            try {
                org.json.simple.JSONArray Favourite = (org.json.simple.JSONArray) Parser.parse(favourite);
                Log.i(TAG, "addRemoveFavorite: "+Favourite);
                for (Object fav_item : Favourite) {

                    org.json.simple.JSONObject fav = (org.json.simple.JSONObject) fav_item;
                    Log.i(TAG, "addRemoveFavoriteidddd: "+fav.get("tickerSymbol")+" "+search_id);
                    if (search_id.equals(fav.get("tickerSymbol"))) {

                        Log.i(TAG, "addRemoveFavoritecondition: ");
                        isFavorite = true;
                        changeStar();
                        break;



                    }
//                    else{
//                        isFavorite = false;
//                        changeStar();
//                        return isFavorite;
//                    }
                }
                if(!isFavorite){
                    isFavorite = false;
                    changeStar();
                }
            }catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if(favourite.isEmpty()){
            isFavorite = false;
            changeStar();


        }
        return isFavorite;
    }

    private  void changeStar(){
        if(isFavorite){
            Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.star);
            Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
            DrawableCompat.setTint(wrappedDrawable, Color.RED);
            starred.setIcon(ContextCompat.getDrawable(this, R.drawable.star));

        }
        else{
            Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.star_outline);
            Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
            DrawableCompat.setTint(wrappedDrawable, Color.RED);
            starred.setIcon(ContextCompat.getDrawable(this, R.drawable.star_outline));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if(item.getItemId() == R.id.action_favorite){
//                Log.i(TAG, "onOptionsItemSelected: ");
//                changeFavorite();
//        }
//        else if (item.getItemId() == android.R.id.home) {
//            onBackPressed();
////            return true;
//        }
//        return true;
        switch (item.getItemId()) {
            case R.id.action_favorite:
                changeFavorite();
                break;
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
    public void changeFavorite() {
        Log.i(TAG, "before fav: "+isFavorite);
        isFavorite = !isFavorite;
        changeStar();
        Log.i(TAG, "after fav: "+isFavorite);
        if(isFavorite){
            JSONObject fav = new JSONObject();
            Double lp = Double.parseDouble(latest_price_main);
            try {
                fav.put("tickerSymbol", search_id);
                fav.put("compName", comp_name);
                fav.put("latestPrice", df.format(lp));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            SharedPreferences sharedPref = getSharedPreferences("LocalStorageValues", MODE_PRIVATE);
            SharedPreferences.Editor sharedEditor = sharedPref.edit();
            String favourite= sharedPref.getString("Favorite","");
            Log.i(TAG, "isFav fav: "+favourite);
            if(favourite.isEmpty()){
                JSONArray Favourite_Array = new JSONArray();

                Favourite_Array.put(fav);
                sharedEditor.putString("Favorite",Favourite_Array.toString());
                sharedEditor.commit();
                Log.i(TAG, "newFav: "+Favourite_Array.toString());
            }
            else if(!favourite.isEmpty()){
                JSONParser Parser = new JSONParser();
                try {
                    org.json.simple.JSONArray Favourite_Array = (org.json.simple.JSONArray) Parser.parse(favourite);
//                    Log.i(TAG, "changeFavorite: "+Favourite_Array);
                    for (Object fav_item : Favourite_Array) {
                        org.json.simple.JSONObject favjson = (org.json.simple.JSONObject) fav_item;
                        if (search_id.equals(favjson.get("tickerSymbol"))) {
                            Favourite_Array.remove(favjson);
                            break;
                        }

                    }
                    Favourite_Array.add(fav);
                    sharedEditor.putString("Favorite",Favourite_Array.toString());
                    sharedEditor.commit();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
            Toast.makeText(context, search_id+" is added to favorites", Toast.LENGTH_LONG).show();


        }
        else{
            SharedPreferences sharedPref = getSharedPreferences("LocalStorageValues", MODE_PRIVATE);
            SharedPreferences.Editor sharedEditor = sharedPref.edit();
            String favourite= sharedPref.getString("Favorite","");
            Log.i(TAG, "!isFav fav: "+favourite);
            JSONParser Parser = new JSONParser();
            try {
                org.json.simple.JSONArray Favourite_Array = (org.json.simple.JSONArray) Parser.parse(favourite);
                for (Object fav_item : Favourite_Array) {
                    org.json.simple.JSONObject favjson = (org.json.simple.JSONObject) fav_item;
                    if (search_id.equals(favjson.get("tickerSymbol"))) {
                        Favourite_Array.remove(favjson);
                        sharedEditor.putString("Favorite",Favourite_Array.toString());
                        sharedEditor.commit();
                        break;
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }


            Toast.makeText(context, search_id+" is removed to favorites", Toast.LENGTH_LONG).show();
        }
    }

    // Fetching Company Description
    private void getCompanyDescription() {

        String comp_url = baseUrl + "companyDescription?ticker=" + search_id;
        Log.i("url", "onCreate123: " + comp_url);
        JsonObjectRequest compDesc = new JsonObjectRequest(Request.Method.GET, comp_url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject compD_response) {
                try {
//                            Log.i("response", "onResponse: "+compD_response.toString());
                    String x = compD_response.getString("ticker");
                    comp_name = compD_response.getString("name");
//                            Log.i("ticker", "onResponse: "+x);
                    companyDescription.add(x);
                    setValuesForCompanyDescription(compD_response);
//
//                            Log.d("---------","TICKER"+x);
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
        requestQueue.add(compDesc);


    }

    private void setValuesForCompanyDescription(JSONObject compD_response) {
        try {
            String tickerSymbol = compD_response.getString("ticker");
            String comp_logo = compD_response.getString("logo");
            String comp_name = compD_response.getString("name");
            TextView ticker = (TextView) findViewById(R.id.ticker_symbol);
            ticker.setText(tickerSymbol);
            TextView name = (TextView) findViewById(R.id.company_name);
            name.setText(comp_name);
            TextView company_name = (TextView) findViewById(R.id.company_name_table);
            company_name.setText(comp_name);
            ImageView logo = findViewById(R.id.company_logo);
            Picasso.get().load(comp_logo).into(logo);

            // For About
            String ipo = compD_response.getString("ipo");
            String ipo_date = ipo.substring(5) + "-" + ipo.substring(0, 4);
            TextView IPO_value = (TextView) findViewById(R.id.IPO_value);
            IPO_value.setText(ipo_date);
            String industry = compD_response.getString("finnhubIndustry");
            TextView industry_value = (TextView) findViewById(R.id.Industry_value);
            industry_value.setText(industry);
            String webpage = compD_response.getString("weburl");
            TextView webpage_value = (TextView) findViewById(R.id.webpage_value);
            webpage_value.setText(webpage);
//            webpage_value.setText(Html.fromHtml("<font color=#2525ff><u>"+webpage+"</u></font>"));
            Linkify.addLinks(webpage_value, Linkify.WEB_URLS);
            webpage_value.setLinkTextColor(Color.parseColor("#2525ff"));


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // Fetching Latest Stock Price
    private void getLatestStockPrice() {

        String latestStock_url = baseUrl + "stockPrice?ticker=" + search_id;
        Log.i("url", "onCreate: " + latestStock_url);
        JsonObjectRequest latestStockPrice = new JsonObjectRequest(Request.Method.GET, latestStock_url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject latest_stock_response) {
                try {
//                    Log.i("latest_response", "onResponse: "+latest_stock_response.toString());
                    setValuesForLatestStockPrice(latest_stock_response);
                    timeManipulation(latest_stock_response.getString("t"));
//                    getHistoricalSummary(unix_from_6, unix_to);
                    //For Tablayout

                    double dp = Double.parseDouble(latest_stock_response.getString("dp"));
                    toolbar = (Toolbar) findViewById(R.id.toolbar);
                    latest_price_main = latest_stock_response.getString("c");

//        setSupportActionBar(toolbar);
                    ViewPager = (androidx.viewpager.widget.ViewPager) findViewById(R.id.viewPager);
                    ViewPageAdapter adapter = new ViewPageAdapter(getSupportFragmentManager());

                    adapter.addFragment(new historicalSummaryFragment(search_id, unix_from_6, unix_to, dp), "");
                    adapter.addFragment(new historicalChartsFragment(search_id), "");

                    ViewPager.setAdapter(adapter);


                    TabLayout = (TabLayout) findViewById(R.id.tabLayout);
                    TabLayout.setupWithViewPager(ViewPager);

                    TabLayout.setSelectedTabIndicatorColor(Color.parseColor("#6200EE"));
                    TabLayout.setTabTextColors(Color.parseColor("#000000"), Color.parseColor("#6200EE"));
                    TabLayout.getTabAt(0).setIcon(R.drawable.chart_line);
                    TabLayout.getTabAt(1).setIcon(R.drawable.clock_time_three);
                    portfolioDetails(latest_stock_response);

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

    private void setValuesForLatestStockPrice(JSONObject latest_stock_response) {
        try {
            Context context = this;
            String latestPrice = latest_stock_response.getString("c");
            double lp = Double.parseDouble(latestPrice);
//            df.format(lp);
//            latestPrice = lp+"";
            String changePrice = latest_stock_response.getString("d");
            double cp = Double.parseDouble(changePrice);
//            df.format(cp);
//            changePrice = cp+"";
            String changePercent = latest_stock_response.getString("dp");
            double cpercent = Double.parseDouble(changePercent);
//            df.format(cpercent);
//            Precision.round(cpercent,2);
//            Log.i("latest_stock+percent", "setValuesForLatestStockPrice: "+df.format(cpercent));
            changePercent = cpercent + "";
            TextView latest_price = (TextView) findViewById(R.id.latest_price);
            latest_price.setText("$"+df.format(lp));
            TextView change_percent = (TextView) findViewById(R.id.change_percent);
            change_percent.setText("$"+df.format(cp) + " (" + df.format(cpercent) + "%)");
            if (cpercent > 0) {
                Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.trending_up);
                Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
                DrawableCompat.setTint(wrappedDrawable, Color.parseColor("#00a400"));


                change_percent.setTextColor(Color.parseColor("#00a400"));
                change_percent.setCompoundDrawablesWithIntrinsicBounds(R.drawable.trending_up, 0, 0, 0);
            } else {
                Drawable unwrappedDrawable = AppCompatResources.getDrawable(context, R.drawable.trending_down);
                Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);
                DrawableCompat.setTint(wrappedDrawable, Color.parseColor("#ff0001"));
                change_percent.setTextColor(Color.parseColor("#ff0001"));
                change_percent.setCompoundDrawablesWithIntrinsicBounds(R.drawable.trending_down, 0, 0, 0);
            }

            //For Stats
            TextView stats_head = (TextView) findViewById(R.id.stats_heading);
            stats_head.setText("Stats");
            String openPrice = latest_stock_response.getString("o");
            double op = Double.parseDouble(openPrice);
            TextView open_price = (TextView) findViewById(R.id.open_price);
            open_price.setText("Open Price : $" + df.format(op));
            String highPrice = latest_stock_response.getString("h");
            double hp = Double.parseDouble(highPrice);
            TextView high_price = (TextView) findViewById(R.id.high_price);
            high_price.setText("High Price : $" + df.format(hp));
            String lowPrice = latest_stock_response.getString("l");
            double lowp = Double.parseDouble(lowPrice);
            TextView low_price = (TextView) findViewById(R.id.low_price);
            low_price.setText("Low Price : $" + df.format(lowp));
            String previousClose = latest_stock_response.getString("pc");
            double pc = Double.parseDouble(previousClose);
            TextView prev_close = (TextView) findViewById(R.id.close_price);
            prev_close.setText("Prev. Close : $" + df.format(pc));


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    //Time Manipulation
    private void timeManipulation(String timestamp) {
        long t = Long.parseLong(timestamp);
        latest_stock_time = new java.util.Date((long) t * 1000);
        int timeinMins = date.getMinutes();
        date.setMinutes(timeinMins - 5);
//        Log.i("date", "today's date: "+date);
//        Log.i("latest_date", "today's date: "+latest_stock_time);
        int comparison = latest_stock_time.compareTo(date);
//        Log.i("comparison", "comparison: "+comparison);
        if (comparison == -1) {
            market_open = false;
        } else {
            market_open = true;
        }
        if (market_open) {
            unix_to = date.getTime() / 1000L;
            date.setHours(date.getHours() - 6);
            unix_from_6 = date.getTime() / 1000L;
//            Log.i("unix from", "timeManipulation: "+unix_from_6);
//            Log.i("unix to", "timeManipulation: "+unix_to);
        } else {
            unix_to = latest_stock_time.getTime() / 1000L;
            latest_stock_time.setHours(latest_stock_time.getHours() - 6);
            unix_from_6 = latest_stock_time.getTime() / 1000L;
//            Log.i("unix from", "timeManipulation: "+unix_from_6);
//            Log.i("unix to", "timeManipulation: "+unix_to);

        }
    }



    // Fetch Company Peers
    private void getCompanyPeers() {

        String compPeers_url = baseUrl + "companyPeers?ticker=" + search_id;
        Log.i("url", "onCreate: " + compPeers_url);
        JsonArrayRequest company_Peers = new JsonArrayRequest(Request.Method.GET, compPeers_url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray comp_peers_response) {
                try {
                    List<String> peerrr = new ArrayList<String>();
//
                    for (int i = 0; i < comp_peers_response.length(); i++) {
                        peerrr.add(comp_peers_response.getString(i));
                    }
                    setValuesForCompanyPeers(comp_peers_response);
//                    Log.i("peers", "Peers: "+companyPeers);

//
//

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
        requestQueue.add(company_Peers);


    }

    //Set Valued for Company Peers
    private void setValuesForCompanyPeers(JSONArray companyPeers) {

        LinearLayout comp_peer = (LinearLayout) findViewById(R.id.company_peers_value);
        for(int i = 0; i<companyPeers.length();i++){
            String peers = "";

            TextView peer = new TextView(context);

            if(peer.getParent() != null) {
                ((ViewGroup)peer.getParent()).removeView(peer); // <- fix
            }
            if(i==0){
                try {
                    peers = companyPeers.getString(i);
//                    peer.setText(peers);
                    peer.setText(Html.fromHtml("<font color=#2525ff><u>"+peers+"</u></font>"));
                    peer.setClickable(true);//make your TextView Clickable
                    peer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String p = peer.getText().toString();
                            Intent intent = new Intent(getBaseContext(), TradeActivity.class);
                            intent.putExtra("SEARCH_ID", p);
                            startActivity(intent);
                        }
                    });
                    comp_peer.addView(peer);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                try {
                    peers = companyPeers.getString(i);
//                    peer.setText(", "+peers);
                    peer.setText(Html.fromHtml("<font color=#2525ff>, <u>"+peers+"</u></font>"));
                    peer.setClickable(true);//make your TextView Clickable
                    peer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String p = peer.getText().toString();
                            Intent intent = new Intent(getBaseContext(), TradeActivity.class);
                            intent.putExtra("SEARCH_ID", p.substring(2));
                            startActivity(intent);
                        }
                    });
                    comp_peer.addView(peer);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }






        }
        // Value for company peers
    }

    // Fetch data for History Chart
//    private void getHistoricalCharts(){
//
//        String histChart_url = baseUrl+"company_historical_charts/"+search_id;
//        Log.i("url", "onCreate: "+histChart_url);
//        JsonObjectRequest histCharts = new JsonObjectRequest(Request.Method.GET, histChart_url,null, new Response.Listener<JSONObject>()
//        {
//            @Override
//            public void onResponse(JSONObject history_chart_response) {
//                try {
////                    Log.i("histchartresponse", "onResponse: "+history_chart_response.toString());
////                    List c = history_summary_response.getString("c");
////                    for(int i = 0;i<history_summary_response.length();i++){
////                    historySummary.add(history_chart_response.getString("c"));
//                    setValuesForHistoricalCharts(history_chart_response);
//                    historySummary.add(history_chart_response.getString("c"));
////                        Log.i("history", "onResponse: "+historySummary);
////                    }
//
//                }catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        },
//                new Response.ErrorListener(){
//                    @Override
//                    public void onErrorResponse(VolleyError error){
//                        error.printStackTrace();
//                    }
//
//                });
//        requestQueue.add(histCharts);
//
//
//    }



    // Fetch Company News
    private void getCompanyNews() {

        String compNews_url = baseUrl + "companyNews?ticker=" + search_id;
        Log.i("url", "onCreate: " + compNews_url);
        JsonArrayRequest company_News = new JsonArrayRequest(Request.Method.GET, compNews_url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray comp_news_response) {
                int news_length = 0;

                try {
//
//                    if(comp_news_response.length() < 20){
//                        news_length = comp_news_response.length();
//                    }
//                    else{
//                        news_length = 20;
//                    }
                    news_length = 0;
                    for (int i = 0; i < comp_news_response.length(); i++) {
                        JSONObject tempNews = new JSONObject();
                        tempNews = comp_news_response.getJSONObject(i);
                        String Url = tempNews.getString("image");

                        if (Url != "" && !Url.isEmpty()) {
//                            System.out.println("check Bool>> "+ Url.isEmpty());
//
//                            System.out.println("IMagesss"+Url+"hhhh "+Url.getClass().getName());
                            companyNews.put(comp_news_response.getJSONObject(i));
                            news_length++;
                        }
                        if (news_length == 20) {
                            break;
                        }

                    }
                    Log.i(TAG, "onResponse: Inside News");

                    List<String> news = new ArrayList<>();
                    newsAdapter = new NewsAdapter(companyNews);

                    newsRecycleView.setAdapter(newsAdapter);
                    ViewCompat.setNestedScrollingEnabled(newsRecycleView, false);
                    Log.i("news", "News2: " + companyNews.length());

//
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
        requestQueue.add(company_News);


    }

    // Fetch Company Recommendation
//    private void getCompanyRecommendations(){
//
//        String compRecomm_url = baseUrl+"company_recommendation/"+search_id;
//        Log.i("url", "onCreate: "+compRecomm_url);
//        JsonArrayRequest company_Recomm= new JsonArrayRequest(Request.Method.GET, compRecomm_url,null, new Response.Listener<JSONArray>()
//        {
//            @Override
//            public void onResponse(JSONArray comp_recomm_response) {
//                try {
////
//                    for(int i = 0;i<comp_recomm_response.length();i++){
//                        companyRecommendation.add(comp_recomm_response.getJSONObject(i));
//                    }
//                    Log.i("recomm", "Recommendation: "+companyRecommendation);
//
////
////
//
//                }catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        },
//                new Response.ErrorListener(){
//                    @Override
//                    public void onErrorResponse(VolleyError error){
//                        error.printStackTrace();
//                    }
//
//                });
//        requestQueue.add(company_Recomm);
//
//
//    }
    private void setValuesForCompanyRecommendation() {
        WebView recommendation_charts = (WebView) findViewById(R.id.highcharts_recommendation);
        WebSettings get_settings = recommendation_charts.getSettings();
        get_settings.setJavaScriptEnabled(true);
        recommendation_charts.loadUrl("file:///android_asset/recommendationChart.html");
        recommendation_charts.clearCache(true);
        get_settings.setDomStorageEnabled(true);
        get_settings.setAllowFileAccessFromFileURLs(true);
        get_settings.setAllowFileAccess(true);
        recommendation_charts.setWebViewClient(new WebViewClient());
        recommendation_charts.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String recommendation_chart_url = baseUrl + "recommendation";

                view.loadUrl("javascript:getRecommendationChartData('" + search_id + "', '" + recommendation_chart_url + "')");
            }
        });
    }

    // Fetch Company Earning
//    private void getCompanyEarnings(){
//
//        String compEarn_url = baseUrl+"company_earnings/"+search_id;
//        Log.i("url", "onCreate: "+compEarn_url);
//        JsonArrayRequest company_Earn= new JsonArrayRequest(Request.Method.GET, compEarn_url,null, new Response.Listener<JSONArray>()
//        {
//            @Override
//            public void onResponse(JSONArray comp_earn_response) {
//                try {
////
//                    for(int i = 0;i<comp_earn_response.length();i++){
//                        companyEarning.add(comp_earn_response.getJSONObject(i));
//                    }
//                    Log.i("earn", "Earnings: "+companyEarning);
//
////
////
//
//                }catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        },
//                new Response.ErrorListener(){
//                    @Override
//                    public void onErrorResponse(VolleyError error){
//                        error.printStackTrace();
//                    }
//
//                });
//        requestQueue.add(company_Earn);
//
//
//    }
    private void setValuesForEPSCharts() {
        WebView eps_charts = (WebView) findViewById(R.id.highcharts_eps);
        WebSettings get_settings = eps_charts.getSettings();
        get_settings.setJavaScriptEnabled(true);
        eps_charts.loadUrl("file:///android_asset/epsChart.html");
        eps_charts.clearCache(true);
        get_settings.setDomStorageEnabled(true);
        get_settings.setAllowFileAccessFromFileURLs(true);
        get_settings.setAllowFileAccess(true);
        eps_charts.setWebViewClient(new WebViewClient());
        eps_charts.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String eps_chart_url = baseUrl + "companyEarnings";

                view.loadUrl("javascript:getEPSChartsData('" + search_id + "', '" + eps_chart_url + "')");
            }
        });
    }


    // Fetch data for Company Social Sentiments
    private void getCompanySocialSentiments() {

        String socialSenti_url = baseUrl + "socialSentiment?ticker=" + search_id;
        Log.i("url", "onCreate: " + socialSenti_url);
        JsonObjectRequest socialSenti = new JsonObjectRequest(Request.Method.GET, socialSenti_url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject social_sentiments_response) {
                try {
//                    Log.i("social_sentiments_response", "onResponse: "+social_sentiments_response.toString());
                    setValuesForSocialSentiments(social_sentiments_response);
                    List<JSONArray> reddit = new ArrayList<JSONArray>();
                    reddit.add(social_sentiments_response.getJSONArray("reddit"));

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
        requestQueue.add(socialSenti);


    }

    private void setValuesForSocialSentiments(JSONObject social_sentiments_response) {
        TextView insights_heading = (TextView) findViewById(R.id.insights_heading);
        insights_heading.setText("Insights");
        TextView social_sentiments_heading = (TextView) findViewById(R.id.social_sentiments_heading);
        social_sentiments_heading.setText("Social Sentiments");
        try {
//            List<JSONArray> social_senti_info = new ArrayList<JSONArray>();
            JSONArray reddit_info = new JSONArray();
            reddit_info = social_sentiments_response.getJSONArray("reddit");
            JSONArray twitter_info = new JSONArray();
            twitter_info = social_sentiments_response.getJSONArray("twitter");
//            Log.i("reddit_info", "setValuesForSocialSentiments: "+reddit_info);
            int reddit_total = 0, reddit_positive = 0, reddit_negative = 0;
            int twitter_total = 0, twitter_positive = 0, twitter_negative = 0;
            JSONObject reddit_details = new JSONObject();
            JSONObject twitter_details = new JSONObject();
            for (int i = 0; i < reddit_info.length(); i++) {

                reddit_details = reddit_info.getJSONObject(i);

                twitter_details = twitter_info.getJSONObject(i);
//                Log.i("reddit_info", "setValuesForSocialSentiments: "+reddit_info.get(i));
                reddit_total += Integer.parseInt(reddit_details.getString("mention"));
                reddit_positive += Integer.parseInt(reddit_details.getString("positiveMention"));
                reddit_negative += Integer.parseInt(reddit_details.getString("negativeMention"));
                twitter_total += Integer.parseInt(twitter_details.getString("mention"));
                twitter_positive += Integer.parseInt(twitter_details.getString("positiveMention"));
                twitter_negative += Integer.parseInt(twitter_details.getString("negativeMention"));
//                Log.i("reddit_info", "reddit total: "+reddit_total);
            }
//            Log.i("reddit_info", "reddit total: "+reddit_total);
            String reddit_total_s = reddit_total + "";
            String reddit_positive_s = reddit_positive + "";
            String reddit_negative_s = reddit_negative + "";
            String twitter_total_s = twitter_total + "";
            String twitter_positive_s = twitter_positive + "";
            String twitter_negative_s = twitter_negative + "";
            TextView redditTotal = (TextView) findViewById(R.id.reddit_total_mentions);
            redditTotal.setText(reddit_total_s);
            TextView redditPositive = (TextView) findViewById(R.id.reddit_positive_mentions);
            redditPositive.setText(reddit_positive_s);
            TextView redditNegative = (TextView) findViewById(R.id.reddit_negative_mentions);
            redditNegative.setText(reddit_negative_s);
            TextView twitterTotal = (TextView) findViewById(R.id.twitter_total_mentions);
            twitterTotal.setText(twitter_total_s);
            TextView twitterPositive = (TextView) findViewById(R.id.twitter_positive_mentions);
            twitterPositive.setText(twitter_positive_s);
            TextView twitterNegative = (TextView) findViewById(R.id.twitter_negative_mentions);
            twitterNegative.setText(twitter_negative_s);

//                reddit_total += reddit_info[i].getString("mention");


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void portfolioDetails(JSONObject latest_stock_response) {

        TextView portfolioHeading = (TextView) findViewById(R.id.portfolio_heading);
        portfolioHeading.setText("Portfolio");
        TextView shares = (TextView) findViewById(R.id.shares);
        shares.setText("Shares Owned:");
        TextView avgCost = (TextView) findViewById(R.id.avg_cost);
        avgCost.setText("Avg. Cost / Shares:");
        TextView total_cost = (TextView) findViewById(R.id.total_cost);
        total_cost.setText("Total Cost:");
        TextView change_heading = (TextView) findViewById(R.id.change_heading);
        change_heading.setText("Change:");
        TextView market_value = (TextView) findViewById(R.id.market_value_heading);
        market_value.setText("Market Value:");
        Button trade_button = (Button) findViewById(R.id.trade_button_value);
        SharedPreferences sharedPref = getSharedPreferences("LocalStorageValues", MODE_PRIVATE);
        SharedPreferences.Editor sharedEditor = sharedPref.edit();
        String portfolio= sharedPref.getString("Portfolio","");
        Log.i(TAG, "portfolioDetails: "+portfolio);
        loadPortfolioValues(latest_stock_response);
        trade_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog trade_dialog = new Dialog(context);
                trade_dialog.setContentView(R.layout.portfolio_trade);
                trade_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                TextView trade_heading = (TextView) trade_dialog.findViewById(R.id.trade_heading);
                trade_heading.setText("Trade " + comp_name + " shares");
                EditText share_value = (EditText) trade_dialog.findViewById(R.id.share_value);
                share_value.setText("0");
                TextView total_value = (TextView) trade_dialog.findViewById(R.id.total_value);
                String latestPrice;
                SharedPreferences sharedPref = getSharedPreferences("LocalStorageValues", MODE_PRIVATE);
                SharedPreferences.Editor sharedEditor = sharedPref.edit();
                String cash_balance = sharedPref.getString("cash_balance", "");
                double nw = Double.parseDouble(cash_balance);
                TextView money_left = (TextView) trade_dialog.findViewById(R.id.money_left);
                money_left.setText("$" + df.format(nw) + " to buy " + search_id);
                try {
                    latestPrice = latest_stock_response.getString("c");
                    double lp = Double.parseDouble(latestPrice);
                    total_value.setText("0*$" + df.format(lp) + "/shares = 0.00");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                share_value.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        //
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        //
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        try {
                            String latestPrice = latest_stock_response.getString("c");
                            double lp = Double.parseDouble(latestPrice);
//                            int share = share_value.getText().toString();
                            int share = Integer.valueOf(share_value.getText().toString());
                            int noOfShares = share;
                            double tot = noOfShares*lp;
                            total_value.setText(noOfShares+"*$" + df.format(lp) + "/shares = "+df.format(tot));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });
                Button buy_button = (Button) trade_dialog.findViewById(R.id.buy_button);
                buy_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String net_worth = sharedPref.getString("net_worth", "");
                        double nw = Double.parseDouble(net_worth);
                        EditText share_value = (EditText) trade_dialog.findViewById(R.id.share_value);
                        String share = share_value.getText().toString();
                        int noOfShares = Integer.parseInt(share);

                        String latestPrice = null;
                        try {
                            latestPrice = latest_stock_response.getString("c");
                            double lp = Double.parseDouble(latestPrice);
                            if (noOfShares * lp > nw) {
                                Toast.makeText(context, "Not enough money to buy", Toast.LENGTH_LONG).show();
                            }
                            else if(noOfShares <= 0){
                                Toast.makeText(context, "Cannot buy non-positive shares", Toast.LENGTH_LONG).show();
                            }
                            else{
                                Boolean notPresent = true;
                                SharedPreferences sharedPref = getSharedPreferences("LocalStorageValues", MODE_PRIVATE);
                                SharedPreferences.Editor sharedEditor = sharedPref.edit();
                                String portfolio= sharedPref.getString("Portfolio","");
                                Log.i(TAG, "porttt: "+portfolio);
                                if(portfolio.isEmpty()){
                                    JSONArray Portfolio_Array = new JSONArray();
                                    JSONObject port = new JSONObject();
                                    latestPrice = latest_stock_response.getString("c");
                                    lp = Double.parseDouble(latestPrice);
                                    double totalCost = noOfShares * lp;
                                    port.put("tickerSymbol", search_id);
                                    String sh = String.valueOf(noOfShares);
                                    port.put("numOfShares", sh);
                                    port.put("totalCostStocks", df.format(totalCost));
                                    Portfolio_Array.put(port);
                                    sharedEditor.putString("Portfolio",Portfolio_Array.toString());
                                    sharedEditor.commit();
                                    String cash_balance = sharedPref.getString("cash_balance", "");
                                    double cb = Double.parseDouble(cash_balance);
                                    cb = cb - totalCost;
                                    sharedEditor.putString("cash_balance",df.format(cb));
                                    sharedEditor.commit();

                                }
                                else{
                                    JSONParser Parser = new JSONParser();
                                    try {
                                        org.json.simple.JSONArray Portfolio_Array = (org.json.simple.JSONArray)Parser.parse(portfolio);
                                        for(Object port_item : Portfolio_Array){
//                                            List<String> temp_port = new ArrayList<>();
                                            org.json.simple.JSONObject port = (org.json.simple.JSONObject)port_item;
                                            if(search_id.equals(port.get("tickerSymbol"))){
                                                Log.i(TAG, "insidealready: ");
                                                notPresent = false;
                                                String nOs = (String) port.get("numOfShares");
                                                int no_of_Share = Integer.parseInt(nOs) + noOfShares;
                                                String tot = (String) port.get("totalCostStocks");
                                                double totVal = Double.parseDouble(tot);
                                                Portfolio_Array.remove(port);
                                                latestPrice = latest_stock_response.getString("c");
                                                lp = Double.parseDouble(latestPrice);
                                                double totalCost = (noOfShares * lp) + totVal;
                                                port.put("tickerSymbol", search_id);
                                                String sh = String.valueOf(no_of_Share);
                                                port.put("numOfShares", sh);
                                                port.put("totalCostStocks", df.format(totalCost));
                                                Portfolio_Array.add(port);
                                                sharedEditor.putString("Portfolio",Portfolio_Array.toString());
                                                sharedEditor.commit();
                                                double cb = Double.parseDouble(cash_balance);
                                                cb = cb - (noOfShares * lp);
                                                sharedEditor.putString("cash_balance",df.format(cb));
                                                sharedEditor.commit();

                                            }
                                        }
                                        if(notPresent == true){
                                            JSONObject port = new JSONObject();
                                            latestPrice = latest_stock_response.getString("c");
                                            lp = Double.parseDouble(latestPrice);
                                            double totalCost = noOfShares * lp;
                                            port.put("tickerSymbol", search_id);
                                            String sh = String.valueOf(noOfShares);
                                            port.put("numOfShares", sh);
                                            port.put("totalCostStocks", df.format(totalCost));
                                            Portfolio_Array.add(port);
                                            sharedEditor.putString("Portfolio",Portfolio_Array.toString());
                                            sharedEditor.commit();
                                            double cb = Double.parseDouble(cash_balance);
                                            cb = cb - totalCost;
                                            sharedEditor.putString("cash_balance",df.format(cb));
                                            sharedEditor.commit();

                                        }

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                }
                                loadPortfolioValues(latest_stock_response);




                                trade_dialog.dismiss();
                                final Dialog done_dialog = new Dialog(context);
                                done_dialog.setContentView(R.layout.done_layout);
                                done_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                TextView message = (TextView) done_dialog.findViewById(R.id.message);
                                message.setText("You have successfully bought "+noOfShares+" share of "+search_id);
                                Button done = (Button) done_dialog.findViewById(R.id.done);
                                done.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        done_dialog.dismiss();
                                    }
                                });
                                done_dialog.show();


//                                AlertDialog alertDialog = new MaterialAlertDialogBuilder(context, R.style.alertdialog).create(); //Read Update
//                                alertDialog.setTitle("Congratulations");
//                                alertDialog.setMessage("You have successfully bought "+noOfShares+" share of"+search_id);
//                                alertDialog.setButton( -1,"Done", new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//
//
//                                    }
//
//                                });
//                                alertDialog.show();


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                });

                Button sell_button = (Button) trade_dialog.findViewById(R.id.sell_button);
                sell_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String net_worth = sharedPref.getString("net_worth", "");
                        double nw = Double.parseDouble(net_worth);
                        EditText share_value = (EditText) trade_dialog.findViewById(R.id.share_value);
                        String share = share_value.getText().toString();
                        int noOfShares = Integer.parseInt(share);

                        String latestPrice = null;
                        try {
                            latestPrice = latest_stock_response.getString("c");
                            double lp = Double.parseDouble(latestPrice);
                            if(noOfShares <= 0){
                                Toast.makeText(context, "Cannot sell non-positive shares", Toast.LENGTH_LONG).show();
                            }
                            else{
                                Boolean notPresent = true;
                                SharedPreferences sharedPref = getSharedPreferences("LocalStorageValues", MODE_PRIVATE);
                                SharedPreferences.Editor sharedEditor = sharedPref.edit();
                                String portfolio= sharedPref.getString("Portfolio","");
                                Log.i(TAG, "porttt: "+portfolio);
                                if(portfolio.isEmpty()){
                                    Toast.makeText(context, "Not enough shares to sell", Toast.LENGTH_LONG).show();
//

                                }
                                else{
                                    JSONParser Parser = new JSONParser();
                                    try {
                                        org.json.simple.JSONArray Portfolio_Array = (org.json.simple.JSONArray)Parser.parse(portfolio);
                                        for(Object port_item : Portfolio_Array){
//                                            List<String> temp_port = new ArrayList<>();
                                            org.json.simple.JSONObject port = (org.json.simple.JSONObject)port_item;
                                            if(search_id.equals(port.get("tickerSymbol"))){
                                                notPresent = false;
                                                String nOs = (String) port.get("numOfShares");
                                                int no_of_Share = Integer.parseInt(nOs) - noOfShares;
                                                if(no_of_Share < 0){
                                                    Toast.makeText(context, "Not enough shares to sell’", Toast.LENGTH_LONG).show();
                                                }
                                                else{
                                                    String tot = (String) port.get("totalCostStocks");
                                                    double totVal = Double.parseDouble(tot);
                                                    Portfolio_Array.remove(port);
                                                    latestPrice = latest_stock_response.getString("c");
                                                    lp = Double.parseDouble(latestPrice);
                                                    double totalCost = totVal - (noOfShares * lp);
                                                    port.put("tickerSymbol", search_id);
                                                    String sh = String.valueOf(no_of_Share);
                                                    port.put("numOfShares", sh);
                                                    port.put("totalCostStocks", df.format(totalCost));
                                                    Portfolio_Array.add(port);
                                                    sharedEditor.putString("Portfolio",Portfolio_Array.toString());
                                                    sharedEditor.commit();
                                                    double cb = Double.parseDouble(cash_balance);
                                                    cb = cb + (noOfShares * lp);
                                                    sharedEditor.putString("cash_balance",df.format(cb));
                                                    sharedEditor.commit();
                                                    if(no_of_Share == 0){
                                                        Portfolio_Array.remove(port);
                                                        sharedEditor.putString("Portfolio",Portfolio_Array.toString());
                                                        sharedEditor.commit();

                                                        restoreInitialPortfolio();
                                                    }
                                                    else{
                                                        loadPortfolioValues(latest_stock_response);
                                                    }

                                                    trade_dialog.dismiss();
                                                    final Dialog done_dialog = new Dialog(context);
                                                    done_dialog.setContentView(R.layout.done_layout);
                                                    done_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                    TextView message = (TextView) done_dialog.findViewById(R.id.message);
                                                    message.setText("You have successfully sold "+noOfShares+" share of "+search_id);
                                                    Button done = (Button) done_dialog.findViewById(R.id.done);
                                                    done.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            done_dialog.dismiss();
                                                        }
                                                    });
                                                    done_dialog.show();
//                                                    AlertDialog alertDialog = new MaterialAlertDialogBuilder(context, R.style.alertdialog).create(); //Read Update
//                                                    alertDialog.setTitle("Congratulations");
//                                                    alertDialog.setMessage("You have successfully sold "+noOfShares+" share of"+search_id);
//                                                    alertDialog.setButton( -1,"Done", new DialogInterface.OnClickListener() {
//                                                        public void onClick(DialogInterface dialog, int which) {
//
//
//                                                        }
//
//                                                    });
//                                                    alertDialog.show();

                                                }


                                            }
                                        }
                                        if(notPresent == true){
                                            Toast.makeText(context, "Not enough shares to sell’", Toast.LENGTH_LONG).show();
//

                                        }

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                }



                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }




                });
                trade_dialog.show();


            }
        });
    }
    private void restoreInitialPortfolio(){
        TextView shares_owned = (TextView) findViewById(R.id.shares_owned);
        shares_owned.setText("0");
        TextView avg_cost_value = (TextView) findViewById(R.id.avg_cost_value);
        avg_cost_value.setText("$0.00");
        TextView total_cost_value = (TextView) findViewById(R.id.total_cost_value);
        total_cost_value.setText("$0.00");
        TextView change_value = (TextView) findViewById(R.id.change_value);
        avg_cost_value.setText("$0.00");
        change_value.setText("$0.00");
        TextView market_value_Val = (TextView) findViewById(R.id.market_value_Val);
        market_value_Val.setText("$0.00");
    }

    private void loadPortfolioValues(JSONObject latest_stock_response){
        SharedPreferences sharedPref = getSharedPreferences("LocalStorageValues", MODE_PRIVATE);
        SharedPreferences.Editor sharedEditor = sharedPref.edit();
        String portfolio= sharedPref.getString("Portfolio","");
        //Update portfolio
        if(!portfolio.isEmpty()){
            Log.i(TAG, "portfolioDetailsemptyyyy: ");
            JSONParser Parser = new JSONParser();
            org.json.simple.JSONArray Portfolio_Array;
            try {
                double lp = 0.00;
                try {
                    String latestPrice = latest_stock_response.getString("c");
                    lp = Double.parseDouble(latestPrice);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Portfolio_Array = (org.json.simple.JSONArray)Parser.parse(portfolio);
                Log.i(TAG, "Portfolio_Array>>: "+Portfolio_Array);
                for(Object port_item : Portfolio_Array) {
                    org.json.simple.JSONObject port = (org.json.simple.JSONObject) port_item;
//                    Log.i(TAG, "portfolioDetails port1: "+port.get("tickerSymbol").getClass().getName()+" SID> "+search_id.getClass().getName());
//                    String ticker = (String) port.get("tickerSymbol");
                    if(search_id.equals(port.get("tickerSymbol"))){
                        String nOs = (String) port.get("numOfShares");
                        Log.i(TAG, "nos: "+nOs);
                        String tot = (String) port.get("totalCostStocks");
                        Log.i(TAG, "portfolioDetails first: "+nOs+tot);
                        int s = Integer.parseInt(nOs);
                        double TOT = Double.parseDouble(tot);
                        double avg = 0.00;
                        if(s <= 0){
                            avg = 0.00;
                        }
                        else{
                            avg = TOT / s;
                        }

                        double change = lp - avg;
                        Log.i(TAG, "portfolioDetails vals: "+s+' '+TOT+' '+ avg+' '+change);
                        TextView shares_owned = (TextView) findViewById(R.id.shares_owned);
                        shares_owned.setText(nOs);
                        TextView avg_cost_value = (TextView) findViewById(R.id.avg_cost_value);
                        avg_cost_value.setText("$"+df.format(avg));
                        TextView total_cost_value = (TextView) findViewById(R.id.total_cost_value);
                        total_cost_value.setText("$"+tot);
                        TextView change_value = (TextView) findViewById(R.id.change_value);
                        avg_cost_value.setText("$"+df.format(avg));
                        change_value.setText("$"+df.format(change));
                        TextView market_value_Val = (TextView) findViewById(R.id.market_value_Val);
                        market_value_Val.setText("$"+df.format(lp));


                    }

                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

    }
}