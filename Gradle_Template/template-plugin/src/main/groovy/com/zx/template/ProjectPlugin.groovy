package com.zx.template

import com.alibaba.android.arouter.register.launch.PluginLaunch
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryPlugin
import com.zx.template.extension.ZxExtension
import com.zx.template.transform.LogTransform
import kotlin.Unit
import kotlin.jvm.functions.Function1
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.internal.AndroidExtensionsSubpluginIndicator
import org.jetbrains.kotlin.gradle.internal.Kapt3GradleSubplugin
import org.jetbrains.kotlin.gradle.plugin.KaptAnnotationProcessorOptions
import org.jetbrains.kotlin.gradle.plugin.KaptExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinAndroidPluginWrapper

import java.text.SimpleDateFormat

abstract class ProjectPlugin implements Plugin<Project>{
    abstract boolean isApplication()

    @Override
    void apply(Project project) {
        println "zhouxin Plugin start:" + project.getName() + project
        project.extensions.create("zx", ZxExtension.class)
        Class<BaseExtension> androidPlugin = isApplication() ? AppPlugin.class : LibraryPlugin.class
        project.getPlugins().apply(androidPlugin)

        BaseExtension extension =  project.extensions.getByName("android") as BaseExtension
        // 或者使用
//        BaseExtension extension = project.extensions.getByType(BaseExtension)

        extension.compileSdkVersion = 30
        extension.buildToolsVersion = "30.0.3"
        extension.defaultConfig.minSdkVersion = 19
        extension.defaultConfig.targetSdkVersion =30
        extension.buildTypes
                .getByName("release")
                .proguardFiles(extension.getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro')
                .consumerProguardFiles('proguard-rules.pro')
                .setMinifyEnabled(false)
        extension.compileOptions.sourceCompatibility = JavaVersion.VERSION_1_8
        extension.compileOptions.targetCompatibility = JavaVersion.VERSION_1_8
//        extension.buildFeatures.setViewBinding(true)


        // 升级android-gradle 4.1.1  放在配置结束阶段 会报错
        project.getPlugins().apply(KotlinAndroidPluginWrapper.class)
//        project.getPlugins().apply(AndroidExtensionsSubpluginIndicator.class)
        project.getPlugins().apply(Kapt3GradleSubplugin.class)

        //ARouter
        project.getPlugins().apply(PluginLaunch.class)

        String moduleName = project.getName()
        project.extensions.getByType(KaptExtension.class).arguments(new Function1<KaptAnnotationProcessorOptions, Unit>() {
            @Override
            Unit invoke(KaptAnnotationProcessorOptions kaptAnnotationProcessorOptions) {
                kaptAnnotationProcessorOptions.arg("AROUTER_MODULE_NAME", moduleName)
//                kaptAnnotationProcessorOptions.arg("AROUTER_GENERATE_DOC", "enable")
                return null
            }
        })

        // 放在配置结束阶段 会失效
        project.dependencies.add('kapt', 'com.alibaba:arouter-compiler:2.0.0')
        addOthersPlugin(project)

        extension.defaultConfig.javaCompileOptions.annotationProcessorOptions.argument("AROUTER_MODULE_NAME", project.getName())
//        extension.defaultConfig.javaCompileOptions.annotationProcessorOptions.argument("AROUTER_GENERATE_DOC", "enable")
//        extension.defaultConfig.javaCompileOptions.annotationProcessorOptions.arguments.put "AROUTER_GENERATE_DOC", "enable"


        project.afterEvaluate {
//            if (isApplication()) {
//                println "zhouxin logTransform2 ：${project.zx.logTransform}"
//            }

            doConfig(project, project.zx)
        }
        if (isApplication()) {
//            println "zhouxin logTransform1 ：${project.zx.logTransform}"
            // todo:zx /被去掉了
            LogTransform asmTransform = new LogTransform("${getUserInfo(project)}")
            extension.registerTransform(asmTransform)
        }
    }

    void addOthersPlugin(Project project) {

    }

    void doConfig(Project project, ZxExtension zxExtension) {

        //ARouter
        if (zxExtension.ARouter) {
            println "Project ${project.getName()} set ARouter"
            project.dependencies.add('api', 'com.alibaba:arouter-api:2.0.0')
        }

        if (zxExtension.kotlin){
            project.dependencies.add('api', 'androidx.core:core-ktx:1.5.0')
            project.dependencies.add('api', 'org.jetbrains.kotlin:kotlin-stdlib:1.5.10')
        }

    }

    String getUserInfo(Project project) {
        try {
            def userName = new ByteArrayOutputStream()
            project.exec {
                commandLine 'git', 'config', '--get', 'user.name'
                standardOutput = userName
            }
            def userEmail = new ByteArrayOutputStream()
            project.exec {
                commandLine 'git', 'config', '--get', 'user.email'
                standardOutput = userEmail
            }
            def format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
            return "开发者信息 userName : ${userName.toString().trim()}  userEmail: ${userEmail.toString().trim()} 编译时间: ${format.format(new Date())}"
        } catch (ignored) {
        }
        return "Unknown"
    }
}