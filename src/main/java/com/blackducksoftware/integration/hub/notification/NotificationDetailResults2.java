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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.blackducksoftware.integration.hub.api.UriSingleResponse;
import com.blackducksoftware.integration.hub.api.core.HubResponse;

public class NotificationDetailResults2 {
    private final List<NotificationDetailResult2> notificationResults;
    private final Date latestNotificationCreatedAtDate;
    private final String latestNotificationCreatedAtString;

    public NotificationDetailResults2(final List<NotificationDetailResult2> notificationResults, final Date latestNotificationCreatedAtDate, final String latestNotificationCreatedAtString) {
        this.notificationResults = notificationResults;
        this.latestNotificationCreatedAtDate = latestNotificationCreatedAtDate;
        this.latestNotificationCreatedAtString = latestNotificationCreatedAtString;
    }

    public NotificationDetailResults2(final List<NotificationDetailResult2> notificationResults, final Optional<Date> latestNotificationCreatedAtDate, final Optional<String> latestNotificationCreatedAtString) {
        this.notificationResults = notificationResults;
        this.latestNotificationCreatedAtDate = latestNotificationCreatedAtDate.orElse(null);
        this.latestNotificationCreatedAtString = latestNotificationCreatedAtString.orElse(null);
    }

    public Optional<Date> getLatestNotificationCreatedAtDate() {
        return Optional.ofNullable(latestNotificationCreatedAtDate);
    }

    public Optional<String> getLatestNotificationCreatedAtString() {
        return Optional.ofNullable(latestNotificationCreatedAtString);
    }

    public boolean isEmpty() {
        return notificationResults == null || notificationResults.isEmpty();
    }

    public List<UriSingleResponse<? extends HubResponse>> getAllLinks() {
        final List<UriSingleResponse<? extends HubResponse>> uriResponses = new ArrayList<>();
        notificationResults.forEach(result -> uriResponses.addAll(result.getAllLinks()));
        return uriResponses;
    }

}
