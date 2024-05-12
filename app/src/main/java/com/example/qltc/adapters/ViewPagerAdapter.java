package com.example.qltc.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.qltc.views.fragments.StatsFragment;
import com.example.qltc.views.fragments.TransactionsFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {


    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: return new TransactionsFragment();
            case 1: return new StatsFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
