package com.zx.moduleinit;

import android.app.Application;

public abstract class ModuleInit implements IModuleInit {
    /**
     * @return 唯一标示
     */
    @Override
    public abstract String tag();

    /**
     * 在UI线程调用的初始化方法
     */
    @Override
    public abstract void init(Application application);

    /**
     * 在非UI线程调用的初始化方法
     */
    @Override
    public abstract void asyncInit(Application application);

    /**
     * 根据进程名判断是否需要执行初始化类
     */
    @Override
    public boolean filter(Application application, String processName) {
        return true;
    }
}
