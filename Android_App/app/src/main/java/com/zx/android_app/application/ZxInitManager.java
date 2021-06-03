package com.zx.android_app.application;

import com.zx.android_app.application.init.AppUtilInit;
import com.zx.android_app.application.init.RouterInit;
import com.zx.android_app.application.init.RxJavaInit;
import com.zx.moduleinit.ModuleInitManager;

import java.util.HashMap;
import java.util.Map;

public class ZxInitManager extends ModuleInitManager {

    private static final String TAG = ZxInitManager.class.getSimpleName();

    private final Map<String, Long> stagedTime = new HashMap<>();
    private final Map<String, Long> asyncStagedTime = new HashMap<>();

    public ZxInitManager() {
//        registerModuleInit(new SoLoaderInit());
        registerModuleInit(new RouterInit());
        registerModuleInit(new RxJavaInit());
        registerModuleInit(new AppUtilInit());
//        registerModuleInit(new CrashReportInit());
//        registerModuleInit(new NetModuleInit());
//        registerModuleInit(new AsyncSharedPreferenceInit());
//        registerModuleInit(new WebViewInit());
//        registerModuleInit(new H5Init());
//        registerModuleInit(new YXIMConfigInit());
//        registerModuleInit(new FastModuleInit());
////        registerModuleInit(new IMModuleInit());
//        registerModuleInit(new GameInit());
//        registerModuleInit(new UMPreInit());
    }

    @Override
    public void onInitStart(String tag) {
        stagedTime.put(tag, System.currentTimeMillis());
    }

    @Override
    public void onInitEnd(String tag) {
        Long startTime = stagedTime.get(tag);
        if (startTime != null) {
            long costTime = System.currentTimeMillis() - startTime;
            stagedTime.remove(tag);
        }
    }

    @Override
    public void onAsynInitStart(String tag) {
        asyncStagedTime.put(tag, System.currentTimeMillis());
    }

    @Override
    public void onAsyncInitEnd(String tag) {
        asyncStagedTime.remove(tag);
    }
}
