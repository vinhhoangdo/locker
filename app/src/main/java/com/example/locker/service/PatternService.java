package com.example.locker.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.locker.R;
import com.example.locker.databinding.LayoutPatternServiceBinding;
import com.example.locker.receiver.LockStateReceiver;
import com.example.locker.util.SharedPreferencesHelper;
import com.example.patternlockview.PatternLockView;
import com.example.patternlockview.listener.PatternLockViewListener;
import com.example.patternlockview.utils.PatternLockUtils;
import com.example.patternlockview.utils.ResourceUtils;

import java.util.List;

public class PatternService extends Service implements View.OnClickListener{
    LayoutPatternServiceBinding binding;
    SharedPreferencesHelper sharedPreferencesHelper;
    private WindowManager mWindowManager;
    WindowManager.LayoutParams params;
    private PatternLockView mPatternLockView;
    private final PatternLockViewListener mPatternLockViewListener = new PatternLockViewListener() {
        @Override
        public void onStarted() {
            Log.d(getClass().getName(), "Pattern drawing started");
        }

        @Override
        public void onProgress(List<PatternLockView.Dot> progressPattern) {
            Log.d(getClass().getName(), "Pattern progress: " +
                    PatternLockUtils.patternToString(mPatternLockView, progressPattern));
        }

        @Override
        public void onComplete(List<PatternLockView.Dot> pattern) {
            String strPattern = PatternLockUtils.patternToString(mPatternLockView, pattern);
            Log.d(getClass().getName(), "Pattern complete: " + strPattern);
            if (sharedPreferencesHelper.getPattern().equalsIgnoreCase(strPattern)) {
                mPatternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);
                stopSelf();
            } else {
                mPatternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG);
                clearWrongPattern();
            }
        }

        @Override
        public void onCleared() {
            Log.d(getClass().getName(), "Pattern has been cleared");
        }
    };
    private void clearWrongPattern() {
        new Handler().postDelayed(() -> mPatternLockView.clearPattern(), 500);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerReceiver(new LockStateReceiver(), new IntentFilter("android.intent.action.PHONE_STATE"));
        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this);
        binding = LayoutPatternServiceBinding.inflate(LayoutInflater.from(this));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {

            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.TYPE_PHONE, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    PixelFormat.TRANSLUCENT);
        } else {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT);
        }

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(binding.getRoot(), params);
        mPatternLockView = binding.patternLockView;
        mPatternLockView.setDotCount(3);
        mPatternLockView.setDotNormalSize((int) ResourceUtils.getDimensionInPx(this, com.example.patternlockview.R.dimen.pattern_lock_dot_size));
        mPatternLockView.setDotSelectedSize((int) ResourceUtils.getDimensionInPx(this, com.example.patternlockview.R.dimen.pattern_lock_dot_selected_size));
        mPatternLockView.setPathWidth((int) ResourceUtils.getDimensionInPx(this, com.example.patternlockview.R.dimen.pattern_lock_path_width));
        mPatternLockView.setAspectRatioEnabled(true);
        mPatternLockView.setAspectRatio(PatternLockView.AspectRatio.ASPECT_RATIO_HEIGHT_BIAS);
        mPatternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);
        mPatternLockView.setDotAnimationDuration(150);
        mPatternLockView.setPathEndAnimationDuration(100);
        mPatternLockView.setCorrectStateColor(ResourceUtils.getColor(this, com.example.patternlockview.R.color.white));
        mPatternLockView.setInStealthMode(false);
        mPatternLockView.setTactileFeedbackEnabled(true);
        mPatternLockView.setInputEnabled(true);
        mPatternLockView.addPatternLockListener(mPatternLockViewListener);
        binding.back.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWindowManager.removeView(binding.getRoot());
    }

    @Override
    public void onClick(@NonNull View view) {
        if (view.getId() == R.id.back) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
            stopSelf();
        }
    }
}
