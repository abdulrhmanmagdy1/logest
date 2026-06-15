package com.edham.logistics.data.local.database.dao

import androidx.room.*
import com.edham.logistics.data.local.database.PaymentEntity

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments")
    suspend fun getAllPayments(): List<PaymentEntity>

    @Query("SELECT * FROM payments WHERE id = :paymentId")
    suspend fun getPaymentById(paymentId: String): PaymentEntity?

    @Query("SELECT * FROM payments WHERE shipment_id = :shipmentId")
    suspend fun getPaymentsByShipmentId(shipmentId: String): List<PaymentEntity>

    @Query("SELECT * FROM payments WHERE status = :status")
    suspend fun getPaymentsByStatus(status: String): List<PaymentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPayments(payments: List<PaymentEntity>)

    @Update
    suspend fun updatePayment(payment: PaymentEntity)

    @Delete
    suspend fun deletePayment(payment: PaymentEntity)

    @Query("DELETE FROM payments WHERE id = :paymentId")
    suspend fun deletePaymentById(paymentId: String)
}
