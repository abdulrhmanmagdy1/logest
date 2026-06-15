package com.edham.logistics.feature.driver.data.repository

import com.edham.logistics.core.network.api.DriverApi
import com.edham.logistics.data.local.dao.DriverFeatureDao
import com.edham.logistics.data.local.entity.SurveyAnswerEntity
import com.edham.logistics.feature.driver.data.models.Survey
import com.edham.logistics.feature.driver.data.models.SurveySubmission
import com.edham.logistics.core.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SurveyRepository @Inject constructor(
    private val api: DriverApi,
    private val dao: DriverFeatureDao
) {

    fun getPostMissionSurvey(): Flow<Resource<Survey>> = flow {
        emit(Resource.Loading())
        try {
            val response = api.getPostMissionSurvey()
            if (response.isSuccessful && response.body()?.success == true) {
                response.body()?.data?.let {
                    emit(Resource.Success(it))
                } ?: emit(Resource.Error("No data"))
            } else {
                emit(Resource.Error(response.message()))
            }
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Network error"))
        }
    }

    suspend fun submitSurvey(submission: SurveySubmission): Resource<Unit> {
        return try {
            val response = api.submitSurvey(submission)
            if (response.isSuccessful) {
                // Mark in local DB as completed/synced
                submission.answers.forEach {
                    dao.insertSurveyAnswer(SurveyAnswerEntity(
                        shipmentId = submission.shipmentId,
                        questionId = it.questionId,
                        answer = it.answer
                    ))
                }
                Resource.Success(Unit)
            } else {
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Network error")
        }
    }
}
