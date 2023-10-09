## maven-publishing-gradle-plugin

### 开发说明

最近深研了Gradle的运作机制，惊叹设计巧妙！Gradle使用Groovy、Kotlin语法，相比于同宗的Java，可以说是隐晦很多，不过Gradle的可玩性比Maven高很多。基于此开发了个人使用的发布到Maven中央仓库的插件，该插件是对官方插件maven-publishing的封装，经过封装后不用再大量繁琐的配置，只需要配置仓库url和仓库描述信息即可。

### 使用说明

在需要发布的项目中，按照普通引入二进制插件的方法即可。 在build.gradle中进行配置：

- groovy

```groovy
buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
    }
    dependencies {
        classpath 'com.xiaohaoo:maven-publishing-gradle-plugin:1.0.1'
    }
}

apply plugin: 'com.xiaohaoo.maven-publishing'

//配置发布
xiaohaoMavenPublishing {
    url = "https://github.com/xiaohaoo/maven-publishing-gradle-plugin"
    description = "发布到maven仓库的Gradle插件"
}
```

- kotlin

```kotlin
import com.xiaohaoo.gradle.plugin.MavenPublishingPluginExtension

buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
    }
    dependencies {
        classpath("com.xiaohaoo:maven-publishing-gradle-plugin:1.0.1")
    }
}

apply(plugin = "com.xiaohaoo.maven-publishing")

configure<MavenPublishingPluginExtension> {
    url = "https://github.com/xiaohaoo/maven-publishing-gradle-plugin"
    description = "发布到maven仓库的Gradle插件"
}
```
