package com.zx.environmentservice.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.zx.environmentservice.EnvironmentService;


public class EnvUtil {

    public static boolean isDebug() {
        Context context = EnvironmentService.getInstance().getContext();
        return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

    public static boolean isDebug(Context context) {
        return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }

    public static boolean isRelease() {
        return !EnvUtil.isDebug();
    }

}
