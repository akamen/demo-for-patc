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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.api.generated.view.ExternalExtensionConfigValueView;
import com.blackducksoftware.integration.hub.api.generated.view.ExternalExtensionUserView;
import com.blackducksoftware.integration.hub.api.generated.view.ExternalExtensionView;
import com.blackducksoftware.integration.hub.api.view.MetaHandler;
import com.blackducksoftware.integration.hub.service.model.UserConfigItem;
import com.blackducksoftware.integration.hub.service.model.UserConfigTransform;
import com.blackducksoftware.integration.parallel.processor.ParallelResourceProcessor;
import com.blackducksoftware.integration.parallel.processor.ParallelResourceProcessorResults;

public class ExtensionConfigService extends DataService {
    private final UserConfigTransform userConfigTransform;

    public ExtensionConfigService(final HubService hubService) {
        super(hubService);
        userConfigTransform = new UserConfigTransform(hubService);
    }

    private ParallelResourceProcessor<UserConfigItem, ExternalExtensionUserView> createProcessor() {
        final ParallelResourceProcessor<UserConfigItem, ExternalExtensionUserView> parallelProcessor = new ParallelResourceProcessor<>(logger);
        parallelProcessor.addTransformer(ExternalExtensionUserView.class, userConfigTransform);

        return parallelProcessor;
    }

    public Map<String, ExternalExtensionConfigValueView> getGlobalConfigMap(final String extensionUrl) throws IntegrationException {
        Map<String, ExternalExtensionConfigValueView> globalConfigMap = new HashMap<>();
        final ExternalExtensionView extension = hubService.getResponse(extensionUrl, ExternalExtensionView.class);
        final String globalOptionsLink = hubService.getFirstLink(extension, MetaHandler.GLOBAL_OPTIONS_LINK);
        globalConfigMap = createGlobalConfigMap(globalOptionsLink);
        return globalConfigMap;
    }

    public ParallelResourceProcessorResults<UserConfigItem> getUserConfigList(final String extensionUrl) throws IntegrationException {
        final ExternalExtensionView extension = hubService.getResponse(extensionUrl, ExternalExtensionView.class);
        final String userOptionsLink = hubService.getFirstLink(extension, MetaHandler.USER_OPTIONS_LINK);
        final List<ExternalExtensionUserView> userOptionList = hubService.getResponses(userOptionsLink, ExternalExtensionUserView.class, true);
        try (ParallelResourceProcessor<UserConfigItem, ExternalExtensionUserView> parallelProcessor = createProcessor()) {
            final ParallelResourceProcessorResults<UserConfigItem> itemList = parallelProcessor.process(userOptionList);
            return itemList;
        } catch (final IOException ex) {
            logger.debug("Error closing processor", ex);
            return new ParallelResourceProcessorResults<>(Collections.emptyList(), Arrays.asList(ex));
        }
    }

    private Map<String, ExternalExtensionConfigValueView> createGlobalConfigMap(final String globalConfigUrl) throws IntegrationException {
        final List<ExternalExtensionConfigValueView> itemList = hubService.getResponses(globalConfigUrl, ExternalExtensionConfigValueView.class, true);
        final Map<String, ExternalExtensionConfigValueView> itemMap = createConfigMap(itemList);
        return itemMap;
    }

    private Map<String, ExternalExtensionConfigValueView> createConfigMap(final List<ExternalExtensionConfigValueView> itemList) {
        final Map<String, ExternalExtensionConfigValueView> itemMap = new HashMap<>(itemList.size());
        for (final ExternalExtensionConfigValueView item : itemList) {
            itemMap.put(item.name, item);
        }
        return itemMap;
    }
}
