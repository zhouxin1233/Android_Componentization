package com.zx.moduleinit;

import android.app.Application;

/**
 * @Description: 在App初始化时调用模块初始化方法的接口定义
 */
public interface IModuleInit {
    /**
     * @return 唯一标示
     */
    String tag();

    /**
     * 在UI线程调用的初始化方法
     */
    void init(Application application);

    /**
     * 在非UI线程调用的初始化方法
     */
    void asyncInit(Application application);

    /**
     * 根据进程名判断是否需要执行初始化类
     */
    boolean filter(Application application, String processName);
}
