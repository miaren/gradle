package org.gradle.internal.declarativedsl.analysis

import org.gradle.internal.declarativedsl.language.DataType
import org.gradle.internal.declarativedsl.language.Expr
import org.gradle.internal.declarativedsl.language.FunctionCall
import org.gradle.internal.declarativedsl.language.LanguageTreeElement
import org.gradle.internal.declarativedsl.language.LocalValue


data class ResolutionResult(
    val topLevelReceiver: ObjectOrigin.TopLevelReceiver,
    val assignments: List<AssignmentRecord>,
    val additions: List<DataAddition>,
    val errors: List<ResolutionError>,
)


data class DataAddition(val container: ObjectOrigin, val dataObject: ObjectOrigin)


data class ResolutionError(
    val element: LanguageTreeElement,
    val errorReason: ErrorReason
)


sealed interface ErrorReason {
    data class AmbiguousImport(val fqName: FqName) : ErrorReason
    data class UnresolvedReference(val reference: Expr) : ErrorReason
    data class NonReadableProperty(val property: DataProperty) : ErrorReason
    data class ReadOnlyPropertyAssignment(val property: DataProperty) : ErrorReason
    data class UnresolvedFunctionCallArguments(val functionCall: FunctionCall) : ErrorReason
    data class UnresolvedFunctionCallReceiver(val functionCall: FunctionCall) : ErrorReason
    data class UnresolvedFunctionCallSignature(val functionCall: FunctionCall) : ErrorReason
    data class AmbiguousFunctions(val functions: List<FunctionCallResolver.FunctionResolutionAndBinding>) : ErrorReason
    data class ValReassignment(val localVal: LocalValue) : ErrorReason
    data class ExternalReassignment(val external: ObjectOrigin.External) : ErrorReason
    data class AssignmentTypeMismatch(val expected: DataType, val actual: DataType) : ErrorReason

    // TODO: these two are never reported for now, instead it is UnresolvedFunctionCallSignature
    data object UnusedConfigureLambda : ErrorReason
    data object MissingConfigureLambda : ErrorReason

    data object AccessOnCurrentReceiverOnlyViolation : ErrorReason
    data class DuplicateLocalValue(val name: String) : ErrorReason
    data object UnresolvedAssignmentLhs : ErrorReason // TODO: report candidate with rejection reasons
    data object UnresolvedAssignmentRhs : ErrorReason // TODO: resolution trace here, too?
    data object UnitAssignment : ErrorReason
    data object DanglingPureExpression : ErrorReason
}
