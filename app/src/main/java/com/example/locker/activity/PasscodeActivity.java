package com.example.locker.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.locker.util.LockerState;
import com.example.locker.util.SharedPreferencesHelper;
import com.example.locker.util.Utils;
import com.example.passcodelockview.IndicatorDots;
import com.example.passcodelockview.PasscodeLockListener;
import com.example.passcodelockview.PasscodeLockView;

public class PasscodeActivity extends AppCompatActivity implements View.OnClickListener {
    ActivityPasscodeBinding binding;
    SharedPreferencesHelper sharedPreferencesHelper;

    private PasscodeLockView mPasscodeLockView;
    private String mPasscode;
    private @LockerState
    int mDrawState;

    private final PasscodeLockListener mPasscodeLockListener = new PasscodeLockListener() {
        @Override
        public void onComplete(String pin) {
            Log.d(getClass().getName(), "Passcode complete: " + pin);
            if (mDrawState == LockerState.DRAW_FIRST) {
                mPasscode = pin;
                mPasscodeLockView.setInputEnabled(false);
                mPasscodeLockView.resetPasscodeLockView();
                pin = "";
                setDrawState(LockerState.DRAW_LAST);
            }
            if (mDrawState == LockerState.DRAW_LAST) {
                if (pin.length() != 0) {
                    if (pin.equals(mPasscode)) {
                        sharedPreferencesHelper.setPasscode(pin);
                        sharedPreferencesHelper.setLockType(Constant.PASSCODE_TYPE);
                        setDrawState(LockerState.DRAW_LAST_DONE);
                        navigateAction();
                    } else {
                        pin = "";
                        mPasscodeLockView.setInputEnabled(false);
                        mPasscodeLockView.resetWrongInputPasscode();
                        Utils.doubleClickVibrate(binding.getRoot());
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
            setDrawStateForSetting(LockerState.DRAW_LAST);
        } else {
            setDrawState(LockerState.DRAW_FIRST);
        }
        binding.back.setOnClickListener(v -> {
            startActivity(new Intent(this, LockerSettingActivity.class));
            finish();
        });
    }

    private void setDrawState(@LockerState int drawState) {
        mDrawState = drawState;
        switch (drawState) {
            case LockerState.DRAW_FIRST:
                binding.profileName.setText(R.string.enter_passcode_to_unlock);
                binding.resetPasscode.setVisibility(View.GONE);
                break;
            case LockerState.DRAW_LAST:
                binding.profileName.setText(R.string.re_enter_passcode_to_unlock);
                binding.resetPasscode.setVisibility(View.VISIBLE);
                break;
            case LockerState.DRAW_LAST_DONE:
                binding.profileName.setText(R.string.matched_passcode);
                binding.resetPasscode.setVisibility(View.GONE);
                break;
        }
    }

    private void setDrawStateForSetting(@LockerState int drawState) {
        mDrawState = drawState;
        switch (drawState) {
            case LockerState.DRAW_LAST:
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
            setDrawState(LockerState.DRAW_FIRST);
        }
    }
}
