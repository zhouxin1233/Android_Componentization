package com.zx.android_app.application.init;

import android.app.Application;

import com.zx.moduleinit.ModuleInit;
import com.zx.util.base.AppLifecycleManager;

/**
 * @Description: AppUtilInit
 */
public class AppUtilInit extends ModuleInit {
    @Override
    public String tag() {
        return "AppUtil";
    }

    @Override
    public void init(Application application) {
        try {
            AppLifecycleManager.getInstance().init(application);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void asyncInit(Application application) {

    }

    @Override
    public boolean filter(Application application, String processName) {
        return application.getPackageName().equals(processName);
    }
}
