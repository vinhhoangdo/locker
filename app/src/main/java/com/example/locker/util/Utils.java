package com.example.locker.util;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class Utils {
    public void selectTabLayout(Context context, TextView textViewLeft, TextView textViewRight, View viewLeft, View viewRight, TextView textViewSelect) {
        if (textViewSelect == textViewLeft) {
            viewLeft.setVisibility(View.VISIBLE);
            viewRight.setVisibility(View.INVISIBLE);
        } else {
            viewLeft.setVisibility(View.INVISIBLE);
            viewRight.setVisibility(View.VISIBLE);
        }
    }

    public static boolean checkOverlayPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        } else {
            return true;
        }
    }

    public static boolean checkUsageAccessPermission(@NonNull Context context) {
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    applicationInfo.uid, applicationInfo.packageName
            );
            return mode == AppOpsManager.MODE_ALLOWED;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean isOvlUsagePermissionChecked(Context context) {
        return checkOverlayPermission(context) && checkUsageAccessPermission(context);
    }
}
