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

package org.gradle.nativeplatform;

/**
 * @author matis
 */
public enum LinkableObjectLinkType {
    /**
     * (Default) strong dependencies must be present at runtime.
     */
    STRONG,
    /**
     * Weak dependencies may be missing at runtime.
     */
    WEAK,
    /**
     * Upward dependencies are provided by the caller.
     * Most usages of this imply that the product only be used with dlopen().
     */
    UPWARD
}
