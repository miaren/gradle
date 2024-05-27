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

package org.gradle.language.internal;

import com.google.common.annotations.VisibleForTesting;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.internal.artifacts.configurations.ConfigurationRolesForMigration;
import org.gradle.api.internal.artifacts.configurations.RoleBasedConfigurationContainerInternal;
import org.gradle.language.nativeplatform.internal.Names;

import javax.annotation.Nonnull;
import javax.inject.Inject;

/**
 * @author matis
 */
public class NativeFeature {

    // Dependency configurations
    private final Configuration implementation;
    private final Configuration linkOnly;
    private final Configuration runtimeOnly;

    // Resolvable configurations
    private final Configuration includePath;
    private final Configuration runtimePath;
    private final Configuration linktimePath;

    // Outgoing variants
    private final Configuration apiElements;
    private final Configuration runtimeElements;
    private final Configuration linktimeElements;

    private NativeFeature(RoleBasedConfigurationContainerInternal configurations, Names names) {
        apiElements = configurations.migratingUnlocked(names.withSuffix("apiElements"), ConfigurationRolesForMigration.CONSUMABLE_DEPENDENCY_SCOPE_TO_CONSUMABLE);
        runtimeElements = configurations.migratingUnlocked(names.withSuffix("runtimeElements"), ConfigurationRolesForMigration.CONSUMABLE_DEPENDENCY_SCOPE_TO_CONSUMABLE);
        linktimeElements = configurations.migratingUnlocked(names.withSuffix("linktimeElements"), ConfigurationRolesForMigration.CONSUMABLE_DEPENDENCY_SCOPE_TO_CONSUMABLE);

        @SuppressWarnings("deprecation")
        Configuration includePath = configurations.resolvableDependencyScopeUnlocked(names.withSuffix("includePath"));
        this.includePath = includePath;
        @SuppressWarnings("deprecation")
        Configuration runtimePath = configurations.resolvableDependencyScopeUnlocked(names.withSuffix("runtimePath"));
        this.runtimePath = runtimePath;
        @SuppressWarnings("deprecation")
        Configuration linktimePath = configurations.resolvableDependencyScopeUnlocked(names.withSuffix("linktimePath"));
        this.linktimePath = linktimePath;

        implementation = configurations.dependencyScopeUnlocked(names.withSuffix("implementation"));
        includePath.extendsFrom(implementation);
        linktimePath.extendsFrom(implementation);
        linktimeElements.extendsFrom(implementation);
        runtimePath.extendsFrom(implementation);
        runtimeElements.extendsFrom(implementation);

        linkOnly = configurations.dependencyScopeUnlocked(names.withSuffix("linkOnly"));
        includePath.extendsFrom(linkOnly);
        linktimePath.extendsFrom(linkOnly);
        linktimeElements.extendsFrom(linkOnly);

        runtimeOnly = configurations.dependencyScopeUnlocked(names.withSuffix("runtimeOnly"));
        runtimePath.extendsFrom(runtimeOnly);
        runtimeElements.extendsFrom(runtimeOnly);
    }

    @Nonnull
    public Configuration getImplementation() {
        return implementation;
    }

    @Nonnull
    public Configuration getLinkOnly() {
        return linkOnly;
    }

    @Nonnull
    public Configuration getRuntimeOnly() {
        return runtimeOnly;
    }

    @Nonnull
    public Configuration getIncludePath() {
        return includePath;
    }

    @Nonnull
    public Configuration getRuntimePath() {
        return runtimePath;
    }

    @Nonnull
    public Configuration getLinktimePath() {
        return linktimePath;
    }

    @Nonnull
    public Configuration getApiElements() {
        return apiElements;
    }

    @Nonnull
    public Configuration getRuntimeElements() {
        return runtimeElements;
    }

    public Configuration getLinktimeElements() {
        return linktimeElements;
    }

    public void extendsFrom(@Nonnull NativeFeature nativeFeature) {
        implementation.extendsFrom(nativeFeature.implementation);
        linkOnly.extendsFrom(nativeFeature.linkOnly);
        runtimeOnly.extendsFrom(nativeFeature.runtimeOnly);
    }

    @VisibleForTesting
    static NativeFeature create(@Nonnull RoleBasedConfigurationContainerInternal configurations) {
        return create(configurations, Names.of("main"));
    }

    @VisibleForTesting
    static NativeFeature create(@Nonnull RoleBasedConfigurationContainerInternal configurations, @Nonnull Names names) {
        return new Application(configurations, names) {};
    }

    @VisibleForTesting
    static NativeFeature createWithApi(@Nonnull RoleBasedConfigurationContainerInternal configurations) {
        return createWithApi(configurations, Names.of("main"));
    }

    @VisibleForTesting
    static NativeFeature createWithApi(@Nonnull RoleBasedConfigurationContainerInternal configurations, @Nonnull Names names) {
        return new Library(configurations, names){};
    }

    public static abstract class Application extends NativeFeature {

        @Inject
        public Application(RoleBasedConfigurationContainerInternal configurations, Names names) {
            super(configurations, names);
        }

    }

    public static abstract class Library extends NativeFeature {

        // Configurable dependency configurations
        private final Configuration api;
        private final Configuration headerOnlyApi;

        @Inject
        public Library(RoleBasedConfigurationContainerInternal configurations, Names names) {
            super(configurations, names);

            headerOnlyApi = configurations.dependencyScopeUnlocked(names.withSuffix("headerOnlyApi"));
            getApiElements().extendsFrom(headerOnlyApi);

            api = configurations.dependencyScopeUnlocked(names.withSuffix("api"));
            getImplementation().extendsFrom(api);
            getApiElements().extendsFrom(api);
        }

        @Override
        public void extendsFrom(@Nonnull NativeFeature nativeFeature) {
            super.extendsFrom(nativeFeature);
            if (nativeFeature instanceof Library) {
                Library library = (Library)nativeFeature;
                api.extendsFrom(library.api);
                headerOnlyApi.extendsFrom(library.headerOnlyApi);
            }
        }

        @Nonnull
        public Configuration getApi() {
            return api;
        }

        @Nonnull
        public Configuration getHeaderOnlyApi() {
            return headerOnlyApi;
        }

    }

}
