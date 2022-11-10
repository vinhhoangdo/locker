package com.example.locker.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.locker.R;
import com.example.locker.activity.PasscodeActivity;
import com.example.locker.activity.PatternActivity;
import com.example.locker.databinding.FragmentLockerSettingBinding;
import com.example.locker.util.Constant;
import com.example.locker.util.SharedPreferencesHelper;

public class LockerSettingFragment extends Fragment implements View.OnClickListener {
    FragmentLockerSettingBinding binding;
    SharedPreferencesHelper sharedPreferencesHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentLockerSettingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(requireContext());
        binding.tvOptions.setText(R.string.select_lock_type);
        binding.inPasscode.pcName.setText(R.string.passcode);
        binding.inPattern.ptName.setText(R.string.pattern);
        binding.inAppBar.appBarBack.setOnClickListener(this);
        binding.inAppBar.txTitle.setText(R.string.setting);
        binding.inAppBar.appBarBack.setVisibility(View.VISIBLE);
        binding.inAppBar.setting.setVisibility(View.GONE);

    }

    @Override
    public void onResume() {
        super.onResume();
        settingLockerLayout();
        navigateActivity();
    }

    private void settingLockerLayout() {
        switch (sharedPreferencesHelper.getLockType()) {
            case Constant.PASSCODE_TYPE:
                binding.inPasscode.pcRadio.setImageResource(R.drawable.icon_radio_on);
                binding.inPasscode.pcImage.setImageResource(R.drawable.icon_passcode_on);
                binding.inPattern.ptRadio.setImageResource(R.drawable.icon_radio_off);
                binding.inPattern.ptImage.setImageResource(R.drawable.icon_pattern);
                break;
            case Constant.PATTERN_TYPE:
                binding.inPattern.ptRadio.setImageResource(R.drawable.icon_radio_on);
                binding.inPattern.ptImage.setImageResource(R.drawable.icon_pattern_on);
                binding.inPasscode.pcRadio.setImageResource(R.drawable.icon_radio_off);
                binding.inPasscode.pcImage.setImageResource(R.drawable.icon_passcode);
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
        binding.inPasscode.passcode.setOnClickListener(v -> {
            Intent i = new Intent(requireActivity(), PasscodeActivity.class);
            startActivity(i);
        });

        binding.inPattern.pattern.setOnClickListener(v -> {
            Intent i = new Intent(requireActivity(), PatternActivity.class);
            startActivity(i);
        });

    }

    @Override
    public void onClick(@NonNull View view) {
        int id = view.getId();
        if (id == R.id.appBarBack) {
            Navigation.findNavController(view).popBackStack();
        }
    }
}
