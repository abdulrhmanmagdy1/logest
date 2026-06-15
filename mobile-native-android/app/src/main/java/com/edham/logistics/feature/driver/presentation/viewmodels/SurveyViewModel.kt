package com.edham.logistics.feature.driver.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edham.logistics.core.utils.Resource
import com.edham.logistics.core.utils.TokenManager
import com.edham.logistics.feature.driver.data.models.Survey
import com.edham.logistics.feature.driver.data.models.SurveyAnswer
import com.edham.logistics.feature.driver.data.models.SurveySubmission
import com.edham.logistics.feature.driver.data.repository.SurveyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SurveyViewModel @Inject constructor(
    private val repository: SurveyRepository
) : ViewModel() {

    private val _survey = MutableStateFlow<Resource<Survey>>(Resource.Loading())
    val survey = _survey.asStateFlow()

    private val _submissionStatus = MutableSharedFlow<Resource<Unit>>()
    val submissionStatus = _submissionStatus.asSharedFlow()

    private val answers = mutableMapOf<String, String>()

    init {
        loadSurvey()
    }

    private fun loadSurvey() {
        viewModelScope.launch {
            repository.getPostMissionSurvey().collect {
                _survey.value = it
            }
        }
    }

    fun setAnswer(questionId: String, answer: String) {
        answers[questionId] = answer
    }

    fun submitSurvey(shipmentId: String) {
        val driverId = TokenManager.getUserId() ?: return
        val submission = SurveySubmission(
            shipmentId = shipmentId,
            driverId = driverId,
            answers = answers.map { SurveyAnswer(it.key, it.value) }
        )

        viewModelScope.launch {
            _submissionStatus.emit(Resource.Loading())
            val result = repository.submitSurvey(submission)
            _submissionStatus.emit(result)
        }
    }
}
