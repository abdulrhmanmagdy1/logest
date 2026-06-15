package com.edham.logistics.ui.home.driver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.edham.logistics.R
import com.edham.logistics.app.AuthSession
import com.edham.logistics.core.network.RetrofitClient
import com.edham.logistics.data.remote.api.SurveyApi
import com.edham.logistics.data.remote.dto.SurveySubmitRequest
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch

/**
 * Post-trip driver survey — collects rating and feedback after completing a delivery.
 * Connected to real SurveyApi backend.
 */
class DriverSurveyFragment : Fragment() {

    private lateinit var surveyApi: SurveyApi
    private lateinit var session: AuthSession
    private var isSubmitting = false
    private var shipmentId: String = "#DLV1042" // Default

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        surveyApi = RetrofitClient.createApi()
        session = AuthSession.get(requireContext())
        shipmentId = arguments?.getString("SHIPMENT_ID") ?: "#DLV1042"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_driver_survey, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ratingBar = view.findViewById<RatingBar>(R.id.ratingBar)
        val etComment = view.findViewById<TextInputEditText>(R.id.etComment)
        val btnSubmit = view.findViewById<MaterialButton>(R.id.btnSubmitSurvey)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)

        btnSubmit.setOnClickListener {
            if (isSubmitting) return@setOnClickListener

            val rating = ratingBar.rating.toInt()
            val comment = etComment.text?.toString().orEmpty()

            if (rating == 0) {
                Toast.makeText(requireContext(), "يرجى اختيار التقييم", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            submitSurvey(rating, comment, btnSubmit, progressBar, ratingBar, etComment)
        }
    }

    private fun submitSurvey(
        rating: Int, comment: String,
        btnSubmit: MaterialButton, progressBar: ProgressBar,
        ratingBar: RatingBar, etComment: TextInputEditText
    ) {
        isSubmitting = true
        btnSubmit.isEnabled = false
        btnSubmit.text = "جاري الإرسال..."
        progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            try {
                val driverId = session.email ?: "unknown"
                val request = SurveySubmitRequest(
                    driverId = driverId,
                    shipmentId = shipmentId,
                    rating = rating,
                    comment = comment.ifEmpty { null }
                )

                val response = surveyApi.submitSurvey(request)
                if (response.isSuccessful) {
                    Toast.makeText(
                        requireContext(),
                        "تم إرسال الاستبيان بنجاح — شكراً لك!",
                        Toast.LENGTH_SHORT
                    ).show()
                    ratingBar.rating = 0f
                    etComment.text?.clear()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "خطأ في الإرسال: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "فشل الاتصال: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                isSubmitting = false
                btnSubmit.isEnabled = true
                btnSubmit.text = "إرسال الاستبيان"
                progressBar.visibility = View.GONE
            }
        }
    }
}
