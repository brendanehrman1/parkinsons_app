package com.xlsoft.planowestapp3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    public static final String PREF_NAME = "sharedPrefs";
    private static final String TAG = "MainActivity";

    private EditText urlInput;
    private ImageView backBtn;
    private ImageView forwardBtn;
    private TextView tabCountDisplay;
    private ListView tiltMenu;
    private SensorManager sensorManager;
    Sensor accelerometer;

    private int tabCount;
    private int tabNum;
    private double x;
    private double y;
    private double z;
    private boolean isLocked;
    private WebView[] webViews;
    private ArrayList<String> urls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadData();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        sensorManager.registerListener(MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void loadData() {
        urlInput = (EditText) findViewById(R.id.addressBar);
        backBtn = (ImageView) findViewById(R.id.backBtn);
        forwardBtn = (ImageView) findViewById(R.id.forwardBtn);
        tabCountDisplay = (TextView) findViewById(R.id.tabCount);
        tiltMenu = (ListView) findViewById(R.id.tiltMenu);
        webViews = new WebView[] {findViewById(R.id.webView1), findViewById(R.id.webView2), findViewById(R.id.webView3), findViewById(R.id.webView4), findViewById(R.id.webView5), findViewById(R.id.webView6)};
        urls = new ArrayList<String>();
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        tabCount = sharedPreferences.getInt("TABS", 1);
        tabNum = sharedPreferences.getInt("TAB_NUM", 0);
        String[] urlArr = sharedPreferences.getString("URLS", "www.google.com").split("\\|");
        isLocked = sharedPreferences.getBoolean("LOCKED", false);
        for (int i = 0; i < urlArr.length; i++)
            urls.add(urlArr[i]);
        for (int i = 0; i < 6; i++)
            if (i != tabNum)
                webViews[i].setVisibility(View.GONE);
        urlInput.setText(urls.get(tabNum));
        tabCountDisplay.setText(Integer.toString(tabCount));
        search(urlInput);
        if (!webViews[tabNum].canGoBack())
            backBtn.setColorFilter(Color.parseColor("#999999"));
        if (!webViews[tabNum].canGoForward())
            forwardBtn.setColorFilter(Color.parseColor("#999999"));
        ArrayList<TiltMenuEntry> menuItems = new ArrayList<>();
        menuItems.add(new TiltMenuEntry("Lock", 0));
        menuItems.add(new TiltMenuEntry("Unlock", 1));
        menuItems.add(new TiltMenuEntry("Center", 2));
        TiltMenuListAdapter tiltMenuListAdapter = new TiltMenuListAdapter(this, R.layout.tilt_menu_entry_layout, menuItems);
        tiltMenu.setAdapter(tiltMenuListAdapter);
        tiltMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)
                    lock();
                else if (position == 1)
                    unlock();
                else
                    recenter();
            }
        });
        tiltMenu.setVisibility(View.GONE);
        urlInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus)
                    tiltMenu.setVisibility(View.GONE);
            }
        });
        urlInput.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    search(urlInput);
                    return true;
                }
                return false;
            }
        });
        for (int i = 0; i < 6; i++) {
            webViews[i].getSettings().setJavaScriptEnabled(true);
            webViews[i].setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
                @Override
                public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                    super.doUpdateVisitedHistory(view, url, isReload);
                    urls.set(tabNum, webViews[tabNum].getUrl().split("://")[1]);
                    urlInput.setText(webViews[tabNum].getUrl().split("://")[1]);
                    if (!webViews[tabNum].canGoForward())
                        forwardBtn.setColorFilter(Color.parseColor("#999999"));
                    if (webViews[tabNum].canGoBack())
                        backBtn.setColorFilter(Color.parseColor("#686868"));
                    saveData();
                }
            });
        }
    }

    public void search(View v) {
        urls.set(tabNum, urlInput.getText().toString());
        webViews[tabNum].loadUrl("http://" + urlInput.getText().toString());
        if (!webViews[tabNum].canGoForward())
            forwardBtn.setColorFilter(Color.parseColor("#999999"));
        if (webViews[tabNum].canGoBack())
            backBtn.setColorFilter(Color.parseColor("#686868"));
        tiltMenu.setVisibility(View.GONE);
        saveData();
    }

    public void goHome(View v) {
        urlInput.setText("www.google.com");
        tiltMenu.setVisibility(View.GONE);
        search(urlInput);
    }

    public void goBack(View v) {
        if (webViews[tabNum].canGoBack()) {
            webViews[tabNum].goBack();
            urls.set(tabNum, webViews[tabNum].getUrl().split("://")[1]);
            urlInput.setText(webViews[tabNum].getUrl().split("://")[1]);
            if (!webViews[tabNum].canGoBack())
                backBtn.setColorFilter(Color.parseColor("#999999"));
            if (webViews[tabNum].canGoForward())
                forwardBtn.setColorFilter(Color.parseColor("#686868"));
            saveData();
        }
    }

    public void goForward(View v) {
        if (webViews[tabNum].canGoForward()) {
            webViews[tabNum].goForward();
            urls.set(tabNum, webViews[tabNum].getUrl().split("://")[1]);
            urlInput.setText(webViews[tabNum].getUrl().split("://")[1]);
            if (!webViews[tabNum].canGoForward())
                forwardBtn.setColorFilter(Color.parseColor("#999999"));
            if (webViews[tabNum].canGoBack())
                backBtn.setColorFilter(Color.parseColor("#686868"));
            saveData();
        }
    }

    public void addTab(View v) {
        if (tabCount < 6) {
            urls.add("www.google.com");
            tabNum = tabCount;
            tabCount++;
            saveData();
            startActivity(new Intent(MainActivity.this, MainActivity.class));
        }
    }

    public void goToTabs(View v) {
        startActivity(new Intent(MainActivity.this, TabActivity.class));
    }

    public void goToSettings(View v) {
        if (tiltMenu.getVisibility() == View.VISIBLE)
            tiltMenu.setVisibility(View.GONE);
        else
            tiltMenu.setVisibility(View.VISIBLE);
    }

    public void lock() {
        isLocked = true;
        SharedPreferences.Editor edit = getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit();
        edit.putBoolean("LOCKED", true);
        edit.apply();
    }

    public void unlock() {
        isLocked = false;
        SharedPreferences.Editor edit = getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit();
        edit.putBoolean("LOCKED", false);
        edit.apply();
        recenter();
    }

    public void recenter() {
        x = 0;
        y = 0;
        z = 0;
    }

    public void saveData() {
        SharedPreferences.Editor edit = getSharedPreferences(PREF_NAME, MODE_PRIVATE).edit();
        edit.putInt("TABS", tabCount);
        edit.putInt("TAB_NUM", tabNum);
        String urlStr = "";
        for (int i = 0; i < urls.size() - 1; i++)
            urlStr += urls.get(i) + "|";
        urlStr += urls.get(urls.size() - 1);
        edit.putString("URLS", urlStr);
        edit.apply();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (x == 0 && y == 0 && z == 0) {
            x = event.values[0];
            y = event.values[1];
            z = event.values[2];
            for (int i = 0; i < 6; i++) {
                RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams)webViews[i].getLayoutParams();
                relativeParams.setMargins(0, (int)(60 * ((float) getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT)), 0, (int)(50 * ((float) getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT)));
                webViews[i].setLayoutParams(relativeParams);
            }
        }
        else if (!isLocked) {
            int left = -(int)((int)((x - event.values[0]) * 10) * ((float) getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
            int top = (int)((60 + (int)((y - event.values[1]) * 10)) * ((float) getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
            int right = (int)((int)((x - event.values[0]) * 10) * ((float) getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
            int bottom = (int)((50 - (int)((y - event.values[1]) * 10)) * ((float) getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
            for (int i = 0; i < 6; i++) {
                RelativeLayout.LayoutParams relativeParams = (RelativeLayout.LayoutParams)webViews[i].getLayoutParams();
                relativeParams.setMargins(left, top, right, bottom);
                webViews[i].setLayoutParams(relativeParams);
            }
            //System.out.println(left + ", " + top + ", " + right + ", " + bottom);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
