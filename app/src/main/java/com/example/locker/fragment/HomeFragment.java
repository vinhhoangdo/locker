package com.example.locker.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.example.locker.R;
import com.example.locker.adapter.pager.HomePager;
import com.example.locker.databinding.FragmentHomeBinding;
import com.example.locker.util.Constant;
import com.example.locker.util.SharedPreferencesHelper;
import com.example.locker.util.Utils;

public class HomeFragment extends Fragment implements View.OnClickListener{
    FragmentHomeBinding binding;
    HomePager homePager;
    SharedPreferencesHelper sharedPreferencesHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(requireContext());
        setupOwnViewPager();

    }

    private void setupOwnViewPager() {
        binding.inAppBar.setting.setOnClickListener(this);
        binding.inAppBar.txTitle.setText(R.string.app_name);
        binding.inAppBar.appBarBack.setVisibility(View.GONE);
        binding.inAppBar.setting.setVisibility(View.VISIBLE);
        binding.inTabLayout.tvAllApps.setText(getResources().getText(R.string.allApps));
        binding.inTabLayout.tvLockedApps.setText(getResources().getText(R.string.lockedApps));
        homePager = new HomePager(getChildFragmentManager(), getLifecycle());
        binding.pager.setAdapter(homePager);
        binding.inTabLayout.tvAllApps.setOnClickListener(
                view -> binding.pager.setCurrentItem(0, true));
        binding.inTabLayout.tvLockedApps.setOnClickListener(
                view -> binding.pager.setCurrentItem(1, true));

        binding.pager.registerOnPageChangeCallback(
                new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        if (position == 1) {
                            new Utils().selectTabLayout(requireContext(), binding.inTabLayout.tvAllApps, binding.inTabLayout.tvLockedApps,
                                    binding.inTabLayout.vLeft, binding.inTabLayout.vRight, binding.inTabLayout.tvLockedApps);
                        } else {
                            new Utils().selectTabLayout(requireContext(), binding.inTabLayout.tvAllApps, binding.inTabLayout.tvLockedApps,
                                    binding.inTabLayout.vLeft, binding.inTabLayout.vRight, binding.inTabLayout.tvAllApps);
                        }
                    }
                }
        );
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.setting){
            sharedPreferencesHelper.setSettingType(Constant.SETTING_NAV);
            Navigation.findNavController(view).navigate(R.id.action_homeFragment_to_lockerSettingFragment);
        }
    }
}
