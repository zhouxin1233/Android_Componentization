package com.zx.android_app.application.init

import android.app.Application
import android.util.Log
import com.zx.environmentservice.EnvironmentService
import com.zx.moduleinit.ModuleInit
import io.reactivex.plugins.RxJavaPlugins

class RxJavaInit :ModuleInit() {
    override fun tag(): String {
        return "RxJava"
    }

    override fun init(application: Application?) {
        setRxJavaErrorHandler()
    }

    override fun asyncInit(application: Application?) {
    }
    private fun setRxJavaErrorHandler() {
        /*
         * RxJava2 当取消订阅后(dispose())，RxJava抛出的异常后续无法接收(此时后台线程仍在跑，可能会抛出IO等异常),全部由RxJavaPlugin接收，需要提前设置ErrorHandler
         * https://github.com/ReactiveX/RxJava/wiki/What's-different-in-2.0#error-handling
         */
        RxJavaPlugins.setErrorHandler { throwable: Throwable? ->
            if (throwable != null && EnvironmentService.getInstance().isDebug) {
                val msg = Log.getStackTraceString(throwable)
//                throwable.printStackTrace()
                Log.e(tag(),"Rxjava默认异常,具体看Log:$msg ")
            }
        }
    }
}