package com.example.stocks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class historicalChartsFragment extends Fragment {
    View view;
//    private String baseUrl = "http://10.0.2.2:8080/";
    private String baseUrl = "https://csci571hw8-backend-346006.wl.r.appspot.com/";
    String ticker;
    //    private String baseUrl = "https://webtech1205backend.wl.r.appspot.com/";
    public historicalChartsFragment(String search_id) {
        this.ticker = search_id;
        //Set value for historical charts



    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.historicalchart, container, false);
        setValuesForHistoricalCharts();
        return view;
    }

    private void setValuesForHistoricalCharts(){
        WebView historical_charts = (WebView) view.findViewById(R.id.highcharts_sma_vbp);
        WebSettings get_settings = historical_charts.getSettings();
        get_settings.setJavaScriptEnabled(true);
        historical_charts.loadUrl("file:///android_asset/historyCharts.html");
        historical_charts.clearCache(true);
        get_settings.setDomStorageEnabled(true);
//            get_settings.setAllowFileAccessFromFileURLs(true);
        get_settings.setAllowFileAccess(true);
        historical_charts.setWebViewClient(new WebViewClient());
        historical_charts.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                String hist_chart_url = baseUrl+"historicalData";

                view.loadUrl("javascript:getHistoricalChartsData('" + ticker + "', '" + hist_chart_url + "')");
            }
        });

    }
}
