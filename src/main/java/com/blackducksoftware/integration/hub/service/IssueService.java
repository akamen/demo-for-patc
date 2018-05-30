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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.api.generated.view.IssueView;
import com.blackducksoftware.integration.hub.api.generated.view.VersionBomComponentView;
import com.blackducksoftware.integration.hub.service.model.RequestFactory;
import com.blackducksoftware.integration.rest.HttpMethod;
import com.blackducksoftware.integration.rest.request.BodyContent;
import com.blackducksoftware.integration.rest.request.Request;
import com.blackducksoftware.integration.rest.request.Response;

public class IssueService extends DataService {
    public IssueService(final HubService hubService) {
        super(hubService);
    }

    public List<IssueView> getIssues(final VersionBomComponentView versionBomComponentView) throws IntegrationException {
        // TODO supported in Hub 4.7.0. Once that Hub version is released, it should be a link multiple response.
        final IssueView issueView = hubService.getResponse(versionBomComponentView, VersionBomComponentView.COMPONENT_ISSUES_LINK_RESPONSE);
        return Arrays.asList(issueView);
    }

    public String createIssue(final IssueView issueItem, final String uri) throws IntegrationException {
        final Request request = RequestFactory.createCommonPostRequestBuilder(issueItem).uri(uri).build();
        return hubService.executePostRequestAndRetrieveURL(request);
    }

    public void updateIssue(final IssueView issueItem, final String uri) throws IntegrationException {
        final Request request = new Request.Builder(uri).method(HttpMethod.PUT).bodyContent(new BodyContent(issueItem)).build();
        try (Response response = hubService.executeRequest(request)) {
        } catch (final IOException e) {
            throw new IntegrationException(e.getMessage(), e);
        }
    }

    public void deleteIssue(final IssueView issueItem) throws IntegrationException {
        final String codeLocationItemUrl = hubService.getHref(issueItem);
        deleteIssue(codeLocationItemUrl);
    }

    public void deleteIssue(final String issueItemUri) throws IntegrationException {
        final Request request = new Request.Builder(issueItemUri).method(HttpMethod.DELETE).build();
        try (Response response = hubService.executeRequest(request)) {
        } catch (final IOException e) {
            throw new IntegrationException(e.getMessage(), e);
        }
    }

}
