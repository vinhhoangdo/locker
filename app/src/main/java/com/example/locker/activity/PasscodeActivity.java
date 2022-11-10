package com.example.locker.activity;

import android.annotation.SuppressLint;
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
import androidx.core.content.ContextCompat;

import com.example.locker.MainActivity;
import com.example.locker.R;
import com.example.locker.databinding.ActivityPasscodeBinding;
import com.example.locker.util.Constant;
import com.example.locker.util.DrawState;
import com.example.locker.util.SharedPreferencesHelper;
import com.example.passcodelockview.IndicatorDots;
import com.example.passcodelockview.PasscodeLockListener;
import com.example.passcodelockview.PasscodeLockView;

public class PasscodeActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityPasscodeBinding binding;
    SharedPreferencesHelper sharedPreferencesHelper;

    private PasscodeLockView mPasscodeLockView;
    private String mPasscode;
    private @DrawState
    int mDrawState;

    private final PasscodeLockListener mPasscodeLockListener = new PasscodeLockListener() {
        @Override
        public void onComplete(String pin) {
            Log.d(getClass().getName(), "Passcode complete: " + pin);
            if (mDrawState == DrawState.DRAW_FIRST) {
                mPasscode = pin;
                mPasscodeLockView.setInputEnabled(false);
                mPasscodeLockView.resetPasscodeLockView();
                pin = "";
                setDrawState(DrawState.DRAW_LAST);
            }
            if (mDrawState == DrawState.DRAW_LAST) {
                if (pin.length() != 0) {
                    if (pin.equals(mPasscode)) {
                        sharedPreferencesHelper.setPasscode(pin);
                        sharedPreferencesHelper.setLockType(Constant.PASSCODE_TYPE);
                        setDrawState(DrawState.DRAW_LAST_DONE);
                        navigateAction();
                    } else {
                        pin = "";
                        mPasscodeLockView.setInputEnabled(false);
                        mPasscodeLockView.resetWrongInputPasscode();
                    }
                }
            }
        }

        @Override
        public void onEmpty() {
            Log.d(getClass().getName(), "Passcode empty");
        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {
            Log.d(getClass().getName(), "Passcode changed, new length " + pinLength + " with intermediate passcode " + intermediatePin);
        }
    };

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPasscodeBinding.inflate(getLayoutInflater());
        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(getApplicationContext());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(binding.getRoot());
        mPasscodeLockView = binding.pinLockView;
        IndicatorDots mIndicatorDots = binding.indicatorDots;
        mPasscodeLockView.attachIndicatorDots(mIndicatorDots);
        mPasscodeLockView.setPasscodeLockListener(mPasscodeLockListener);

        mPasscodeLockView.setPinLength(4);
        mPasscodeLockView.setTextColor(ContextCompat.getColor(this, R.color.white));
        mIndicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FIXED);
        binding.resetPasscode.setOnClickListener(this);

        if (sharedPreferencesHelper.getSettingType().equals(Constant.SPLASH_NAV) && sharedPreferencesHelper.getPasscode() != null) {
            binding.back.setVisibility(View.GONE);
            mPasscode = sharedPreferencesHelper.getPasscode();
            setDrawStateForSetting(DrawState.DRAW_LAST);
        } else {
            setDrawState(DrawState.DRAW_FIRST);
        }
        binding.back.setOnClickListener(v -> {
            startActivity(new Intent(this, LockerSettingActivity.class));
            finish();
        });
    }

    private void setDrawState(@DrawState int drawState) {
        mDrawState = drawState;
        switch (drawState) {
            case DrawState.DRAW_FIRST:
                binding.profileName.setText(R.string.enter_passcode_to_unlock);
                binding.resetPasscode.setVisibility(View.GONE);
                break;
            case DrawState.DRAW_LAST:
                binding.profileName.setText(R.string.re_enter_passcode_to_unlock);
                binding.resetPasscode.setVisibility(View.VISIBLE);
                break;
            case DrawState.DRAW_LAST_DONE:
                binding.profileName.setText(R.string.matched_passcode);
                binding.resetPasscode.setVisibility(View.GONE);
                break;
        }
    }

    private void setDrawStateForSetting(@DrawState int drawState) {
        mDrawState = drawState;
        switch (drawState) {
            case DrawState.DRAW_LAST:
                binding.profileName.setText(R.string.enter_passcode_to_unlock);
                binding.resetPasscode.setVisibility(View.GONE);
                break;
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

    @Override
    public void onClick(@NonNull View view) {
        if (view.getId() == R.id.reset_passcode) {
            mPasscodeLockView.setInputEnabled(true);
            mPasscodeLockView.resetPasscodeLockView();
            setDrawState(DrawState.DRAW_FIRST);
        }
    }
}
