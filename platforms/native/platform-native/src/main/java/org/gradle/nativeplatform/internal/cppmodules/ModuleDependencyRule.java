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

package org.gradle.nativeplatform.internal.cppmodules;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author matis
 */
public interface ModuleDependencyRule {

    @Nonnull
    String getPrimaryOutput();

    @Nonnull
    List<ModuleUnit> getProvides();

    @Nonnull
    List<Requirement> getRequires();

    interface Requirement {

        @Nonnull
        String getLogicalName();

        @Nonnull
        String getSourcePath();

    }

}
