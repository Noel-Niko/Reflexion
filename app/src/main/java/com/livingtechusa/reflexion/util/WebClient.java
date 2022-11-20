package com.livingtechusa.reflexion.util;


import static java.sql.DriverManager.println;

import android.webkit.WebView;
import android.webkit.WebViewClient;

class WebClient extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }
    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        //Save the last visited URL to shared preference
        println(url);
    }
}