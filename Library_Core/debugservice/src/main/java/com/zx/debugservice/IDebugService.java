package com.zx.debugservice;

import android.app.Activity;

import com.alibaba.android.arouter.facade.template.IProvider;

import java.util.Map;

public interface IDebugService extends IProvider {
    /**
     * 开启调试
     */
    void openDebugPanel();

    /**
     * @return 项目的api环境
     */
    boolean isApiTest();

    boolean isApiUat();

    boolean isApiRelease();

    /**
     * @return 获取mock地址
     */
    String getMockUrl();


    /**
     * @return mock，true 开启，false 关闭
     */
    boolean getMockStatus();

    /**
     * 打开h5调试页面
     */
    void openH5Test();

    void addRequestData(Map<String, Object> map);

    void enable(Activity activity);

    void disable();

    boolean isEnabled();
}
