package com.edham.logistics.ui

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.edham.logistics.MainActivity
import com.edham.logistics.R
import com.edham.logistics.presentation.invoices.InvoiceAdapter
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI Tests for Invoice Management Flow
 * Tests critical user interactions in invoice creation, payment, and management
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class InvoiceManagementUITest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun invoiceManagement_viewInvoiceList_shouldDisplayAllInvoices() {
        // Navigate to invoices screen
        onView(withId(R.id.nav_invoices))
            .perform(click())
        
        // Verify invoice list is displayed
        onView(withId(R.id.rvInvoices))
            .check(matches(isDisplayed()))
        
        // Verify at least one invoice is in the list
        onView(withId(R.id.rvInvoices))
            .perform(RecyclerViewActions.scrollToPosition<InvoiceAdapter.InvoiceViewHolder>(0))
        
        onView(withId(R.id.tvInvoiceNumber))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.tvInvoiceAmount))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.tvInvoiceStatus))
            .check(matches(isDisplayed()))
    }

    @Test
    fun invoiceManagement_clickInvoice_shouldNavigateToDetails() {
        // Navigate to invoices screen
        onView(withId(R.id.nav_invoices))
            .perform(click())
        
        // Click on first invoice in list
        onView(withId(R.id.rvInvoices))
            .perform(RecyclerViewActions.actionOnItemAtPosition<InvoiceAdapter.InvoiceViewHolder>(0, click()))
        
        // Verify navigation to invoice details
        onView(withId(R.id.invoiceDetailsContainer))
            .check(matches(isDisplayed()))
        
        // Verify invoice details are displayed
        onView(withId(R.id.tvInvoiceNumber))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.tvInvoiceDate))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.tvDueDate))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.tvTotalAmount))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.tvInvoiceStatus))
            .check(matches(isDisplayed()))
    }

    @Test
    fun invoiceManagement_filterByStatus_shouldFilterInvoices() {
        // Navigate to invoices screen
        onView(withId(R.id.nav_invoices))
            .perform(click())
        
        // Click filter button
        onView(withId(R.id.btnFilter))
            .perform(click())
        
        // Select "Pending" status
        onView(withText("Pending"))
            .perform(click())
        
        // Apply filter
        onView(withId(R.id.btnApplyFilter))
            .perform(click())
        
        // Verify filtered results
        onView(withId(R.id.rvInvoices))
            .check(matches(isDisplayed()))
        
        // Verify all visible invoices have "Pending" status
        onView(withId(R.id.tvInvoiceStatus))
            .check(matches(withText("Pending")))
    }

    @Test
    fun invoiceManagement_searchByInvoiceNumber_shouldFindInvoice() {
        // Navigate to invoices screen
        onView(withId(R.id.nav_invoices))
            .perform(click())
        
        // Enter invoice number
        onView(withId(R.id.etSearchInvoice))
            .perform(typeText("INV-2024-001"), closeSoftKeyboard())
        
        // Click search button
        onView(withId(R.id.btnSearch))
            .perform(click())
        
        // Verify search results
        onView(withId(R.id.rvInvoices))
            .check(matches(isDisplayed()))
        
        // Verify the searched invoice is displayed
        onView(withId(R.id.tvInvoiceNumber))
            .check(matches(withText("INV-2024-001")))
    }

    @Test
    fun invoiceManagement_payInvoice_shouldNavigateToPaymentScreen() {
        // Navigate to invoice details
        onView(withId(R.id.nav_invoices))
            .perform(click())
        
        onView(withId(R.id.rvInvoices))
            .perform(RecyclerViewActions.actionOnItemAtPosition<InvoiceAdapter.InvoiceViewHolder>(0, click()))
        
        // Click pay button
        onView(withId(R.id.btnPayInvoice))
            .perform(click())
        
        // Verify navigation to payment screen
        onView(withId(R.id.paymentContainer))
            .check(matches(isDisplayed()))
        
        // Verify payment details are displayed
        onView(withId(R.id.tvPaymentAmount))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.etCardNumber))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.etExpiryDate))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.etCVV))
            .check(matches(isDisplayed()))
    }

    @Test
    fun invoiceManagement_processPayment_withValidCard_shouldShowSuccess() {
        // Navigate to payment screen
        onView(withId(R.id.nav_invoices))
            .perform(click())
        
        onView(withId(R.id.rvInvoices))
            .perform(RecyclerViewActions.actionOnItemAtPosition<InvoiceAdapter.InvoiceViewHolder>(0, click()))
        
        onView(withId(R.id.btnPayInvoice))
            .perform(click())
        
        // Enter card details
        onView(withId(R.id.etCardNumber))
            .perform(typeText("4111111111111111"), closeSoftKeyboard())
        
        onView(withId(R.id.etExpiryDate))
            .perform(typeText("12/25"), closeSoftKeyboard())
        
        onView(withId(R.id.etCVV))
            .perform(typeText("123"), closeSoftKeyboard())
        
        onView(withId(R.id.etCardholderName))
            .perform(typeText("Test User"), closeSoftKeyboard())
        
        // Click pay button
        onView(withId(R.id.btnConfirmPayment))
            .perform(click())
        
        // Verify payment success message
        onView(withText("Payment processed successfully"))
            .check(matches(isDisplayed()))
        
        // Verify invoice status is updated to "Paid"
        onView(withId(R.id.tvInvoiceStatus))
            .check(matches(withText("Paid")))
    }

    @Test
    fun invoiceManagement_processPayment_withInvalidCard_shouldShowError() {
        // Navigate to payment screen
        onView(withId(R.id.nav_invoices))
            .perform(click())
        
        onView(withId(R.id.rvInvoices))
            .perform(RecyclerViewActions.actionOnItemAtPosition<InvoiceAdapter.InvoiceViewHolder>(0, click()))
        
        onView(withId(R.id.btnPayInvoice))
            .perform(click())
        
        // Enter invalid card details
        onView(withId(R.id.etCardNumber))
            .perform(typeText("1234567890123456"), closeSoftKeyboard())
        
        onView(withId(R.id.etExpiryDate))
            .perform(typeText("12/20"), closeSoftKeyboard()) // Expired
        
        onView(withId(R.id.etCVV))
            .perform(typeText("123"), closeSoftKeyboard())
        
        // Click pay button
        onView(withId(R.id.btnConfirmPayment))
            .perform(click())
        
        // Verify payment error message
        onView(withId(R.id.tvPaymentError))
            .check(matches(isDisplayed()))
            .check(matches(withText("Invalid card details")))
    }

    @Test
    fun invoiceManagement_exportInvoiceToPDF_shouldGeneratePDF() {
        // Navigate to invoice details
        onView(withId(R.id.nav_invoices))
            .perform(click())
        
        onView(withId(R.id.rvInvoices))
            .perform(RecyclerViewActions.actionOnItemAtPosition<InvoiceAdapter.InvoiceViewHolder>(0, click()))
        
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
        onView(withText("Invoice exported successfully"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun invoiceManagement_sendInvoiceReminder_shouldSendEmail() {
        // Navigate to invoice details
        onView(withId(R.id.nav_invoices))
            .perform(click())
        
        onView(withId(R.id.rvInvoices))
            .perform(RecyclerViewActions.actionOnItemAtPosition<InvoiceAdapter.InvoiceViewHolder>(0, click()))
        
        // Click send reminder button
        onView(withId(R.id.btnSendReminder))
            .perform(click())
        
        // Verify reminder dialog is displayed
        onView(withText("Send Payment Reminder"))
            .check(matches(isDisplayed()))
        
        // Verify customer email is pre-filled
        onView(withId(R.id.etRecipientEmail))
            .check(matches(isDisplayed()))
        
        // Add custom message
        onView(withId(R.id.etCustomMessage))
            .perform(typeText("This is a friendly reminder about your pending payment."), closeSoftKeyboard())
        
        // Send reminder
        onView(withId(R.id.btnSend))
            .perform(click())
        
        // Verify success message
        onView(withText("Reminder sent successfully"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun invoiceManagement_createInvoice_shouldNavigateToCreateForm() {
        // Navigate to invoices screen
        onView(withId(R.id.nav_invoices))
            .perform(click())
        
        // Click create invoice button
        onView(withId(R.id.btnCreateInvoice))
            .perform(click())
        
        // Verify navigation to create invoice form
        onView(withId(R.id.createInvoiceContainer))
            .check(matches(isDisplayed()))
        
        // Verify all form fields are present
        onView(withId(R.id.etCustomerName)).check(matches(isDisplayed()))
        onView(withId(R.id.etCustomerEmail)).check(matches(isDisplayed()))
        onView(withId(R.id.etAmount)).check(matches(isDisplayed()))
        onView(withId(R.id.etTax)).check(matches(isDisplayed()))
        onView(withId(R.id.etDueDate)).check(matches(isDisplayed()))
        onView(withId(R.id.etDescription)).check(matches(isDisplayed()))
    }

    @Test
    fun invoiceManagement_createInvoice_withValidData_shouldCreateInvoice() {
        // Navigate to create invoice form
        onView(withId(R.id.nav_invoices))
            .perform(click())
        
        onView(withId(R.id.btnCreateInvoice))
            .perform(click())
        
        // Fill in invoice details
        onView(withId(R.id.etCustomerName))
            .perform(typeText("Ahmed Mohammed"), closeSoftKeyboard())
        
        onView(withId(R.id.etCustomerEmail))
            .perform(typeText("ahmed@example.com"), closeSoftKeyboard())
        
        onView(withId(R.id.etAmount))
            .perform(typeText("1000.00"), closeSoftKeyboard())
        
        onView(withId(R.id.etTax))
            .perform(typeText("150.00"), closeSoftKeyboard())
        
        onView(withId(R.id.etDueDate))
            .perform(typeText("2024-12-31"), closeSoftKeyboard())
        
        onView(withId(R.id.etDescription))
            .perform(typeText("Shipping services for December 2024"), closeSoftKeyboard())
        
        // Click create button
        onView(withId(R.id.btnCreate))
            .perform(click())
        
        // Verify success message
        onView(withText("Invoice created successfully"))
            .check(matches(isDisplayed()))
        
        // Verify navigation back to invoice list
        onView(withId(R.id.rvInvoices))
            .check(matches(isDisplayed()))
    }

    @Test
    fun invoiceManagement_createInvoice_withInvalidData_shouldShowValidationErrors() {
        // Navigate to create invoice form
        onView(withId(R.id.nav_invoices))
            .perform(click())
        
        onView(withId(R.id.btnCreateInvoice))
            .perform(click())
        
        // Click create button without filling required fields
        onView(withId(R.id.btnCreate))
            .perform(click())
        
        // Verify validation errors
        onView(withId(R.id.etCustomerName))
            .check(matches(hasErrorText("Customer name is required")))
        
        onView(withId(R.id.etCustomerEmail))
            .check(matches(hasErrorText("Customer email is required")))
        
        onView(withId(R.id.etAmount))
            .check(matches(hasErrorText("Amount is required")))
        
        onView(withId(R.id.etDueDate))
            .check(matches(hasErrorText("Due date is required")))
    }

    @Test
    fun invoiceManagement_viewInvoiceStatistics_shouldShowStats() {
        // Navigate to invoices screen
        onView(withId(R.id.nav_invoices))
            .perform(click())
        
        // Click statistics button
        onView(withId(R.id.btnStatistics))
            .perform(click())
        
        // Verify statistics screen is displayed
        onView(withId(R.id.statisticsContainer))
            .check(matches(isDisplayed()))
        
        // Verify statistics are displayed
        onView(withId(R.id.tvTotalInvoices))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.tvPaidInvoices))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.tvPendingInvoices))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.tvOverdueInvoices))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.tvTotalRevenue))
            .check(matches(isDisplayed()))
    }

    @Test
    fun invoiceManagement_viewOverdueInvoices_shouldFilterOverdue() {
        // Navigate to invoices screen
        onView(withId(R.id.nav_invoices))
            .perform(click())
        
        // Click filter button
        onView(withId(R.id.btnFilter))
            .perform(click())
        
        // Select "Overdue" status
        onView(withText("Overdue"))
            .perform(click())
        
        // Apply filter
        onView(withId(R.id.btnApplyFilter))
            .perform(click())
        
        // Verify overdue invoices are displayed
        onView(withId(R.id.rvInvoices))
            .check(matches(isDisplayed()))
        
        // Verify all visible invoices are overdue
        onView(withId(R.id.tvInvoiceStatus))
            .check(matches(withText("Overdue")))
    }

    @Test
    fun invoiceManagement_applyDiscount_shouldUpdateInvoiceAmount() {
        // Navigate to invoice details
        onView(withId(R.id.nav_invoices))
            .perform(click())
        
        onView(withId(R.id.rvInvoices))
            .perform(RecyclerViewActions.actionOnItemAtPosition<InvoiceAdapter.InvoiceViewHolder>(0, click()))
        
        // Click apply discount button
        onView(withId(R.id.btnApplyDiscount))
            .perform(click())
        
        // Verify discount dialog is displayed
        onView(withText("Apply Discount"))
            .check(matches(isDisplayed()))
        
        // Enter discount amount
        onView(withId(R.id.etDiscountAmount))
            .perform(typeText("50.00"), closeSoftKeyboard())
        
        // Enter discount reason
        onView(withId(R.id.etDiscountReason))
            .perform(typeText("Loyalty discount"), closeSoftKeyboard())
        
        // Apply discount
        onView(withId(R.id.btnApply))
            .perform(click())
        
        // Verify discount is applied
        onView(withId(R.id.tvDiscountAmount))
            .check(matches(isDisplayed()))
        
        // Verify total amount is updated
        onView(withId(R.id.tvTotalAmount))
            .check(matches(isDisplayed()))
    }

    @Test
    fun invoiceManagement_refundPayment_shouldProcessRefund() {
        // Navigate to paid invoice details
        onView(withId(R.id.nav_invoices))
            .perform(click())
        
        // Find and click on a paid invoice
        onView(withId(R.id.rvInvoices))
            .perform(RecyclerViewActions.actionOnItemAtPosition<InvoiceAdapter.InvoiceViewHolder>(0, click()))
        
        // Click refund button (only visible for paid invoices)
        onView(withId(R.id.btnRefund))
            .perform(click())
        
        // Verify refund dialog is displayed
        onView(withText("Process Refund"))
            .check(matches(isDisplayed()))
        
        // Enter refund amount
        onView(withId(R.id.etRefundAmount))
            .perform(typeText("100.00"), closeSoftKeyboard())
        
        // Enter refund reason
        onView(withId(R.id.etRefundReason))
            .perform(typeText("Customer requested refund"), closeSoftKeyboard())
        
        // Process refund
        onView(withId(R.id.btnProcessRefund))
            .perform(click())
        
        // Verify success message
        onView(withText("Refund processed successfully"))
            .check(matches(isDisplayed()))
        
        // Verify invoice status is updated to "Refunded"
        onView(withId(R.id.tvInvoiceStatus))
            .check(matches(withText("Refunded")))
    }

    @Test
    fun invoiceManagement_viewPaymentHistory_shouldShowPaymentRecords() {
        // Navigate to invoice details
        onView(withId(R.id.nav_invoices))
            .perform(click())
        
        onView(withId(R.id.rvInvoices))
            .perform(RecyclerViewActions.actionOnItemAtPosition<InvoiceAdapter.InvoiceViewHolder>(0, click()))
        
        // Click payment history button
        onView(withId(R.id.btnPaymentHistory))
            .perform(click())
        
        // Verify payment history screen is displayed
        onView(withId(R.id.paymentHistoryContainer))
            .check(matches(isDisplayed()))
        
        // Verify payment history list is displayed
        onView(withId(R.id.rvPaymentHistory))
            .check(matches(isDisplayed()))
        
        // Verify payment details are displayed
        onView(withId(R.id.tvPaymentDate))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.tvPaymentAmount))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.tvPaymentMethod))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.tvTransactionId))
            .check(matches(isDisplayed()))
    }

    @Test
    fun invoiceManagement_bulkAction_selectMultipleInvoices_shouldShowBulkOptions() {
        // Navigate to invoices screen
        onView(withId(R.id.nav_invoices))
            .perform(click())
        
        // Enable selection mode
        onView(withId(R.id.btnSelectMode))
            .perform(click())
        
        // Select multiple invoices
        onView(withId(R.id.rvInvoices))
            .perform(RecyclerViewActions.actionOnItemAtPosition<InvoiceAdapter.InvoiceViewHolder>(0, click()))
        
        onView(withId(R.id.rvInvoices))
            .perform(RecyclerViewActions.actionOnItemAtPosition<InvoiceAdapter.InvoiceViewHolder>(1, click()))
        
        // Verify bulk action options are displayed
        onView(withId(R.id.btnBulkExport))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.btnBulkSendReminder))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.btnBulkDelete))
            .check(matches(isDisplayed()))
        
        // Click bulk export
        onView(withId(R.id.btnBulkExport))
            .perform(click())
        
        // Verify export dialog
        onView(withText("Export Selected Invoices"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun invoiceManagement_refreshList_shouldUpdateData() {
        // Navigate to invoices screen
        onView(withId(R.id.nav_invoices))
            .perform(click())
        
        // Pull to refresh
        onView(withId(R.id.swipeRefreshLayout))
            .perform(swipeDown())
        
        // Verify loading indicator appears
        onView(withId(R.id.progressBar))
            .check(matches(isDisplayed()))
        
        // Wait for refresh to complete
        Thread.sleep(2000)
        
        // Verify list is updated
        onView(withId(R.id.rvInvoices))
            .check(matches(isDisplayed()))
    }
}
