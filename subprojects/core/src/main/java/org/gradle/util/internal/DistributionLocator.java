/*
 * Copyright 2021 the original author or authors.
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
package org.gradle.util.internal;

import org.apache.commons.lang.StringUtils;
import org.gradle.internal.UncheckedException;
import org.gradle.util.GradleVersion;

import java.net.URI;
import java.net.URISyntaxException;

public class DistributionLocator {

    private static final String SERVICES_GRADLE_BASE_URL_PROPERTY = "org.gradle.internal.services.base.url";
    private static final String SERVICES_GRADLE_ARCHIVE_NAME = "org.gradle.internal.services.archive.name";
    private static final String SERVICES_GRADLE_ARCHIVE_EXT = "org.gradle.internal.services.archive.extension";
    private static final String SERVICES_GRADLE_BASE_URL = "https://services.gradle.org";
    private static final String RELEASE_REPOSITORY = "/distributions";
    private static final String SNAPSHOT_REPOSITORY = "/distributions-snapshots";

    public static String getBaseUrl() {
        return System.getProperty(SERVICES_GRADLE_BASE_URL_PROPERTY, SERVICES_GRADLE_BASE_URL);
    }

    public URI getDistributionFor(GradleVersion version) {
        return getDistributionFor(version, "bin");
    }

    public URI getDistributionFor(GradleVersion version, String type) {
        String archiveName = System.getProperty(SERVICES_GRADLE_ARCHIVE_NAME, "gradle");
        return getDistribution(getDistributionRepository(version), version, archiveName, type);
    }

    private String getDistributionRepository(GradleVersion version) {
        String url = System.getProperty(SERVICES_GRADLE_BASE_URL_PROPERTY);
        if (StringUtils.isNotBlank(url))
            return url;
        url = SERVICES_GRADLE_BASE_URL;
        if (version.isSnapshot()) {
            return url + SNAPSHOT_REPOSITORY;
        } else {
            return url + RELEASE_REPOSITORY;
        }
    }

    private URI getDistribution(
        String repositoryUrl, GradleVersion version, String archiveName,
        String archiveClassifier
    ) {
        String archiveExt = System.getProperty(SERVICES_GRADLE_ARCHIVE_EXT, ".zip");
        try {
            return new URI(repositoryUrl + "/" + archiveName + "-" + version.getVersion() + "-" + archiveClassifier + archiveExt);
        } catch (URISyntaxException e) {
            throw UncheckedException.throwAsUncheckedException(e);
        }
    }
}
