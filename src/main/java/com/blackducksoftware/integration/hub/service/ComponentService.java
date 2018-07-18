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
import java.util.List;
import java.util.Optional;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.api.generated.component.RemediationOptionsView;
import com.blackducksoftware.integration.hub.api.generated.discovery.ApiDiscovery;
import com.blackducksoftware.integration.hub.api.generated.view.ComponentSearchResultView;
import com.blackducksoftware.integration.hub.api.generated.view.ComponentVersionView;
import com.blackducksoftware.integration.hub.api.generated.view.ComponentView;
import com.blackducksoftware.integration.hub.api.generated.view.VulnerabilityV2View;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.exception.HubIntegrationException;
import com.blackducksoftware.integration.hub.service.model.HubMediaTypes;
import com.blackducksoftware.integration.hub.service.model.HubQuery;
import com.blackducksoftware.integration.hub.service.model.RequestFactory;
import com.blackducksoftware.integration.log.IntLogger;
import com.blackducksoftware.integration.rest.request.Request;
import com.blackducksoftware.integration.rest.request.Response;
import com.google.gson.JsonElement;

public class ComponentService extends DataService {
    public static final String REMEDIATING_LINK = "remediating";

    public ComponentService(final HubService hubService, final IntLogger logger) {
        super(hubService, logger);
    }

    public ComponentVersionView getComponentVersion(final ExternalId externalId) throws IntegrationException {
        for (final ComponentVersionView componentVersion : this.getAllComponentVersions(externalId)) {
            if (componentVersion.versionName.equals(externalId.version)) {
                return componentVersion;
            }
        }
        final String errMsg = "Could not find version " + externalId.version + " of component " + externalId.createHubOriginId();
        logger.error(errMsg);
        throw new HubIntegrationException(errMsg);
    }

    public List<ComponentVersionView> getAllComponentVersions(final ExternalId externalId) throws IntegrationException {
        final ComponentSearchResultView componentSearchView = getExactComponentMatch(externalId);
        final ComponentView componentView = hubService.getResponse(componentSearchView.component, ComponentView.class);

        final List<ComponentVersionView> componentVersionViews = hubService.getAllResponses(componentView, ComponentView.VERSIONS_LINK_RESPONSE);
        return componentVersionViews;
    }

    public ComponentSearchResultView getExactComponentMatch(final ExternalId externalId) throws IntegrationException {
        final List<ComponentSearchResultView> allComponents = getAllComponents(externalId);
        final String hubOriginIdToMatch = externalId.createHubOriginId();
        for (final ComponentSearchResultView componentItem : allComponents) {
            if (null != hubOriginIdToMatch) {
                if (hubOriginIdToMatch.equals(componentItem.originId)) {
                    return componentItem;
                }
            }
        }
        throw new HubIntegrationException("Couldn't find an exact component that matches " + hubOriginIdToMatch);
    }

    public List<ComponentSearchResultView> getAllComponents(final ExternalId externalId) throws IntegrationException {
        final String forge = externalId.forge.getName();
        final String hubOriginId = externalId.createHubOriginId();
        final String componentQuery = String.format("%s|%s", forge, hubOriginId);
        final Optional<HubQuery> hubQuery = HubQuery.createQuery("id", componentQuery);

        final Request.Builder requestBuilder = RequestFactory.createCommonGetRequestBuilder(hubQuery);
        final List<ComponentSearchResultView> allComponents = hubService.getAllResponses(ApiDiscovery.COMPONENTS_LINK_RESPONSE, requestBuilder);
        return allComponents;
    }

    public List<VulnerabilityV2View> getVulnerabilitiesFromComponentVersion(final ExternalId externalId) throws IntegrationException {
        final ComponentSearchResultView componentSearchView = getExactComponentMatch(externalId);
        final String componentVersionURL = componentSearchView.version;
        if (null != componentVersionURL) {
            final ComponentVersionView componentVersion = hubService.getResponse(componentVersionURL, ComponentVersionView.class);
            final Request.Builder requestBuilder = RequestFactory.createCommonGetRequestBuilder().mimeType(HubMediaTypes.VULNERABILITY_REQUEST_SERVICE_V1);
            final List<VulnerabilityV2View> vulnerabilityList = hubService.getAllResponses(componentVersion, ComponentVersionView.VULNERABILITIES_LINK_RESPONSE, requestBuilder);
            return vulnerabilityList;
        }

        throw new HubIntegrationException("Couldn't get a componentVersion url from the component matching " + externalId.createExternalId());
    }

    // TODO deprecate when the REMEDIATING_LINK is included in ComponentVersionView
    public RemediationOptionsView getRemediationInformation(final ComponentVersionView componentVersionView) throws IntegrationException {
        final String href = hubService.getHref(componentVersionView);
        try {
            final String remediatingURL = href + "/" + REMEDIATING_LINK;
            try (final Response response = hubService.executeGetRequest(remediatingURL);) {
                final JsonElement jsonElement = hubService.getJsonParser().parse(response.getContentString());
                return hubService.getGson().fromJson(jsonElement, RemediationOptionsView.class);
            } catch (final IOException ioException) {
                throw new IntegrationException(ioException);
            }
        } catch (final Exception genericException) {
            throw new IntegrationException(genericException);
        }
    }

}
