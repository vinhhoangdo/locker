package com.example.locker.util;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.example.locker.R;
import com.example.locker.databinding.LayoutPermissionDialogBinding;

public class PermissionDialog extends DialogFragment {
    LayoutPermissionDialogBinding binding;
    public static final String PERMISSION_TAG = "PERMISSION_TAG";
    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        binding = LayoutPermissionDialogBinding.inflate(getLayoutInflater());
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(binding.getRoot());

        binding.overlayContent.setText(R.string.overlay_permission_content);
        binding.usagesContent.setText(R.string.usages_access_permission_content);

        binding.overlay.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + requireContext().getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            requireContext().startActivity(intent);
        });

        binding.usages.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            startActivity(intent);
        });
        binding.btnDismiss.setOnClickListener(v -> dismiss());
        return alertDialogBuilder.create();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getContext() != null) {
            setConfigStateDrawable(binding.checkOverlay, Utils.checkOverlayPermission(getContext()));
            setConfigStateDrawable(binding.checkUsages, Utils.checkUsageAccessPermission(getContext()));
            if (Utils.isOvlUsagePermissionChecked(getContext())) {
                dismiss();
            }
        }
    }

    private void setConfigStateDrawable(ImageView view, boolean state) {
        if (state) {
            view.setImageResource(R.drawable.icon_accept);
        } else {
            view.setImageResource(R.drawable.icon_deny);
        }
    }
}
