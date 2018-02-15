/**
 * Hub Common
 *
 * Copyright (C) 2017 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.api;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.api.bom.BomImportService;
import com.blackducksoftware.integration.hub.api.project.ProjectService;
import com.blackducksoftware.integration.hub.api.project.version.ProjectVersionService;
import com.blackducksoftware.integration.hub.api.report.ReportCategoriesEnum;
import com.blackducksoftware.integration.hub.api.report.ReportService;
import com.blackducksoftware.integration.hub.api.report.VersionReport;
import com.blackducksoftware.integration.hub.api.view.MetaHandler;
import com.blackducksoftware.integration.hub.dataservice.scan.ScanStatusDataService;
import com.blackducksoftware.integration.hub.model.enumeration.ProjectVersionDistributionEnum;
import com.blackducksoftware.integration.hub.model.enumeration.ProjectVersionPhaseEnum;
import com.blackducksoftware.integration.hub.model.enumeration.ReportFormatEnum;
import com.blackducksoftware.integration.hub.model.request.ProjectRequest;
import com.blackducksoftware.integration.hub.model.request.ProjectVersionRequest;
import com.blackducksoftware.integration.hub.model.view.ProjectVersionView;
import com.blackducksoftware.integration.hub.model.view.ProjectView;
import com.blackducksoftware.integration.hub.request.HubRequest;
import com.blackducksoftware.integration.hub.rest.RestConnectionTestHelper;
import com.blackducksoftware.integration.hub.service.HubServicesFactory;
import com.blackducksoftware.integration.log.IntLogger;
import com.blackducksoftware.integration.test.annotation.HubConnectionTest;

@Category(HubConnectionTest.class)
public class ReportRequestServiceTestIT {
    private final RestConnectionTestHelper restConnectionTestHelper = new RestConnectionTestHelper();

    @Test
    public void testGenerateReport() throws Exception {
        final HubServicesFactory hubServicesFactory = restConnectionTestHelper.createHubServicesFactory();
        final ProjectService projectService = hubServicesFactory.createProjectService();
        final ProjectVersionService projectVersionService = hubServicesFactory.createProjectVersionService();
        final BomImportService importService = hubServicesFactory.createBomImportService();
        final ReportService reportservice = hubServicesFactory.createReportService(120 * 1000);

        final ProjectView project = getProject(projectService, restConnectionTestHelper.getProperty("TEST_REPORT_PROJECT"));
        final ProjectVersionView version = getVersion(projectVersionService, project, restConnectionTestHelper.getProperty("TEST_REPORT_VERSION"));
        try {
            importService.importBomFile(getBDIOSingleDependency(), "application/ld+json");
            waitForHub(hubServicesFactory.createScanStatusDataService(120 * 1000), project.name, version.versionName, hubServicesFactory.getRestConnection().logger);

            final ReportCategoriesEnum[] categories = new ReportCategoriesEnum[2];
            categories[0] = ReportCategoriesEnum.VERSION;
            categories[1] = ReportCategoriesEnum.COMPONENTS;

            final VersionReport report = reportservice.generateHubReport(version, ReportFormatEnum.JSON, categories);
            assertNotNull(report);
            assertNotNull(report.getAggregateBomViewEntries());
            assertTrue(!report.getAggregateBomViewEntries().isEmpty());
        } finally {
            final MetaHandler metaService = new MetaHandler(restConnectionTestHelper.createIntLogger());
            final HubRequest hubRequest = projectService.getHubRequestFactory().createRequest(metaService.getHref(project));
            hubRequest.executeDelete();
        }
    }

    public void waitForHub(final ScanStatusDataService scanStatusDataService, final String hubProjectName, final String hubProjectVersion, final IntLogger logger) {
        try {
            scanStatusDataService.assertBomImportScanStartedThenFinished(hubProjectName, hubProjectVersion);
        } catch (final IntegrationException e) {
            logger.error(String.format("There was an error waiting for the Bom calculations: %s", e.getMessage()), e);
        }
    }

    private ProjectView getProject(final ProjectService projectService, final String projectName) {
        ProjectView project;
        try {
            project = projectService.getProjectByName(projectName);
        } catch (final IntegrationException e) {
            try {
                final String projectUrl = projectService.createHubProject(new ProjectRequest(projectName));
                project = projectService.getView(projectUrl, ProjectView.class);
            } catch (final IntegrationException e1) {
                throw new RuntimeException(e1);
            }
        }
        return project;
    }

    private ProjectVersionView getVersion(final ProjectVersionService versionService, final ProjectView project, final String versionName) {
        ProjectVersionView version;
        try {
            version = versionService.getProjectVersion(project, versionName);
        } catch (final IntegrationException e) {
            try {
                final String versionUrl = versionService.createHubVersion(project, new ProjectVersionRequest(ProjectVersionDistributionEnum.INTERNAL, ProjectVersionPhaseEnum.DEVELOPMENT, versionName));
                version = versionService.getView(versionUrl, ProjectVersionView.class);
            } catch (final IntegrationException e1) {
                throw new RuntimeException(e1);
            }
        }
        return version;
    }

    public File getBDIOSingleDependency() {
        return restConnectionTestHelper.getFile("bdio/Dependency_bdio.jsonld");
    }

}
