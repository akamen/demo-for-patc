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
package com.blackducksoftware.integration.hub.notification;

import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

import com.blackducksoftware.integration.hub.api.generated.view.ComponentVersionView;
import com.blackducksoftware.integration.hub.api.generated.view.PolicyRuleView;

public class PolicyViolationContentItem extends PolicyContentItem {
    private final List<PolicyRuleView> policyRuleList;

    public PolicyViolationContentItem(final Date createdAt, final ProjectVersionModel projectVersion,
            final String componentName,
            final ComponentVersionView componentVersion, final String componentUrl,
            final String componentVersionUrl,
            final List<PolicyRuleView> policyRuleList,
            final String componentIssueUrl) throws URISyntaxException {
        super(createdAt, projectVersion, componentName, componentVersion, componentUrl, componentVersionUrl, componentIssueUrl);
        this.policyRuleList = policyRuleList;
    }

    public List<PolicyRuleView> getPolicyRuleList() {
        return policyRuleList;
    }

}
