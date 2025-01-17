/*
 * Copyright 2010 the original author or authors.
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

package org.gradle.process.internal;

import org.gradle.api.Describable;
import org.gradle.process.ExecResult;
import org.gradle.process.TerminationMode;

import javax.annotation.Nullable;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public interface ExecHandle extends Describable {

    File getDirectory();

    String getCommand();

    List<String> getArguments();

    Map<String, String> getEnvironment();

    /**
     * Starts this process, blocking until the process has started.
     *
     * @return this
     */
    ExecHandle start();

    void removeStartupContext();

    ExecHandleState getState();

    /**
     * Aborts the process, blocking until the process has exited. Does nothing if the process has already completed.
     * Depending on the core-dumping configuration, ABORT or TERMINATE is sent to the process.
     */
    void abort();

    /**
     * Aborts the process. Does nothing if the process has already completed.
     * Unlike {@link ExecHandle#abort}, this does not block.
     */
    void kill(TerminationMode mode);

    /**
     * Waits for the process to finish. Returns immediately if the process has already completed.
     *
     * @return result
     */
    ExecResult waitForFinish();

    /**
     * Waits for the process to finish. Returns immediately if the process has already completed.
     *
     * @return result
     */
    ExecResult waitForFinish(long time, TimeUnit unit);

    /**
     * Returns the result of the process execution, if it has finished.  If the process has not finished, returns null.
     */
    @Nullable
    ExecResult getExecResult();

    void addListener(ExecHandleListener listener);

    void removeListener(ExecHandleListener listener);
}
