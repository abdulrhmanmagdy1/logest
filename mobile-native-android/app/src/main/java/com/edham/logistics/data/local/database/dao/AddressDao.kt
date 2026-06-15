package com.edham.logistics.data.local.database.dao

import androidx.room.*
import com.edham.logistics.data.local.database.AddressEntity

@Dao
interface AddressDao {
    @Query("SELECT * FROM addresses")
    suspend fun getAllAddresses(): List<AddressEntity>

    @Query("SELECT * FROM addresses WHERE id = :addressId")
    suspend fun getAddressById(addressId: String): AddressEntity?

    @Query("SELECT * FROM addresses WHERE user_id = :userId")
    suspend fun getAddressesByUserId(userId: String): List<AddressEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddress(address: AddressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAddresses(addresses: List<AddressEntity>)

    @Update
    suspend fun updateAddress(address: AddressEntity)

    @Delete
    suspend fun deleteAddress(address: AddressEntity)

    @Query("DELETE FROM addresses WHERE id = :addressId")
    suspend fun deleteAddressById(addressId: String)
}
