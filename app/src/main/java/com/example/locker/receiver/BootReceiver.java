package com.example.locker.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.locker.service.AppLaunchDetectionService;
import com.example.locker.util.Constant;

import java.util.Objects;

public class BootReceiver extends BroadcastReceiver {
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), ACTION)) {
            Intent serviceIntent = new Intent(context, AppLaunchDetectionService.class);
            serviceIntent.setAction(Constant.ACTION.START_FOREGROUND);
            context.startService(serviceIntent);
            Toast.makeText(context, "Boot conpleted", Toast.LENGTH_SHORT).show();
        }
    }
}
