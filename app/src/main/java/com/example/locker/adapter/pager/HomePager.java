package com.example.locker.adapter.pager;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.locker.fragment.AllApplicationFragment;
import com.example.locker.fragment.LockedApplicationFragment;

public class HomePager extends FragmentStateAdapter {

    public HomePager(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 1) {
            return new LockedApplicationFragment();
        }
        return new AllApplicationFragment();
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
