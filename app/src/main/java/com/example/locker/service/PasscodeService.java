package com.example.locker.service;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.locker.R;
import com.example.locker.databinding.LayoutPasscodeServiceBinding;
import com.example.locker.receiver.LockStateReceiver;
import com.example.locker.util.SharedPreferencesHelper;
import com.example.locker.util.Utils;
import com.example.passcodelockview.IndicatorDots;
import com.example.passcodelockview.PasscodeLockListener;
import com.example.passcodelockview.PasscodeLockView;

public class PasscodeService extends Service implements View.OnClickListener{
    LayoutPasscodeServiceBinding binding;
    private WindowManager mWindowManager;
    WindowManager.LayoutParams params;
    SharedPreferencesHelper sharedPreferencesHelper;
    private PasscodeLockView mPasscodeLockView;
    LockStateReceiver lockStateReceiver = new LockStateReceiver();
    private final PasscodeLockListener mPasscodeLockListener = new PasscodeLockListener() {
        @Override
        public void onComplete(String pin) {
            if (sharedPreferencesHelper.getPasscode().equalsIgnoreCase(pin)) {
                stopSelf();
            } else {
                Utils.doubleClickVibrate(binding.getRoot());
                pin = "";
                mPasscodeLockView.setInputEnabled(false);
                mPasscodeLockView.resetWrongInputPasscode();
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
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerReceiver(lockStateReceiver, HomeAppFilter());
        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this);
        binding = LayoutPasscodeServiceBinding.inflate(LayoutInflater.from(this));
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

        mPasscodeLockView = binding.pinLockView;
        IndicatorDots mIndicatorDots = binding.indicatorDots;
        mPasscodeLockView.attachIndicatorDots(mIndicatorDots);
        mPasscodeLockView.setPasscodeLockListener(mPasscodeLockListener);

        mPasscodeLockView.setPinLength(4);
        mPasscodeLockView.setTextColor(ContextCompat.getColor(this, R.color.white));
        mIndicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FIXED);
        binding.back.setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWindowManager.removeView(binding.getRoot());
        unregisterReceiver(lockStateReceiver);
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

    @NonNull
    private IntentFilter HomeAppFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        return filter;
    }
}
