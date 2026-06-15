package com.edham.logistics.feature.auth.domain.usecase

import com.edham.logistics.core.utils.Result
import com.edham.logistics.feature.auth.domain.model.RegisterRequest
import com.edham.logistics.feature.auth.domain.model.User
import com.edham.logistics.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(request: RegisterRequest): Result<User> {
        return authRepository.register(request)
    }
}
