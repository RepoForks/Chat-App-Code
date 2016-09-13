package com.tubiapp.demochatxmpp.abstracts;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;

public abstract class BaseActivity extends Activity {
    public static ActionBar actionBar;
    protected ACActivityCallback activityCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        actionBar = this.getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#004D40")));
        actionBar.setTitle(Html.fromHtml("<font color='#ffffff'>Chat App Demo </font>"));
        // actionBar.setSubtitle(Html.fromHtml("<font color='#ffffff'>Chat App</font>"));

        initDatas();
        initRootViews();
        initUIComponents();
        initListeners();
        loadData();
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.action_settings:
                Toast.makeText(getBaseContext(), "You selected Setting", Toast.LENGTH_SHORT).show();
                break;

        }
        return true;

    }*/

    protected abstract void initDatas();

    protected abstract void initRootViews();

    protected abstract void initUIComponents();

    protected abstract void initListeners();

    protected abstract void loadData();

    public void startActivityForResult(Intent intent, int requestCode,
                                       ACActivityCallback activityCallback) {
        startActivityForResult(intent, requestCode);
        this.activityCallback = activityCallback;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (activityCallback != null) {
            activityCallback.onReceiveData(requestCode, resultCode, data);
        }
    }

    public interface ACActivityCallback {
        void onReceiveData(int requestCode, int resultCode, Intent data);
    }
}