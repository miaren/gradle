/*
 * Copyright 2025 the original author or authors.
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

import org.gradle.internal.UncheckedException;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author matis
 */
public final class ProcessKiller {

    private ProcessKiller() {
    }

    public long kill(Process handle, Signal signal) {
        final long pid;
        try {
            Method toHandleMethod = Process.class.getDeclaredMethod("toHandle");
            Object processHandle = toHandleMethod.invoke(handle);
            Class<?> handleClass = Class.forName("java.lang.ProcessHandle");
            Method pidMethod = handleClass.getDeclaredMethod("pid");
            pid = (long) pidMethod.invoke(processHandle);
        } catch (Exception ex) {
            throw UncheckedException.throwAsUncheckedException(ex);
        }

        kill(pid, signal);

        return pid;
    }

    public void kill(long pid, Signal signal) {
        if (pid <= 1)
            throw new IllegalArgumentException("Invalid PID: " + pid);

        try {
            Runtime.getRuntime().exec(String.format("kill -%d %d", signal.getNumber(), pid));
        } catch (IOException e) {
            throw UncheckedException.throwAsUncheckedException(e);
        }
    }

    public static ProcessKiller get() {
        return new ProcessKiller();
    }

}
