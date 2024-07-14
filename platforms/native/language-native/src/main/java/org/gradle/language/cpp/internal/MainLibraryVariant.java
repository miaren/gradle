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

package org.gradle.language.cpp.internal;

import com.google.common.collect.ImmutableSet;
import org.gradle.api.Action;
import org.gradle.api.DomainObjectSet;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.PublishArtifact;
import org.gradle.api.attributes.AttributeContainer;
import org.gradle.api.component.ComponentWithVariants;
import org.gradle.api.component.SoftwareComponent;
import org.gradle.api.internal.attributes.AttributeContainerInternal;
import org.gradle.api.internal.attributes.ImmutableAttributes;
import org.gradle.api.internal.attributes.ImmutableAttributesFactory;
import org.gradle.api.internal.component.SoftwareComponentInternal;
import org.gradle.api.internal.component.UsageContext;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.publish.internal.component.ConfigurationSoftwareComponentVariant;

import java.util.LinkedHashSet;
import java.util.Set;

public class MainLibraryVariant implements ComponentWithVariants, SoftwareComponentInternal {
    private final LinkedHashSet<ConfigurationSoftwareComponentVariant> artifacts = new LinkedHashSet<>();
    private final Configuration dependencies;
    private final ImmutableAttributesFactory immutableAttributesFactory;
    private final DomainObjectSet<SoftwareComponent> variants;
    private final AttributeContainerInternal attributeContainer;

    public MainLibraryVariant(Configuration dependencies, ImmutableAttributesFactory immutableAttributesFactory,
                              AttributeContainerInternal attributeContainer, ObjectFactory objectFactory) {
        this.dependencies = dependencies;
        this.immutableAttributesFactory = immutableAttributesFactory;
        this.attributeContainer = attributeContainer;
        this.variants = objectFactory.domainObjectSet(SoftwareComponent.class);
    }

    @Override
    public String getName() {
        return "api";
    }

    @Override
    public Set<? extends UsageContext> getUsages() {
        // TODO: Also consider exposing modular API?
        return ImmutableSet.copyOf(artifacts);
    }

    @Override
    public Set<? extends SoftwareComponent> getVariants() {
        return variants;
    }

    public void addArtifact(PublishArtifact ignoredArtifact) {
        addArtifact("api", ignoredArtifact);
    }

    public void addArtifact(String name, PublishArtifact artifact) {
        addArtifact(name, artifact, ignored -> {});
    }

    public void addArtifact(String name, PublishArtifact artifact, Action<AttributeContainer> configure) {
        AttributeContainerInternal attributes = immutableAttributesFactory.mutable();
        configure.execute(attributes);
        ImmutableAttributes finalAttributes = immutableAttributesFactory.concat(attributeContainer.asImmutable(), attributes.asImmutable());
        artifacts.add(new ConfigurationSoftwareComponentVariant(name, finalAttributes, ImmutableSet.of(artifact), dependencies));
    }

    /**
     * Adds a child variant
     */
    public void addVariant(SoftwareComponent variant) {
        variants.add(variant);
    }


}
