/*
 * Copyright 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.nativeplatform.internal;

import org.gradle.nativeplatform.ConfigurableLinkableDependencySpec;
import org.gradle.nativeplatform.LinkableObjectLinkType;

/**
 * @author matis
 */
public final class DefaultConfigurableLinkableDependencySpec implements ConfigurableLinkableDependencySpec {

    private final String name;
    private boolean wholeArchive;
    private boolean framework;
    private LinkableObjectLinkType linkType;

    public DefaultConfigurableLinkableDependencySpec(String name) {
        this.name = name;
        this.wholeArchive = false;
        this.framework = false;
        this.linkType = LinkableObjectLinkType.STRONG;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setWholeArchive(boolean wholeArchive) {
        this.wholeArchive = wholeArchive;
    }

    @Override
    public boolean isWholeArchive() {
        return wholeArchive;
    }

    @Override
    public void setLinkType(LinkableObjectLinkType linkType) {
        this.linkType = linkType;
    }

    @Override
    public LinkableObjectLinkType getLinkType() {
        return linkType;
    }

    @Override
    public boolean isFramework() {
        return framework;
    }

    public void setFramework(boolean isFramework) {
        this.framework = isFramework;
    }

}
