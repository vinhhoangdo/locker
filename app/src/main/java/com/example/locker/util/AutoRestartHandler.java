package com.example.locker.util;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Arrays;

public class AutoRestartHandler {
    /***
     * Xiaomi
     */
    private static final String XIAOMI_BRAND = "xiaomi";
    private static final String PACKAGE_XIAOMI_MAIN = "com.miui.securitycenter";
    private static final String PACKAGE_XIAOMI_COMPONENT = "com.miui.permcenter.autostart.AutoStartManagementActivity";

    /***
     * Letv
     */
    private static final String LETV_BRAND = "letv";
    private static final String PACKAGE_LETV_MAIN = "com.letv.android.letvsafe";
    private static final String PACKAGE_LETV_COMPONENT = "com.letv.android.letvsafe.AutobootManagementActivity";

    /***
     * ASUS ROG
     */
    private static final String ASUS_BRAND = "asus";
    private static final String PACKAGE_ASUS_MAIN = "com.asus.mobilemanager";
    private static final String PACKAGE_ASUS_COMPONENT = "com.asus.mobilemanager.powersaver.PowerSaverSettings";
    private static final String PACKAGE_ASUS_COMPONENT_FALLBACK = "com.asus.mobilemanager.autostart.AutoStartActivity";

    /***
     * Honor
     */
    private static final String HONOR_BRAND = "honor";
    private static final String PACKAGE_HONOR_MAIN = "com.huawei.systemmanager";
    private static final String PACKAGE_HONOR_COMPONENT = "com.huawei.systemmanager.optimize.process.ProtectActivity";

    /***
     * Huawei
     */
    private static final String HUAWEI_BRAND = "huawei";
    private static final String PACKAGE_HUAWEI_MAIN = "com.huawei.systemmanager";
    private static final String PACKAGE_HUAWEI_COMPONENT = "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity";
    private static final String PACKAGE_HUAWEI_COMPONENT_FALLBACK = "com.huawei.systemmanager.optimize.process.ProtectActivity";

    /***
     * Nokia
     */
    private static final String NOKIA_BRAND = "nokia";
    private static final String PACKAGE_NOKIA_MAIN = "com.evenwell.powersaving.g3";
    private static final String PACKAGE_NOKIA_COMPONENT = "com.evenwell.powersaving.g3.exception.PowerSaverExceptionActivity";

    /***
     * Oppo
     */
    private static final String OPPO_BRAND = "oppo";
    private static final String PACKAGE_OPPO_MAIN = "com.coloros.safecenter";
    private static final String PACKAGE_OPPO_FALLBACK = "com.oppo.safe";
    private static final String PACKAGE_OPPO_COMPONENT = "com.coloros.safecenter.permission.startup.StartupAppListActivity";
    private static final String PACKAGE_OPPO_COMPONENT_FALLBACK = "com.oppo.safe.permission.startup.StartupAppListActivity";
    private static final String PACKAGE_OPPO_COMPONENT_FALLBACK_A = "com.coloros.safecenter.startupapp.StartupAppListActivity";

    /***
     * Vivo
     */
    private static final String VIVO_BRAND = "vivo";
    private static final String PACKAGE_VIVO_MAIN = "com.iqoo.secure";
    private static final String PACKAGE_VIVO_FALLBACK = "com.vivo.permissionmanger";
    private static final String PACKAGE_VIVO_COMPONENT = "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity";
    private static final String PACKAGE_VIVO_COMPONENT_FALLBACK = "com.vivo.permissionmanger.activity.BgStartUpManagerActivity";
    private static final String PACKAGE_VIVO_COMPONENT_FALLBACK_A = "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager";

    /***
     * Samsung
     */
    private static final String SAMSUNG_BRAND = "samsung";
    private static final String PACKAGE_SAMSUNG_MAIN = "com.samsung.android.lool";
    private static final String PACKAGE_SAMSUNG_COMPONENT = "com.samsung.android.sm.ui.battery.BatteryActivity";
    private static final String PACKAGE_SAMSUNG_COMPONENT_2 = "com.samsung.android.sm.battery.ui.usage.CheckableAppListActivity";
    private static final String PACKAGE_SAMSUNG_COMPONENT_3 = "com.samsung.android.sm.battery.ui.BatteryActivity";

    /***
     * One plus
     */
    private static final String ONE_PLUS_BRAND = "oneplus";
    private static final String PACKAGE_ONE_PLUS_MAIN = "com.oneplus.security";
    private static final String PACKAGE_ONE_PLUS_COMPONENT = "com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity";

    public static final ArrayList<String> PACKAGES_TO_CHECK_FOR_PERMISSION = new ArrayList<>(Arrays.asList(
            ASUS_BRAND,
            LETV_BRAND,
            NOKIA_BRAND,
            XIAOMI_BRAND,
            VIVO_BRAND,
            HONOR_BRAND,
            HUAWEI_BRAND,
            OPPO_BRAND,
            SAMSUNG_BRAND,
            ONE_PLUS_BRAND
    ));

    public static final ArrayList<String> PACKAGES_CHECK_BACKGROUND_ACTIVITY = new ArrayList<>(Arrays.asList(
            SAMSUNG_BRAND,
            ONE_PLUS_BRAND
    ));

    @NonNull
    @Contract(" -> new")
    public static AutoRestartHandler getInstance() {
        return new AutoRestartHandler();
    }

    public void getAutoStartPermission(Context context) {
        String buildInfo = Build.BRAND.toLowerCase();
        switch (buildInfo) {
            case ASUS_BRAND:
                autoStartAsus(context);
                break;
            case LETV_BRAND:
                autoStartLetv(context);
                break;
            case NOKIA_BRAND:
                autoStartNokia(context);
                break;
            case XIAOMI_BRAND:
                autoStartXiaomi(context);
                break;
            case VIVO_BRAND:
                autoStartVivo(context);
                break;
            case HONOR_BRAND:
                autoStartHonor(context);
                break;
            case OPPO_BRAND:
                autoStartOppo(context);
                break;
            case HUAWEI_BRAND:
                autoStartHuawei(context);
                break;
            case SAMSUNG_BRAND:
                autoStartSamsung(context);
                break;
            case ONE_PLUS_BRAND:
                autoStartOnePlus(context);
                break;

        }
    }

    private void autoStartXiaomi(final Context context) {
        try {
            startIntent(context, PACKAGE_XIAOMI_MAIN, PACKAGE_XIAOMI_COMPONENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void autoStartAsus(final Context context) {
        try {
            startIntent(context, PACKAGE_ASUS_MAIN, PACKAGE_ASUS_COMPONENT);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                startIntent(context, PACKAGE_ASUS_MAIN, PACKAGE_ASUS_COMPONENT_FALLBACK);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void autoStartHuawei(final Context context) {
        try {
            startIntent(context, PACKAGE_HUAWEI_MAIN, PACKAGE_HUAWEI_COMPONENT);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                startIntent(context, PACKAGE_HUAWEI_MAIN, PACKAGE_HUAWEI_COMPONENT_FALLBACK);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void autoStartLetv(final Context context) {
        try {
            startIntent(context, PACKAGE_LETV_MAIN, PACKAGE_LETV_COMPONENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void autoStartNokia(final Context context) {
        try {
            startIntent(context, PACKAGE_NOKIA_MAIN, PACKAGE_NOKIA_COMPONENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void autoStartHonor(final Context context) {
        try {
            startIntent(context, PACKAGE_HONOR_MAIN, PACKAGE_HONOR_COMPONENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void autoStartOnePlus(final Context context) {
        try {
            startIntent(context, PACKAGE_ONE_PLUS_MAIN, PACKAGE_ONE_PLUS_COMPONENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void autoStartOppo(final Context context) {
        try {
            startIntent(context, PACKAGE_OPPO_MAIN, PACKAGE_OPPO_COMPONENT);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                startIntent(context, PACKAGE_OPPO_FALLBACK, PACKAGE_OPPO_COMPONENT_FALLBACK);
            } catch (Exception ex) {
                ex.printStackTrace();
                try {
                    startIntent(context, PACKAGE_OPPO_MAIN, PACKAGE_OPPO_COMPONENT_FALLBACK_A);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    private void autoStartVivo(final Context context) {
        try {
            startIntent(context, PACKAGE_VIVO_MAIN, PACKAGE_VIVO_COMPONENT);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                startIntent(context, PACKAGE_VIVO_FALLBACK, PACKAGE_VIVO_COMPONENT_FALLBACK);
            } catch (Exception ex) {
                ex.printStackTrace();
                try {
                    startIntent(context, PACKAGE_VIVO_MAIN, PACKAGE_VIVO_COMPONENT_FALLBACK_A);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    @SuppressLint("NewApi")
    private void autoStartSamsung(final Context context) {
        try {
            Intent intent = new Intent();
            String packageName = context.getPackageName();
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (pm.isIgnoringBatteryOptimizations(packageName))
                intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            else {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
            }
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startIntent(Context context, String packageName, String componentName) {
        try {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(packageName, componentName));
            context.startActivity(intent);
        } catch (Exception exception) {
            exception.printStackTrace();
            throw exception;
        }
    }

}
