package com.edham.logistics.data.local.dao

import androidx.room.*
import com.edham.logistics.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DriverFeatureDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrips(trips: List<TripEntity>)

    @Query("SELECT * FROM trips WHERE date = :date")
    fun getTripsByDate(date: String): Flow<List<TripEntity>>

    @Query("SELECT * FROM trips WHERE status IN ('active', 'new')")
    fun getActiveTrips(): Flow<List<TripEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaypoint(waypoint: WaypointEntity)

    @Query("SELECT * FROM waypoints WHERE tripId = :tripId")
    fun getWaypointsByTrip(tripId: String): Flow<List<WaypointEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSurveyAnswer(answer: SurveyAnswerEntity)

    @Query("SELECT * FROM survey_answers WHERE shipmentId = :shipmentId")
    suspend fun getSurveyAnswersByShipment(shipmentId: String): List<SurveyAnswerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttachment(attachment: AttachmentEntity)

    @Query("SELECT * FROM attachments WHERE shipmentId = :shipmentId")
    fun getAttachmentsByShipment(shipmentId: String): Flow<List<AttachmentEntity>>

    @Delete
    suspend fun deleteAttachment(attachment: AttachmentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationCacheEntity)

    @Query("SELECT * FROM location_cache WHERE syncStatus = 'PENDING'")
    suspend fun getPendingLocations(): List<LocationCacheEntity>

    @Query("UPDATE location_cache SET syncStatus = 'SYNCED' WHERE id IN (:ids)")
    suspend fun markLocationsSynced(ids: List<Long>)
}
