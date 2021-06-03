package com.zx.android_app;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.zx.debugservice.DebugService;
import com.zx.environmentservice.IEnvironmentService;
import com.zx.environmentservice.util.EnvUtil;

/**
 * EnvironmentService的实现
 */
@Route(path = "/environment/service")
public class ZxEnvironmentService implements IEnvironmentService {
    private Context mContext;
    private String sAppChannel;

    @Override
    public boolean isDebug() {
        return EnvUtil.isDebug();
    }

    @Override
    public boolean isBeta() {
        if (!BuildConfig.TEST) {
            return false;
        }
        return DebugService.getInstance().isApiTest();
    }

    @Override
    public boolean isDev() {
        return BuildConfig.TEST;
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public String getVersion() {
        try {
            PackageManager packageManager = mContext.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(getContext().getPackageName(), 0);
            return packInfo.versionName;
        } catch (Exception ignored) {
        }
        return "";
    }

    @Override
    public String getAppId() {
        return "1";
    }

    @Override
    public String getDeviceId() {
        // todo
        return Settings.System.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @Override
    public String getChannelId() {
        return "zx-gw";
    }

    @Override
    public String getBuildTime() {
        return BuildConfig.BUILD_TIME + "_" + BuildConfig.ABI;
    }

    @Override
    public String getFlavor() {
        return BuildConfig.FLAVOR;
    }

    @Override
    public void init(Context context) {
        this.mContext = context.getApplicationContext();
    }
}
