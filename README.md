## maven-publishing-gradle-plugin

### 开发说明

最近深研了gradle的运作机制，惊叹设计巧妙！gradle使用groovy语法，相比于同宗的Java，可以说是隐晦很多，不过gradle的可玩性比maven高很多。基于此开发了个人使用的发布到maven中央仓库的插件，该插件是对官方插件maven-publishing的封装，经过封装后不用再大量繁琐的配置，只需要配置仓库url和仓库描述信息即可。

### 使用说明

在需要发布的项目中，按照普通引入二进制插件的方法即可。 在build.gradle中进行配置：

```gradle
buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
    }
    dependencies {
        classpath 'com.xiaohaoo:maven-publishing-gradle-plugin:1.0'
    }
}

apply plugin: 'com.xiaohaoo.maven-publishing'

//发布配置
xiaohaoMavenPublishing {
    url = "https://github.com/xiaohaoo/maven-publishing-gradle-plugin"
    description = "发布到maven仓库的gradle插件"
}

```
