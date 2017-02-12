package com.watermelon.mn.watermelon;

/**
 * Created by Martin on 9/20/2016.
 */
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import me.relex.circleindicator.CircleIndicator;

public class SplashScreenActivity extends AppCompatActivity {

    private ViewPager splashScreenViewPager;
    private SplashScreenViewPagerAdapter splashScreenViewPagerAdapter;
    private LinearLayout getStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        splashScreenViewPager = (ViewPager) findViewById(R.id.splashScreenViewPager);
        splashScreenViewPager.addOnPageChangeListener(new CircularViewPagerHandler(splashScreenViewPager));

        splashScreenViewPagerAdapter = new SplashScreenViewPagerAdapter(getSupportFragmentManager());
        splashScreenViewPager.setAdapter(splashScreenViewPagerAdapter);

        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.splashScreenViewPagerIndicator);
        indicator.setViewPager(splashScreenViewPager);

        getStarted= (LinearLayout) findViewById(R.id.splashScreenButtonGetStarted);
        getStarted.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

    }
}