package com.edham.logistics.data.local.database.dao

import androidx.room.*
import com.edham.logistics.data.local.entity.DocumentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentDao {
    
    @Query("SELECT * FROM documents")
    suspend fun getAllDocuments(): List<DocumentEntity>
    
    @Query("SELECT * FROM documents WHERE id = :documentId")
    suspend fun getDocument(documentId: String): DocumentEntity?
    
    @Query("SELECT * FROM documents WHERE status = :status ORDER BY createdAt DESC")
    suspend fun getDocumentsByStatus(status: String): List<DocumentEntity>
    
    @Query("SELECT * FROM documents WHERE type = :type ORDER BY createdAt DESC")
    suspend fun getDocumentsByType(type: String): List<DocumentEntity>
    
    @Query("SELECT * FROM documents WHERE createdBy = :userId ORDER BY createdAt DESC")
    suspend fun getDocumentsByUser(userId: String): List<DocumentEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: DocumentEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocuments(documents: List<DocumentEntity>)
    
    @Update
    suspend fun updateDocument(document: DocumentEntity)
    
    @Delete
    suspend fun deleteDocument(document: DocumentEntity)
    
    @Query("DELETE FROM documents WHERE id = :documentId")
    suspend fun deleteDocumentById(documentId: String)
    
    @Query("DELETE FROM documents")
    suspend fun deleteAllDocuments()
    
    @Query("SELECT * FROM documents ORDER BY createdAt DESC")
    fun observeAllDocuments(): Flow<List<DocumentEntity>>
    
    @Query("SELECT * FROM documents WHERE id = :documentId")
    fun observeDocument(documentId: String): Flow<DocumentEntity?>
}
