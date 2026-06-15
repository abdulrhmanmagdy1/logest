package com.edham.logistics.ui

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.edham.logistics.MainActivity
import com.edham.logistics.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI Tests for Report Generation Flow
 * Tests critical user interactions in Business Intelligence report generation and management
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class ReportGenerationUITest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun reportGeneration_viewReportsDashboard_shouldDisplayAllReports() {
        // Navigate to reports screen
        onView(withId(R.id.nav_reports))
            .perform(click())
        
        // Verify reports dashboard is displayed
        onView(withId(R.id.reportsContainer))
            .check(matches(isDisplayed()))
        
        // Verify quick report options are displayed
        onView(withId(R.id.btnRevenueReport))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.btnShipmentReport))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.btnDriverReport))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.btnCustomerReport))
            .check(matches(isDisplayed()))
        
        // Verify report history section
        onView(withId(R.id.rvReportHistory))
            .check(matches(isDisplayed()))
    }

    @Test
    fun reportGeneration_generateRevenueReport_shouldCreateReport() {
        // Navigate to reports screen
        onView(withId(R.id.nav_reports))
            .perform(click())
        
        // Click revenue report button
        onView(withId(R.id.btnRevenueReport))
            .perform(click())
        
        // Verify report configuration dialog is displayed
        onView(withText("Generate Revenue Report"))
            .check(matches(isDisplayed()))
        
        // Select report type
        onView(withId(R.id.spReportType))
            .perform(click())
        
        onView(withText("Detailed"))
            .perform(click())
        
        // Select date range
        onView(withId(R.id.btnSelectDateRange))
            .perform(click())
        
        // Select last 30 days
        onView(withText("Last 30 Days"))
            .perform(click())
        
        // Generate report
        onView(withId(R.id.btnGenerate))
            .perform(click())
        
        // Verify loading indicator is displayed
        onView(withId(R.id.progressBar))
            .check(matches(isDisplayed()))
        
        // Wait for report generation (in real test, use IdlingResource)
        Thread.sleep(3000)
        
        // Verify report results are displayed
        onView(withId(R.id.tvTotalRevenue))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.tvRevenueGrowth))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.chartRevenueByMonth))
            .check(matches(isDisplayed()))
    }

    @Test
    fun reportGeneration_generateShipmentReport_shouldCreateReport() {
        // Navigate to reports screen
        onView(withId(R.id.nav_reports))
            .perform(click())
        
        // Click shipment report button
        onView(withId(R.id.btnShipmentReport))
            .perform(click())
        
        // Verify report configuration dialog is displayed
        onView(withText("Generate Shipment Performance Report"))
            .check(matches(isDisplayed()))
        
        // Select date range
        onView(withId(R.id.btnSelectDateRange))
            .perform(click())
        
        onView(withText("Last 7 Days"))
            .perform(click())
        
        // Select performance metrics
        onView(withId(R.id.cbOnTimeDelivery))
            .perform(click())
        
        onView(withId(R.id.cbDeliveryByRegion))
            .perform(click())
        
        // Generate report
        onView(withId(R.id.btnGenerate))
            .perform(click())
        
        // Verify report results are displayed
        onView(withId(R.id.tvTotalShipments))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.tvOnTimeDeliveryRate))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.chartShipmentStatus))
            .check(matches(isDisplayed()))
    }

    @Test
    fun reportGeneration_generateDriverReport_shouldCreateReport() {
        // Navigate to reports screen
        onView(withId(R.id.nav_reports))
            .perform(click())
        
        // Click driver report button
        onView(withId(R.id.btnDriverReport))
            .perform(click())
        
        // Verify report configuration dialog is displayed
        onView(withText("Generate Driver Performance Report"))
            .check(matches(isDisplayed()))
        
        // Select drivers (multiple selection)
        onView(withId(R.id.btnSelectDrivers))
            .perform(click())
        
        // Select first driver
        onView(withId(R.id.cbDriver1))
            .perform(click())
        
        // Select second driver
        onView(withId(R.id.cbDriver2))
            .perform(click())
        
        // Confirm selection
        onView(withId(R.id.btnConfirm))
            .perform(click())
        
        // Select date range
        onView(withId(R.id.btnSelectDateRange))
            .perform(click())
        
        onView(withText("Last Month"))
            .perform(click())
        
        // Generate report
        onView(withId(R.id.btnGenerate))
            .perform(click())
        
        // Verify report results are displayed
        onView(withId(R.id.rvDriverPerformance))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.chartDriverComparison))
            .check(matches(isDisplayed()))
    }

    @Test
    fun reportGeneration_generateCustomerReport_shouldCreateReport() {
        // Navigate to reports screen
        onView(withId(R.id.nav_reports))
            .perform(click())
        
        // Click customer report button
        onView(withId(R.id.btnCustomerReport))
            .perform(click())
        
        // Verify report configuration dialog is displayed
        onView(withText("Generate Customer Behavior Report"))
            .check(matches(isDisplayed()))
        
        // Select customer segments
        onView(withId(R.id.btnSelectSegments))
            .perform(click())
        
        // Select premium segment
        onView(withId(R.id.cbPremiumSegment))
            .perform(click())
        
        // Select regular segment
        onView(withId(R.id.cbRegularSegment))
            .perform(click())
        
        // Confirm selection
        onView(withId(R.id.btnConfirm))
            .perform(click())
        
        // Select date range
        onView(withId(R.id.btnSelectDateRange))
            .perform(click())
        
        onView(withText("Last Quarter"))
            .perform(click())
        
        // Generate report
        onView(withId(R.id.btnGenerate))
            .perform(click())
        
        // Verify report results are displayed
        onView(withId(R.id.tvTotalCustomers))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.tvCustomerRetention))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.chartCustomerSegments))
            .check(matches(isDisplayed()))
    }

    @Test
    fun reportGeneration_exportReportToPDF_shouldGeneratePDF() {
        // Generate a report first
        onView(withId(R.id.nav_reports))
            .perform(click())
        
        onView(withId(R.id.btnRevenueReport))
            .perform(click())
        
        onView(withId(R.id.btnGenerate))
            .perform(click())
        
        // Wait for report generation
        Thread.sleep(3000)
        
        // Click export button
        onView(withId(R.id.btnExport))
            .perform(click())
        
        // Select PDF format
        onView(withText("PDF"))
            .perform(click())
        
        // Confirm export
        onView(withId(R.id.btnConfirmExport))
            .perform(click())
        
        // Verify success message
        onView(withText("Report exported to PDF successfully"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun reportGeneration_exportReportToExcel_shouldGenerateExcel() {
        // Generate a report first
        onView(withId(R.id.nav_reports))
            .perform(click())
        
        onView(withId(R.id.btnRevenueReport))
            .perform(click())
        
        onView(withId(R.id.btnGenerate))
            .perform(click())
        
        // Wait for report generation
        Thread.sleep(3000)
        
        // Click export button
        onView(withId(R.id.btnExport))
            .perform(click())
        
        // Select Excel format
        onView(withText("Excel"))
            .perform(click())
        
        // Confirm export
        onView(withId(R.id.btnConfirmExport))
            .perform(click())
        
        // Verify success message
        onView(withText("Report exported to Excel successfully"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun reportGeneration_scheduleReport_shouldCreateSchedule() {
        // Navigate to reports screen
        onView(withId(R.id.nav_reports))
            .perform(click())
        
        // Click schedule report button
        onView(withId(R.id.btnScheduleReport))
            .perform(click())
        
        // Verify schedule dialog is displayed
        onView(withText("Schedule Report"))
            .check(matches(isDisplayed()))
        
        // Select report type
        onView(withId(R.id.spReportType))
            .perform(click())
        
        onView(withText("Revenue Report"))
            .perform(click())
        
        // Select frequency
        onView(withId(R.id.spFrequency))
            .perform(click())
        
        onView(withText("Weekly"))
            .perform(click())
        
        // Select day of week
        onView(withId(R.id.spDayOfWeek))
            .perform(click())
        
        onView(withText("Monday"))
            .perform(click())
        
        // Select time
        onView(withId(R.id.btnSelectTime))
            .perform(click())
        
        // Select 9:00 AM
        onView(withText("9:00 AM"))
            .perform(click())
        
        // Add recipients
        onView(withId(R.id.btnAddRecipient))
            .perform(click())
        
        // Enter email
        onView(withId(R.id.etRecipientEmail))
            .perform(typeText("manager@company.com"), closeSoftKeyboard())
        
        // Add recipient
        onView(withId(R.id.btnAdd))
            .perform(click())
        
        // Create schedule
        onView(withId(R.id.btnCreateSchedule))
            .perform(click())
        
        // Verify success message
        onView(withText("Report scheduled successfully"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun reportGeneration_viewScheduledReports_shouldDisplaySchedules() {
        // Navigate to reports screen
        onView(withId(R.id.nav_reports))
            .perform(click())
        
        // Click scheduled reports tab
        onView(withId(R.id.tabScheduledReports))
            .perform(click())
        
        // Verify scheduled reports list is displayed
        onView(withId(R.id.rvScheduledReports))
            .check(matches(isDisplayed()))
        
        // Verify schedule details are displayed
        onView(withId(R.id.tvReportType))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.tvFrequency))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.tvNextRun))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.tvRecipients))
            .check(matches(isDisplayed()))
    }

    @Test
    fun reportGeneration_cancelScheduledReport_shouldCancelSchedule() {
        // Navigate to scheduled reports
        onView(withId(R.id.nav_reports))
            .perform(click())
        
        onView(withId(R.id.tabScheduledReports))
            .perform(click())
        
        // Click on first scheduled report
        onView(withId(R.id.rvScheduledReports))
            .perform(click())
        
        // Click cancel button
        onView(withId(R.id.btnCancelSchedule))
            .perform(click())
        
        // Confirm cancellation
        onView(withText("Cancel"))
            .perform(click())
        
        // Verify success message
        onView(withText("Schedule cancelled successfully"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun reportGeneration_viewReportHistory_shouldDisplayPreviousReports() {
        // Navigate to reports screen
        onView(withId(R.id.nav_reports))
            .perform(click())
        
        // Verify report history section is displayed
        onView(withId(R.id.rvReportHistory))
            .check(matches(isDisplayed()))
        
        // Verify report details are displayed
        onView(withId(R.id.tvReportTitle))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.tvReportType))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.tvReportDate))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.tvReportStatus))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.tvFileSize))
            .check(matches(isDisplayed()))
    }

    @Test
    fun reportGeneration_clickHistoricalReport_shouldViewReport() {
        // Navigate to reports screen
        onView(withId(R.id.nav_reports))
            .perform(click())
        
        // Click on first report in history
        onView(withId(R.id.rvReportHistory))
            .perform(click())
        
        // Verify report details are displayed
        onView(withId(R.id.reportDetailsContainer))
            .check(matches(isDisplayed()))
        
        // Verify report content is displayed
        onView(withId(R.id.tvReportTitle))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.tvReportPeriod))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.tvGeneratedAt))
            .check(matches(isDisplayed()))
    }

    @Test
    fun reportGeneration_deleteHistoricalReport_shouldDeleteReport() {
        // Navigate to reports screen
        onView(withId(R.id.nav_reports))
            .perform(click())
        
        // Long press on first report in history
        onView(withId(R.id.rvReportHistory))
            .perform(longClick())
        
        // Click delete option
        onView(withText("Delete"))
            .perform(click())
        
        // Confirm deletion
        onView(withText("Delete"))
            .perform(click())
        
        // Verify success message
        onView(withText("Report deleted successfully"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun reportGeneration_createCustomReport_shouldNavigateToCustomReportBuilder() {
        // Navigate to reports screen
        onView(withId(R.id.nav_reports))
            .perform(click())
        
        // Click custom report button
        onView(withId(R.id.btnCustomReport))
            .perform(click())
        
        // Verify custom report builder is displayed
        onView(withId(R.id.customReportContainer))
            .check(matches(isDisplayed()))
        
        // Verify report builder options
        onView(withId(R.id.spDataSource))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.spReportType))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.rvAvailableMetrics))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.rvSelectedMetrics))
            .check(matches(isDisplayed()))
    }

    @Test
    fun reportGeneration_buildCustomReport_shouldCreateCustomReport() {
        // Navigate to custom report builder
        onView(withId(R.id.nav_reports))
            .perform(click())
        
        onView(withId(R.id.btnCustomReport))
            .perform(click())
        
        // Select data source
        onView(withId(R.id.spDataSource))
            .perform(click())
        
        onView(withText("Shipments"))
            .perform(click())
        
        // Select report type
        onView(withId(R.id.spReportType))
            .perform(click())
        
        onView(withText("Summary"))
            .perform(click())
        
        // Add metrics
        onView(withId(R.id.btnAddMetric))
            .perform(click())
        
        // Select first metric
        onView(withId(R.id.cbTotalShipments))
            .perform(click())
        
        // Select second metric
        onView(withId(R.id.cbAverageDeliveryTime))
            .perform(click())
        
        // Confirm metrics selection
        onView(withId(R.id.btnConfirm))
            .perform(click())
        
        // Select date range
        onView(withId(R.id.btnSelectDateRange))
            .perform(click())
        
        onView(withText("Last 30 Days"))
            .perform(click())
        
        // Generate custom report
        onView(withId(R.id.btnGenerateCustom))
            .perform(click())
        
        // Verify custom report is displayed
        onView(withId(R.id.customReportResults))
            .check(matches(isDisplayed()))
        
        // Verify selected metrics are displayed
        onView(withId(R.id.tvTotalShipments))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.tvAverageDeliveryTime))
            .check(matches(isDisplayed()))
    }

    @Test
    fun reportGeneration_saveReportTemplate_shouldCreateTemplate() {
        // Generate a report first
        onView(withId(R.id.nav_reports))
            .perform(click())
        
        onView(withId(R.id.btnRevenueReport))
            .perform(click())
        
        // Configure report
        onView(withId(R.id.btnSelectDateRange))
            .perform(click())
        
        onView(withText("Last Month"))
            .perform(click())
        
        // Save as template
        onView(withId(R.id.btnSaveAsTemplate))
            .perform(click())
        
        // Verify save template dialog
        onView(withText("Save Report Template"))
            .check(matches(isDisplayed()))
        
        // Enter template name
        onView(withId(R.id.etTemplateName))
            .perform(typeText("Monthly Revenue Report"), closeSoftKeyboard())
        
        // Enter template description
        onView(withId(R.id.etTemplateDescription))
            .perform(typeText("Monthly revenue analysis with growth trends"), closeSoftKeyboard())
        
        // Save template
        onView(withId(R.id.btnSave))
            .perform(click())
        
        // Verify success message
        onView(withText("Template saved successfully"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun reportGeneration_loadReportTemplate_shouldLoadConfiguration() {
        // Navigate to reports screen
        onView(withId(R.id.nav_reports))
            .perform(click())
        
        // Click templates button
        onView(withId(R.id.btnTemplates))
            .perform(click())
        
        // Verify templates list is displayed
        onView(withId(R.id.rvTemplates))
            .check(matches(isDisplayed()))
        
        // Click on first template
        onView(withId(R.id.rvTemplates))
            .perform(click())
        
        // Verify template configuration is loaded
        onView(withId(R.id.tvTemplateName))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.tvTemplateDescription))
            .check(matches(isDisplayed()))
        
        // Generate report from template
        onView(withId(R.id.btnGenerateFromTemplate))
            .perform(click())
        
        // Verify report is generated with template settings
        onView(withId(R.id.reportResults))
            .check(matches(isDisplayed()))
    }

    @Test
    fun reportGeneration_shareReport_shouldOpenShareDialog() {
        // Generate a report first
        onView(withId(R.id.nav_reports))
            .perform(click())
        
        onView(withId(R.id.btnRevenueReport))
            .perform(click())
        
        onView(withId(R.id.btnGenerate))
            .perform(click())
        
        // Wait for report generation
        Thread.sleep(3000)
        
        // Click share button
        onView(withId(R.id.btnShare))
            .perform(click())
        
        // Verify share dialog is displayed
        onView(withText("Share Report"))
            .check(matches(isDisplayed()))
        
        // Verify share options
        onView(withId(R.id.btnShareEmail))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.btnShareLink))
            .check(matches(isDisplayed()))
        
        // Click share via email
        onView(withId(R.id.btnShareEmail))
            .perform(click())
        
        // Verify email composition dialog
        onView(withId(R.id.etRecipientEmail))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.etSubject))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.etMessage))
            .check(matches(isDisplayed()))
    }

    @Test
    fun reportGeneration_printReport_shouldOpenPrintDialog() {
        // Generate a report first
        onView(withId(R.id.nav_reports))
            .perform(click())
        
        onView(withId(R.id.btnRevenueReport))
            .perform(click())
        
        onView(withId(R.id.btnGenerate))
            .perform(click())
        
        // Wait for report generation
        Thread.sleep(3000)
        
        // Click print button
        onView(withId(R.id.btnPrint))
            .perform(click())
        
        // Verify print dialog is displayed
        onView(withText("Print Report"))
            .check(matches(isDisplayed()))
        
        // Verify print options
        onView(withId(R.id.spPrinter))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.spPaperSize))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.cbColorPrint))
            .check(matches(isDisplayed()))
        
        // Select printer
        onView(withId(R.id.spPrinter))
            .perform(click())
        
        onView(withText("Office Printer"))
            .perform(click())
        
        // Print report
        onView(withId(R.id.btnPrint))
            .perform(click())
        
        // Verify success message
        onView(withText("Report sent to printer"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun reportGeneration_refreshDashboard_shouldUpdateData() {
        // Navigate to reports screen
        onView(withId(R.id.nav_reports))
            .perform(click())
        
        // Pull to refresh
        onView(withId(R.id.swipeRefreshLayout))
            .perform(swipeDown())
        
        // Verify loading indicator appears
        onView(withId(R.id.progressBar))
            .check(matches(isDisplayed()))
        
        // Wait for refresh to complete
        Thread.sleep(2000)
        
        // Verify dashboard is updated
        onView(withId(R.id.reportsContainer))
            .check(matches(isDisplayed()))
    }

    @Test
    fun reportGeneration_filterReportHistory_shouldFilterReports() {
        // Navigate to reports screen
        onView(withId(R.id.nav_reports))
            .perform(click())
        
        // Click filter button for report history
        onView(withId(R.id.btnFilterHistory))
            .perform(click())
        
        // Verify filter dialog is displayed
        onView(withText("Filter Report History"))
            .check(matches(isDisplayed()))
        
        // Select report type filter
        onView(withId(R.id.spReportType))
            .perform(click())
        
        onView(withText("Revenue Report"))
            .perform(click())
        
        // Select date range filter
        onView(withId(R.id.spDateRange))
            .perform(click())
        
        onView(withText("Last 7 Days"))
            .perform(click())
        
        // Apply filter
        onView(withId(R.id.btnApplyFilter))
            .perform(click())
        
        // Verify filtered results
        onView(withId(R.id.rvReportHistory))
            .check(matches(isDisplayed()))
    }
}
