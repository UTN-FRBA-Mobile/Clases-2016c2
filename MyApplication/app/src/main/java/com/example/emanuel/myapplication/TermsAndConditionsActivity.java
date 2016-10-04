package com.example.emanuel.myapplication;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * Created by emanuel on 3/10/16.
 */
public class TermsAndConditionsActivity extends AppCompatActivity {

    private static final String APP_SCHEME = "app://";
    WebView webView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termsandconditions);
        webView = (WebView) findViewById(R.id.webView);
        loadPage();
    }

    private void loadPage() {
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.getSettings().setJavaScriptEnabled(true);
        String url = "file:///android_asset/termsAndConditions.html";
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.loadUrl("javascript:setText(\"¡Hola JavaScript!\")");
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(APP_SCHEME)) {
                    return performCommand(url.substring(APP_SCHEME.length()));
                }
                return false;
            }
        });
        webView.loadUrl(url);
    }

    private boolean performCommand(String command) {
        switch (command) {
            case "accept":
                acceptTermsAndConditions();
                return true;
            case "doNotAccept":
                doNotAcceptTermsAndConditions();
                return true;
        }
        return false;
    }

    private void acceptTermsAndConditions() {
        Toast.makeText(this, R.string.accept, Toast.LENGTH_LONG).show();
        finish();
    }

    private void doNotAcceptTermsAndConditions() {
        Toast.makeText(this, R.string.dontAccept, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(R.string.acceptMenu).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                acceptTermsAndConditions();
                return true;
            }
        });
        menu.add(R.string.dontAcceptMenu).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                doNotAcceptTermsAndConditions();
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
