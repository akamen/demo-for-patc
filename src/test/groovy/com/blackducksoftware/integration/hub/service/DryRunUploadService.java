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
package com.blackducksoftware.integration.hub.service;

import java.io.File;

import com.blackducksoftware.integration.hub.api.core.HubPath;
import com.blackducksoftware.integration.hub.service.model.RequestFactory;
import com.blackducksoftware.integration.log.IntLogger;
import com.blackducksoftware.integration.rest.request.Request;
import com.blackducksoftware.integration.rest.request.Response;

public class DryRunUploadService extends DataService {
    public DryRunUploadService(final HubService hubService, final IntLogger logger) {
        super(hubService, logger);
    }

    public DryRunUploadResponse uploadDryRunFile(final File dryRunFile) throws Exception {
        final String uri = hubService.getUri(new HubPath("/api/v1/scans"));
        final Request request = RequestFactory.createCommonPostRequestBuilder(dryRunFile).uri(uri).build();
        try (Response response = hubService.executeRequest(request)) {
            final String responseString = response.getContentString();
            final DryRunUploadResponse uploadResponse = hubService.getGson().fromJson(responseString, DryRunUploadResponse.class);
            uploadResponse.json = responseString;
            return uploadResponse;
        }
    }

}
