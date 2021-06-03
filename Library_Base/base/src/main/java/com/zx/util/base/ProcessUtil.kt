package com.zx.util.base

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Process
import android.text.TextUtils
import android.util.Log

/**
 *
 */
class ProcessUtil {

    companion object {

        /**
         * 获取当前进程名
         *
         * @param context
         * @return 进程名
         */
        @SuppressLint("PrivateApi", "DiscouragedPrivateApi")
        @JvmStatic
        fun getProcessName(context: Context): String? {

            var processName: String? = null
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    processName = Application.getProcessName()
                } else {
                    val declaredMethod = Class.forName(
                        "android.app.ActivityThread", false,
                        Application::class.java.classLoader
                    )
                        .getDeclaredMethod("currentProcessName")
                    declaredMethod.isAccessible = true
                    val invoke = declaredMethod.invoke(null)
                    if (invoke is String) {
                        processName = invoke
                    }
                }
                if (!TextUtils.isEmpty(processName)) {
                    return processName
                }
            } catch (ignore: Throwable) {
                ignore.printStackTrace()
                Log.e("getProcessName", "ProcessNameEmpty can't find process name ${ignore.message}")
            }

            // ActivityManager
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            while (true) {
                val runningAppProcesses = am.runningAppProcesses
                if (runningAppProcesses != null) {
                    for (info in runningAppProcesses) {
                        if (info.pid == Process.myPid()) {
                            processName = info.processName
                            break
                        }
                    }
                }

                // go home
                if (!TextUtils.isEmpty(processName)) {
                    return processName
                }

                // take a rest and again
                try {
                    Thread.sleep(100L)
                } catch (ex: InterruptedException) {
                    ex.printStackTrace()
                }
            }
        }
    }

}