package com.zx.environmentservice;

import android.content.Context;

import com.alibaba.android.arouter.launcher.ARouter;

/**
 * IEnvironmentService的封装类，提供对外接口
 */
public class EnvironmentService implements IEnvironmentService{

    private IEnvironmentService wrappedEnvironmentService;

    private static class Inner {
        private static EnvironmentService sInstance = new EnvironmentService();
    }
    private EnvironmentService(){
        this.wrappedEnvironmentService = ARouter.getInstance().navigation(IEnvironmentService.class);
    }

    public static EnvironmentService getInstance() {
        return Inner.sInstance;
    }


    @Override
    public boolean isDebug() {
        return this.wrappedEnvironmentService.isDebug();
    }

    @Override
    public boolean isBeta() {
        return this.wrappedEnvironmentService.isBeta();
    }

    @Override
    public boolean isDev() {
        return this.wrappedEnvironmentService.isDev();
    }

    @Override
    public Context getContext() {
        return this.wrappedEnvironmentService.getContext();
    }

    @Override
    public String getVersion() {
        return this.wrappedEnvironmentService.getVersion();
    }

    @Override
    public String getAppId() {
        return this.wrappedEnvironmentService.getAppId();
    }

    @Override
    public String getDeviceId() {
        return this.wrappedEnvironmentService.getDeviceId();
    }

    @Override
    public String getChannelId() {
        return this.wrappedEnvironmentService.getChannelId();
    }

    @Override
    public String getFlavor() {
        return this.wrappedEnvironmentService.getFlavor();
    }

    @Override
    public void init(Context context) {

    }
}
