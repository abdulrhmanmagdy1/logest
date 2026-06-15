package com.edham.logistics.data.remote.mapper

import com.edham.logistics.data.local.entity.DocumentEntity
import com.edham.logistics.data.remote.dto.DocumentDto
import com.edham.logistics.data.remote.dto.DocumentTemplateDto
import com.edham.logistics.data.remote.dto.SignatureDto
import com.edham.logistics.data.remote.dto.TemplateFieldDto
import com.edham.logistics.domain.model.Document
import com.edham.logistics.domain.model.DocumentStatus
import com.edham.logistics.domain.model.DocumentTemplate
import com.edham.logistics.domain.model.DocumentType
import com.edham.logistics.domain.model.FieldType
import com.edham.logistics.domain.model.Signature
import com.edham.logistics.domain.model.TemplateField
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object DocumentMapper {
    
    private val gson = Gson()
    
    // Domain to Entity
    fun toEntity(document: Document): DocumentEntity {
        return DocumentEntity(
            id = document.id,
            title = document.title,
            type = document.type.name,
            templateId = document.template?.id,
            content = document.content,
            metadata = gson.toJson(document.metadata),
            status = document.status.name,
            signatures = gson.toJson(document.signatures),
            versions = gson.toJson(document.versions),
            createdBy = document.createdBy,
            createdAt = document.createdAt,
            updatedAt = document.updatedAt,
            fileUrl = document.fileUrl,
            tags = gson.toJson(document.tags)
        )
    }
    
    // Entity to Domain
    fun toDomain(entity: DocumentEntity): Document {
        return Document(
            id = entity.id,
            title = entity.title,
            type = DocumentType.valueOf(entity.type),
            template = null, // Will need separate query for template
            content = entity.content,
            metadata = gson.fromJson(entity.metadata, com.edham.logistics.domain.model.DocumentMetadata::class.java),
            status = DocumentStatus.valueOf(entity.status),
            signatures = gson.fromJson(entity.signatures, object : TypeToken<List<Signature>>() {}.type),
            versions = gson.fromJson(entity.versions, object : TypeToken<List<com.edham.logistics.domain.model.DocumentVersion>>() {}.type),
            createdBy = entity.createdBy,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            fileUrl = entity.fileUrl,
            tags = gson.fromJson(entity.tags, object : TypeToken<List<String>>() {}.type)
        )
    }
    
    // DTO to Domain
    fun toDomain(dto: DocumentDto): Document {
        return Document(
            id = dto.id,
            title = dto.title,
            type = DocumentType.valueOf(dto.type),
            template = dto.template?.let { toDomainTemplate(it) },
            content = dto.content,
            metadata = dto.metadata,
            status = DocumentStatus.valueOf(dto.status),
            signatures = dto.signatures.map { toDomainSignature(it) },
            versions = dto.versions.map { toDomainVersion(it) },
            createdBy = dto.createdBy,
            createdAt = java.time.Instant.parse(dto.createdAt).toEpochMilli(),
            updatedAt = java.time.Instant.parse(dto.updatedAt).toEpochMilli(),
            fileUrl = dto.fileUrl,
            tags = dto.tags
        )
    }
    
    // Domain to DTO
    fun toDto(document: Document): DocumentDto {
        return DocumentDto(
            id = document.id,
            title = document.title,
            type = document.type.name,
            template = document.template?.let { toDtoTemplate(it) },
            content = document.content,
            metadata = document.metadata,
            status = document.status.name,
            signatures = document.signatures.map { toDtoSignature(it) },
            versions = document.versions.map { toDtoVersion(it) },
            createdBy = document.createdBy,
            createdAt = java.time.Instant.ofEpochMilli(document.createdAt).toString(),
            updatedAt = java.time.Instant.ofEpochMilli(document.updatedAt).toString(),
            fileUrl = document.fileUrl,
            tags = document.tags
        )
    }
    
    fun toDomainTemplate(dto: DocumentTemplateDto): DocumentTemplate {
        return DocumentTemplate(
            id = dto.id,
            name = dto.name,
            description = dto.description,
            fields = dto.fields.map { toDomainTemplateField(it) },
            headerImageUrl = dto.headerImageUrl,
            footerText = dto.footerText
        )
    }
    
    private fun toDtoTemplate(template: DocumentTemplate): DocumentTemplateDto {
        return DocumentTemplateDto(
            id = template.id,
            name = template.name,
            description = template.description,
            fields = template.fields.map { toDtoTemplateField(it) },
            headerImageUrl = template.headerImageUrl,
            footerText = template.footerText
        )
    }
    
    private fun toDomainTemplateField(dto: TemplateFieldDto): TemplateField {
        return TemplateField(
            id = dto.id,
            name = dto.name,
            label = dto.label,
            type = FieldType.valueOf(dto.type),
            required = dto.required,
            placeholder = dto.placeholder,
            options = dto.options
        )
    }
    
    private fun toDtoTemplateField(field: TemplateField): TemplateFieldDto {
        return TemplateFieldDto(
            id = field.id,
            name = field.name,
            label = field.label,
            type = field.type.name,
            required = field.required,
            placeholder = field.placeholder,
            options = field.options
        )
    }
    
    private fun toDomainSignature(dto: SignatureDto): Signature {
        return Signature(
            id = dto.id,
            userId = dto.userId,
            userName = dto.userName,
            signatureImage = dto.signatureImage,
            signatureData = dto.signatureData,
            timestamp = java.time.Instant.parse(dto.timestamp).toEpochMilli(),
            ipAddress = dto.ipAddress,
            deviceInfo = dto.deviceInfo
        )
    }
    
    private fun toDtoSignature(signature: Signature): SignatureDto {
        return SignatureDto(
            id = signature.id,
            userId = signature.userId,
            userName = signature.userName,
            signatureImage = signature.signatureImage,
            signatureData = signature.signatureData,
            timestamp = java.time.Instant.ofEpochMilli(signature.timestamp).toString(),
            ipAddress = signature.ipAddress,
            deviceInfo = signature.deviceInfo
        )
    }
    
    private fun toDomainVersion(dto: com.edham.logistics.data.remote.dto.DocumentVersionDto): com.edham.logistics.domain.model.DocumentVersion {
        return com.edham.logistics.domain.model.DocumentVersion(
            versionNumber = dto.versionNumber,
            content = dto.content,
            createdBy = dto.createdBy,
            createdAt = java.time.Instant.parse(dto.createdAt).toEpochMilli(),
            changeLog = dto.changeLog
        )
    }
    
    private fun toDtoVersion(version: com.edham.logistics.domain.model.DocumentVersion): com.edham.logistics.data.remote.dto.DocumentVersionDto {
        return com.edham.logistics.data.remote.dto.DocumentVersionDto(
            versionNumber = version.versionNumber,
            content = version.content,
            createdBy = version.createdBy,
            createdAt = java.time.Instant.ofEpochMilli(version.createdAt).toString(),
            changeLog = version.changeLog
        )
    }
}
