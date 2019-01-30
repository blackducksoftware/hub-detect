/**
 * detectable
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detectable.detectables.bitbake;

import java.io.File;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.ExtractionEnvironment;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.SystemExecutableFinder;
import com.synopsys.integration.detectable.detectable.executable.ExecutableType;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.ExecutableNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PropertyInsufficientDetectableResult;

public class BitbakeDetectable extends Detectable {
    private final BitbakeDetectableOptions bitbakeDetectableOptions;
    private final FileFinder fileFinder;
    private final BitbakeExtractor bitbakeExtractor;
    private final SystemExecutableFinder systemExecutableFinder;

    private File foundBuildEnvScript;
    private File bashExe;

    public BitbakeDetectable(final DetectableEnvironment detectableEnvironment, final FileFinder fileFinder, final BitbakeDetectableOptions bitbakeDetectableOptions, final BitbakeExtractor bitbakeExtractor,
        final SystemExecutableFinder systemExecutableFinder) {
        super(detectableEnvironment, "Bitbake", "Bitbake");
        this.fileFinder = fileFinder;
        this.bitbakeDetectableOptions = bitbakeDetectableOptions;
        this.bitbakeExtractor = bitbakeExtractor;
        this.systemExecutableFinder = systemExecutableFinder;
    }

    @Override
    public DetectableResult applicable() {
        foundBuildEnvScript = fileFinder.findFile(environment.getDirectory(), bitbakeDetectableOptions.getBuildEnvName());
        if (foundBuildEnvScript != null) {
            return new FileNotFoundDetectableResult(bitbakeDetectableOptions.getBuildEnvName());
        }

        if (bitbakeDetectableOptions.getPackageNames() == null || bitbakeDetectableOptions.getPackageNames().length == 0) {
            return new PropertyInsufficientDetectableResult();
        }

        return new PassedDetectableResult();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        bashExe = systemExecutableFinder.findExecutable(ExecutableType.BASH);
        if (bashExe == null) {
            return new ExecutableNotFoundDetectableResult("bash");
        }

        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        return bitbakeExtractor.extract(extractionEnvironment, foundBuildEnvScript, environment.getDirectory(), bitbakeDetectableOptions.getPackageNames(), bashExe);
    }
}