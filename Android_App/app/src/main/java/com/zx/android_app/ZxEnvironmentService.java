package com.zx.android_app;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zx.environmentservice.IEnvironmentService;

/**
 * EnvironmentService的实现
 */
@Route(path = "/environment/service")
public class ZxEnvironmentService implements IEnvironmentService {
    private Context mContext;
    private String sAppChannel;

    @Override
    public boolean isDebug() {
        return false;
    }

    @Override
    public boolean isBeta() {
        return false;
    }

    @Override
    public boolean isDev() {
        return false;
    }

    @Override
    public Context getContext() {
        return null;
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public String getAppId() {
        return null;
    }

    @Override
    public String getDeviceId() {
        return null;
    }

    @Override
    public String getChannelId() {
        return null;
    }

    @Override
    public void init(Context context) {

    }
}
