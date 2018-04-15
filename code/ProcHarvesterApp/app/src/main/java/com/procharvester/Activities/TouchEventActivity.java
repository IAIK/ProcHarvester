package com.procharvester.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.procharvester.R;

public class TouchEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_touch_event);
        getSupportActionBar().setTitle(TouchEventActivity.class.getSimpleName());
    }
}
