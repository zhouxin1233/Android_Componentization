package com.zx.environmentservice;

import android.content.Context;

import com.alibaba.android.arouter.facade.template.IProvider;

/**
 * APP环境服务接口
 */
public interface IEnvironmentService extends IProvider {
    /**
     * @return APP当前运行环境是否是Debug模式，与打包方式相关
     */
    boolean isDebug();

    /**
     * @return APP当前是否是beta环境，API环境相关
     */
    boolean isBeta();

    /**
     * @return APP当前运行环境是否是内部可切环境调试模式
     */
    boolean isDev();

    /**
     * @return 返回当前APP的全局Context
     */
    Context getContext();

    /**
     * @return 返回APP版本号
     */
    String getVersion();

    /**
     * @return 返回APP的唯一ID
     */
    String getAppId();

    /**
     * @return 返回设备唯一ID
     */
    String getDeviceId();

    /**
     * @return 返回渠道ID
     */
    String getChannelId();

    /**
     * 获取当前编译时间
     *
     * @return 编译时间
     */
    default String getBuildTime() {
        return String.valueOf(System.currentTimeMillis());
    }
}
