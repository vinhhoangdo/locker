package com.example.locker.util;


import static com.example.locker.util.AutoRestartHandler.PACKAGES_TO_CHECK_FOR_PERMISSION;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.example.locker.R;
import com.example.locker.databinding.LayoutPermissionDialogBinding;

public class PermissionDialog extends DialogFragment {
    LayoutPermissionDialogBinding binding;
    SharedPreferencesHelper sharedPreferencesHelper;
    private String phoneType;
    public static final String PERMISSION_TAG = "PERMISSION_TAG";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = LayoutPermissionDialogBinding.inflate(getLayoutInflater());
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        sharedPreferencesHelper = SharedPreferencesHelper.getInstance(requireActivity());
        alertDialogBuilder.setView(binding.getRoot());
        setUpPermissionView();
        return alertDialogBuilder.create();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onResume() {
        super.onResume();
        if (getContext() != null) {
            setConfigStateDrawable(binding.checkOverlay, Utils.checkOverlayPermission(getContext()));
            setConfigStateDrawable(binding.checkUsages, Utils.checkUsageAccessPermission(getContext()));
            setConfigStateDrawable(binding.checkAutostart, Utils.isBackgroundActivity(getContext()) | Utils.isAutoStartEnabled(getContext()));
            if (Utils.isRunningBackgroundService(requireContext())) {
                dismiss();
            }
        }
    }

    @SuppressLint("NewApi")
    private void setUpPermissionView() {
        phoneType = Build.BRAND.toLowerCase();
        binding.overlayContent.setText(R.string.overlay_permission_content);
        binding.usagesContent.setText(R.string.usages_access_permission_content);
        binding.autostartContent.setText(R.string.autostart_permission_content);
        if (PACKAGES_TO_CHECK_FOR_PERMISSION.contains(phoneType)) {
            binding.autostart.setVisibility(View.VISIBLE);
        } else {
            binding.autostart.setVisibility(View.GONE);
        }

        binding.overlay.setOnClickListener(v -> {
            Intent intent = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + requireContext().getPackageName()));
            }
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            requireContext().startActivity(intent);
        });

        binding.usages.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        });

        binding.autostart.setOnClickListener(v -> {
            Log.e("XXX", "" + Utils.isBackgroundActivity(requireContext()));
            Log.e("XXXX", "" + Utils.isAutoStartEnabled(requireContext()));
            Log.e("XXXXX", "" + (Utils.isBackgroundActivity(requireContext()) | Utils.isAutoStartEnabled(requireContext())));
            AutoRestartHandler.getInstance().getAutoStartPermission(requireActivity());
        });
        binding.btnDismiss.setOnClickListener(v -> dismiss());
    }

    private void setConfigStateDrawable(ImageView view, boolean state) {
        if (state) {
            view.setImageResource(R.drawable.icon_accept);
        } else {
            view.setImageResource(R.drawable.icon_deny);
        }
    }
}
