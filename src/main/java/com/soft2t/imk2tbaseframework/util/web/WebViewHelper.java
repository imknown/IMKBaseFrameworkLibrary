package com.soft2t.imk2tbaseframework.util.web;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewHelper {

    public static void configWebView(WebView wb, Context context) {
        configWebView(wb, context, true);
    }

    @SuppressWarnings("deprecation")
    @SuppressLint({"SetJavaScriptEnabled"})
    public static void configWebView(final WebView wb, Context context, boolean useMyOwnClient) {
        WebSettings ws = wb.getSettings();
        ws.setJavaScriptEnabled(true);

        ws.setDomStorageEnabled(true);

        ws.setSavePassword(false);

        ws.setSupportZoom(false);

        ws.setBuiltInZoomControls(false);

        ws.setUseWideViewPort(true);

        ws.setDefaultTextEncodingName("utf-8");

        ws.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);

        ws.setLoadWithOverviewMode(true);

        // region Cache
        ws.setDatabaseEnabled(true);
        ws.setAppCacheEnabled(true);
        ws.setAllowFileAccess(true);
        ws.setDatabasePath(context.getCacheDir().toString());
        ws.setAppCachePath(context.getCacheDir().toString());
        ws.setAppCacheMaxSize(1024 * 1024 * 8);
        if (NetworkUtil.hasConnectionsForNetwork(context)) {
            ws.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            ws.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        // endregion

        if (useMyOwnClient) {
            wb.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            });

            wb.setWebChromeClient(new WebChromeClient());

            wb.setOnKeyListener(new OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == 1 && keyCode == 4 && wb.canGoBack()) {
                        wb.goBack();
                        return true;
                    } else {
                        return false;
                    }
                }
            });
        }

        wb.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });
    }

    public static String getRealHtml(String title, String html) {
        String realHtml = "<html><head><title>";
        realHtml += title;
        realHtml += "</title>";
        realHtml += "<meta name=\"viewport\" content=\"width=620, initial-scale=2.0, user-scalable=0, minimum-scale=2.0, maximum-scale=2.0\">";
        realHtml += "<style>strong, b {font-size:20px;text-indent: 0em;}p {margin:0;padding:0;}h1 {margin:0;padding:0;text-indent: 0em;}</style>";
        realHtml += "</head><body><div style='color:#01B2F1;font-size:24px;line-height:24px;text-align:center;margin:15px auto;'>";
        realHtml += title;
        realHtml += "</div><div style='color:#000;font-size:18px;line-height:32px;text-align:left;margin:5px 10px;'>";
        realHtml += html;
        realHtml += "</div></body></html>";

        return realHtml;
    }
}
