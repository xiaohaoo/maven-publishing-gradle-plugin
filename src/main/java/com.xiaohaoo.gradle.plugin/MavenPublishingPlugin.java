/*
 * Copyright (c) 2022 xiaohao
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.xiaohaoo.gradle.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginExtension;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.external.javadoc.MinimalJavadocOptions;
import org.gradle.plugins.signing.SigningExtension;
import org.gradle.plugins.signing.SigningPlugin;

import java.util.Map;

/**
 * @author xiaohao
 * @version 1.0
 * @date 2022/1/22 8:37 PM
 */
public class MavenPublishingPlugin implements Plugin<Project> {

    private final String publicationName = "xiaohaoMavenPublishing";

    @Override
    public void apply(Project rootProject) {

        //应用官方MavenPublishPlugin
        applyPlugins(rootProject);

        //配置发布产物
        configureJavaPluginExtension(rootProject);


        rootProject.getExtensions().create(publicationName, MavenPublishingPluginExtension.class);

        rootProject.afterEvaluate(project -> {

            //发布信息配置
            configurePublishingExtension(project);

            //配置签名
            configureSigningExtension(project);

            //配置javadoc
            configureJavadoc(project);

            project.getLogger().info("{}：自定义发布插件配置成功", getClass());

        });
    }

    /**
     * 配置官方插件maven-publishing的发布信息以及task
     * @param project 项目
     */
    public void configurePublishingExtension(Project project) {
        PublishingExtension publishingExtension = project.getExtensions().getByType(PublishingExtension.class);
        MavenPublishingPluginExtension mavenPublishingPluginExtension = project.getExtensions().getByType(MavenPublishingPluginExtension.class);
        final String name = project.getName();
        final String version = String.valueOf(project.getVersion());
        final String group = String.valueOf(project.getGroup());
        Map<String, ?> projectProperties = project.getProperties();

        //配置发布仓库
        publishingExtension.repositories(artifactRepositories -> artifactRepositories.maven(mavenArtifactRepository -> {
            final String snapshotUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/";
            final String releaseUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/";
            mavenArtifactRepository.setName("MavenCenter");
            mavenArtifactRepository.setUrl(version.endsWith("SNAPSHOT") ? snapshotUrl : releaseUrl);
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
                    final String gitUrl = mavenPublishingPluginExtension.getUrl();
                    mavenPomScm.getConnection().set(String.format("%s.git", gitUrl.replaceAll("https[s]?", "scm:git:git")));
                    mavenPomScm.getDeveloperConnection().set(String.format("%s.git", gitUrl.replaceAll("https[s]?", "scm:git:ssh")));
                    mavenPomScm.getUrl().set(mavenPublishingPluginExtension.getUrl());
                });

                project.getLogger().info("{}：mavenPublishingPluginExtension配置信息：{}", getClass(), mavenPublishingPluginExtension);

                mavenPom.licenses(mavenPomLicenseSpec -> mavenPomLicenseSpec.license(mavenPomLicense -> {
                    mavenPomLicense.getName().set("GNU AFFERO GENERAL PUBLIC LICENSE, Version 3");
                    mavenPomLicense.getUrl().set("http://www.gnu.org/licenses/agpl-3.0.txt");
                }));

                mavenPom.developers(mavenPomDeveloperSpec -> mavenPomDeveloperSpec.developer(mavenPomDeveloper -> {
                    mavenPomDeveloper.getId().set("xiaohao");
                    mavenPomDeveloper.getName().set("xiaohao");
                    mavenPomDeveloper.getEmail().set("sdwenhappy@163.com");
                    project.getLogger().info("{}：mavenPomDeveloper配置信息：{}", getClass(), mavenPomDeveloper);

                }));
            });
        });
    }

    /**
     * 配置签名信息
     * @param project {@code Project}
     */
    public void configureSigningExtension(Project project) {
        SigningExtension signingExtension = project.getExtensions().getByType(SigningExtension.class);
        PublishingExtension publishingExtension = project.getExtensions().getByType(PublishingExtension.class);
        signingExtension.sign(publishingExtension.getPublications().getByName(publicationName));
    }

    /**
     * 配置javadoc任务
     * @param project {@code Project}
     */
    public void configureJavadoc(Project project) {
        project.getTasks().withType(Javadoc.class, javadoc -> javadoc.options(MinimalJavadocOptions::quiet));
    }


    /**
     * 配置JavaPlugin
     * @param project {@code Project}
     */
    public void configureJavaPluginExtension(Project project) {
        project.getExtensions().configure(JavaPluginExtension.class, javaPluginExtension -> {
            javaPluginExtension.withJavadocJar();
            javaPluginExtension.withSourcesJar();
        });
    }


    /**
     * 启用官方插件
     * @param project {@code Project}
     */
    public void applyPlugins(Project project) {
        project.getPluginManager().apply(MavenPublishPlugin.class);
        project.getPluginManager().apply(SigningPlugin.class);
    }


}