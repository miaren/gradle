/*
 * Copyright 2014 the original author or authors.
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

package org.gradle.nativeplatform.toolchain.internal;

import com.google.common.collect.Iterables;
import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.Transformer;
import org.gradle.api.tasks.WorkResult;
import org.gradle.api.tasks.WorkResults;
import org.gradle.internal.FileUtils;
import org.gradle.internal.operations.BuildOperationExecutor;
import org.gradle.internal.operations.BuildOperationQueue;
import org.gradle.internal.operations.BuildOperationToken;
import org.gradle.internal.os.OperatingSystem;
import org.gradle.internal.work.WorkerLeaseService;
import org.gradle.nativeplatform.internal.CompilerOutputFileNamingSchemeFactory;
import org.gradle.nativeplatform.internal.cppmodules.ModuleDependencyRule;
import org.gradle.nativeplatform.internal.cppmodules.ModuleSchema;
import org.gradle.nativeplatform.internal.cppmodules.ModuleUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

public abstract class NativeCompiler<T extends NativeCompileSpec> extends AbstractCompiler<T> {
    private static final Logger log = LoggerFactory.getLogger(NativeCompiler.class);
    private final Transformer<T, T> specTransformer;
    private final String objectFileExtension;

    private final CompilerOutputFileNamingSchemeFactory compilerOutputFileNamingSchemeFactory;

    public NativeCompiler(BuildOperationExecutor buildOperationExecutor, CompilerOutputFileNamingSchemeFactory compilerOutputFileNamingSchemeFactory, CommandLineToolInvocationWorker commandLineToolInvocationWorker, CommandLineToolContext invocationContext, ArgsTransformer<T> argsTransformer, Transformer<T, T> specTransformer, String objectFileExtension, boolean useCommandFile, WorkerLeaseService workerLeaseService) {
        super(buildOperationExecutor, commandLineToolInvocationWorker, invocationContext, argsTransformer, useCommandFile, workerLeaseService);
        this.compilerOutputFileNamingSchemeFactory = compilerOutputFileNamingSchemeFactory;
        this.objectFileExtension = objectFileExtension;
        this.specTransformer = specTransformer;
    }

    @Override
    public WorkResult execute(final T spec) {
        final T transformedSpec = specTransformer.transform(spec);

        super.execute(spec);

        return WorkResults.didWork(!transformedSpec.getSourceFiles().isEmpty());
    }

    // TODO(daniel): Should support in a better way multi file invocation.
    @Override
    protected Action<BuildOperationQueue<CommandLineToolInvocation>> newInvocationAction(final T spec, final List<String> genericArgs) {
        final File objectDir = spec.getObjectFileDir();
        return new Action<BuildOperationQueue<CommandLineToolInvocation>>() {
            @Override
            public void execute(BuildOperationQueue<CommandLineToolInvocation> buildQueue) {
                buildQueue.setLogLocation(spec.getOperationLogger().getLogLocation());
                if (spec.isUsingModuleDependencySchema()) {
                    for (File sourceFile : spec.getSourceFiles()) {
                        CommandLineToolInvocation perFileInvocation = createPerFileInvocation(genericArgs, sourceFile, objectDir, spec);
                        BuildOperationToken token = buildQueue.submit(perFileInvocation);
                        try {
                            // Wait for each job to be done before queuing more.
                            if (null == token) {
                                continue;
                            }
                            token.get();
                        } catch (Exception e) {
                            throw new GradleException("Couldn't execute precompiled module interface", e);
                        }
                    }
                } else {
                    for (File sourceFile : spec.getSourceFiles()) {
                        CommandLineToolInvocation perFileInvocation = createPerFileInvocation(genericArgs, sourceFile, objectDir, spec);
                        buildQueue.add(perFileInvocation);
                    }
                }
            }
        };
    }

    protected List<String> getSourceArgs(File sourceFile) {
        return Collections.singletonList(sourceFile.getAbsolutePath());
    }

    protected abstract List<String> getOutputArgs(T spec, File outputFile);

    @Override
    protected abstract void addOptionsFileArgs(List<String> args, File tempDir);

    protected abstract List<String> getPCHArgs(T spec);

    protected File getOutputFileDir(File sourceFile, File objectFileDir, String fileSuffix) {
        boolean windowsPathLimitation = OperatingSystem.current().isWindows();

        File outputFile = compilerOutputFileNamingSchemeFactory.create()
            .withObjectFileNameSuffix(fileSuffix)
            .withOutputBaseFolder(objectFileDir)
            .map(sourceFile);
        return windowsPathLimitation ? FileUtils.assertInWindowsPathLengthLimitation(outputFile) : outputFile;
    }

    protected List<String> maybeGetPCHArgs(final T spec, File sourceFile) {
        if (spec.getPreCompiledHeader() == null || !spec.getSourceFilesForPch().contains(sourceFile)) {
            return new ArrayList<>();
        }

        return getPCHArgs(spec);
    }

    protected CommandLineToolInvocation createPerFileInvocation(List<String> genericArgs, File sourceFile, File objectDir, T spec) {
        LinkedList<String> sourceArgs = new LinkedList<>(getSourceArgs(sourceFile));

        ModuleUnit moduleUnit = null;
        File outputFile = null;
        boolean precompile = false;

        if (spec.isUsingModuleDependencySchema()) {
            ModuleSchema schema = spec.getModuleDependencySchema();
            try {
                moduleUnit = schema.getUnitForSourceFile(sourceFile);

                if (moduleUnit.isInterface()) {
                    precompile = true;
                }

                outputFile = new File(moduleUnit.getRule().getPrimaryOutput());
            } catch (NoSuchElementException e) {
                log.info("Compiling an import-less module unit: {}", sourceFile);
            }
        }

        if (precompile) {
            sourceArgs.addFirst("--precompile");
        } else {
            sourceArgs.addFirst("-c");
        }

        if (spec.isUsingModuleDependencySchema()) {
            if (sourceFile.getName().endsWith(".pcm")) {
                sourceArgs.addFirst("pcm");
            } else {
                sourceArgs.addFirst("c++-module");
            }
            sourceArgs.addFirst("-x");
        }

        if (null == outputFile) {
            outputFile = getOutputFileDir(sourceFile, objectDir, objectFileExtension);
        }

        File outputDirectory = outputFile.getParentFile();
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }

        if (spec.isUsingModuleDependencySchema()) {
            ModuleSchema schema = spec.getModuleDependencySchema();
            ModuleDependencyRule rule = schema.getRuleForOutputFile(outputFile);
            LinkedHashSet<String> args = new LinkedHashSet<>();
            collectRequiredUnits(args, schema, rule);
            sourceArgs.addAll(args);
        }

        List<String> outputArgs = getOutputArgs(spec, outputFile);
        List<String> pchArgs = maybeGetPCHArgs(spec, sourceFile);

        return newInvocation("compiling ".concat(sourceFile.getName()), objectDir, buildPerFileArgs(genericArgs, sourceArgs, outputArgs, pchArgs), spec.getOperationLogger());
    }

    private static void collectRequiredUnits(Set<String> args, ModuleSchema schema, ModuleDependencyRule rule) {
        for (ModuleDependencyRule.Requirement req : rule.getRequires()) {
            ModuleUnit depUnit = schema.getUnitByLogicalName(req.getLogicalName());
            ModuleDependencyRule depRule = depUnit.getRule();

            // Add transitive dependencies.
            collectRequiredUnits(args, schema, depRule);

            String depOutputFile = depRule.getPrimaryOutput();
            args.add(String.format("-fmodule-file=%s=%s", depUnit.getLogicalName(), depOutputFile));
        }
    }

    protected Iterable<String> buildPerFileArgs(List<String> genericArgs, List<String> sourceArgs, List<String> outputArgs, List<String> pchArgs) {
        return Iterables.concat(genericArgs, pchArgs, sourceArgs, outputArgs);
    }
}
