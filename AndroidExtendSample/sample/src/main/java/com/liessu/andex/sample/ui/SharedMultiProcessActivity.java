package com.liessu.andex.sample.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.liessu.andex.sample.AndexApplication;
import com.liessu.andex.sample.R;
import com.liessu.andex.sample.adapter.SharedMultiAdapter;
import com.liessu.andex.sharedmulti.SharedPreferencesResolver;

import java.util.Map;

public class SharedMultiProcessActivity extends AppCompatActivity {
    private static final String TAG = "SharedProActivity";

    private Context context;
    private RecyclerView sharedMultiRecyclerView;
    private SharedMultiAdapter sharedMultiAdapter;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shared_multi_process);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        sharedMultiRecyclerView = (RecyclerView) findViewById(R.id.recycler_value);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        sharedMultiRecyclerView.setLayoutManager(layoutManager);

        context = this;
        Log.d(TAG, "onCreate. Before getAll:"+ System.currentTimeMillis());
        sharedPreferences = new SharedPreferencesResolver(AndexApplication.getContext());
        Map<String , ?>  mapValues = sharedPreferences.getAll();
        Log.d(TAG, "onCreate. After getAll:"+ System.currentTimeMillis());
        sharedMultiAdapter = new SharedMultiAdapter(context , mapValues);
        sharedMultiRecyclerView.setAdapter(sharedMultiAdapter);

        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }
    }



}
