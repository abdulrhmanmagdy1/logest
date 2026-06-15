import SwiftUI

struct ShipmentDetailView: View {
    let shipmentId: Int
    @Environment(\.dismiss) var dismiss
    @State private var isLoading = false

    var body: some View {
        ZStack {
            AppColors.cockpitBlack.ignoresSafeArea()

            VStack(spacing: 0) {
                // Custom Navigation Bar
                headerView

                ScrollView {
                    VStack(spacing: 20) {
                        // Map Section
                        LiveMapView()
                            .frame(height: 250)

                        // Shipment Info Card
                        infoCard

                        // Timeline/Status Section
                        statusTimeline

                        Spacer(minLength: 30)
                    }
                    .padding()
                }

                // Bottom Action Button (for Driver)
                actionButton
            }
        }
        .navigationBarHidden(true)
    }

    var headerView: some View {
        HStack {
            Button(action: { dismiss() }) {
                Image(systemName: "chevron.right")
                    .foregroundColor(.white)
                    .padding()
                    .background(AppColors.cockpitBlack2)
                    .clipShape(Circle())
            }

            Spacer()

            Text("تفاصيل الشحنة #\(shipmentId)")
                .font(.headline)
                .foregroundColor(.white)

            Spacer()

            Button(action: {}) {
                Image(systemName: "phone.fill")
                    .foregroundColor(AppColors.cockpitGreen)
                    .padding()
                    .background(AppColors.cockpitBlack2)
                    .clipShape(Circle())
            }
        }
        .padding()
    }

    var infoCard: some View {
        VStack(alignment: .leading, spacing: 15) {
            HStack {
                Label("المحتوى: مواد غذائية مبردة", systemImage: "box.truck")
                    .font(.subheadline)
                    .foregroundColor(AppColors.cockpitBlue)
                Spacer()
                Text("-18°C")
                    .font(.caption)
                    .bold()
                    .padding(6)
                    .background(AppColors.greenGlow)
                    .foregroundColor(AppColors.cockpitGreen)
                    .cornerRadius(8)
            }

            Divider().background(AppColors.border)

            locationRow(type: "نقطة الاستلام", name: "مستودع السلي المركزي", address: "الرياض، مخرج 18", color: .green)
            locationRow(type: "نقطة التسليم", name: "هايبر بندة - الياسمين", address: "الرياض، طريق الملك عبدالعزيز", color: .red)
        }
        .padding()
        .background(AppColors.cockpitBlack2)
        .cornerRadius(20)
        .overlay(RoundedRectangle(cornerRadius: 20).stroke(AppColors.border, lineWidth: 1))
    }

    var statusTimeline: some View {
        VStack(alignment: .leading, spacing: 15) {
            Text("حالة الرحلة")
                .font(.headline)
                .foregroundColor(.white)

            timelineItem(status: "تم استلام الطلب", time: "09:00 AM", isCompleted: true)
            timelineItem(status: "جاري التحميل", time: "10:30 AM", isCompleted: true)
            timelineItem(status: "في الطريق", time: "11:15 AM", isCurrent: true)
            timelineItem(status: "تم التسليم", time: "--:--", isPending: true)
        }
        .padding()
        .background(AppColors.cockpitBlack2)
        .cornerRadius(20)
    }

    func locationRow(type: String, name: String, address: String, color: Color) -> some View {
        HStack(alignment: .top) {
            VStack {
                Circle().fill(color).frame(width: 10, height: 10)
                Rectangle().fill(color.opacity(0.3)).frame(width: 2, height: 30)
            }
            VStack(alignment: .leading, spacing: 2) {
                Text(type).font(.caption2).foregroundColor(.gray)
                Text(name).font(.subheadline).bold().foregroundColor(.white)
                Text(address).font(.caption).foregroundColor(.gray)
            }
        }
    }

    func timelineItem(status: String, time: String, isCompleted: Bool = false, isCurrent: Bool = false, isPending: Bool = false) -> some View {
        HStack {
            Image(systemName: isCompleted ? "checkmark.circle.fill" : (isCurrent ? "arrow.right.circle.fill" : "circle"))
                .foregroundColor(isCompleted ? .green : (isCurrent ? AppColors.cockpitBlue : .gray))

            Text(status)
                .foregroundColor(isPending ? .gray : .white)
                .font(.subheadline)

            Spacer()

            Text(time)
                .font(.caption2)
                .foregroundColor(.gray)
        }
    }

    var actionButton: some View {
        Button(action: { /* Handle status update */ }) {
            Text("تحديث حالة الشحنة")
                .bold()
                .frame(maxWidth: .infinity)
                .frame(height: 56)
                .background(AppColors.cockpitGreen)
                .foregroundColor(AppColors.cockpitBlack)
                .cornerRadius(16)
                .padding()
        }
        .background(AppColors.cockpitBlack2)
    }
}
