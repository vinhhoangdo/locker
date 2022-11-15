package com.example.locker.util;

import static com.example.locker.util.AutoRestartHandler.PACKAGES_CHECK_BACKGROUND_ACTIVITY;
import static com.example.locker.util.AutoRestartHandler.PACKAGES_TO_CHECK_FOR_PERMISSION;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.lang.reflect.Method;

public class Utils {
    private static final int APP_AUTO_START_PERMISSION_CODE = 10008;

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

    public static boolean isAutoStartEnabled(@NonNull Context context) {
        try {
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            Method method = AppOpsManager.class.getMethod("checkOpNoThrow", int.class, int.class, String.class);
            int result = (int) method.invoke(appOpsManager, APP_AUTO_START_PERMISSION_CODE, android.os.Process.myUid(), context.getPackageName());
            return result == AppOpsManager.MODE_ALLOWED;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isBackgroundActivity(@NonNull Context context) {
        try {
            String packageName = context.getPackageName();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return pm.isIgnoringBatteryOptimizations(packageName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isRunningBackgroundService(Context context) {
        String phoneType = Build.BRAND.toLowerCase();
        if (PACKAGES_TO_CHECK_FOR_PERMISSION.contains(phoneType)) {
            if (PACKAGES_CHECK_BACKGROUND_ACTIVITY.contains(phoneType)) {
                return isOvlUsagePermissionChecked(context) && isBackgroundActivity(context);
            } else {
                return isOvlUsagePermissionChecked(context) && isAutoStartEnabled(context);
            }
        } else {
            return isOvlUsagePermissionChecked(context);
        }
    }

    public static void doubleClickVibrate(@NonNull View view) {
        Runnable r = () -> {
            int flag = HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING | HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING;
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, flag);
        };
        view.postDelayed(r, Constant.VIBRATE_DELAY);
        view.postDelayed(r, Constant.VIBRATE_DELAY + 100);
    }
}
