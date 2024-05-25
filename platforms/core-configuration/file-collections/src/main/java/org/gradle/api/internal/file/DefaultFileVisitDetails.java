/*
 * Copyright 2015 the original author or authors.
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

package org.gradle.api.internal.file;

import org.gradle.api.UncheckedIOException;
import org.gradle.api.file.FileVisitDetails;
import org.gradle.api.file.RelativePath;
import org.gradle.internal.file.Chmod;
import org.gradle.internal.file.Stat;
import org.gradle.internal.nativeintegration.filesystem.FileSystem;
import org.gradle.internal.nativeintegration.services.FileSystems;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

public class DefaultFileVisitDetails extends DefaultFileTreeElement implements FileVisitDetails {
    private final AtomicBoolean stop;

    public DefaultFileVisitDetails(File file, RelativePath relativePath, AtomicBoolean stop, Chmod chmod, Stat stat) {
        super(file, relativePath, chmod, stat);
        this.stop = stop;
    }

    @Override
    public void stopVisiting() {
        stop.set(true);
    }

    @Override
    public FileVisitDetails followLink() {
        if (!isSymbolicLink())
            throw new UnsupportedOperationException();
        try {
            File linkTargetFile = getFile().getCanonicalFile();
            FileSystem fileSystem = FileSystems.getDefault();
            RelativePath path = new RelativePath(linkTargetFile.isDirectory(), linkTargetFile.getCanonicalPath().split("/"));
            return new DefaultFileVisitDetails(linkTargetFile, path, stop, fileSystem, fileSystem);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

}
