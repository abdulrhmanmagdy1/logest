import SwiftUI

struct DriverRewardsView: View {
    @Environment(\.dismiss) var dismiss

    var body: some View {
        ZStack {
            AppColors.cockpitBlack.ignoresSafeArea()

            VStack(spacing: 0) {
                headerSection

                ScrollView {
                    VStack(spacing: 20) {
                        // Earnings Highlight
                        earningsHighlight

                        // Performance Badges
                        badgesSection

                        // Recent Bonuses List
                        bonusesList

                        Spacer()
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
            Text("المكافآت والأداء")
                .font(.headline)
                .foregroundColor(.white)
            Spacer()
            Image(systemName: "star.fill")
                .foregroundColor(.yellow)
                .padding()
        }
        .padding()
    }

    var earningsHighlight: some View {
        VStack(spacing: 12) {
            Text("إجمالي حوافز الشهر")
                .font(.caption)
                .foregroundColor(.gray)

            Text("2,450")
                .font(.system(size: 48, weight: .black, design: .monospaced))
                .foregroundColor(AppColors.cockpitGreen)

            Text("ريال سعودي")
                .font(.subheadline)
                .foregroundColor(.white)
        }
        .frame(maxWidth: .infinity)
        .padding(30)
        .background(AppColors.cockpitBlack2)
        .cornerRadius(24)
        .overlay(RoundedRectangle(cornerRadius: 24).stroke(AppColors.greenGlow, lineWidth: 1))
    }

    var badgesSection: some View {
        VStack(alignment: .leading, spacing: 15) {
            Text("الأوسمة المستحقة").font(.headline).foregroundColor(.white)

            HStack(spacing: 15) {
                badgeView(icon: "shield.fill", title: "قيادة آمنة", color: .blue)
                badgeView(icon: "bolt.fill", title: "توصيل سريع", color: .orange)
                badgeView(icon: "heart.fill", title: "تقييم ممتاز", color: .red)
            }
        }
    }

    func badgeView(icon: String, title: String, color: Color) -> some View {
        VStack {
            ZStack {
                Circle().fill(color.opacity(0.15)).frame(width: 60, height: 60)
                Image(systemName: icon).font(.title2).foregroundColor(color)
            }
            Text(title).font(.caption).foregroundColor(.white)
        }
        .frame(maxWidth: .infinity)
    }

    var bonusesList: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("سجل المكافآت").font(.headline).foregroundColor(.white)

            bonusRow(title: "مكافأة الانضباط الأسبوعي", amount: "+200", date: "24 مايو")
            bonusRow(title: "بونص توصيل مبرد (VIP)", amount: "+150", date: "22 مايو")
            bonusRow(title: "حافز المسافات الطويلة", amount: "+500", date: "20 مايو")
        }
    }

    func bonusRow(title: String, amount: String, date: String) -> some View {
        HStack {
            VStack(alignment: .leading) {
                Text(title).font(.subheadline).foregroundColor(.white)
                Text(date).font(.caption2).foregroundColor(.gray)
            }
            Spacer()
            Text(amount)
                .font(.subheadline).bold()
                .foregroundColor(AppColors.cockpitGreen)
        }
        .padding()
        .background(AppColors.cockpitCard)
        .cornerRadius(12)
    }
}
