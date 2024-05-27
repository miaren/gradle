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

import org.gradle.api.Action;
import org.gradle.nativeplatform.ConfigurableLinkableDependencySpec;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class DefaultLinkerSpec extends AbstractBinaryToolSpec implements LinkerSpec {

    private final List<File> objectFiles = new ArrayList<File>();
    private final List<File> libraries = new ArrayList<File>();
    private final List<File> libraryPath = new ArrayList<File>();
    private final List<String> frameworks = new ArrayList<String>();
    private final List<File> frameworkPath = new ArrayList<File>();
    private final List<Action<ConfigurableLinkableDependencySpec>> linkableDependencySpecActions = new LinkedList<Action<ConfigurableLinkableDependencySpec>>();
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
    public List<String> getFrameworks() {
        return frameworks;
    }

    @Override
    public void frameworks(Iterable<String> frameworks) {
        addAllStrings(this.frameworks, frameworks);
    }

    @Override
    public void eachDependency(Action<ConfigurableLinkableDependencySpec> action) {
        linkableDependencySpecActions.add(action);
    }

    public List<Action<ConfigurableLinkableDependencySpec>> getLinkableDependencySpecActions() {
        return linkableDependencySpecActions;
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
    public List<File> getFrameworkPath() {
        return frameworkPath;
    }

    @Override
    public void frameworkPath(File... frameworkPath) {
        Collections.addAll(this.frameworkPath, frameworkPath);
    }

    @Override
    public void frameworkPath(Iterable<File> frameworkPath) {
        addAll(this.frameworkPath, frameworkPath);
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

    private void addAllStrings(List<String> list, Iterable<String> iterable) {
        for (String string : iterable) {
            list.add(string);
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
