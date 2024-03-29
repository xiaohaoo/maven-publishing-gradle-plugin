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

import org.gradle.api.provider.Property;

/**
 * @author xiaohao
 * @version 1.0
 * @since 2022/1/22 11:07 PM
 */

public interface MavenPublishingPluginExtension {
    Property<String> getUrl();

    //项目的描述
    Property<String> getDescription();

    Property<String> getComponent();
}
