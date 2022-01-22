package com.xiaohaoo.gradle.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;
import org.gradle.plugins.signing.SigningExtension;
import org.gradle.plugins.signing.SigningPlugin;

import java.util.Map;

/**
 * @author xiaohao
 * @version 1.0
 * @date 2022/1/22 8:37 PM
 */
public class MavenPublishingPlugin implements Plugin<Project> {
    @Override
    public void apply(Project rootProject) {
        rootProject.afterEvaluate(project -> {


            final String name = project.getName();
            final String version = String.valueOf(project.getVersion());
            final String group = String.valueOf(project.getGroup());
            Map<String, ?> projectProperties = project.getProperties();

            //配置官方发布插件中的名字
            final String publicationName = "xiaohaoMavenPublishing";


            //应用官方MavenPublishPlugin
            project.getPluginManager().apply(MavenPublishPlugin.class);
            project.getPluginManager().apply(SigningPlugin.class);

            //配置发布产物
            project.getExtensions().configure(JavaPluginExtension.class, javaPluginExtension -> {
                javaPluginExtension.withJavadocJar();
                javaPluginExtension.withJavadocJar();
            });

            //发布信息配置
            PublishingExtension publishingExtension = project.getExtensions().getByType(PublishingExtension.class);

            //自定义配置发布信息
            MavenPublishingPluginExtension mavenPublishingPluginExtension = project.getExtensions().create("xiaohaoMavenPublishing",
                MavenPublishingPluginExtension.class);

            //配置发布仓库
            publishingExtension.repositories(artifactRepositories -> artifactRepositories.maven(mavenArtifactRepository -> {
                final String snapshotUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/";
                final String releaseUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/";
                mavenArtifactRepository.artifactUrls(version.endsWith("SNAPSHOT") ? snapshotUrl : releaseUrl);
                mavenArtifactRepository.credentials(passwordCredentials -> {
                    passwordCredentials.setUsername(String.valueOf(projectProperties.get("ossrhUsername")));
                    passwordCredentials.setPassword(String.valueOf(projectProperties.get("ossrhPassword")));
                });
            }));

            //配置发布信息
            publishingExtension.publications(publications -> {
                MavenPublication mavenPublishing = publications.create(publicationName, MavenPublication.class);
                mavenPublishing.setGroupId(group);
                mavenPublishing.setArtifactId(name);
                mavenPublishing.setVersion(version);

                mavenPublishing.from(project.getComponents().getByName("java"));
                mavenPublishing.pom(mavenPom -> {
                    mavenPom.getName().set(name);
                    mavenPom.getDescription().set(mavenPublishingPluginExtension.getDescription());
                    mavenPom.getUrl().set(mavenPublishingPluginExtension.getUrl());
                    mavenPom.scm(mavenPomScm -> {
                        mavenPomScm.getConnection().set(mavenPublishingPluginExtension.getUrl());
                        mavenPomScm.getDeveloperConnection().set(mavenPublishingPluginExtension.getUrl());
                        mavenPomScm.getUrl().set(mavenPublishingPluginExtension.getUrl());
                    });

                    mavenPom.licenses(mavenPomLicenseSpec -> mavenPomLicenseSpec.license(mavenPomLicense -> {
                        mavenPomLicense.getName().set("GNU AFFERO GENERAL PUBLIC LICENSE, Version 3");
                        mavenPomLicense.getUrl().set("http://www.gnu.org/licenses/agpl-3.0.txt");
                    }));

                    mavenPom.developers(mavenPomDeveloperSpec -> mavenPomDeveloperSpec.developer(mavenPomDeveloper -> {
                        mavenPomDeveloper.getId().set("xiaohao");
                        mavenPomDeveloper.getName().set("xiaohao");
                        mavenPomDeveloper.getEmail().set("sdwenhappy@163.com");
                    }));
                });
            });

            //配置签名
            project.getExtensions().configure(SigningExtension.class,
                signingExtension -> signingExtension.sign(publishingExtension.getPublications().getByName(publicationName)));
        });
    }
}