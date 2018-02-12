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
package com.blackducksoftware.integration.hub.dataservice;

import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.api.generated.discovery.ApiDiscovery;
import com.blackducksoftware.integration.hub.api.generated.response.CurrentVersionView;
import com.blackducksoftware.integration.hub.dataservice.phonehome.PhoneHomeCallable;
import com.blackducksoftware.integration.hub.dataservice.phonehome.PhoneHomeResponse;
import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.blackducksoftware.integration.hub.service.HubDataService;
import com.blackducksoftware.integration.log.IntLogger;
import com.blackducksoftware.integration.phonehome.PhoneHomeClient;
import com.blackducksoftware.integration.phonehome.PhoneHomeRequestBody;
import com.blackducksoftware.integration.phonehome.PhoneHomeRequestBodyBuilder;
import com.blackducksoftware.integration.phonehome.enums.BlackDuckName;
import com.blackducksoftware.integration.phonehome.enums.PhoneHomeSource;
import com.blackducksoftware.integration.phonehome.enums.ThirdPartyName;

public class PhoneHomeDataService extends HubDataService {
    private final IntLogger logger;
    private final HubRegistrationDataService hubRegistrationRequestService;
    private final PhoneHomeClient phoneHomeClient;
    private final ExecutorService executorService;

    public PhoneHomeDataService(final RestConnection restConnection, final PhoneHomeClient phoneHomeClient, final HubRegistrationDataService hubRegistrationRequestService) {
        super(restConnection);
        this.logger = restConnection.logger;
        this.hubRegistrationRequestService = hubRegistrationRequestService;
        this.phoneHomeClient = phoneHomeClient;
        final ThreadFactory threadFactory = Executors.defaultThreadFactory();
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), threadFactory);
    }

    public void phoneHome(final ThirdPartyName thirdPartyName, final String thirdPartyVersion, final String pluginVersion) {
        phoneHome(thirdPartyName.getName(), thirdPartyVersion, pluginVersion);
    }

    public void phoneHome(final String thirdPartyName, final String thirdPartyVersion, final String pluginVersion) {
        final PhoneHomeRequestBodyBuilder phoneHomeRequestBodyBuilder = createInitialPhoneHomeRequestBodyBuilder(thirdPartyName, thirdPartyVersion, pluginVersion);
        phoneHome(phoneHomeRequestBodyBuilder);
    }

    public void phoneHome(final PhoneHomeRequestBodyBuilder phoneHomeRequestBodyBuilder) {
        try {
            final PhoneHomeRequestBody phoneHomeRequestBody = phoneHomeRequestBodyBuilder.build();
            phoneHome(phoneHomeRequestBody);
        } catch (final Exception e) {
            logger.debug("Could not build phone home body" + e.getMessage());
        }
    }

    public void phoneHome(final PhoneHomeRequestBody phoneHomeRequestBody) {
        if (phoneHomeRequestBody == PhoneHomeRequestBody.DO_NOT_PHONE_HOME) {
            logger.debug("Skipping phone-home");
        } else {
            try {
                phoneHomeClient.postPhoneHomeRequest(phoneHomeRequestBody);
            } catch (final Exception e) {
                logger.debug("Problem with phone-home : " + e.getMessage(), e);
            }
        }
    }

    public PhoneHomeRequestBodyBuilder createInitialPhoneHomeRequestBodyBuilder(final ThirdPartyName thirdPartyName, final String thirdPartyVersion, final String pluginVersion) {
        return createInitialPhoneHomeRequestBodyBuilder(thirdPartyName.getName(), thirdPartyVersion, pluginVersion);
    }

    public PhoneHomeRequestBodyBuilder createInitialPhoneHomeRequestBodyBuilder(final String thirdPartyName, final String thirdPartyVersion, final String pluginVersion) {
        final PhoneHomeRequestBodyBuilder phoneHomeRequestBodyBuilder = createInitialPhoneHomeRequestBodyBuilder();
        phoneHomeRequestBodyBuilder.setThirdPartyName(thirdPartyName);
        phoneHomeRequestBodyBuilder.setThirdPartyVersion(thirdPartyVersion);
        phoneHomeRequestBodyBuilder.setPluginVersion(pluginVersion);
        return phoneHomeRequestBodyBuilder;
    }

    public PhoneHomeRequestBodyBuilder createInitialPhoneHomeRequestBodyBuilder() {
        final PhoneHomeRequestBodyBuilder phoneHomeRequestBodyBuilder = new PhoneHomeRequestBodyBuilder();
        try {
            final CurrentVersionView currentVersion = getResponseFromLinkResponse(ApiDiscovery.CURRENT_VERSION_LINK_RESPONSE);
            String registrationId = null;
            try {
                // We need to wrap this because this will most likely fail unless they are running as an admin
                registrationId = hubRegistrationRequestService.getRegistrationId();
            } catch (final IntegrationException e) {
            }
            final URL hubHostName = hubRegistrationRequestService.getHubBaseUrl();
            phoneHomeRequestBodyBuilder.setRegistrationId(registrationId);
            phoneHomeRequestBodyBuilder.setHostName(hubHostName.toString());
            phoneHomeRequestBodyBuilder.setBlackDuckName(BlackDuckName.HUB);
            phoneHomeRequestBodyBuilder.setBlackDuckVersion(currentVersion.version);
            phoneHomeRequestBodyBuilder.setSource(PhoneHomeSource.INTEGRATIONS);
        } catch (final Exception e) {
            logger.debug("Couldn't detail phone home request builder: " + e.getMessage());
        }
        return phoneHomeRequestBodyBuilder;
    }

    public PhoneHomeResponse startPhoneHome(final ThirdPartyName thirdPartyName, final String thirdPartyVersion, final String pluginVersion) {
        return startPhoneHome(thirdPartyName.getName(), thirdPartyVersion, pluginVersion);
    }

    public PhoneHomeResponse startPhoneHome(final String thirdPartyName, final String thirdPartyVersion, final String pluginVersion) {
        final PhoneHomeRequestBodyBuilder phoneHomeRequestBodyBuilder = createInitialPhoneHomeRequestBodyBuilder(thirdPartyName, thirdPartyVersion, pluginVersion);
        return startPhoneHome(phoneHomeRequestBodyBuilder);
    }

    public PhoneHomeResponse startPhoneHome(final PhoneHomeRequestBodyBuilder phoneHomeRequestBodyBuilder) {
        try {
            final PhoneHomeRequestBody phoneHomeRequestBody = phoneHomeRequestBodyBuilder.build();
            return startPhoneHome(phoneHomeRequestBody);
        } catch (final Exception e) {
            logger.debug("Could not build phone home body" + e.getMessage(), e);
        }
        return null;
    }

    public PhoneHomeResponse startPhoneHome(final PhoneHomeRequestBody phoneHomeRequestBody) {
        final PhoneHomeCallable task = new PhoneHomeCallable(logger, phoneHomeClient, phoneHomeRequestBody);
        final Future<Boolean> resultTask = executorService.submit(task);
        return new PhoneHomeResponse(resultTask);
    }

}
