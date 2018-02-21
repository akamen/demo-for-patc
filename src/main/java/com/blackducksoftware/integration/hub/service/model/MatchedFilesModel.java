/**
 * hub-common
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.service.model;

import java.util.HashSet;
import java.util.Set;

import com.blackducksoftware.integration.hub.api.generated.component.CompositePathWithArchiveContext;
import com.blackducksoftware.integration.hub.api.generated.enumeration.MatchedFileUsagesType;
import com.blackducksoftware.integration.hub.api.generated.view.MatchedFileView;

public class MatchedFilesModel {
    private final String path;
    private final String archiveContext;
    private final String fileName;
    private final String compositePathContext;
    private final Set<MatchedFileUsagesType> usages;

    public MatchedFilesModel(final MatchedFileView matchedFile) {
        final CompositePathWithArchiveContext pathView = matchedFile.filePath;
        this.path = pathView.path;
        this.archiveContext = pathView.archiveContext;
        this.fileName = pathView.fileName;
        this.compositePathContext = pathView.compositePathContext;
        this.usages = new HashSet<>(matchedFile.usages);
    }

    public String getPath() {
        return path;
    }

    public String getArchiveContext() {
        return archiveContext;
    }

    public String getFileName() {
        return fileName;
    }

    public String getCompositePathContext() {
        return compositePathContext;
    }

    public Set<MatchedFileUsagesType> getUsages() {
        return usages;
    }
}
