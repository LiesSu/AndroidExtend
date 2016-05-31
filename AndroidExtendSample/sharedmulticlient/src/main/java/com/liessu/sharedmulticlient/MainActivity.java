package com.liessu.sharedmulticlient;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.liessu.extendlib.sharedmulti.SharedPreferencesResolver;

public class MainActivity extends AppCompatActivity {
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        final EditText editText = (android.widget.EditText) findViewById(R.id.value_edit);
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
}
