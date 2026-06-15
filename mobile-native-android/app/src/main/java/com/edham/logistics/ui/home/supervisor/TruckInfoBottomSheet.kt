package com.edham.logistics.ui.home.supervisor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.edham.logistics.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class TruckInfoBottomSheet(
    private val truckData: Map<String, Any>,
    private val onViewDetails: (String) -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_truck_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val shipmentId = truckData["shipmentId"] as? String ?: ""
        val driverName = truckData["driverName"] as? String ?: "سائق"
        val temp = truckData["temperature"] as? Double
        val battery = truckData["batteryLevel"] as? Number

        view.findViewById<TextView>(R.id.tvTruckId).text = "شحنة #$shipmentId"
        view.findViewById<TextView>(R.id.tvDriverName).text = "السائق: $driverName"
        view.findViewById<TextView>(R.id.tvTempStatus).text = if (temp != null) "$temp°م" else "--"
        view.findViewById<TextView>(R.id.tvBattery).text = if (battery != null) "$battery%" else "--"
        
        view.findViewById<View>(R.id.btnViewDetails).setOnClickListener {
            onViewDetails(shipmentId)
            dismiss()
        }
    }
}
