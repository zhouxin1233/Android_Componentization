# Android_Componentization 

#### 介绍
{**Android组件化方案**
Android_Componentization 是自己工作几年来遇到的比较好的一种组件化方案。项目源码请看 [https://gitee.com/zhouxin1233/android_componentization](https://gitee.com/zhouxin1233/android_componentization)}

#### 软件架构
Android组件化架构
1.  壳工程:    Android_App
2.  业务模块:   Module_Home,Module_Splash,Module_Login
3.  基础View:  Library_View
4.  核心库:    Library_Core
5.  基础工具类:  Library_Base


#### 使用说明

1.  组件化实现的核心在 Gradle_Template工程中
2.  本地源码 和aar 切换 通过 module.local 文件配置， 具体可参考Android_App 根目录下module.local文件
3.  各个某块打包上传 是依赖 pom.properties配置， 本地仓库需要将 MAVEN_LOCAL_URL改成本地的文件夹路径(gradle.properties 和 各个lib
    /module 下的 pom.properties)

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request


#### 特技
1.  如果有疑问 请与我交流 微信： 1217104365
    <img src="https://gitee.com/zhouxin1233/android_componentization/raw/master/Library_View/view/src/main/res/drawable-xxhdpi/reward.jpg" />

2.  ![这是一种鼓励](https://gitee.com/zhouxin1233/android_componentization/raw/master/Library_View/view/src/main/res/drawable-xxhdpi/reward.jpg)
