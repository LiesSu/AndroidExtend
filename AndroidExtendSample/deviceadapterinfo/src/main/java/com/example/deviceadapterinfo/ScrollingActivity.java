package com.example.deviceadapterinfo;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.liessu.extendlib.display.DisplayMeticsEx;

public class ScrollingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DisplayMeticsEx displayMeticsEx = new DisplayMeticsEx(this);
        final TextView displayTextView = (TextView) findViewById(R.id.display_info);
        final TextView deviceTextView = (TextView) findViewById(R.id.device_info);
        if (displayTextView != null) {
            displayTextView.setText(displayMeticsEx.toString());
        }
        if (deviceTextView != null) {
            deviceTextView.setText(getDeviceInfo());
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "使用隐藏属性获得屏幕信息", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    if (displayTextView != null) {
                        displayTextView.setText(displayMeticsEx.getNonCompatString());
                    }
                }
            });
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public String getDeviceInfo(){
        StringBuilder str = new StringBuilder();
        str.append("名称："+Build.MODEL);
        str.append("\n版本：Android "+ Build.VERSION.RELEASE);
        str.append("\n版本号：API"+Build.VERSION.SDK_INT);
        return str.toString();
    }
}
