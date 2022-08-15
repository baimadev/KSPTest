package com.holderzone.store.ksptest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.holderzone.store.annotation.Route;

@Route(route = "Second")
public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }
}