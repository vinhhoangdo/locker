package com.example.locker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.locker.R;
import com.example.locker.databinding.ActivityLockerSettingBinding;
import com.example.locker.util.Constant;
import com.example.locker.util.SharedPreferencesHelper;

import java.util.Objects;

public class LockerSettingActivity extends AppCompatActivity {

    ActivityLockerSettingBinding binding;
    SharedPreferencesHelper sharedPreferencesHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLockerSettingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(this);
        binding.tvOptions.setText(R.string.select_lock_type);
        binding.inPasscode.pcName.setText(R.string.passcode);
        binding.inPattern.ptName.setText(R.string.pattern);
        settingLockerLayout();
        navigateActivity();
    }

    private void settingLockerLayout() {
        switch (sharedPreferencesHelper.getLockType()) {
            case Constant.PASSCODE_TYPE:
                binding.inPasscode.pcRadio.setImageResource(R.drawable.icon_radio_on);
                binding.inPasscode.pcImage.setImageResource(R.drawable.icon_passcode_on);
                break;
            case Constant.PATTERN_TYPE:
                binding.inPattern.ptRadio.setImageResource(R.drawable.icon_radio_on);
                binding.inPattern.ptImage.setImageResource(R.drawable.icon_pattern_on);
                break;
            default:
                binding.inPattern.ptRadio.setImageResource(R.drawable.icon_radio_off);
                binding.inPattern.ptImage.setImageResource(R.drawable.icon_pattern);
                binding.inPasscode.pcRadio.setImageResource(R.drawable.icon_radio_off);
                binding.inPasscode.pcImage.setImageResource(R.drawable.icon_passcode);
                break;

        }
    }

    private void navigateActivity() {
        if (!sharedPreferencesHelper.getLockType().equals("DEFAULT") && sharedPreferencesHelper.getSettingType().equals(Constant.SPLASH_NAV)) {
            switch (sharedPreferencesHelper.getLockType()) {
                case Constant.PASSCODE_TYPE:
                    Intent i = new Intent(this, PasscodeActivity.class);
                    startActivity(i);
                    finish();
                    break;
                case Constant.PATTERN_TYPE:
                    Intent intent = new Intent(this, PatternActivity.class);
                    startActivity(intent);
                    finish();
                    break;
            }
        } else {
            binding.inPasscode.passcode.setOnClickListener(v -> {
                Intent i = new Intent(this, PasscodeActivity.class);
                startActivity(i);
                finish();
            });

            binding.inPattern.pattern.setOnClickListener(v -> {
                Intent i = new Intent(this, PatternActivity.class);
                startActivity(i);
                finish();
            });
        }
    }
}
