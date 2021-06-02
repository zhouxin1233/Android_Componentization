package com.zx.moduleinit;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;

/**
 * @Description: 模块初始化管理器
 */
public class ModuleInitManager {
    private List<IModuleInit> mModuleInits = new LinkedList<>();

    /**
     * 注册一个AppInit
     *
     * @param appInit 注册的AppInit
     */
    public void registerModuleInit(IModuleInit appInit) {
        mModuleInits.add(appInit);
    }

    /**
     * 分发AppInit
     * 在 Application初始化的时机调用
     *
     * @param application 当前的Application实例
     */
    public void dispatchAppInit(final Application application) {
        String processName = getProcessName(application);
        final List<IModuleInit> asyncInits = new LinkedList<>();
        for (IModuleInit appInit : mModuleInits) {
            if (appInit.filter(application, processName)) {
                onInitStart(appInit.tag());
                appInit.init(application);
                onInitEnd(appInit.tag());
                asyncInits.add(appInit);
            }
        }
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                for (IModuleInit appInit : asyncInits) {
                    onAsynInitStart(appInit.tag());
                    appInit.asyncInit(application);
                    onAsyncInitEnd(appInit.tag());
                }
            }
        });
    }

    public IModuleInit getInitModule(String tag) {
        if (TextUtils.isEmpty(tag)) return null;
        for (IModuleInit appInit : mModuleInits) {
            if (tag.equals(appInit.tag())) {
                return appInit;
            }
        }
        return null;
    }


    public void onInitStart(String tag) {

    }

    public void onInitEnd(String tag) {

    }

    public void onAsynInitStart(String tag) {

    }

    public void onAsyncInitEnd(String tag) {

    }
    @SuppressLint("DiscouragedPrivateApi")
    private static String getProcessName(Context context) {
        String processName = null;

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                processName = Application.getProcessName();
            } else {
               Method declaredMethod = Class.forName("android.app.ActivityThread", false, Application.class.getClassLoader())
                        .getDeclaredMethod("currentProcessName");
                declaredMethod.setAccessible(true);
                Object invoke = declaredMethod.invoke(null);
                if (invoke instanceof String) {
                    processName = (String) invoke;
                }
            }
            if (!TextUtils.isEmpty(processName)) {
                return processName;
            }
        } catch (Throwable ignore) {
        }

        // ActivityManager
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));

        while (true) {
            List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
            if (runningAppProcesses != null) {
                for (ActivityManager.RunningAppProcessInfo info : runningAppProcesses) {
                    if (info.pid == android.os.Process.myPid()) {
                        processName = info.processName;
                        break;
                    }
                }
            }

            // go home
            if (!TextUtils.isEmpty(processName)) {
                return processName;
            }

            // take a rest and again
            try {
                Thread.sleep(100L);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
