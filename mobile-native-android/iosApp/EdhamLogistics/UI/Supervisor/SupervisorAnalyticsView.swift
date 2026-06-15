import SwiftUI
import Charts

struct SupervisorAnalyticsView: View {
    @Environment(\.dismiss) var dismiss

    // Sample data for charts
    let tempData: [TempReading] = [
        .init(time: "08:00", value: -18.2),
        .init(time: "10:00", value: -17.5),
        .init(time: "12:00", value: -18.8),
        .init(time: "14:00", value: -16.0),
        .init(time: "16:00", value: -18.1),
        .init(time: "18:00", value: -18.5)
    ]

    let performanceData: [DailyPerformance] = [
        .init(day: "الأحد", count: 12),
        .init(day: "الاثنين", count: 15),
        .init(day: "الثلاثاء", count: 10),
        .init(day: "الأربعاء", count: 18),
        .init(day: "الخميس", count: 14)
    ]

    var body: some View {
        ZStack {
            AppColors.cockpitBlack.ignoresSafeArea()

            VStack(spacing: 0) {
                headerSection

                ScrollView {
                    VStack(spacing: 25) {
                        // Temperature Trend Chart
                        VStack(alignment: .leading, spacing: 15) {
                            Text("مؤشر حرارة الأسطول (متوسط)")
                                .font(.headline)
                                .foregroundColor(.white)

                            Chart {
                                ForEach(tempData) { item in
                                    LineMark(
                                        x: .value("الوقت", item.time),
                                        y: .value("الحرارة", item.value)
                                    )
                                    .foregroundStyle(AppColors.cockpitGreen)
                                    .interpolationMethod(.catmullRom)

                                    AreaMark(
                                        x: .value("الوقت", item.time),
                                        y: .value("الحرارة", item.value)
                                    )
                                    .foregroundStyle(LinearGradient(colors: [AppColors.cockpitGreen.opacity(0.3), .clear], startPoint: .top, endPoint: .bottom))
                                }
                            }
                            .frame(height: 200)
                            .chartYScale(domain: -20... -10)
                        }
                        .padding()
                        .background(AppColors.cockpitBlack2)
                        .cornerRadius(20)

                        // Delivery Performance Chart
                        VStack(alignment: .leading, spacing: 15) {
                            Text("إحصائيات التسليم اليومية")
                                .font(.headline)
                                .foregroundColor(.white)

                            Chart {
                                ForEach(performanceData) { item in
                                    BarMark(
                                        x: .value("اليوم", item.day),
                                        y: .value("العدد", item.count)
                                    )
                                    .foregroundStyle(AppColors.cockpitBlue)
                                    .cornerRadius(6)
                                }
                            }
                            .frame(height: 200)
                        }
                        .padding()
                        .background(AppColors.cockpitBlack2)
                        .cornerRadius(20)

                        // Summary Stats
                        HStack(spacing: 15) {
                            miniStat(title: "كفاءة الوقود", value: "94%", icon: "fuelpump.fill", color: .orange)
                            miniStat(title: "دقة المواعيد", value: "98%", icon: "clock.badge.checkmark.fill", color: .green)
                        }

                        Spacer(minLength: 50)
                    }
                    .padding()
                }
            }
        }
        .navigationBarHidden(true)
    }

    var headerSection: some View {
        HStack {
            Button(action: { dismiss() }) {
                Image(systemName: "chevron.right")
                    .foregroundColor(.white)
                    .padding()
                    .background(AppColors.cockpitBlack2)
                    .clipShape(Circle())
            }
            Spacer()
            Text("التحليلات المتقدمة")
                .font(.headline)
                .foregroundColor(.white)
            Spacer()
            Image(systemName: "chart.bar.xaxis")
                .foregroundColor(AppColors.cockpitBlue)
                .padding()
        }
        .padding()
    }

    func miniStat(title: String, value: String, icon: String, color: Color) -> some View {
        VStack(spacing: 8) {
            Image(systemName: icon).foregroundColor(color)
            Text(value).font(.title3).bold().foregroundColor(.white)
            Text(title).font(.caption2).foregroundColor(.gray)
        }
        .frame(maxWidth: .infinity)
        .padding()
        .background(AppColors.cockpitBlack2)
        .cornerRadius(16)
        .overlay(RoundedRectangle(cornerRadius: 16).stroke(AppColors.border, lineWidth: 1))
    }
}

struct TempReading: Identifiable {
    let id = UUID()
    let time: String
    let value: Double
}

struct DailyPerformance: Identifiable {
    let id = UUID()
    let day: String
    let count: Int
}
