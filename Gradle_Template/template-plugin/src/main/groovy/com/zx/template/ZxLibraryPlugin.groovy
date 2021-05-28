package com.zx.template

import com.zx.template.extension.ZxExtension
import org.gradle.api.Project
import org.gradle.api.plugins.MavenPlugin
import org.gradle.api.tasks.bundling.Jar

class ZxLibraryPlugin extends ProjectPlugin{
    @Override
    boolean isApplication() {
        return false
    }

    @Override
    void doConfig(Project project, ZxExtension extension) {
        super.doConfig(project, extension)
        configUploadArchives(project)
    }

    void configUploadArchives(Project project) {
        def outFile = new File("${project.projectDir}/pom.properties")
        if (!outFile.exists()) {
            return
        }
        println("Found pom.properties")
        // todo:zx
        project.getPlugins().apply(MavenPlugin.class)
        Properties pomProps = new Properties()
        pomProps.load(new FileInputStream("${project.projectDir}/pom.properties"))
        project.uploadArchives {
            repositories {
                mavenDeployer {
                    repository(url: pomProps.url) {
                        authentication(userName: pomProps.username, password: pomProps.password)
                    }

                    pom.groupId = pomProps.groupId
                    pom.artifactId = pomProps.artifactId
                    pom.version = pomProps.version
                    pom.project {
                        description 'git rev-parse HEAD'.execute([], project.projectDir).text.trim()
                    }
                }
            }
        }
        def task = project.tasks.create("androidSourcesJar", Jar)
        task.getArchiveClassifier().convention("sources" )
        task.getArchiveClassifier().set("sources" )
//        task.classifier = "sources"  // todo:zx
        task.from(project.android.sourceSets.main.java.srcDirs)
        project.artifacts {
            archives task
        }
    }
}