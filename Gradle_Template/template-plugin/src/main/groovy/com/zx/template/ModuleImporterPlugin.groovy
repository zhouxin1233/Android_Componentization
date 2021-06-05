package com.zx.template

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencySubstitutions
import org.gradle.api.initialization.Settings
import org.gradle.api.internal.artifacts.configurations.DefaultConfiguration
import org.gradle.api.internal.artifacts.configurations.ResolutionStrategyInternal
import org.gradle.api.plugins.PluginAware

/**
 * AAR依赖与源码依赖 切换
 */
class ModuleImporterPlugin implements Plugin<PluginAware> {
    @Override
    void apply(PluginAware target) {
        def localModuleFile = new File(target.rootDir, "module.local")
        if (!localModuleFile.exists()) {
            return
        }
        // 7个你应该知道的Gradle实用技巧 https://juejin.cn/post/6947675376835362846?utm_source=gold_browser_extension
        if (target instanceof Settings) {
            println "ModuleImporterPlugin 配置本地项目"
            localModuleFile.each {
                if ("" == it || it.startsWith("//") || it.startsWith("#")) {
                    // skip
                } else {
                    String[] names = it.split("=>").collect { it.trim() }
                    if (names.length >= 2) {
                        String moduleName = names[0]
                        String path = names[1]
                        if (! new File(path).exists()) {
                            println "路径:$path 未找到"
                        }else{
                            println "找到模块:$moduleName,路径:$path"
                        }
                        String projectName = path.find("[^/\\\\]*\$")  //path.substring(path.lastIndexOf("/")+1)
                        println "ProjectName: $projectName"
                        target.include ":${projectName}"
                        target.project(":${projectName}").projectDir = new File(path)
                    }
                }
            }
        } else if (target instanceof Project) {
            println "ModuleImporterPlugin 开始替换"
            def externalDeps = [:]
            def externalDepModule = []
            localModuleFile.each {
                if ("" == it || it.startsWith("//") || it.startsWith("#")) {
                    // skip
                } else {
                    String[] names = it.split("=>").collect { it.trim() }
                    if (names.length >= 2) {
                        String moduleName = names[0]
                        String path = names[1]
                        String projectName = path.find("[^/\\\\]*\$")
                        externalDeps.put(moduleName, projectName)
                        externalDepModule.add(moduleName)
                    }
                }
            }
            target.allprojects {Project p->
                p.configurations.all { DefaultConfiguration configuration ->
                    configuration.resolutionStrategy { ResolutionStrategyInternal resolutionStrategyInternal ->
                        resolutionStrategyInternal.dependencySubstitution {DependencySubstitutions dependencySubstitutions ->
                            externalDeps.each {
//                                println "${it.key} 替换为本地项目 ${it.value}"
                                substitute module(it.key) with project(":${it.value}")
                            }
                        }
                    }
                }
            }
        }
    }
}