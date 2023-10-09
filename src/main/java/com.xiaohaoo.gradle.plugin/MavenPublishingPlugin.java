/*
 * Copyright (c) 2022-2023 xiaohao
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

import org.gradle.api.JavaVersion;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.provider.Property;
import org.gradle.api.publish.PublishingExtension;
import org.gradle.api.publish.maven.MavenPublication;
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.external.javadoc.StandardJavadocDocletOptions;
import org.gradle.plugins.signing.SigningExtension;
import org.gradle.plugins.signing.SigningPlugin;

import java.util.Map;

/**
 * @author xiaohao
 * @version 1.0
 * @date 2022/1/22 8:37 PM
 */
public class MavenPublishingPlugin implements Plugin<Project> {

    private final String defaultPublicationName = "xiaohaoMavenPublishing";

    @Override
    public void apply(final Project rootProject) {
        //创建自定义的MavenPublishingPluginExtension
        rootProject.getExtensions().create(defaultPublicationName, MavenPublishingPluginExtension.class);
        //应用官方MavenPublishPlugin
        applyPlugins(rootProject);

        rootProject.afterEvaluate(this::configurePublishing);
    }

    private void configurePublishing(Project project) {
        PublishingExtension publishingExtension = project.getExtensions().getByType(PublishingExtension.class);
        MavenPublishingPluginExtension mavenPublishingPluginExtension = project.getExtensions().getByType(MavenPublishingPluginExtension.class);
        final String name = project.getName();
        final String version = String.valueOf(project.getVersion());
        final String group = String.valueOf(project.getGroup());

        configureRepositories(project);

        publishingExtension.publications(publications -> {
            MavenPublication mavenPublication = publications.create(defaultPublicationName, MavenPublication.class);
            mavenPublication.setGroupId(group);
            mavenPublication.setArtifactId(name);
            mavenPublication.setVersion(version);
            configurePackaging(project, mavenPublication);
            configurePom(project, mavenPublication);
        });

        configureSigning(project);
        configureJavadoc(project);
        project.getLogger().info("{}: 自定义发布插件配置成功", getClass());
    }

    private void configureRepositories(Project project) {
        final String version = String.valueOf(project.getVersion());
        Map<String, ?> projectProperties = project.getProperties();
        PublishingExtension publishingExtension = project.getExtensions().getByType(PublishingExtension.class);

        publishingExtension.repositories(artifactRepositories -> artifactRepositories.maven(mavenArtifactRepository -> {
            final String snapshotUrl = "https://s01.oss.sonatype.org/content/repositories/snapshots/";
            final String releaseUrl = "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/";
            mavenArtifactRepository.setName("MavenCenter");
            mavenArtifactRepository.setUrl(version.endsWith("SNAPSHOT") ? snapshotUrl : releaseUrl);
            mavenArtifactRepository.credentials(passwordCredentials -> {
                passwordCredentials.setUsername(projectProperties.get("ossrhUsername").toString());
                passwordCredentials.setPassword(projectProperties.get("ossrhPassword").toString());
            });
        }));
    }

    private void configurePackaging(Project project, MavenPublication mavenPublication) {
        MavenPublishingPluginExtension mavenPublishingPluginExtension = project.getExtensions().getByType(MavenPublishingPluginExtension.class);
        Property<String> component = mavenPublishingPluginExtension.getComponent().convention("java");
        mavenPublication.from(project.getComponents().getByName(component.get()));
    }

    private void configurePom(Project project, MavenPublication mavenPublication) {
        final String name = project.getName();
        MavenPublishingPluginExtension mavenPublishingPluginExtension = project.getExtensions().getByType(MavenPublishingPluginExtension.class);
        mavenPublication.pom(mavenPom -> {
            mavenPom.getName().set(name);
            mavenPom.getDescription().set(mavenPublishingPluginExtension.getDescription());
            mavenPom.getUrl().set(mavenPublishingPluginExtension.getUrl());
            mavenPom.scm(mavenPomScm -> {
                final String gitUrl = mavenPublishingPluginExtension.getUrl().get();
                mavenPomScm.getConnection().set(String.format("%s.git", gitUrl.replaceAll("httpss?", "scm:git:")));
                mavenPomScm.getDeveloperConnection().set(String.format("%s.git", gitUrl));
                mavenPomScm.getUrl().set(gitUrl);
            });
            mavenPom.licenses(licenses -> licenses.license(license -> {
                license.getName().set("GNU AFFERO GENERAL PUBLIC LICENSE, Version 3");
                license.getUrl().set("http://www.gnu.org/licenses/agpl-3.0.txt");
            }));
            mavenPom.developers(developers -> developers.developer(developer -> {
                developer.getId().set("xiaohao");
                developer.getName().set("xiaohao");
                developer.getEmail().set("sdwenhappy@163.com");
            }));
        });
    }

    private void configureSigning(Project project) {
        project.getPlugins().withType(SigningPlugin.class, signingPlugin -> {
            SigningExtension signingExtension = project.getExtensions().getByType(SigningExtension.class);
            PublishingExtension publishingExtension = project.getExtensions().getByType(PublishingExtension.class);
            signingExtension.sign(publishingExtension.getPublications());
        });
    }

    private void configureJavadoc(Project project) {
        if (JavaVersion.VERSION_1_8.compareTo(JavaVersion.current()) <= 0) {
            project.getTasks().withType(Javadoc.class, javadoc -> javadoc.options(minimalJavadocOptions -> {
                if (minimalJavadocOptions instanceof StandardJavadocDocletOptions) {
                    minimalJavadocOptions.setEncoding("UTF-8");
                    ((StandardJavadocDocletOptions) minimalJavadocOptions).addStringOption("Xdoclint:none", "-quiet");
                }
            }));
        }
    }

    private void applyPlugins(Project project) {
        project.getPlugins().apply(MavenPublishPlugin.class);
        project.getPlugins().apply(SigningPlugin.class);
    }
}