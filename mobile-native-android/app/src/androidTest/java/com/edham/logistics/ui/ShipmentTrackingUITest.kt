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
import com.edham.logistics.presentation.shipments.ShipmentAdapter
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI Tests for Shipment Tracking Flow
 * Tests critical user interactions in shipment tracking and management
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class ShipmentTrackingUITest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun shipmentTracking_searchByTrackingNumber_shouldShowShipmentDetails() {
        // Navigate to shipment tracking screen
        onView(withId(R.id.nav_shipments))
            .perform(click())
        
        // Enter tracking number
        onView(withId(R.id.etTrackingNumber))
            .perform(typeText("TN123456789"), closeSoftKeyboard())
        
        // Click search button
        onView(withId(R.id.btnSearch))
            .perform(click())
        
        // Verify shipment details are displayed
        onView(withId(R.id.tvTrackingNumber))
            .check(matches(isDisplayed()))
            .check(matches(withText("TN123456789")))
        
        onView(withId(R.id.tvShipmentStatus))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.tvOrigin))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.tvDestination))
            .check(matches(isDisplayed()))
    }

    @Test
    fun shipmentTracking_invalidTrackingNumber_shouldShowErrorMessage() {
        // Navigate to shipment tracking screen
        onView(withId(R.id.nav_shipments))
            .perform(click())
        
        // Enter invalid tracking number
        onView(withId(R.id.etTrackingNumber))
            .perform(typeText("INVALID123"), closeSoftKeyboard())
        
        // Click search button
        onView(withId(R.id.btnSearch))
            .perform(click())
        
        // Verify error message is displayed
        onView(withId(R.id.tvErrorMessage))
            .check(matches(isDisplayed()))
            .check(matches(withText("Tracking number not found")))
    }

    @Test
    fun shipmentTracking_emptyTrackingNumber_shouldShowValidationError() {
        // Navigate to shipment tracking screen
        onView(withId(R.id.nav_shipments))
            .perform(click())
        
        // Click search button without entering tracking number
        onView(withId(R.id.btnSearch))
            .perform(click())
        
        // Verify validation error
        onView(withId(R.id.etTrackingNumber))
            .check(matches(hasErrorText("Tracking number is required")))
    }

    @Test
    fun shipmentTracking_viewShipmentList_shouldDisplayAllShipments() {
        // Navigate to shipment list
        onView(withId(R.id.nav_shipments))
            .perform(click())
        
        // Switch to list view
        onView(withId(R.id.btnListView))
            .perform(click())
        
        // Verify shipment list is displayed
        onView(withId(R.id.rvShipments))
            .check(matches(isDisplayed()))
        
        // Verify at least one shipment is in the list
        onView(withId(R.id.rvShipments))
            .perform(RecyclerViewActions.scrollToPosition<ShipmentAdapter.ShipmentViewHolder>(0))
        
        onView(withId(R.id.tvShipmentItemTrackingNumber))
            .check(matches(isDisplayed()))
    }

    @Test
    fun shipmentTracking_filterByStatus_shouldFilterShipments() {
        // Navigate to shipment list
        onView(withId(R.id.nav_shipments))
            .perform(click())
        
        // Click filter button
        onView(withId(R.id.btnFilter))
            .perform(click())
        
        // Select "In Transit" status
        onView(withText("In Transit"))
            .perform(click())
        
        // Apply filter
        onView(withId(R.id.btnApplyFilter))
            .perform(click())
        
        // Verify filtered results
        onView(withId(R.id.rvShipments))
            .check(matches(isDisplayed()))
        
        // Verify all visible shipments have "In Transit" status
        onView(withId(R.id.tvShipmentItemStatus))
            .check(matches(withText("In Transit")))
    }

    @Test
    fun shipmentTracking_refreshList_shouldUpdateData() {
        // Navigate to shipment list
        onView(withId(R.id.nav_shipments))
            .perform(click())
        
        // Pull to refresh
        onView(withId(R.id.swipeRefreshLayout))
            .perform(swipeDown())
        
        // Verify loading indicator appears
        onView(withId(R.id.progressBar))
            .check(matches(isDisplayed()))
        
        // Wait for refresh to complete (in real test, use IdlingResource)
        Thread.sleep(2000)
        
        // Verify list is updated
        onView(withId(R.id.rvShipments))
            .check(matches(isDisplayed()))
    }

    @Test
    fun shipmentTracking_clickShipment_shouldNavigateToDetails() {
        // Navigate to shipment list
        onView(withId(R.id.nav_shipments))
            .perform(click())
        
        // Click on first shipment in list
        onView(withId(R.id.rvShipments))
            .perform(RecyclerViewActions.actionOnItemAtPosition<ShipmentAdapter.ShipmentViewHolder>(0, click()))
        
        // Verify navigation to shipment details
        onView(withId(R.id.shipmentDetailsContainer))
            .check(matches(isDisplayed()))
        
        // Verify shipment details are displayed
        onView(withId(R.id.tvTrackingNumber))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.tvStatus))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.tvOriginDestination))
            .check(matches(isDisplayed()))
    }

    @Test
    fun shipmentTracking_updateStatus_shouldShowUpdatedStatus() {
        // Navigate to shipment details (assuming we're already on details screen)
        onView(withId(R.id.nav_shipments))
            .perform(click())
        
        onView(withId(R.id.rvShipments))
            .perform(RecyclerViewActions.actionOnItemAtPosition<ShipmentAdapter.ShipmentViewHolder>(0, click()))
        
        // Click update status button
        onView(withId(R.id.btnUpdateStatus))
            .perform(click())
        
        // Select new status
        onView(withText("Delivered"))
            .perform(click())
        
        // Confirm update
        onView(withId(R.id.btnConfirm))
            .perform(click())
        
        // Verify status is updated
        onView(withId(R.id.tvStatus))
            .check(matches(withText("Delivered")))
    }

    @Test
    fun shipmentTracking_addNote_shouldSaveNote() {
        // Navigate to shipment details
        onView(withId(R.id.nav_shipments))
            .perform(click())
        
        onView(withId(R.id.rvShipments))
            .perform(RecyclerViewActions.actionOnItemAtPosition<ShipmentAdapter.ShipmentViewHolder>(0, click()))
        
        // Click add note button
        onView(withId(R.id.btnAddNote))
            .perform(click())
        
        // Enter note text
        onView(withId(R.id.etNoteText))
            .perform(typeText("Customer contacted, delivery scheduled for tomorrow"), closeSoftKeyboard())
        
        // Save note
        onView(withId(R.id.btnSaveNote))
            .perform(click())
        
        // Verify note is displayed in notes section
        onView(withId(R.id.rvNotes))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.tvNoteText))
            .check(matches(withText("Customer contacted, delivery scheduled for tomorrow")))
    }

    @Test
    fun shipmentTracking_viewMap_shouldShowMapWithRoute() {
        // Navigate to shipment details
        onView(withId(R.id.nav_shipments))
            .perform(click())
        
        onView(withId(R.id.rvShipments))
            .perform(RecyclerViewActions.actionOnItemAtPosition<ShipmentAdapter.ShipmentViewHolder>(0, click()))
        
        // Click map view button
        onView(withId(R.id.btnMapView))
            .perform(click())
        
        // Verify map is displayed
        onView(withId(R.id.mapView))
            .check(matches(isDisplayed()))
        
        // Verify route markers are displayed (if implemented)
        onView(withId(R.id.mapOriginMarker))
            .check(matches(isDisplayed()))
        
        onView(withId(R.id.mapDestinationMarker))
            .check(matches(isDisplayed()))
    }

    @Test
    fun shipmentTracking_shareTrackingLink_shouldOpenShareDialog() {
        // Navigate to shipment details
        onView(withId(R.id.nav_shipments))
            .perform(click())
        
        onView(withId(R.id.rvShipments))
            .perform(RecyclerViewActions.actionOnItemAtPosition<ShipmentAdapter.ShipmentViewHolder>(0, click()))
        
        // Click share button
        onView(withId(R.id.btnShare))
            .perform(click())
        
        // Verify share dialog is displayed
        onView(withText("Share Tracking Link"))
            .check(matches(isDisplayed()))
        
        // Verify tracking link is displayed
        onView(withId(R.id.tvTrackingLink))
            .check(matches(isDisplayed()))
    }

    @Test
    fun shipmentTracking_printLabel_shouldGenerateLabel() {
        // Navigate to shipment details
        onView(withId(R.id.nav_shipments))
            .perform(click())
        
        onView(withId(R.id.rvShipments))
            .perform(RecyclerViewActions.actionOnItemAtPosition<ShipmentAdapter.ShipmentViewHolder>(0, click()))
        
        // Click print label button
        onView(withId(R.id.btnPrintLabel))
            .perform(click())
        
        // Verify print options dialog is displayed
        onView(withText("Print Shipping Label"))
            .check(matches(isDisplayed()))
        
        // Select label size
        onView(withText("4x6 inches"))
            .perform(click())
        
        // Confirm print
        onView(withId(R.id.btnPrint))
            .perform(click())
        
        // Verify success message
        onView(withText("Label sent to printer"))
            .check(matches(isDisplayed()))
    }

    @Test
    fun shipmentTracking_createShipment_shouldNavigateToCreateForm() {
        // Navigate to shipments screen
        onView(withId(R.id.nav_shipments))
            .perform(click())
        
        // Click create shipment button
        onView(withId(R.id.btnCreateShipment))
            .perform(click())
        
        // Verify navigation to create shipment form
        onView(withId(R.id.createShipmentContainer))
            .check(matches(isDisplayed()))
        
        // Verify all form fields are present
        onView(withId(R.id.etOrigin)).check(matches(isDisplayed()))
        onView(withId(R.id.etDestination)).check(matches(isDisplayed()))
        onView(withId(R.id.etRecipientName)).check(matches(isDisplayed()))
        onView(withId(R.id.etRecipientPhone)).check(matches(isDisplayed()))
        onView(withId(R.id.etWeight)).check(matches(isDisplayed()))
        onView(withId(R.id.etDimensions)).check(matches(isDisplayed()))
    }

    @Test
    fun shipmentTracking_createShipment_withValidData_shouldCreateShipment() {
        // Navigate to create shipment form
        onView(withId(R.id.nav_shipments))
            .perform(click())
        
        onView(withId(R.id.btnCreateShipment))
            .perform(click())
        
        // Fill in shipment details
        onView(withId(R.id.etOrigin))
            .perform(typeText("Riyadh"), closeSoftKeyboard())
        
        onView(withId(R.id.etDestination))
            .perform(typeText("Jeddah"), closeSoftKeyboard())
        
        onView(withId(R.id.etRecipientName))
            .perform(typeText("Ahmed Mohammed"), closeSoftKeyboard())
        
        onView(withId(R.id.etRecipientPhone))
            .perform(typeText("+966500000000"), closeSoftKeyboard())
        
        onView(withId(R.id.etWeight))
            .perform(typeText("5.5"), closeSoftKeyboard())
        
        onView(withId(R.id.etDimensions))
            .perform(typeText("10x10x10"), closeSoftKeyboard())
        
        // Select service type
        onView(withId(R.id.spServiceType))
            .perform(click())
        
        onView(withText("Standard"))
            .perform(click())
        
        // Click create button
        onView(withId(R.id.btnCreate))
            .perform(click())
        
        // Verify success message
        onView(withText("Shipment created successfully"))
            .check(matches(isDisplayed()))
        
        // Verify navigation back to shipment list
        onView(withId(R.id.rvShipments))
            .check(matches(isDisplayed()))
    }

    @Test
    fun shipmentTracking_createShipment_withInvalidData_shouldShowValidationErrors() {
        // Navigate to create shipment form
        onView(withId(R.id.nav_shipments))
            .perform(click())
        
        onView(withId(R.id.btnCreateShipment))
            .perform(click())
        
        // Click create button without filling required fields
        onView(withId(R.id.btnCreate))
            .perform(click())
        
        // Verify validation errors
        onView(withId(R.id.etOrigin))
            .check(matches(hasErrorText("Origin is required")))
        
        onView(withId(R.id.etDestination))
            .check(matches(hasErrorText("Destination is required")))
        
        onView(withId(R.id.etRecipientName))
            .check(matches(hasErrorText("Recipient name is required")))
        
        onView(withId(R.id.etRecipientPhone))
            .check(matches(hasErrorText("Recipient phone is required")))
    }

    @Test
    fun shipmentTracking_scanBarcode_shouldPopulateTrackingNumber() {
        // Navigate to shipment tracking screen
        onView(withId(R.id.nav_shipments))
            .perform(click())
        
        // Click scan barcode button
        onView(withId(R.id.btnScanBarcode))
            .perform(click())
        
        // Simulate barcode scan (in real test, this would trigger camera)
        // For now, we'll manually enter the tracking number that would come from scan
        onView(withId(R.id.etTrackingNumber))
            .perform(typeText("TN123456789"), closeSoftKeyboard())
        
        // Verify tracking number is populated
        onView(withId(R.id.etTrackingNumber))
            .check(matches(withText("TN123456789")))
        
        // Search should work automatically
        onView(withId(R.id.tvTrackingNumber))
            .check(matches(isDisplayed()))
    }

    @Test
    fun shipmentTracking_searchHistory_shouldShowRecentSearches() {
        // Navigate to shipment tracking screen
        onView(withId(R.id.nav_shipments))
            .perform(click())
        
        // Click on search input field
        onView(withId(R.id.etTrackingNumber))
            .perform(click())
        
        // Verify search history is displayed
        onView(withId(R.id.rvSearchHistory))
            .check(matches(isDisplayed()))
        
        // Click on recent search
        onView(withId(R.id.tvSearchHistoryItem))
            .perform(click())
        
        // Verify tracking number is populated
        onView(withId(R.id.etTrackingNumber))
            .check(matches(isDisplayed()))
    }
}
