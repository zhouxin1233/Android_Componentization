package com.zx.module_splash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
@Route(path = "/launch/splash")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    fun skipHomePage(view: View) {
        ARouter.getInstance().build("/home/entry").navigation()
        finish()
    }

    fun skipLoginPage(view: View) {
        ARouter.getInstance().build("/login/entry").navigation()
        finish()
    }
}