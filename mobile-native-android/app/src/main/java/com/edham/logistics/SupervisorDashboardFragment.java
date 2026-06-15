package com.edham.logistics;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class SupervisorDashboardFragment extends Fragment {

    // Views
    private TextView tvDeliveredToday;
    private TextView tvInTransitToday;
    private TextView tvOnTimeRate;
    private EditText etSearch;
    private RecyclerView rvActiveShipments;

    // Data
    private ActiveShipmentsAdapter adapter;
    private List<ShipmentItem> shipmentsList = new ArrayList<>();
    private List<ShipmentItem> filteredList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_supervisor_dashboard, container, false);
        
        initViews(view);
        setupData();
        setupRecyclerView();
        setupSearch();
        animateCounters();
        
        return view;
    }

    private void initViews(View view) {
        tvDeliveredToday = view.findViewById(R.id.tvDeliveredToday);
        tvInTransitToday = view.findViewById(R.id.tvInTransitToday);
        tvOnTimeRate = view.findViewById(R.id.tvOnTimeRate);
        etSearch = view.findViewById(R.id.etSearch);
        rvActiveShipments = view.findViewById(R.id.rvActiveShipments);
    }

    private void setupData() {
        shipmentsList = getMockShipments();
        filteredList = new ArrayList<>(shipmentsList);
    }

    private void setupRecyclerView() {
        adapter = new ActiveShipmentsAdapter(filteredList);
        rvActiveShipments.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvActiveShipments.setAdapter(adapter);
        rvActiveShipments.setNestedScrollingEnabled(false);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterShipments(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void filterShipments(String query) {
        filteredList.clear();
        
        if (query.isEmpty()) {
            filteredList.addAll(shipmentsList);
        } else {
            String searchQuery = query.toLowerCase();
            for (ShipmentItem shipment : shipmentsList) {
                if (shipment.trackingNumber.toLowerCase().contains(searchQuery) ||
                    shipment.from.toLowerCase().contains(searchQuery) ||
                    shipment.to.toLowerCase().contains(searchQuery) ||
                    shipment.cargoType.toLowerCase().contains(searchQuery)) {
                    filteredList.add(shipment);
                }
            }
        }
        
        adapter.notifyDataSetChanged();
    }

    private void animateCounters() {
        animateCounter(tvDeliveredToday, 543);
        animateCounter(tvInTransitToday, 301);
        animatePercentage(tvOnTimeRate, 87);
    }

    private void animateCounter(TextView textView, int targetValue) {
        ValueAnimator animator = ValueAnimator.ofInt(0, targetValue);
        animator.setDuration(1500);
        animator.addUpdateListener(animation -> {
            textView.setText(String.valueOf(animation.getAnimatedValue()));
        });
        animator.start();
    }

    private void animatePercentage(TextView textView, int targetValue) {
        ValueAnimator animator = ValueAnimator.ofInt(0, targetValue);
        animator.setDuration(1500);
        animator.addUpdateListener(animation -> {
            textView.setText(animation.getAnimatedValue() + "%");
        });
        animator.start();
    }

    private List<ShipmentItem> getMockShipments() {
        List<ShipmentItem> items = new ArrayList<>();
        
        items.add(new ShipmentItem("#EDH-2024-001", "الرياض", "جدة", "بضائع عامة", "250 ر.س", ShipmentStatus.IN_TRANSIT, "سالم محمد", "٢٠ دقيقة"));
        items.add(new ShipmentItem("#EDH-2024-002", "جدة", "الدمام", "أجهزة إلكترونية", "450 ر.س", ShipmentStatus.DELIVERED, "خالد أحمد", "تم التوصيل"));
        items.add(new ShipmentItem("#EDH-2024-003", "الدمام", "الرياض", "مواد غذائية", "180 ر.س", ShipmentStatus.PENDING, "ناصر علي", "٤٥ دقيقة"));
        items.add(new ShipmentItem("#EDH-2024-004", "مكة", "المدينة", "أثاث", "320 ر.س", ShipmentStatus.IN_TRANSIT, "عبدالله سعيد", "١٥ دقيقة"));
        items.add(new ShipmentItem("#EDH-2024-005", "الرياض", "أبها", "بضائع عامة", "290 ر.س", ShipmentStatus.DELAYED, "محمد يوسف", "متأخر ٢٠ دقيقة"));
        items.add(new ShipmentItem("#EDH-2024-006", "الخبر", "الرياض", "مواد طبية", "380 ر.س", ShipmentStatus.IN_TRANSIT, "فهد سلمان", "٣٠ دقيقة"));
        items.add(new ShipmentItem("#EDH-2024-007", "تبوك", "حائل", "أدوات كهربائية", "410 ر.س", ShipmentStatus.PENDING, "خالد العنزي", "٦٠ دقيقة"));
        items.add(new ShipmentItem("#EDH-2024-008", "نجران", "جازان", "ملابس", "220 ر.س", ShipmentStatus.DELIVERED, "سعد مبارك", "تم التوصيل"));
        
        return items;
    }

    // Data Classes
    public static class ShipmentItem {
        public String trackingNumber;
        public String from;
        public String to;
        public String cargoType;
        public String price;
        public ShipmentStatus status;
        public String driverName;
        public String eta;

        public ShipmentItem(String trackingNumber, String from, String to, String cargoType, 
                          String price, ShipmentStatus status, String driverName, String eta) {
            this.trackingNumber = trackingNumber;
            this.from = from;
            this.to = to;
            this.cargoType = cargoType;
            this.price = price;
            this.status = status;
            this.driverName = driverName;
            this.eta = eta;
        }
    }

    public enum ShipmentStatus {
        PENDING, IN_TRANSIT, DELIVERED, DELAYED
    }

    // RecyclerView Adapter
    public class ActiveShipmentsAdapter extends RecyclerView.Adapter<ActiveShipmentsAdapter.ViewHolder> {

        private List<ShipmentItem> items;

        public ActiveShipmentsAdapter(List<ShipmentItem> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_supervisor_shipment, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ShipmentItem item = items.get(position);
            
            holder.tvTrackingNumber.setText(item.trackingNumber);
            holder.tvRoute.setText(item.from + " → " + item.to);
            holder.tvCargoType.setText(item.cargoType);
            holder.tvPrice.setText(item.price);
            holder.tvDriverName.setText(item.driverName);
            holder.tvEta.setText(item.eta);

            // Status styling
            String statusText;
            int statusColor;
            int statusBgColor;
            
            switch (item.status) {
                case PENDING:
                    statusText = "قيد الانتظار";
                    statusColor = R.color.status_pending;
                    statusBgColor = R.color.status_pending_bg;
                    break;
                case IN_TRANSIT:
                    statusText = "قيد النقل";
                    statusColor = R.color.status_in_transit;
                    statusBgColor = R.color.status_in_transit_bg;
                    break;
                case DELIVERED:
                    statusText = "تم التوصيل";
                    statusColor = R.color.status_delivered;
                    statusBgColor = R.color.status_delivered_bg;
                    break;
                case DELAYED:
                    statusText = "متأخرة";
                    statusColor = R.color.status_cancelled;
                    statusBgColor = R.color.status_cancelled_bg;
                    break;
                default:
                    statusText = "غير معروف";
                    statusColor = R.color.text_secondary;
                    statusBgColor = R.color.card_stroke;
                    break;
            }
            
            holder.tvStatus.setText(statusText);
            holder.tvStatus.setTextColor(requireContext().getColor(statusColor));
            holder.tvStatus.setBackgroundColor(requireContext().getColor(statusBgColor));

            // ETA color based on status
            if (item.status == ShipmentStatus.DELAYED) {
                holder.tvEta.setTextColor(requireContext().getColor(R.color.error));
            } else if (item.status == ShipmentStatus.IN_TRANSIT) {
                holder.tvEta.setTextColor(requireContext().getColor(R.color.info));
            } else {
                holder.tvEta.setTextColor(requireContext().getColor(R.color.success));
            }

            holder.itemView.setOnClickListener(v -> {
                // Navigate to shipment details
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTrackingNumber, tvRoute, tvCargoType, tvPrice;
            TextView tvDriverName, tvEta, tvStatus;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvTrackingNumber = itemView.findViewById(R.id.tvTrackingNumber);
                tvRoute = itemView.findViewById(R.id.tvRoute);
                tvCargoType = itemView.findViewById(R.id.tvCargoType);
                tvPrice = itemView.findViewById(R.id.tvPrice);
                tvDriverName = itemView.findViewById(R.id.tvDriverName);
                tvEta = itemView.findViewById(R.id.tvEta);
                tvStatus = itemView.findViewById(R.id.tvStatus);
            }
        }
    }
}
