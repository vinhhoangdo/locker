package com.example.locker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.locker.MainActivity;
import com.example.locker.databinding.ActivityPatternBinding;
import com.example.locker.util.Constant;
import com.example.locker.util.DrawState;
import com.example.locker.util.SharedPreferencesHelper;
import com.example.patternlockview.PatternLockView;
import com.example.patternlockview.listener.PatternLockViewListener;
import com.example.patternlockview.utils.PatternLockUtils;
import com.example.patternlockview.utils.ResourceUtils;
import com.example.patternlockview.R;

import java.util.List;

public class PatternActivity extends AppCompatActivity implements View.OnClickListener {
    SharedPreferencesHelper sharedPreferencesHelper;
    ActivityPatternBinding binding;
    private PatternLockView mPatternLockView;
    private String mPattern;
    private @DrawState
    int mDrawState;
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

            if (pattern.size() < 4) {
                mPatternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG);
                clearWrongPattern();
                return;
            }
            if (mDrawState == DrawState.DRAW_FIRST) {
                mPattern = strPattern;
                setDrawState(DrawState.DRAW_FIRST_DONE);
            }
            if (mDrawState == DrawState.DRAW_LAST) {
                if (strPattern.equals(mPattern)) {
                    mPatternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);
                    if (!sharedPreferencesHelper.getSettingType().equals(Constant.SPLASH_NAV)) {
                        setDrawState(DrawState.DRAW_LAST_DONE);
                    } else {
                        sharedPreferencesHelper.setPattern(mPattern);
                        sharedPreferencesHelper.setLockType(Constant.PATTERN_TYPE);
                        navigateAction();
                    }

                } else {
                    mPatternLockView.setViewMode(PatternLockView.PatternViewMode.WRONG);
                    clearWrongPattern();
                }
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

    private void setDrawState(@DrawState int drawState) {
        mDrawState = drawState;
        switch (drawState) {
            case DrawState.DRAW_FIRST:
                binding.description.setText(com.example.locker.R.string.draw_an_unlock_pattern);
                binding.confirm.setVisibility(View.GONE);
                binding.reset.setVisibility(View.GONE);
                mPatternLockView.setInputEnabled(true);
                break;
            case DrawState.DRAW_FIRST_DONE:
                binding.confirm.setText(com.example.locker.R.string.btn_continue);
                binding.confirm.setVisibility(View.VISIBLE);
                binding.reset.setVisibility(View.VISIBLE);
                mPatternLockView.setInputEnabled(false);
                break;
            case DrawState.DRAW_LAST:
                binding.description.setText(com.example.locker.R.string.re_draw_an_unlock_pattern);
                binding.confirm.setText(com.example.locker.R.string.btn_confirm);
                binding.confirm.setVisibility(View.GONE);
                binding.reset.setVisibility(View.VISIBLE);
                mPatternLockView.setInputEnabled(true);
                break;
            case DrawState.DRAW_LAST_DONE:
                binding.confirm.setText(com.example.locker.R.string.btn_confirm);
                binding.confirm.setVisibility(View.VISIBLE);
                binding.reset.setVisibility(View.VISIBLE);
                mPatternLockView.setInputEnabled(false);
                break;
        }
    }

    private void setDrawStateForSetting(@DrawState int drawState) {
        mDrawState = drawState;
        switch (drawState) {
            case DrawState.DRAW_LAST:
                binding.description.setText(com.example.locker.R.string.draw_an_unlock_pattern);
                binding.confirm.setVisibility(View.GONE);
                binding.reset.setVisibility(View.GONE);
                mPatternLockView.setInputEnabled(true);
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPatternBinding.inflate(getLayoutInflater());
        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());
        mPatternLockView = binding.patternLockView;
        mPatternLockView.setDotCount(3);
        mPatternLockView.setDotNormalSize((int) ResourceUtils.getDimensionInPx(this, R.dimen.pattern_lock_dot_size));
        mPatternLockView.setDotSelectedSize((int) ResourceUtils.getDimensionInPx(this, R.dimen.pattern_lock_dot_selected_size));
        mPatternLockView.setPathWidth((int) ResourceUtils.getDimensionInPx(this, R.dimen.pattern_lock_path_width));
        mPatternLockView.setAspectRatioEnabled(true);
        mPatternLockView.setAspectRatio(PatternLockView.AspectRatio.ASPECT_RATIO_HEIGHT_BIAS);
        mPatternLockView.setViewMode(PatternLockView.PatternViewMode.CORRECT);
        mPatternLockView.setDotAnimationDuration(150);
        mPatternLockView.setPathEndAnimationDuration(100);
        mPatternLockView.setCorrectStateColor(ResourceUtils.getColor(this, R.color.white));
        mPatternLockView.setInStealthMode(false);
        mPatternLockView.setTactileFeedbackEnabled(true);
        mPatternLockView.setInputEnabled(true);
        mPatternLockView.addPatternLockListener(mPatternLockViewListener);


        binding.reset.setOnClickListener(this);

        binding.confirm.setOnClickListener(this);
        if (sharedPreferencesHelper.getPattern() != null && sharedPreferencesHelper.getSettingType().equals(Constant.SPLASH_NAV)) {
            binding.back.setVisibility(View.GONE);
            mPattern = sharedPreferencesHelper.getPattern();
            setDrawStateForSetting(DrawState.DRAW_LAST);
        } else {
            setDrawState(DrawState.DRAW_FIRST);
        }
        binding.back.setOnClickListener(v -> {
            startActivity(new Intent(this, LockerSettingActivity.class));
            finish();
        });
    }

    @Override
    public void onClick(@NonNull View view) {
        int id = view.getId();
        if (id == com.example.locker.R.id.reset) {
            binding.description.setText(com.example.locker.R.string.draw_an_unlock_pattern);
            switch (mDrawState) {
                case DrawState.DRAW_FIRST_DONE:
                case DrawState.DRAW_LAST:
                case DrawState.DRAW_LAST_DONE:
                    mPatternLockView.setInputEnabled(true);
                    mPatternLockView.clearPattern();
                    setDrawState(DrawState.DRAW_FIRST);
                    break;
            }
        }
        if (id == com.example.locker.R.id.confirm) {
            switch (mDrawState) {
                case DrawState.DRAW_FIRST_DONE:
                    mPatternLockView.setInputEnabled(true);
                    mPatternLockView.clearPattern();
                    setDrawState(DrawState.DRAW_LAST);
                    break;
                case DrawState.DRAW_LAST_DONE:
                    mPatternLockView.setInputEnabled(true);
                    sharedPreferencesHelper.setPattern(mPattern);
                    sharedPreferencesHelper.setLockType(Constant.PATTERN_TYPE);
                    navigateAction();
            }
        }
    }

    private void navigateAction() {
        if (sharedPreferencesHelper.getSettingType().equals(Constant.SETTING_NAV)) {
            onBackPressed();
        } else {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }
        finish();
    }
}
