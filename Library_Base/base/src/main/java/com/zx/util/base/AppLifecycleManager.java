package com.zx.util.base;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * @Description: 提供APP的全局生命周期管理, 需要初始化才能正常使用
 */
public class AppLifecycleManager {
    private static final String TAG = AppLifecycleManager.class.getSimpleName();
    private WeakReference<Activity> mActivityRef;
    private Stack<Activity> mActivityStack = new Stack<>();
    private int mForegroundActivityCount = 0;

    private AppLifecycleManager() {
    }


    private static class Inner {
        private static AppLifecycleManager sInstance = new AppLifecycleManager();
    }

    public static AppLifecycleManager getInstance() {
        return Inner.sInstance;
    }

    /**
     * 初始化方法，需要在Application onCreate中调用
     *
     * @param application 应用当前的Application实例
     */
    public void init(Application application) {
        application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                mActivityStack.push(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {
                if (mForegroundActivityCount == 0) {
                    dispatchOnForeground();
                }
                mForegroundActivityCount++;
            }

            @Override
            public void onActivityResumed(Activity activity) {
                mActivityRef = new WeakReference<>(activity);
                if (mActivityRef.get() != mActivityStack.peek()) {
                    Log.e(TAG, "Should Not like this!!Pls Check this situation!!");
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                mForegroundActivityCount--;
                if (mForegroundActivityCount < 0) {
                    mForegroundActivityCount = 0;
                    Log.e(TAG, "Should Not like this!!Pls Check this situation!!");
                }
                if (mForegroundActivityCount == 0) {
                    dispatchOnBackground();
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                mActivityStack.remove(activity);
            }
        });
    }

    /**
     * 获取当前在显示的Activity实例
     *
     * @return 当前显示在前台的Activity
     */
    public Activity getPresentActivity() {
        if (mActivityRef != null) {
            return mActivityRef.get();
        }
        return null;
    }

    /**
     * @return 将当前所有的Activity作为list输出返回
     */
    public List<Activity> getAllActivities() {
        return new LinkedList<>(mActivityStack);
    }

    /**
     * finish 栈中的除了参数之外的所有Activity
     *
     * @param clazz 需要保留的Activity
     */
    public void clearActivities(Class clazz) {
        if (clazz == null) {
            dispatchOnExit();
        }
        synchronized (this) {
            for (Activity activity : mActivityStack) {
                if (activity == null) {
                    continue;
                }
                if (clazz != null && TextUtils.equals(clazz.getSimpleName(), activity.getClass().getSimpleName())) {
                    continue;
                }
                if (!activity.isFinishing()) {
                    activity.finish();
                }
            }
        }
    }

    private Set<AppStatusListener> mListeners = new HashSet<>();

    private Handler mUIHandler = new Handler(Looper.getMainLooper());

    /**
     * 注册APP状态监听器
     *
     * @param listener APP状态监听
     */
    public void registerAppStatusListener(final AppStatusListener listener) {
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                mListeners.add(listener);
            }
        });
    }

    /**
     * 取消注册APP状态监听器
     *
     * @param listener APP状态监听
     */
    public void unregisterAppStatusListener(final AppStatusListener listener) {
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                mListeners.remove(listener);
            }
        });
    }

    private void dispatchOnForeground() {
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                for (AppStatusListener listener : mListeners) {
                    listener.onForeground();
                }
            }
        });
    }

    private void dispatchOnBackground() {
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                for (AppStatusListener listener : mListeners) {
                    listener.onBackground();
                }
            }
        });
    }

    private void dispatchOnExit() {
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                for (AppStatusListener listener : mListeners) {
                    listener.onExit();
                }
            }
        });
    }

    public boolean isInForeground() {
        return mForegroundActivityCount > 0;
    }

    /**
     * APP状态监听接口
     */
    public interface AppStatusListener {
        /**
         * App切换到前台
         */
        void onForeground();

        /**
         * App切换到后台
         */
        void onBackground();

        /**
         * 清栈
         */
        void onExit();
    }
}
