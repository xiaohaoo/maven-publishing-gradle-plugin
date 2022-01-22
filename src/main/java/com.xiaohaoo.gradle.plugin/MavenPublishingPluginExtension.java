package com.xiaohaoo.gradle.plugin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author xiaohao
 * @version 1.0
 * @date 2022/1/22 11:07 PM
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MavenPublishingPluginExtension {
    private String description;
    private String url;
}
