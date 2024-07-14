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
import java.io.File;
import java.util.List;

/**
 * @author matis
 */
public interface ModuleSchema {

    @Nonnull
    List<ModuleDependencyRule> getRules();

    /**
     * @throws java.util.NoSuchElementException if no unit matches the given logical name.
     */
    ModuleUnit getUnitByLogicalName(String logicalName);

    /**
     * @throws java.util.NoSuchElementException if no unit matches the given source file.
     */
    ModuleUnit getUnitForSourceFile(File file);

    /**
     * @throws java.util.NoSuchElementException if no unit matches the given output file.
     */
    ModuleDependencyRule getRuleForOutputFile(File file);

}
