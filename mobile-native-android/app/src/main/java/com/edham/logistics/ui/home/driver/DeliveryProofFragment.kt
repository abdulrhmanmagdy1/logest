package com.edham.logistics.ui.home.driver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.edham.logistics.R
import com.github.gcacace.signaturepad.views.SignaturePad
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DeliveryProofFragment : Fragment() {

    @Inject lateinit var voiceAssistant: com.edham.logistics.core.voice.TacticalVoiceAssistant
    private val viewModel: DeliveryProofViewModel by viewModels()
    private lateinit var signaturePad: SignaturePad
    private lateinit var ivPreview: ImageView
    
    private lateinit var cbTemp: android.widget.CheckBox
    private lateinit var cbSeal: android.widget.CheckBox
    private lateinit var cbQty: android.widget.CheckBox

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_delivery_proof, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        signaturePad = view.findViewById(R.id.signature_pad)
        ivPreview = view.findViewById(R.id.ivUploadPreview)
        cbTemp = view.findViewById(R.id.cbTempCheck)
        cbSeal = view.findViewById(R.id.cbSealCheck)
        cbQty = view.findViewById(R.id.cbQtyCheck)

        setupListeners(view)
        observeViewModel()
    }

    private fun setupListeners(view: View) {
        view.findViewById<View>(R.id.btnClearSignature).setOnClickListener {
            signaturePad.clear()
        }

        view.findViewById<View>(R.id.cardPhotoUpload).setOnClickListener {
            openEliteCamera()
        }

        view.findViewById<View>(R.id.btnCompleteMission).setOnClickListener {
            validateAndSubmit()
        }
    }

    private fun openEliteCamera() {
        val intent = android.content.Intent(requireContext(), com.edham.logistics.ui.screens.EliteCameraActivity::class.java)
        startActivityForResult(intent, 1001)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == android.app.Activity.RESULT_OK) {
            val path = data?.getStringExtra("IMAGE_PATH")
            if (path != null) {
                ivPreview.visibility = View.VISIBLE
                com.bumptech.glide.Glide.with(this).load(path).into(ivPreview)
                Toast.makeText(context, "تم التقاط الوصل بنجاح ✓", Toast.LENGTH_SHORT).show()
                voiceAssistant.speak("تم التقاط الصورة بنجاح")
            }
        }
    }

    private fun validateAndSubmit() {
        if (!cbTemp.isChecked || !cbSeal.isChecked || !cbQty.isChecked) {
            Toast.makeText(context, "يرجى إكمال قائمة التحقق للأمان أولاً 📋", Toast.LENGTH_LONG).show()
            voiceAssistant.speak("يرجى إكمال قائمة التحقق للأمان قبل الإنهاء")
            return
        }

        if (signaturePad.isEmpty) {
            Toast.makeText(context, "توقيع العميل إلزامي لإغلاق المهمة ✍️", Toast.LENGTH_LONG).show()
            return
        }

        val currentLat = 24.7136 
        val currentLng = 46.6753
        val clientLat = 24.7140 
        val clientLng = 46.6760
        
        val isAtLocation = com.edham.logistics.core.utils.GeoUtils.isWithinRadius(
            currentLat, currentLng, clientLat, clientLng, 500f
        )
        
        if (!isAtLocation) {
            Toast.makeText(context, "🚫 خطأ: لا يمكنك إغلاق الرحلة خارج موقع العميل!", Toast.LENGTH_LONG).show()
            voiceAssistant.speak("خطأ أمني. أنت لست في موقع العميل.")
            return
        }

        // 4. Submit
        viewModel.submitProof(
            "TRIP-123", 
            "encoded_sig", 
            listOf("encoded_pod"),
            cbTemp.isChecked,
            cbSeal.isChecked,
            cbQty.isChecked
        )
    }

    private fun observeViewModel() {
        viewModel.isSubmitted.observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(context, "🎉 تم التسليم والتوثيق بنجاح! شكراً لك.", Toast.LENGTH_LONG).show()
                voiceAssistant.speak("تم تسليم الشحنة بنجاح. أحسنت يا بطل.")
                
                // الانتقال إلى الاستبيان بعد إكمال المهمة
                val surveyFragment = DriverSurveyFragment().apply {
                    arguments = Bundle().apply {
                        putString("SHIPMENT_ID", "TRIP-123") // في الحقيقة نستخدم ID المهمة الحقيقي
                    }
                }
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, surveyFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }

        viewModel.error.observe(viewLifecycleOwner) {
            it?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
        }
    }
}
