package com.zx.debugservice;

import android.app.Activity;
import android.content.Context;

import com.alibaba.android.arouter.launcher.ARouter;

import java.util.Map;

public class DebugService implements IDebugService{
    private final IDebugService wrappedDebugService;
    private static class Inner {
        private static DebugService sInstance = new DebugService();
    }

    public static DebugService getInstance() {
        return Inner.sInstance;
    }
    private DebugService() {
        wrappedDebugService = ARouter.getInstance().navigation(IDebugService.class);
    }

    @Override
    public void openDebugPanel() {
        wrappedDebugService.openDebugPanel();
    }

    @Override
    public boolean isApiTest() {
        if (wrappedDebugService == null) {
            return false;
        }

        return wrappedDebugService.isApiTest();
    }

    @Override
    public boolean isApiUat() {
        if (wrappedDebugService == null) {
            return false;
        }

        return wrappedDebugService.isApiUat();
    }

    @Override
    public boolean isApiRelease() {
        if (wrappedDebugService == null) {
            return true;
        }

        return wrappedDebugService.isApiRelease();
    }

    @Override
    public String getMockUrl() {
        if (wrappedDebugService == null) {
            return "";
        }
        return wrappedDebugService.getMockUrl();
    }

    @Override
    public boolean getMockStatus() {
        if (wrappedDebugService == null) {
            return false;
        }
        return wrappedDebugService.getMockStatus();
    }

    @Override
    public void openH5Test() {

    }

    @Override
    public void addRequestData(Map<String, Object> map) {
        if (wrappedDebugService != null) {
            wrappedDebugService.addRequestData(map);
        }
    }

    @Override
    public void enable(Activity activity) {
        if (wrappedDebugService != null) {
            wrappedDebugService.enable(activity);
        }
    }

    @Override
    public void disable() {
        if (wrappedDebugService != null) {
            wrappedDebugService.disable();
        }
    }

    @Override
    public boolean isEnabled() {
        if (wrappedDebugService != null) {
            return wrappedDebugService.isEnabled();
        }
        return false;
    }

    @Override
    public void init(Context context) {
        if (wrappedDebugService != null) {
            wrappedDebugService.init(context);
        }
    }
}
