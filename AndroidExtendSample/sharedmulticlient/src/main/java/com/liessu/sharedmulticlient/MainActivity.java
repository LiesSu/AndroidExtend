package com.liessu.sharedmulticlient;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.liessu.extendlib.display.DisplayMeticsEx;
import com.liessu.extendlib.sharedmulti.SharedPreferencesResolver;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    private Context context;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        editText = (android.widget.EditText) findViewById(R.id.value_edit);
        Button btnGet = (Button) findViewById(R.id.get_value);
        Button btnSet =  (Button) findViewById(R.id.set_value);
        if (btnGet != null) {
            btnGet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferencesResolver sharedPreferences = new SharedPreferencesResolver(context);
                    String loglevel = sharedPreferences.getString("logLevel","3");
                    Snackbar.make(view, "The Loglevel is "+loglevel , Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }

        if (btnSet != null) {
            btnSet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String value = null;
                    if (editText != null) {
                        value = editText.getText().toString();
                    }

                    SharedPreferencesResolver sharedPreferences = new SharedPreferencesResolver(context);
                    sharedPreferences.edit().putString("logLevel",value).apply();
                }
            });
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Message msg = new Handler(Looper.getMainLooper()){
            @Override
            public void dispatchMessage(Message msg) {
                super.dispatchMessage(msg);
//                SharedPreferencesResolver sharedPreferences = new SharedPreferencesResolver(context);
//                editText.setText(sharedPreferences.getString("logLevel","00000"));
            }
        }.obtainMessage();
        msg.sendToTarget();
    }
}
