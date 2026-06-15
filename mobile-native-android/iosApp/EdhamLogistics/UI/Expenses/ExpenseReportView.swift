import SwiftUI

struct ExpenseReportView: View {
    @Environment(\.dismiss) var dismiss
    @State private var expenseType = "وقود"
    @State private var amount = ""
    @State private var odometer = ""
    @State private var notes = ""
    @State private var showImagePicker = false
    @State private var receiptImage: UIImage?
    @State private var isSubmitting = false

    let expenseTypes = ["وقود", "صيانة", "رسوم طريق", "أخرى"]

    var body: some View {
        ZStack {
            AppColors.cockpitBlack.ignoresSafeArea()

            VStack(spacing: 0) {
                headerSection

                ScrollView {
                    VStack(spacing: 20) {
                        // Type Selection
                        VStack(alignment: .leading) {
                            Text("نوع المصروف").font(.caption).foregroundColor(.gray)
                            Picker("Type", selection: $expenseType) {
                                ForEach(expenseTypes, id: \.self) { Text($0) }
                            }
                            .pickerStyle(.segmented)
                            .background(AppColors.cockpitBlack2)
                        }

                        // Amount Field
                        customTextField(label: "المبلغ (ريال)", text: $amount, placeholder: "0.00", icon: "scroll.fill", keyboardType: .decimalPad)

                        // Odometer Field
                        customTextField(label: "قراءة العداد (كم)", text: $odometer, placeholder: "أدخل القراءة الحالية", icon: "speedometer", keyboardType: .numberPad)

                        // Receipt Upload
                        VStack(alignment: .leading) {
                            Text("صورة الإيصال").font(.caption).foregroundColor(.gray)
                            Button(action: { showImagePicker = true }) {
                                if let image = receiptImage {
                                    Image(uiImage: image)
                                        .resizable()
                                        .aspectRatio(contentMode: .fill)
                                        .frame(height: 150)
                                        .cornerRadius(12)
                                } else {
                                    HStack {
                                        Image(systemName: "camera.fill")
                                        Text("التقاط صورة الإيصال")
                                    }
                                    .frame(maxWidth: .infinity)
                                    .frame(height: 100)
                                    .background(AppColors.cockpitBlack2)
                                    .cornerRadius(12)
                                    .overlay(RoundedRectangle(cornerRadius: 12).stroke(AppColors.border, lineWidth: 1, style: StrokeStyle(dash: [5])))
                                }
                            }
                        }

                        // Notes
                        VStack(alignment: .leading) {
                            Text("ملاحظات إضافية").font(.caption).foregroundColor(.gray)
                            TextEditor(text: $notes)
                                .frame(height: 100)
                                .padding(8)
                                .background(AppColors.cockpitBlack2)
                                .cornerRadius(12)
                                .foregroundColor(.white)
                        }

                        Spacer(minLength: 30)
                    }
                    .padding()
                }

                submitButton
            }
        }
        .navigationBarHidden(true)
        .sheet(isPresented: $showImagePicker) {
            ImagePicker(image: $receiptImage)
        }
    }

    var headerSection: some View {
        HStack {
            Button(action: { dismiss() }) {
                Image(systemName: "xmark").foregroundColor(.white).padding().background(AppColors.cockpitBlack2).clipShape(Circle())
            }
            Spacer()
            Text("تسجيل مصروفات").font(.headline).foregroundColor(.white)
            Spacer()
            Rectangle().fill(Color.clear).frame(width: 44, height: 44)
        }
        .padding()
    }

    func customTextField(label: String, text: Binding<String>, placeholder: String, icon: String, keyboardType: UIKeyboardType = .default) -> some View {
        VStack(alignment: .leading) {
            Text(label).font(.caption).foregroundColor(.gray)
            HStack {
                Image(systemName: icon).foregroundColor(AppColors.cockpitGreen)
                TextField(placeholder, text: text)
                    .keyboardType(keyboardType)
                    .foregroundColor(.white)
            }
            .padding()
            .background(AppColors.cockpitBlack2)
            .cornerRadius(12)
            .overlay(RoundedRectangle(cornerRadius: 12).stroke(AppColors.border, lineWidth: 1))
        }
    }

    var submitButton: some View {
        Button(action: { submitExpense() }) {
            HStack {
                if isSubmitting { ProgressView().tint(.black).padding(.trailing, 8) }
                Text(isSubmitting ? "جاري الحفظ..." : "حفظ المصروف")
                    .bold()
            }
            .frame(maxWidth: .infinity)
            .frame(height: 56)
            .background(AppColors.cockpitGreen)
            .foregroundColor(AppColors.cockpitBlack)
            .cornerRadius(16)
            .padding()
        }
        .disabled(isSubmitting)
    }

    func submitExpense() {
        isSubmitting = true
        DispatchQueue.main.asyncAfter(deadline: .now() + 1.5) {
            isSubmitting = false
            dismiss()
        }
    }
}
