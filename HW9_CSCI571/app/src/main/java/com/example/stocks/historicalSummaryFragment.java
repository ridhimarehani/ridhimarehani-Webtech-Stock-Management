package com.example.stocks;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class historicalSummaryFragment extends Fragment {
    View view;
//    private String baseUrl = "http://10.0.2.2:8080/";
    private String baseUrl = "https://csci571hw8-backend-346006.wl.r.appspot.com/";
    String ticker;
    long unix_from_date;
    long unix_to_date;
    double changepercent;
    //    private String baseUrl = "https://webtech1205backend.wl.r.appspot.com/";
    public historicalSummaryFragment(String search_id, long unix_from_6, long unix_to, double dp) {
        this.ticker = search_id;
        this.unix_from_date = unix_from_6;
        this.unix_to_date = unix_to;
        this.changepercent = dp;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.historicalsummary, container, false);
        getHistoricalSummary();
        return view;
    }

    private void getHistoricalSummary() {
        Log.i("unix", "getHistoricalSummary: " + unix_from_date + " " + unix_to_date);
        WebView historical_summ_charts = (WebView) view.findViewById(R.id.highcharts_historical_summary);
        WebSettings get_settings = historical_summ_charts.getSettings();
        get_settings.setJavaScriptEnabled(true);
        historical_summ_charts.loadUrl("file:///android_asset/historicalSummary.html");
        historical_summ_charts.clearCache(true);
        get_settings.setDomStorageEnabled(true);
        get_settings.setAllowFileAccessFromFileURLs(true);
        get_settings.setAllowFileAccess(true);
        historical_summ_charts.setWebViewClient(new WebViewClient());
        historical_summ_charts.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String hist_summ_chart_url = baseUrl + "historicalDataSummary";

                view.loadUrl("javascript:getHistoricalSummaryChartsData('" + ticker + "', '" + hist_summ_chart_url + "', '" + unix_from_date + "', '" + unix_to_date + "', '" + changepercent + "')");
            }
        });
    }

}
