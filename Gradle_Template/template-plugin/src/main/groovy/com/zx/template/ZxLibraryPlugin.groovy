package com.zx.template

import com.zx.template.extension.ZxExtension
import org.gradle.api.Project
import org.gradle.api.component.SoftwareComponent
import org.gradle.api.plugins.MavenPlugin
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.tasks.bundling.Jar

class ZxLibraryPlugin extends ProjectPlugin {
    private Project mProject = null

    @Override
    boolean isApplication() {
        return false
    }

    @Override
    void doConfig(Project project, ZxExtension extension) {
        super.doConfig(project, extension)
        mProject = project
        configUploadArchives(project)
    }

    void configUploadArchives(Project project) {
        def outFile = new File("${project.projectDir}/pom.properties")
        if (!outFile.exists()) {
            return
        }
        println("Found pom.properties")
        // todo:zx
        project.getPlugins().apply(MavenPublishPlugin.class)
        PublishingExtension publishing = project.extensions.getByType(PublishingExtension)

        Properties pomProps = new Properties()
        pomProps.load(new FileInputStream("${project.projectDir}/pom.properties"))

        def sourceJar = project.tasks.create("androidSourcesJar", Jar)
        sourceJar.getArchiveClassifier().convention("sources")
        sourceJar.getArchiveClassifier().set("sources")
//        task.classifier = "sources"
        sourceJar.from(project.android.sourceSets.main.java.srcDirs)
//        project.artifacts {
//            archives sourceJar
//        }

        project.afterEvaluate {
            for (SoftwareComponent components : project.components) {
                publishing.publications({ publications ->
                    publications.create(components.name, MavenPublication.class, { MavenPublication publication ->
                        project.group = pomProps.groupId
                        publication.artifact(sourceJar)

                        publication.groupId = pomProps.groupId
                        publication.artifactId = pomProps.artifactId
                        publication.version = pomProps.version
                        publication.from(components)
                        // todo:zx
                        publication.pom { mavenPom ->
                            mavenPom.name = pomProps.username
                            mavenPom.description = 'git rev-parse HEAD'.execute([], project.projectDir).text.trim()
                        }
                    })
                })
            }

            publishing.repositories { artifactRepositories ->
                artifactRepositories.maven { mavenArtifactRepository ->

                    if (pomProps.IS_LOCAL_MAVEN.toBoolean()) {
                        mavenArtifactRepository.url = pomProps.MAVEN_LOCAL_URL
                    } else {
                        mavenArtifactRepository.url = pomProps.MAVEN_URL
                        mavenArtifactRepository.credentials {
                            credentials ->
                                credentials.username = pomProps.username
                                credentials.password = pomProps.password
                        }
                    }
                }
            }
        }
//        project.uploadArchives {
//            repositories {
//                mavenDeployer {
//                    repository(url: pomProps.url) {
//                        authentication(userName: pomProps.username, password: pomProps.password)
//                    }
//
//                    pom.groupId = pomProps.groupId
//                    pom.artifactId = pomProps.artifactId
//                    pom.version = pomProps.version
//                    pom.project {
//                        description 'git rev-parse HEAD'.execute([], project.projectDir).text.trim()
//                    }
//                }
//            }
//        }
//

    }
}