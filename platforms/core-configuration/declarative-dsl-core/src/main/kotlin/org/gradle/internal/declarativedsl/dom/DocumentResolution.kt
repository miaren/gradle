/*
 * Copyright 2023 the original author or authors.
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

package org.gradle.internal.declarativedsl.dom

import org.gradle.internal.declarativedsl.analysis.DataClass
import org.gradle.internal.declarativedsl.analysis.DataProperty
import org.gradle.internal.declarativedsl.language.DataType
import org.gradle.internal.declarativedsl.analysis.SchemaFunction
import org.gradle.internal.declarativedsl.analysis.SchemaMemberFunction


sealed interface DocumentResolution {
    sealed interface SuccessfulResolution : DocumentResolution
    sealed interface UnsuccessfulResolution : DocumentResolution {
        val reasons: Iterable<ResolutionFailureReason>
    }

    sealed interface PropertyResolution : DocumentResolution {
        data class PropertyAssignmentResolved(val receiverType: DataType, val property: DataProperty) : PropertyResolution, SuccessfulResolution
        data class PropertyNotAssigned(override val reasons: List<PropertyNotAssignedReason>) : PropertyResolution, UnsuccessfulResolution
    }

    sealed interface ElementResolution : DocumentResolution {
        sealed interface SuccessfulElementResolution : ElementResolution, SuccessfulResolution {
            val elementType: DataType

            data class PropertyConfiguringElementResolved(
                override val elementType: DataClass
            ) : SuccessfulElementResolution

            data class ContainerElementResolved(
                override val elementType: DataType,
                val elementFactoryFunction: SchemaMemberFunction,
                val isKeyArguments: Boolean
            ) : SuccessfulElementResolution
        }

        data class ElementNotResolved(override val reasons: List<ElementNotResolvedReason>) : ElementResolution, UnsuccessfulResolution
    }

    data object ErrorResolution : DocumentResolution, UnsuccessfulResolution {
        override val reasons: Iterable<ResolutionFailureReason>
            get() = listOf(IsError)
    }

    sealed interface ValueResolution : DocumentResolution {
        data class LiteralValueResolved(val value: Any) : ValueResolution, SuccessfulResolution

        sealed interface ValueFactoryResolution : ValueResolution {
            data class ValueFactoryResolved(val function: SchemaFunction) : ValueFactoryResolution, SuccessfulResolution
            data class ValueFactoryNotResolved(override val reasons: List<ValueFactoryNotResolvedReason>) : ValueFactoryResolution, UnsuccessfulResolution
        }
    }
}
