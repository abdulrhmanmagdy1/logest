import SwiftUI

struct AccountantDashboardView: View {
    @StateObject private var viewModel = AccountantViewModel()

    var body: some View {
        NavigationView {
            ZStack {
                AppColors.cockpitBlack.ignoresSafeArea()

                if viewModel.isLoading {
                    ProgressView().tint(AppColors.cockpitGreen)
                } else {
                    ScrollView {
                        VStack(spacing: 20) {
                            headerSection

                            financialSummary

                            recentInvoicesSection

                            Spacer()
                        }
                        .padding()
                    }
                    .refreshable {
                        viewModel.fetchData()
                    }
                }
            }
            .navigationBarHidden(true)
        }
    }

    var headerSection: some View {
        HStack {
            VStack(alignment: .leading) {
                Text("المحاسبة المالية")
                    .font(.caption)
                    .foregroundColor(.gray)
                Text("لوحة التحكم")
                    .font(.title2)
                    .bold()
                    .foregroundColor(.white)
            }
            Spacer()
            Button(action: { SessionManager.shared.clearSession() }) {
                Image(systemName: "power")
                    .foregroundColor(.red)
                    .padding(10)
                    .background(AppColors.cockpitBlack2)
                    .clipShape(Circle())
            }
        }
        .padding()
        .background(AppColors.cockpitBlack2)
        .cornerRadius(16)
    }

    var financialSummary: some View {
        VStack(spacing: 15) {
            HStack(spacing: 15) {
                summaryBox(title: "إجمالي الدخل", value: "450,000", color: AppColors.cockpitGreen)
                summaryBox(title: "المصروفات", value: "120,000", color: .red)
            }

            summaryBox(title: "صافي الأرباح", value: "330,000", color: AppColors.cockpitBlue, large: true)
        }
    }

    func summaryBox(title: String, value: String, color: Color, large: Bool = false) -> some View {
        VStack(spacing: 8) {
            Text(title).font(.caption).foregroundColor(.gray)
            Text(value)
                .font(large ? .title : .headline)
                .bold()
                .foregroundColor(color)
            Text("ريال سعودي").font(.system(size: 8)).foregroundColor(.gray)
        }
        .frame(maxWidth: .infinity)
        .padding()
        .background(AppColors.cockpitBlack2)
        .cornerRadius(12)
        .overlay(RoundedRectangle(cornerRadius: 12).stroke(AppColors.border, lineWidth: 1))
    }

    var recentInvoicesSection: some View {
        VStack(alignment: .leading, spacing: 15) {
            Text("آخر الفواتير").font(.headline).foregroundColor(.white)

            ForEach(viewModel.invoices) { invoice in
                invoiceRow(invoice)
            }
        }
    }

    func invoiceRow(_ invoice: Invoice) -> some View {
        HStack {
            VStack(alignment: .leading) {
                Text(invoice.customerName).font(.subheadline).bold().foregroundColor(.white)
                Text("رقم الفاتورة: \(invoice.number)").font(.caption2).foregroundColor(.gray)
            }
            Spacer()
            VStack(alignment: .trailing) {
                Text("\(invoice.amount) ريال").font(.subheadline).bold().foregroundColor(AppColors.cockpitBlue)
                Text(invoice.status)
                    .font(.system(size: 8))
                    .padding(.horizontal, 6)
                    .padding(.vertical, 2)
                    .background(invoice.status == "مدفوعة" ? Color.green.opacity(0.2) : Color.orange.opacity(0.2))
                    .foregroundColor(invoice.status == "مدفوعة" ? .green : .orange)
                    .cornerRadius(4)
            }
        }
        .padding()
        .background(AppColors.cockpitCard)
        .cornerRadius(12)
    }
}

class AccountantViewModel: ObservableObject {
    @Published var isLoading = false
    @Published var invoices: [Invoice] = [
        Invoice(customerName: "شركة بندة للتجزئة", number: "INV-2024-001", amount: "12,500", status: "مدفوعة"),
        Invoice(customerName: "أسواق العثيم", number: "INV-2024-002", amount: "8,300", status: "قيد الانتظار"),
        Invoice(customerName: "التميمي ماركت", number: "INV-2024-003", amount: "15,000", status: "مدفوعة")
    ]

    func fetchData() {
        isLoading = true
        DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
            self.isLoading = false
        }
    }
}

struct Invoice: Identifiable {
    let id = UUID()
    let customerName: String
    let number: String
    let amount: String
    let status: String
}
