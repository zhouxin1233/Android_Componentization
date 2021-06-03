package com.zx.android_app.application.init;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;
import com.zx.environmentservice.util.EnvUtil;
import com.zx.moduleinit.ModuleInit;

/**
 * 对ARouter的初始化设置
 */
public class RouterInit extends ModuleInit {
    @Override
    public String tag() {
        return "Router";
    }

    @Override
    public void init(Application application) {
        try {
            if (EnvUtil.isDebug(application)) {
                ARouter.openLog();
                ARouter.openDebug();
            }
            ARouter.init(application);
        }catch (Throwable t){
            t.printStackTrace();
        }
    }

    @Override
    public void asyncInit(Application application) {

    }
}
