/*
 * Copyright 2017 the original author or authors.
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

package org.gradle.language.internal;

import org.gradle.api.Action;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileCollection;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.tasks.util.PatternSet;
import org.gradle.language.ComponentDependencies;
import org.gradle.language.ComponentWithDependencies;
import org.gradle.language.nativeplatform.ComponentWithObjectFiles;
import org.gradle.language.nativeplatform.internal.ComponentWithNames;
import org.gradle.language.nativeplatform.internal.Names;

public abstract class DefaultNativeBinary implements ComponentWithNames, ComponentWithObjectFiles, ComponentWithDependencies {
    private final Names names;
    private final DirectoryProperty objectsDir;
    private final NativeFeature feature;
    private final DefaultComponentDependencies dependencies;

    public DefaultNativeBinary(Names names, ObjectFactory objectFactory, NativeFeature feature) {
        this.names = names;

        this.objectsDir = objectFactory.directoryProperty();
        this.feature = objectFactory.newInstance(NativeFeature.Application.class, getNames());
        this.feature.extendsFrom(feature);
        dependencies = objectFactory.newInstance(DefaultComponentDependencies.class, this.feature);
    }

    @Override
    public String getName() {
        return names.getName();
    }

    @Override
    public Names getNames() {
        return names;
    }

    public DirectoryProperty getObjectsDir() {
        return objectsDir;
    }

    @Override
    public FileCollection getObjects() {
        return objectsDir.getAsFileTree().matching(new PatternSet().include("**/*.obj", "**/*.o"));
    }

    @Override
    public ComponentDependencies getDependencies() {
        return dependencies;
    }

    public void dependencies(Action<? super ComponentDependencies> action) {
        action.execute(getDependencies());
    }

    public NativeFeature getFeature() {
        return feature;
    }

}
