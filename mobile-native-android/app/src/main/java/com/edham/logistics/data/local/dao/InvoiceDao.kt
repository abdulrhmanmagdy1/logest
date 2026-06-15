package com.edham.logistics.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.edham.logistics.data.local.entity.InvoiceEntity
import java.util.Date

@Dao
interface InvoiceDao {
    @Query("SELECT * FROM invoices WHERE id = :invoiceId")
    suspend fun getInvoiceById(invoiceId: String): InvoiceEntity?
    
    @Query("SELECT * FROM invoices WHERE shipmentId = :shipmentId")
    suspend fun getInvoicesByShipmentId(shipmentId: String): List<InvoiceEntity>
    
    @Query("SELECT * FROM invoices WHERE customerId = :customerId")
    suspend fun getInvoicesByCustomerId(customerId: String): List<InvoiceEntity>
    
    @Query("SELECT * FROM invoices WHERE status = :status")
    suspend fun getInvoicesByStatus(status: String): List<InvoiceEntity>
    
    @Query("SELECT * FROM invoices WHERE dueDate < :date AND status = 'pending'")
    suspend fun getOverdueInvoices(date: Date): List<InvoiceEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: InvoiceEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoices(invoices: List<InvoiceEntity>)
    
    @Query("UPDATE invoices SET status = :status, paidDate = :paidDate WHERE id = :invoiceId")
    suspend fun updateInvoiceStatus(invoiceId: String, status: String, paidDate: Date?)
    
    @Query("DELETE FROM invoices WHERE id = :invoiceId")
    suspend fun deleteInvoice(invoiceId: String)
    
    @Query("SELECT * FROM invoices ORDER BY issuedDate DESC")
    suspend fun getAllInvoices(): List<InvoiceEntity>
}
