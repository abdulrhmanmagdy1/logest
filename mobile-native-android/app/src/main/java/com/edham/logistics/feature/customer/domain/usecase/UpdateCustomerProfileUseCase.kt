package com.edham.logistics.feature.customer.domain.usecase

import com.edham.logistics.core.utils.Result
import com.edham.logistics.feature.customer.domain.model.CustomerProfile
import com.edham.logistics.feature.customer.domain.repository.CustomerRepository
import javax.inject.Inject

class UpdateCustomerProfileUseCase @Inject constructor(
    private val customerRepository: CustomerRepository
) {
    
    suspend operator fun invoke(profile: CustomerProfile): Result<CustomerProfile> {
        return customerRepository.updateCustomerProfile(profile)
    }
}
