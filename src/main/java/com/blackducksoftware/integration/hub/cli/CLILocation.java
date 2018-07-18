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
package com.blackducksoftware.integration.hub.cli;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import com.blackducksoftware.integration.hub.exception.HubIntegrationException;
import com.blackducksoftware.integration.log.IntLogger;

public class CLILocation {
    public static final String CLI_UNZIP_DIR = "Hub_Scan_Installation";
    public static final String VERSION_FILE_NAME = "hubVersion.txt";
    public static final String DEFAULT_CLI_DOWNLOAD = "scan.cli.zip";
    public static final String WINDOWS_CLI_DOWNLOAD = "scan.cli-windows.zip";
    public static final String MAC_CLI_DOWNLOAD = "scan.cli-macosx.zip";

    private final File directoryToInstallTo;
    private final IntLogger logger;

    public CLILocation(final IntLogger logger, final File directoryToInstallTo) {
        if (logger == null) {
            throw new IllegalArgumentException("You must provided a logger.");
        }
        if (directoryToInstallTo == null) {
            throw new IllegalArgumentException("You must provided a directory to install the CLI to.");
        }
        this.logger = logger;
        this.directoryToInstallTo = directoryToInstallTo;
    }

    public File getJreSecurityDirectory() throws IOException {
        final File javaHome = getJavaHome();
        File jreSecurityDirectory = new File(javaHome, "lib");
        jreSecurityDirectory = new File(jreSecurityDirectory, "security");

        return jreSecurityDirectory;
    }

    public String getCLIDownloadUrl(final IntLogger logger, final String hubUrl) {
        if (SystemUtils.IS_OS_MAC_OSX) {
            return getCLIWrapperLink(hubUrl, MAC_CLI_DOWNLOAD);
        } else if (SystemUtils.IS_OS_WINDOWS) {
            return getCLIWrapperLink(hubUrl, WINDOWS_CLI_DOWNLOAD);
        } else {
            return getCLIWrapperLink(hubUrl, DEFAULT_CLI_DOWNLOAD);
        }
    }

    public File getOneJarFile() throws IOException {
        final File cliHomeFile = getCLIHome();
        if (cliHomeFile == null) {
            logger.error("Could not find the CLI home directory");
            return null;
        }
        File oneJarFile = new File(cliHomeFile, "lib");
        oneJarFile = new File(oneJarFile, "cache");
        oneJarFile = new File(oneJarFile, "scan.cli.impl-standalone.jar");

        oneJarFile.setExecutable(true);

        return oneJarFile;
    }

    public File createHubVersionFile() throws HubIntegrationException, IOException {
        if (!directoryToInstallTo.exists() && !directoryToInstallTo.mkdirs()) {
            throw new HubIntegrationException("Could not create the directory : " + directoryToInstallTo.getCanonicalPath());
        }

        return new File(directoryToInstallTo, VERSION_FILE_NAME);
    }

    public File getCLIInstallDir() {
        return new File(directoryToInstallTo, CLI_UNZIP_DIR);
    }

    public String getCanonicalPath() throws IOException {
        return directoryToInstallTo.getCanonicalPath();
    }

    public File getCLIHome() throws IOException {
        return getCLIHome(logger);
    }

    private File getCLIHome(final IntLogger logger) throws IOException {
        final File cliHome = getCLIInstallDir();
        if (cliHome == null) {
            logger.error("Could not find the CLI home directory");
            return null;
        }
        final File[] installDirFiles = cliHome.listFiles();
        if (installDirFiles == null) {
            logger.error("Could not find the directories in the CLI home : " + cliHome.getCanonicalPath());
            return null;
        }
        if (installDirFiles.length > 1) {
            for (final File currentFile : installDirFiles) {
                if (!currentFile.getName().contains("windows") && !currentFile.isHidden()) {
                    return currentFile;
                }
            }
            return null;
        } else if (installDirFiles.length == 1) {
            return installDirFiles[0];
        } else {
            logger.error("Could not find any directories in the CLI home : " + cliHome.getCanonicalPath());
            return null;
        }
    }

    public File getProvidedJavaExec() throws IOException {
        final File javaHome = getJavaHome();
        File javaExec = new File(javaHome, "bin");
        if (SystemUtils.IS_OS_WINDOWS) {
            javaExec = new File(javaExec, "java.exe");
        } else {
            javaExec = new File(javaExec, "java");
        }
        if (!javaExec.exists()) {
            logger.error("The java executable does not exist at : " + javaExec.getCanonicalPath());
            return null;
        }

        javaExec.setExecutable(true);
        return javaExec;
    }

    public File getJavaHome() throws IOException {
        final File cliHomeFile = getCLIHome();
        if (cliHomeFile == null) {
            logger.error("Could not find the CLI home directory");
            return null;
        }

        final File[] files = cliHomeFile.listFiles();
        final File jreFolder = findFileByName(files, "jre");
        if (jreFolder == null) {
            logger.error("Could not find the JRE directory in : " + cliHomeFile.getCanonicalPath());
            return null;
        }

        final File javaHome = getJreContentsDirectory(jreFolder);
        return javaHome;
    }

    public boolean getCLIExists(final IntLogger logger) throws IOException {
        final File cli = getCLI(logger);
        return cli != null && cli.exists();
    }

    public File getCLI(final IntLogger logger) throws IOException {
        final File cliHomeFile = getCLIHome(logger);
        if (cliHomeFile == null) {
            logger.error("Could not find the CLI home directory");
            return null;
        }

        // find the lib folder in the iScan directory
        logger.debug("BlackDuck scan directory: " + cliHomeFile.getCanonicalPath());
        final File[] files = cliHomeFile.listFiles();
        if (files == null || files.length <= 0) {
            logger.error("No files found in the BlackDuck scan directory.");
            return null;
        }

        logger.debug("directories in the BlackDuck scan directory: " + files.length);
        final File libFolder = findFileByName(files, "lib");
        if (libFolder == null) {
            logger.error("Could not find the lib directory of the CLI.");
            return null;
        }

        logger.debug("BlackDuck scan lib directory: " + libFolder.getCanonicalPath());
        File hubScanJar = null;
        for (final File file : libFolder.listFiles()) {
            if (file.getName().startsWith("scan.cli") && file.getName().endsWith(".jar")) {
                hubScanJar = file;
                hubScanJar.setExecutable(true);
                break;
            }
        }

        return hubScanJar;
    }

    private File getJreContentsDirectory(final File jreFolder) {
        File jreContents = jreFolder;

        final List<String> filenames = Arrays.asList(jreContents.list());
        if (filenames.contains("Contents")) {
            jreContents = new File(jreContents, "Contents");
            jreContents = new File(jreContents, "Home");
        }

        return jreContents;
    }

    private File findFileByName(final File[] files, final String name) {
        if (files != null && files.length > 0) {
            for (final File file : files) {
                if (name.equalsIgnoreCase(file.getName())) {
                    return file;
                }
            }
        }
        return null;
    }

    private String getCLIWrapperLink(final String hubUrl, final String downloadFilename) {
        if (StringUtils.isBlank(hubUrl)) {
            throw new IllegalArgumentException("You must provide a valid Hub URL in order to get the correct link.");
        }
        final StringBuilder urlBuilder = new StringBuilder();
        urlBuilder.append(hubUrl);
        if (!hubUrl.endsWith("/")) {
            urlBuilder.append("/");
        }
        urlBuilder.append("download");
        urlBuilder.append("/");
        urlBuilder.append(downloadFilename);
        return urlBuilder.toString();
    }

    public File getDirectoryToInstallTo() {
        return directoryToInstallTo;
    }

}
