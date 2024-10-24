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

import java.io.File;
import java.util.List;

/**
 * A high level interface to the linker, specifying what is to be linked and how.
 */
public interface LinkerSpec extends BinaryToolSpec {

    List<File> getObjectFiles();

    void objectFiles(Iterable<File> objectFiles);

    List<File> getLibraries();

    void libraries(Iterable<File> libraries);

    /**
     * Specify which static libraries should be included as whole archives (ELF only).
     */
    void wholeArchives(Iterable<File> libraries);

    List<File> getWholeArchives();

    List<File> getLibraryPath();

    void libraryPath(File... libraryPath);

    void libraryPath(List<File> libraryPath);

    File getOutputFile();

    void setOutputFile(File outputFile);

    boolean isDebuggable();

    void setDebuggable(boolean debuggable);
}
