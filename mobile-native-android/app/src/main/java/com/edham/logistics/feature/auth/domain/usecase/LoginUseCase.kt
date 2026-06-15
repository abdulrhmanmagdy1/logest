package com.edham.logistics.feature.auth.domain.usecase

import com.edham.logistics.core.utils.Result
import com.edham.logistics.feature.auth.domain.model.LoginRequest
import com.edham.logistics.feature.auth.domain.model.LoginResponse
import com.edham.logistics.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(request: LoginRequest): Result<LoginResponse> {
        return authRepository.login(request)
    }
}
