package com.edham.logistics.feature.admin.data.remote.mapper

import com.edham.logistics.feature.admin.data.remote.dto.DashboardStatsDto
import com.edham.logistics.feature.admin.data.remote.dto.UserManagementDto
import com.edham.logistics.feature.admin.data.remote.dto.SystemHealthDto
import com.edham.logistics.feature.admin.domain.model.DashboardStats
import com.edham.logistics.feature.admin.domain.model.UserManagement
import com.edham.logistics.feature.admin.domain.model.SystemSettings
import com.edham.logistics.feature.admin.domain.model.ActivityLog
import com.edham.logistics.feature.admin.domain.model.SystemHealth
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdminMapper @Inject constructor() {

    fun mapToDashboardStats(dto: DashboardStatsDto): DashboardStats {
        return DashboardStats(
            totalShipments = dto.totalShipments,
            activeShipments = dto.activeShipments,
            completedShipments = dto.completedShipments,
            cancelledShipments = dto.cancelledShipments,
            totalDrivers = dto.totalDrivers,
            activeDrivers = dto.activeDrivers,
            totalVehicles = dto.totalVehicles,
            availableVehicles = dto.availableVehicles,
            totalRevenue = dto.totalRevenue,
            monthlyRevenue = dto.monthlyRevenue,
            totalCustomers = dto.totalCustomers,
            newCustomersThisMonth = dto.newCustomersThisMonth,
            pendingTasks = dto.pendingTasks,
            overdueTasks = dto.overdueTasks,
            systemHealth = mapToSystemHealth(dto.systemHealth)
        )
    }

    fun mapToSystemHealth(dto: SystemHealthDto): SystemHealth {
        return SystemHealth(
            overallHealth = dto.overallHealth,
            apiStatus = dto.apiStatus,
            databaseStatus = dto.databaseStatus,
            lastBackupTime = dto.lastBackupTime,
            storageUsage = dto.storageUsage,
            memoryUsage = dto.memoryUsage,
            cpuUsage = dto.cpuUsage
        )
    }

    fun mapToUserManagement(dto: UserManagementDto): UserManagement {
        return UserManagement(
            id = dto.id,
            name = dto.name,
            email = dto.email,
            phone = dto.phone,
            role = dto.role,
            status = dto.status,
            department = dto.department,
            position = dto.position,
            hireDate = dto.hireDate,
            lastLogin = dto.lastLogin,
            profileImage = dto.profileImage,
            permissions = dto.permissions
        )
    }

    fun mapToUserManagementDto(domain: UserManagement): UserManagementDto {
        return UserManagementDto(
            id = domain.id,
            name = domain.name,
            email = domain.email,
            phone = domain.phone,
            role = domain.role,
            status = domain.status,
            department = domain.department,
            position = domain.position,
            hireDate = domain.hireDate,
            lastLogin = domain.lastLogin,
            profileImage = domain.profileImage,
            permissions = domain.permissions
        )
    }

    fun mapToUserManagementList(dtos: List<UserManagementDto>): List<UserManagement> {
        return dtos.map { mapToUserManagement(it) }
    }

    fun mapToUserManagementDtoList(domains: List<UserManagement>): List<UserManagementDto> {
        return domains.map { mapToUserManagementDto(it) }
    }
}
