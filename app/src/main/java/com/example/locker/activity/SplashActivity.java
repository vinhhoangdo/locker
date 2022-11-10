package com.example.locker.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.locker.R;
import com.example.locker.databinding.ActivitySplashBinding;
import com.example.locker.util.Constant;
import com.example.locker.util.SharedPreferencesHelper;


@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {
    SharedPreferencesHelper sharedPreferencesHelper;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySplashBinding binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this);
        binding.tvSplash.setText(R.string.app_name);
        binding.imLockSpl.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate));
        binding.imLargeCircle.startAnimation(AnimationUtils.loadAnimation(this, R.anim.blink));
        new Handler().postDelayed(() -> {
            sharedPreferencesHelper.setSettingType(Constant.SPLASH_NAV);
            Intent intent = new Intent(SplashActivity.this, LockerSettingActivity.class);
            startActivity(intent);
            finish();
        }, 3000);
    }
}
