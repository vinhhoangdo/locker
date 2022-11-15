package com.example.locker;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.locker.databinding.ActivityMainBinding;
import com.example.locker.service.AppLaunchDetectionService;
import com.example.locker.util.Constant;
import com.example.locker.util.Utils;

public class MainActivity extends AppCompatActivity{
    ActivityMainBinding binding;
    NavController navController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Utils.isRunningBackgroundService(this)) {
            Intent intent = new Intent(MainActivity.this, AppLaunchDetectionService.class);
            intent.setAction(Constant.ACTION.START_FOREGROUND);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }
        }
    }
}