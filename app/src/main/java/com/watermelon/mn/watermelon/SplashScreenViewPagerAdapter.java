package com.watermelon.mn.watermelon;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by nithinkumar on 16/09/16.
 */
public class SplashScreenViewPagerAdapter extends FragmentStatePagerAdapter {

    public SplashScreenViewPagerAdapter(FragmentManager fm) {
        super(fm);

    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            FragmentSplashScreen1 fragmentSplashScreen1 = new FragmentSplashScreen1();
            return fragmentSplashScreen1;
        } else if (position == 1) {
            FragmentSplashScreen2 fragmentSplashScreen2 = new FragmentSplashScreen2();
            return fragmentSplashScreen2;
        } else {
            FragmentSplashScreen3 fragmentSplashScreen3 = new FragmentSplashScreen3();
            return fragmentSplashScreen3;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
