package com.edham.logistics.app

import androidx.annotation.StringRes
import com.edham.logistics.R

/**
 * The five user roles supported by Edham Logistics.
 *
 * - [CUSTOMER]   : Places shipping orders. Uses the e-commerce style home.
 * - [SUPERVISOR] : Oversees operations, approves/dispatches loads.
 * - [ACCOUNTANT] : Manages invoices, payments, financial reports.
 * - [DRIVER]     : Sees assigned routes and delivers shipments.
 * - [WORKSHOP]   : Manages vehicle maintenance, oil changes, service alerts.
 */
enum class UserRole(@StringRes val titleRes: Int) {
    CUSTOMER(R.string.role_customer),
    SUPERVISOR(R.string.role_supervisor),
    ACCOUNTANT(R.string.role_accountant),
    DRIVER(R.string.role_driver),
    WORKSHOP(R.string.role_workshop);

    companion object {
        fun fromTabIndex(index: Int): UserRole = when(index) {
            0 -> CUSTOMER
            1 -> SUPERVISOR
            2 -> ACCOUNTANT
            3 -> DRIVER
            4 -> WORKSHOP
            else -> CUSTOMER
        }
    }
}
