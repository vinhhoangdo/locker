package com.example.locker.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.locker.service.PasscodeService;
import com.example.locker.service.PatternService;
import com.example.locker.util.Constant;
import com.example.locker.util.SharedPreferencesHelper;

public class LockStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance(context);
        String type = sharedPreferencesHelper.getLockType();
        if (type.equalsIgnoreCase(Constant.PASSCODE_TYPE)) {
            context.stopService(new Intent(context, PasscodeService.class));
        } else if (type.equalsIgnoreCase(Constant.PATTERN_TYPE)) {
            context.stopService(new Intent(context, PatternService.class));
        }
    }
}
