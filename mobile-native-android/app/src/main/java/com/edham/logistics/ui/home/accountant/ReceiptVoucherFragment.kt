package com.edham.logistics.ui.home.accountant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.edham.logistics.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReceiptVoucherFragment : Fragment() {

    private val viewModel: AccountantViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_receipt_voucher, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val etClient = view.findViewById<TextInputEditText>(R.id.etClientName)
        val etAmount = view.findViewById<TextInputEditText>(R.id.etAmount)
        val toggleGroup = view.findViewById<com.google.android.material.button.MaterialButtonToggleGroup>(R.id.togglePaymentMethod)

        view.findViewById<MaterialButton>(R.id.btnConfirmVoucher).setOnClickListener {
            val client = etClient?.text?.toString() ?: ""
            val amount = etAmount?.text?.toString()?.toDoubleOrNull() ?: 0.0
            
            val method = when (toggleGroup.checkedButtonId) {
                R.id.btnCash -> "CASH"
                R.id.btnTransfer -> "TRANSFER"
                R.id.btnCheque -> "CHEQUE"
                else -> "CASH"
            }
            
            if (client.isNotEmpty() && amount > 0) {
                viewModel.submitReceiptVoucher(client, amount, method)
            } else {
                Toast.makeText(context, "يرجى إكمال البيانات", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.voucherSubmitted.observe(viewLifecycleOwner) {
            if (it) {
                Toast.makeText(context, "✅ تم حفظ السند وتحديث الخزنة بنجاح!", Toast.LENGTH_LONG).show()
                activity?.onBackPressed()
            }
        }
    }
}
