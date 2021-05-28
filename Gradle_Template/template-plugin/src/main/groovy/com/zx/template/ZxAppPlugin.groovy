package com.zx.template

import com.zx.template.extension.ZxExtension
import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.component.ComponentIdentifier
import org.gradle.api.artifacts.component.ComponentSelector
import org.gradle.api.artifacts.result.DependencyResult
import org.gradle.api.artifacts.result.ResolutionResult
import org.gradle.api.artifacts.result.ResolvedComponentResult
import org.gradle.api.artifacts.result.ResolvedDependencyResult
import org.gradle.api.artifacts.result.UnresolvedDependencyResult
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.api.tasks.TaskAction

class ZxAppPlugin extends ProjectPlugin {

    static class MyPrinterTask extends DefaultTask {
        // todo:zx
        def outFile = new File(getProject().rootDir, 'depsTree.txt')

        @TaskAction
        void walk() {
            if (outFile.exists()) {
                outFile.delete()
                outFile.createNewFile()
            }
            // todo:zx 这里输出是啥  这里走了吗
            Configuration configuration = null
            if ("none" == getProject().zx.deps) {
                return
            } else if (getProject().zx.deps != null) {
                configuration = getProject().configurations[getProject().zx.deps + "ReleaseRuntimeClasspath"]
            } else {
                project.configurations.each {
                    if (it.getName().toLowerCase().contains("releaseRuntimeClasspath".toLowerCase())) {
                        configuration = it
                    }
                }
            }
            configuration.canBeResolved = true
            configuration.incoming.afterResolve {
                logger.quiet 'afterResolve'
            }
            ResolutionResult resolutionResult = configuration.incoming.resolutionResult
            ResolvedComponentResult root = resolutionResult.root
            traverseDependencies(0, root.dependencies)
            logger.quiet 'walk out'
        }

        private void traverseDependencies(int level, Set<? extends DependencyResult> results) {
            for (DependencyResult result : results) {
                if (result instanceof ResolvedDependencyResult) {
                    ResolvedComponentResult componentResult = result.selected
                    ComponentIdentifier componentIdentifier = componentResult.id
                    String node = calculateIndentation(level, componentResult.dependencies.size() > 0) + "$componentIdentifier.displayName ($componentResult.selectionReason)"
                    logger.quiet node
                    outFile << node + '\n\n'
                    if (!componentIdentifier.displayName.startsWith("com.android.support")) {
                        traverseDependencies(level + 1, componentResult.dependencies)
                    }
                } else if (result instanceof UnresolvedDependencyResult) {
                    ComponentSelector componentSelector = result.attempted
                    String node = calculateIndentation(level) + "$componentSelector.displayName (failed)"
                    logger.quiet node
                    outFile << node + '\n\n'
                }
            }
        }

        private static String calculateIndentation(int level, boolean haveDeps) {
            return '|   ' * (level > 0 ? (level - 1) : 0) + (haveDeps ? "+---" : "|   ")
        }
    }

    @Override
    boolean isApplication() {
        return true
    }

    @Override
    void apply(Project project) {
        super.apply(project)
        project.tasks.create('printDependencyList', MyPrinterTask.class)
        project.gradle.addBuildListener(new BuildListener() {
            @Override
            void buildStarted(Gradle gradle) {

            }

            @Override
            void settingsEvaluated(Settings settings) {

            }

            @Override
            void projectsLoaded(Gradle gradle) {

            }

            @Override
            void projectsEvaluated(Gradle gradle) {

            }

            @Override
            void buildFinished(BuildResult result) {
                if (result.failure == null) {
                    Configuration configuration = null
                    //  todo:zx 能找到 ReleaseRuntimeClasspath ？
                    if ("none" == project.zx.deps) {
                        return
                    } else if (project.zx.deps != null) {
                        configuration = project.configurations[project.zx.deps + "ReleaseRuntimeClasspath"]
                    } else {
                        project.configurations.each {
                            if (it.getName().toLowerCase().contains("releaseRuntimeClasspath".toLowerCase())) {
                                configuration = it
                            }
                        }
                    }
                    if (configuration == null) {
                        println "无法输出deps.txt,请尝试关闭instant run后再试"
                        return
                    }
                    println "输出依赖项开始"
                    try {
                        ResolutionResult resolutionResult = configuration.incoming.resolutionResult
                        new File(project.rootDir, 'deps.txt').delete()
                        File depsFile = new File(project.rootDir, 'deps.txt')
                        def tmpList = []
                        resolutionResult.allComponents.sort().each {
                            tmpList += it.toString()
                        }
                        tmpList.sort()
                        tmpList.each {
                            depsFile << "${it}\n\n"
                        }
                    } catch (Throwable t) {
                        t.printStackTrace()
                        println "输出依赖错误，e=" + t.getLocalizedMessage()
                    }
                    println "输出依赖项结束"
                }
            }
        })
    }

    @Override
    void addOthersPlugin(Project project) {
        super.addOthersPlugin(project)
        //todo:zx 添加 tracker-plugin
//        project.getPlugins().apply(TrackerPlugin.class)
    }

    @Override
    void doConfig(Project project, ZxExtension extension) {
        super.doConfig(project, extension)

        // todo:zx tracker plugin
//        AutoSettingParams autoSettingParams = project.extensions.findByName("YppTracker")
//        autoSettingParams.isOpenTracker = yppExtension.tracker
    }
}