package com.blackducksoftware.integration.hub.report.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.blackducksoftware.integration.hub.report.risk.api.RiskCategories;
import com.blackducksoftware.integration.hub.report.risk.api.RiskCounts;
import com.blackducksoftware.integration.hub.report.risk.api.RiskProfile;
import com.blackducksoftware.integration.hub.util.TestUtils;
import com.blackducksoftware.integration.hub.util.XStreamHelperTest;

public class HubRiskReportDataTest {
    @Test
    public void testSetReportHighRisks() {
        RiskCounts counts = new RiskCounts(1, 0, 0, 0, 0);
        RiskCategories categories = new RiskCategories(counts, counts, counts, counts, counts);
        RiskProfile riskProfile = new RiskProfile(0, categories);
        AggregateBomViewEntry bomEntry = new AggregateBomViewEntry(null, null, null, null, null, null, null, null, null, null, null, null, riskProfile);
        List<AggregateBomViewEntry> aggregateBomViewEntries = new ArrayList<AggregateBomViewEntry>();
        aggregateBomViewEntries.add(bomEntry);
        VersionReport report = new VersionReport(null, aggregateBomViewEntries);
        HubRiskReportData hubRiskReportData = new HubRiskReportData();
        hubRiskReportData.setReport(report);

        assertEquals(1, hubRiskReportData.getVulnerabilityRiskHighCount());
        assertEquals(0, hubRiskReportData.getVulnerabilityRiskMediumCount());
        assertEquals(0, hubRiskReportData.getVulnerabilityRiskLowCount());
        assertEquals(0, hubRiskReportData.getVulnerabilityRiskNoneCount());

        assertEquals(1, hubRiskReportData.getLicenseRiskHighCount());
        assertEquals(0, hubRiskReportData.getLicenseRiskMediumCount());
        assertEquals(0, hubRiskReportData.getLicenseRiskLowCount());
        assertEquals(0, hubRiskReportData.getLicenseRiskNoneCount());

        assertEquals(1, hubRiskReportData.getOperationalRiskHighCount());
        assertEquals(0, hubRiskReportData.getOperationalRiskMediumCount());
        assertEquals(0, hubRiskReportData.getOperationalRiskLowCount());
        assertEquals(0, hubRiskReportData.getOperationalRiskNoneCount());
    }

    @Test
    public void testSetReportSecurityHighRisks() {
        RiskCounts counts = new RiskCounts(1, 1, 1, 1, 1);
        RiskCategories categories = new RiskCategories(counts, counts, counts, counts, counts);
        RiskProfile riskProfile = new RiskProfile(0, categories);
        AggregateBomViewEntry bomEntry = new AggregateBomViewEntry(null, null, null, null, null, null, null, null, null, null, null, null, riskProfile);
        List<AggregateBomViewEntry> aggregateBomViewEntries = new ArrayList<AggregateBomViewEntry>();
        aggregateBomViewEntries.add(bomEntry);
        VersionReport report = new VersionReport(null, aggregateBomViewEntries);
        HubRiskReportData hubRiskReportData = new HubRiskReportData();
        hubRiskReportData.setReport(report);

        assertEquals(1, hubRiskReportData.getVulnerabilityRiskHighCount());
        assertEquals(0, hubRiskReportData.getVulnerabilityRiskMediumCount());
        assertEquals(0, hubRiskReportData.getVulnerabilityRiskLowCount());
        assertEquals(0, hubRiskReportData.getVulnerabilityRiskNoneCount());
    }

    @Test
    public void testSetReportSecurityMediumRisks() {
        RiskCounts counts = new RiskCounts(0, 1, 1, 1, 1);
        RiskCategories categories = new RiskCategories(counts, counts, counts, counts, counts);
        RiskProfile riskProfile = new RiskProfile(0, categories);
        AggregateBomViewEntry bomEntry = new AggregateBomViewEntry(null, null, null, null, null, null, null, null, null, null, null, null, riskProfile);
        List<AggregateBomViewEntry> aggregateBomViewEntries = new ArrayList<AggregateBomViewEntry>();
        aggregateBomViewEntries.add(bomEntry);
        VersionReport report = new VersionReport(null, aggregateBomViewEntries);
        HubRiskReportData hubRiskReportData = new HubRiskReportData();
        hubRiskReportData.setReport(report);

        assertEquals(0, hubRiskReportData.getVulnerabilityRiskHighCount());
        assertEquals(1, hubRiskReportData.getVulnerabilityRiskMediumCount());
        assertEquals(0, hubRiskReportData.getVulnerabilityRiskLowCount());
        assertEquals(0, hubRiskReportData.getVulnerabilityRiskNoneCount());
    }

    @Test
    public void testSetReportMediumRisks() {
        RiskCounts counts = new RiskCounts(0, 1, 0, 0, 0);
        RiskCategories categories = new RiskCategories(counts, counts, counts, counts, counts);
        RiskProfile riskProfile = new RiskProfile(0, categories);
        AggregateBomViewEntry bomEntry = new AggregateBomViewEntry(null, null, null, null, null, null, null, null, null, null, null, null, riskProfile);
        List<AggregateBomViewEntry> aggregateBomViewEntries = new ArrayList<AggregateBomViewEntry>();
        aggregateBomViewEntries.add(bomEntry);
        VersionReport report = new VersionReport(null, aggregateBomViewEntries);
        HubRiskReportData hubRiskReportData = new HubRiskReportData();
        hubRiskReportData.setReport(report);

        assertEquals(0, hubRiskReportData.getVulnerabilityRiskHighCount());
        assertEquals(1, hubRiskReportData.getVulnerabilityRiskMediumCount());
        assertEquals(0, hubRiskReportData.getVulnerabilityRiskLowCount());
        assertEquals(0, hubRiskReportData.getVulnerabilityRiskNoneCount());

        assertEquals(0, hubRiskReportData.getLicenseRiskHighCount());
        assertEquals(1, hubRiskReportData.getLicenseRiskMediumCount());
        assertEquals(0, hubRiskReportData.getLicenseRiskLowCount());
        assertEquals(0, hubRiskReportData.getLicenseRiskNoneCount());

        assertEquals(0, hubRiskReportData.getOperationalRiskHighCount());
        assertEquals(1, hubRiskReportData.getOperationalRiskMediumCount());
        assertEquals(0, hubRiskReportData.getOperationalRiskLowCount());
        assertEquals(0, hubRiskReportData.getOperationalRiskNoneCount());
    }

    @Test
    public void testSetReportLowRisks() {
        RiskCounts counts = new RiskCounts(0, 0, 1, 0, 0);
        RiskCategories categories = new RiskCategories(counts, counts, counts, counts, counts);
        RiskProfile riskProfile = new RiskProfile(0, categories);
        AggregateBomViewEntry bomEntry = new AggregateBomViewEntry(null, null, null, null, null, null, null, null, null, null, null, null, riskProfile);
        List<AggregateBomViewEntry> aggregateBomViewEntries = new ArrayList<AggregateBomViewEntry>();
        aggregateBomViewEntries.add(bomEntry);
        VersionReport report = new VersionReport(null, aggregateBomViewEntries);
        HubRiskReportData hubRiskReportData = new HubRiskReportData();
        hubRiskReportData.setReport(report);

        assertEquals(0, hubRiskReportData.getVulnerabilityRiskHighCount());
        assertEquals(0, hubRiskReportData.getVulnerabilityRiskMediumCount());
        assertEquals(1, hubRiskReportData.getVulnerabilityRiskLowCount());
        assertEquals(0, hubRiskReportData.getVulnerabilityRiskNoneCount());

        assertEquals(0, hubRiskReportData.getLicenseRiskHighCount());
        assertEquals(0, hubRiskReportData.getLicenseRiskMediumCount());
        assertEquals(1, hubRiskReportData.getLicenseRiskLowCount());
        assertEquals(0, hubRiskReportData.getLicenseRiskNoneCount());

        assertEquals(0, hubRiskReportData.getOperationalRiskHighCount());
        assertEquals(0, hubRiskReportData.getOperationalRiskMediumCount());
        assertEquals(1, hubRiskReportData.getOperationalRiskLowCount());
        assertEquals(0, hubRiskReportData.getOperationalRiskNoneCount());
    }

    @Test
    public void testSetReportNoneRisks() {
        RiskCounts counts = new RiskCounts(0, 0, 0, 0, 0);
        RiskCategories categories = new RiskCategories(counts, counts, counts, counts, counts);
        RiskProfile riskProfile = new RiskProfile(0, categories);
        AggregateBomViewEntry bomEntry = new AggregateBomViewEntry(null, null, null, null, null, null, null, null, null, null, null, null, riskProfile);
        List<AggregateBomViewEntry> aggregateBomViewEntries = new ArrayList<AggregateBomViewEntry>();
        aggregateBomViewEntries.add(bomEntry);
        VersionReport report = new VersionReport(null, aggregateBomViewEntries);
        HubRiskReportData hubRiskReportData = new HubRiskReportData();
        hubRiskReportData.setReport(report);

        assertEquals(0, hubRiskReportData.getVulnerabilityRiskHighCount());
        assertEquals(0, hubRiskReportData.getVulnerabilityRiskMediumCount());
        assertEquals(0, hubRiskReportData.getVulnerabilityRiskLowCount());
        assertEquals(1, hubRiskReportData.getVulnerabilityRiskNoneCount());

        assertEquals(0, hubRiskReportData.getLicenseRiskHighCount());
        assertEquals(0, hubRiskReportData.getLicenseRiskMediumCount());
        assertEquals(0, hubRiskReportData.getLicenseRiskLowCount());
        assertEquals(1, hubRiskReportData.getLicenseRiskNoneCount());

        assertEquals(0, hubRiskReportData.getOperationalRiskHighCount());
        assertEquals(0, hubRiskReportData.getOperationalRiskMediumCount());
        assertEquals(0, hubRiskReportData.getOperationalRiskLowCount());
        assertEquals(1, hubRiskReportData.getOperationalRiskNoneCount());
    }

    @Test
    public void testReadingFromInputStream() {
        HubRiskReportData hubRiskReportData = new HubRiskReportData();
        TestUtils.setValue(HubRiskReportData.class, hubRiskReportData, "vulnerabilityRiskHighCount", 1000);
        TestUtils.setValue(HubRiskReportData.class, hubRiskReportData, "vulnerabilityRiskMediumCount", 2000);
        TestUtils.setValue(HubRiskReportData.class, hubRiskReportData, "vulnerabilityRiskLowCount", 3000);
        TestUtils.setValue(HubRiskReportData.class, hubRiskReportData, "vulnerabilityRiskNoneCount", 4000);

        TestUtils.setValue(HubRiskReportData.class, hubRiskReportData, "licenseRiskHighCount", 5000);
        TestUtils.setValue(HubRiskReportData.class, hubRiskReportData, "licenseRiskMediumCount", 6000);
        TestUtils.setValue(HubRiskReportData.class, hubRiskReportData, "licenseRiskLowCount", 7000);
        TestUtils.setValue(HubRiskReportData.class, hubRiskReportData, "licenseRiskNoneCount", 8000);

        TestUtils.setValue(HubRiskReportData.class, hubRiskReportData, "operationalRiskHighCount", 9000);
        TestUtils.setValue(HubRiskReportData.class, hubRiskReportData, "operationalRiskMediumCount", 10000);
        TestUtils.setValue(HubRiskReportData.class, hubRiskReportData, "operationalRiskLowCount", 11000);
        TestUtils.setValue(HubRiskReportData.class, hubRiskReportData, "operationalRiskNoneCount", 12000);

        assertEquals(1000, hubRiskReportData.getVulnerabilityRiskHighCount());
        assertEquals(2000, hubRiskReportData.getVulnerabilityRiskMediumCount());
        assertEquals(3000, hubRiskReportData.getVulnerabilityRiskLowCount());
        assertEquals(4000, hubRiskReportData.getVulnerabilityRiskNoneCount());

        assertEquals(5000, hubRiskReportData.getLicenseRiskHighCount());
        assertEquals(6000, hubRiskReportData.getLicenseRiskMediumCount());
        assertEquals(7000, hubRiskReportData.getLicenseRiskLowCount());
        assertEquals(8000, hubRiskReportData.getLicenseRiskNoneCount());

        assertEquals(9000, hubRiskReportData.getOperationalRiskHighCount());
        assertEquals(10000, hubRiskReportData.getOperationalRiskMediumCount());
        assertEquals(11000, hubRiskReportData.getOperationalRiskLowCount());
        assertEquals(12000, hubRiskReportData.getOperationalRiskNoneCount());

        InputStream inputStream = TestUtils.getInputStreamFromClasspathFile(XStreamHelperTest.toReadClasspathEntry);
        hubRiskReportData.readFromInputStream(inputStream);
        IOUtils.closeQuietly(inputStream);

        assertEquals(13, hubRiskReportData.getVulnerabilityRiskHighCount());
        assertEquals(14, hubRiskReportData.getVulnerabilityRiskMediumCount());
        assertEquals(15, hubRiskReportData.getVulnerabilityRiskLowCount());
        assertEquals(16, hubRiskReportData.getVulnerabilityRiskNoneCount());

        assertEquals(17, hubRiskReportData.getLicenseRiskHighCount());
        assertEquals(18, hubRiskReportData.getLicenseRiskMediumCount());
        assertEquals(19, hubRiskReportData.getLicenseRiskLowCount());
        assertEquals(20, hubRiskReportData.getLicenseRiskNoneCount());

        assertEquals(21, hubRiskReportData.getOperationalRiskHighCount());
        assertEquals(22, hubRiskReportData.getOperationalRiskMediumCount());
        assertEquals(23, hubRiskReportData.getOperationalRiskLowCount());
        assertEquals(24, hubRiskReportData.getOperationalRiskNoneCount());
    }

    @Test
    public void testWritingToOutputStream() {
        HubRiskReportData hubRiskReportData = new HubRiskReportData();
        TestUtils.setValue(HubRiskReportData.class, hubRiskReportData, "vulnerabilityRiskHighCount", 1000);
        TestUtils.setValue(HubRiskReportData.class, hubRiskReportData, "vulnerabilityRiskMediumCount", 2000);
        TestUtils.setValue(HubRiskReportData.class, hubRiskReportData, "vulnerabilityRiskLowCount", 3000);
        TestUtils.setValue(HubRiskReportData.class, hubRiskReportData, "vulnerabilityRiskNoneCount", 4000);

        TestUtils.setValue(HubRiskReportData.class, hubRiskReportData, "licenseRiskHighCount", 5000);
        TestUtils.setValue(HubRiskReportData.class, hubRiskReportData, "licenseRiskMediumCount", 6000);
        TestUtils.setValue(HubRiskReportData.class, hubRiskReportData, "licenseRiskLowCount", 7000);
        TestUtils.setValue(HubRiskReportData.class, hubRiskReportData, "licenseRiskNoneCount", 8000);

        TestUtils.setValue(HubRiskReportData.class, hubRiskReportData, "operationalRiskHighCount", 9000);
        TestUtils.setValue(HubRiskReportData.class, hubRiskReportData, "operationalRiskMediumCount", 10000);
        TestUtils.setValue(HubRiskReportData.class, hubRiskReportData, "operationalRiskLowCount", 11000);
        TestUtils.setValue(HubRiskReportData.class, hubRiskReportData, "operationalRiskNoneCount", 12000);

        OutputStream outputStream = new ByteArrayOutputStream();
        hubRiskReportData.writeToOutputStream(outputStream);
        String xml = outputStream.toString();
        IOUtils.closeQuietly(outputStream);

        assertTrue(xml.contains("vulnerabilityRiskHighCount"));
        assertTrue(xml.contains("1000"));
        assertTrue(xml.contains("operationalRiskNoneCount"));
        assertTrue(xml.contains("12000"));
    }

}