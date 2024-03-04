/*
 * Copyright 2013 the original author or authors.
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

import com.google.common.collect.ImmutableSet;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

public class DefaultLinkerSpec extends AbstractBinaryToolSpec implements LinkerSpec {

    private final List<File> objectFiles = new ArrayList<File>();
    private final List<File> libraries = new ArrayList<File>();
    private final List<File> libraryPath = new ArrayList<File>();
    private Predicate<File> wholeArchivesPredicate = any -> false;
    private File outputFile;
    private boolean debuggable;

    @Override
    public List<File> getObjectFiles() {
        return objectFiles;
    }

    @Override
    public void objectFiles(Iterable<File> objectFiles) {
        addAll(this.objectFiles, objectFiles);
    }

    @Override
    public List<File> getLibraries() {
        return libraries;
    }

    @Override
    public void libraries(Iterable<File> libraries) {
        addAll(this.libraries, libraries);
    }

    @Override
    @Deprecated
    public void wholeArchives(Iterable<File> libraries) {
        ImmutableSet<File> libs = ImmutableSet.of();
        if (null != libraries)
            libs = ImmutableSet.copyOf(libraries);
        wholeArchives(libs::contains);
    }

    @Override
    public void wholeArchives(Predicate<File> predicate) {
        if (null == predicate)
            predicate = any -> false;
        wholeArchivesPredicate = predicate;
    }

    @Override
    public Predicate<File> getWholeArchivesPredicate() {
        return wholeArchivesPredicate;
    }

    @Override
    public List<File> getLibraryPath() {
        return libraryPath;
    }

    @Override
    public void libraryPath(File... libraryPath) {
        Collections.addAll(this.libraryPath, libraryPath);
    }

    @Override
    public void libraryPath(List<File> libraryPath) {
        this.libraryPath.addAll(libraryPath);
    }

    @Override
    public File getOutputFile() {
        return outputFile;
    }

    @Override
    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    private void addAll(List<File> list, Iterable<File> iterable) {
        for (File file : iterable) {
            list.add(file);
        }
    }

    @Override
    public boolean isDebuggable() {
        return debuggable;
    }

    @Override
    public void setDebuggable(boolean debuggable) {
        this.debuggable = debuggable;
    }
}
