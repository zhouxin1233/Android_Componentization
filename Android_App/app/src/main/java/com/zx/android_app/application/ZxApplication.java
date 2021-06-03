package com.zx.android_app.application;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import androidx.multidex.MultiDex;

import com.zx.android_app.ZxDelayInitManagerKt;
import com.zx.environmentservice.EnvironmentService;
import com.zx.util.base.ProcessUtil;

public class ZxApplication extends Application {
    public static long startTime = System.nanoTime();

    private static long endTime = 0;
    private boolean isShouldHandleRest = false;

    private final ZxInitManager mInitManager = new ZxInitManager();

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        if (endTime > 0) {
            startTime = System.nanoTime();
            isShouldHandleRest = true;
        } else {
            isShouldHandleRest = false;
        }
        MultiDex.install(base);
//        SorakaUtils.initSoraka(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /**
         * 如果需要在Application onCreate()中初始化,请在 FastModuleInit 中添加 addInitFunction()方法
         * 耗时方法请尽量懒加载或异步
         */
        String processName = ProcessUtil.getProcessName(this);
        mInitManager.dispatchAppInit(this);
        ZxDelayInitManagerKt.appInit(this);

        if (runInMainProcess(getPackageName(), processName)) {
            handleApplicationInitEnd();
        }
    }
    public  boolean runInMainProcess(String packageName, String defaultProcessName) {
        if (packageName.equals(defaultProcessName)){
            return true;
        }else if (TextUtils.isEmpty(defaultProcessName)){
            return packageName.equals(ProcessUtil.getProcessName(EnvironmentService.getInstance().getContext()));
        }else {
            return false;
        }
    }
    private void handleApplicationInitEnd() {
        endTime = System.nanoTime();
    }

    public static long getEndTime() {
        return endTime;
    }
}
