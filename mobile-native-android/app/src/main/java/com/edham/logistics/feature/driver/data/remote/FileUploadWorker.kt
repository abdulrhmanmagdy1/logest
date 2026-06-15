package com.edham.logistics.feature.driver.data.remote

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.edham.logistics.core.network.api.DriverApi
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

@HiltWorker
class FileUploadWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val api: DriverApi
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val shipmentId = inputData.getString("shipment_id") ?: return Result.failure()
        val filePath = inputData.getString("file_path") ?: return Result.failure()
        val file = File(filePath)

        if (!file.exists()) return Result.failure()

        val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

        return try {
            val response = api.uploadAttachment(shipmentId, body)
            if (response.isSuccessful) {
                Result.success()
            } else {
                if (runAttemptCount < 3) Result.retry() else Result.failure()
            }
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }
}
