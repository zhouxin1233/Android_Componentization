# Android_Componentization 

#### 介绍
{**Android组件化方案：**
Android_Componentization 是自己工作几年来遇到的比较好的一种组件化方案。}

#### 软件架构
Android组件化架构
1.  壳工程:    Android_App
2.  业务模块:   Module_Home,Module_Splash,Module_Login
3.  基础View:  Library_View
4.  核心库:    Library_Core
5.  基础工具类:  Library_Base

组件化实现的核心在 Gradle_Template工程中


#### 使用说明

1.  导入Gradle_Template，将gradle.properties 和 pom.properties 中 mavenLocalUrl 修改成你自己的本地仓库地址
2.  生成组件化plugin：编译Gradle_Template工程 上传组件化plugin到本地仓库
3.  使用组件化plugin：本地源码 和aar 切换 通过 module.local 文件配置， 具体可参考Android_App 根目录下module.local文件

各个模块打包上传 是依赖 pom.properties配置 实现的

#### 感谢
  如果有疑问 请与我交流 微信： 1217104365

  <img src="https://gitee.com/zhouxin1233/android_componentization/raw/master/Library_View/view/src/main/res/drawable-xxhdpi/reward.jpg" alt="这是一种鼓励" width="300" height="300" align="bottom" />
