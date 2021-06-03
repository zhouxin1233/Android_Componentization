package com.zx.android_app

import android.app.Application
import com.zx.moduleinit.ModuleInitManager
import java.util.*


fun appInit(application: Application) {
//    if (LoginTools.isAcceptAgreement()) {
        ZxDelayInitManager.dispatchAppInit(application)
//    }
}

fun actionWakeUpInit(application: Application) {
//    if (!LoginTools.isAcceptAgreement()) {
        ZxDelayInitManager.dispatchAppInit(application)
//    }
}

private object ZxDelayInitManager : ModuleInitManager() {
    private val TAG = ZxDelayInitManager::class.java.simpleName

    @Volatile
    var inited = false

    init {
//        registerModuleInit(SMInit())
//        registerModuleInit(AvengerInit())
//        registerModuleInit(AnalyticsInit())
//        registerModuleInit(LoganInit())
////        registerModuleInit(PushInit())
//        registerModuleInit(UMInit())
//        registerModuleInit(IMModuleInit())
//        registerModuleInit(ChatRoomInit())
//        registerModuleInit(LiveInit())
//        registerModuleInit(MiitMdidInit())
//        registerModuleInit(MercuryInit())
//        registerModuleInit(BXVipLevelInit())
//        registerModuleInit(YppGameInit())
//        registerModuleInit(IdleInit())
    }

    override fun dispatchAppInit(application: Application?) {
        if (!inited) {
            inited = true
            super.dispatchAppInit(application)
        }
    }

    private val stagedTime: MutableMap<String, Long> = HashMap()
    private val asyncStagedTime: MutableMap<String, Long> = HashMap()

    override fun onInitStart(tag: String) {
        stagedTime[tag] = System.currentTimeMillis()
    }

    override fun onInitEnd(tag: String) {
        val costTime = System.currentTimeMillis() - stagedTime[tag]!!
        stagedTime.remove(tag)
    }

    override fun onAsynInitStart(tag: String) {
        asyncStagedTime[tag] = System.currentTimeMillis()
    }

    override fun onAsyncInitEnd(tag: String) {
        asyncStagedTime.remove(tag)
    }

}