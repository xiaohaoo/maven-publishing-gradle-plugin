package com.xiaohaoo.gradle.plugin;

import lombok.*;

/**
 * @author xiaohao
 * @version 1.0
 * @date 2022/1/22 11:07 PM
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MavenPublishingPluginExtension {
    private String description;
    private String url;
}
