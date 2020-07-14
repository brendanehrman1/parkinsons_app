package com.xlsoft.planowestapp3;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import java.util.ArrayList;
import static com.xlsoft.planowestapp3.MainActivity.PREF_NAME;

public class TabActivity extends AppCompatActivity {

    private WebView[] tabs;
    private View[] deleteBtns;
    private ImageView addBtn;

    private int tabCount;
    private ArrayList<String> urls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab);
        loadData();
    }
    
    public void loadData() {
        tabs = new WebView[] {findViewById(R.id.tab1), findViewById(R.id.tab2), findViewById(R.id.tab3), findViewById(R.id.tab4), findViewById(R.id.tab5), findViewById(R.id.tab6)};
        deleteBtns = new View[] {findViewById(R.id.delete1), findViewById(R.id.delete2), findViewById(R.id.delete3), findViewById(R.id.delete4), findViewById(R.id.delete5), findViewById(R.id.delete6)};
        addBtn = (ImageView) findViewById(R.id.addBtn);
        urls = new ArrayList<String>();
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        tabCount = sharedPreferences.getInt("TABS", 1);
        String[] urlArr = sharedPreferences.getString("URLS", "www.google.com").split("\\|");
        for (int i = 0; i < urlArr.length; i++)
            urls.add(urlArr[i]);
        if (tabCount == 6)
            addBtn.setColorFilter(Color.parseColor("#999999"));
        else
            addBtn.setColorFilter(Color.parseColor("#686868"));
        for (int i = 0; i < 6; i++) {
            tabs[i].getSettings().setUseWideViewPort(true);
            tabs[i].setInitialScale(1);
            tabs[i].getSettings().setJavaScriptEnabled(true);
            tabs[i].setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            });
            if (i < urls.size()) {
                tabs[i].setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        goToTab(v);
                        return true;
                    }
                });
                tabs[i].loadUrl("http://" + urls.get(i));
                tabs[i].setVisibility(View.VISIBLE);
                deleteBtns[i].setVisibility(View.VISIBLE);
            } else {
                tabs[i].setVisibility(View.GONE);
                deleteBtns[i].setVisibility(View.GONE);
            }
        }
    }

    public void deleteTab(View v) {
        for (int i = 0; i < 6; i++)
            if (deleteBtns[i].getId() == v.getId())
                urls.remove(i);
        tabCount--;
        boolean isEmpty = false;
        if (tabCount == 0) {
            isEmpty = true;
            tabCount = 1;
            urls.add("www.google.com");
        }
        saveData();
        loadData();
        if (isEmpty) {
            SharedPreferences.Editor edit = getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit();
            edit.putInt("TAB_NUM", 0);
            edit.apply();
            startActivity(new Intent(TabActivity.this, MainActivity.class));
        }
    }

    public void addTab(View v) {
        if (tabCount < 6) {
            urls.add("www.google.com");
            tabCount++;
            saveData();
            loadData();
        }
    }

    public void saveData() {
        SharedPreferences.Editor edit = getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit();
        edit.putInt("TABS", tabCount);
        String urlStr = "";
        for (int i = 0; i < urls.size() - 1; i++)
            urlStr += urls.get(i) + "|";
        urlStr += urls.get(urls.size() - 1);
        edit.putString("URLS", urlStr);
        edit.apply();
    }

    public void goToTab(View v) {
        int tabNum = 0;
        for (int i = 0; i < tabs.length; i++) {
            if (tabs[i].getId() == v.getId())
                tabNum = i;
        }
        SharedPreferences.Editor edit = getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit();
        edit.putInt("TAB_NUM", tabNum);
        edit.apply();
        startActivity(new Intent(TabActivity.this, MainActivity.class));
    }
}
