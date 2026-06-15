import SwiftUI

struct LoginView: View {
    @StateObject private var viewModel = LoginViewModel()

    var body: some View {
        ZStack {
            AppColors.cockpitBlack.ignoresSafeArea()

            VStack(spacing: 30) {
                Spacer()

                // Logo or Icon
                ZStack {
                    Circle()
                        .fill(AppColors.greenGlow)
                        .frame(width: 100, height: 100)
                    Image(systemName: "truck.box.fill")
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .frame(width: 50, height: 50)
                        .foregroundColor(AppColors.cockpitGreen)
                }

                VStack(spacing: 10) {
                    Text("إدهام للنقل")
                        .font(.largeTitle)
                        .bold()
                        .foregroundColor(.white)
                    Text("نظام إدارة الأسطول المبرد")
                        .font(.subheadline)
                        .foregroundColor(.gray)
                }

                VStack(spacing: 20) {
                    // Username field
                    TextField("اسم المستخدم", text: $viewModel.username)
                        .padding()
                        .background(AppColors.cockpitBlack2)
                        .cornerRadius(12)
                        .overlay(RoundedRectangle(cornerRadius: 12).stroke(AppColors.border, lineWidth: 1))
                        .foregroundColor(.white)
                        .autocapitalization(.none)

                    // Password field
                    SecureField("كلمة المرور", text: $viewModel.password)
                        .padding()
                        .background(AppColors.cockpitBlack2)
                        .cornerRadius(12)
                        .overlay(RoundedRectangle(cornerRadius: 12).stroke(AppColors.border, lineWidth: 1))
                        .foregroundColor(.white)
                }
                .padding(.horizontal)

                if let error = viewModel.errorMessage {
                    Text(error)
                        .foregroundColor(.red)
                        .font(.caption)
                }

                Button(action: { viewModel.login() }) {
                    HStack {
                        if viewModel.isLoading {
                            ProgressView()
                                .tint(AppColors.cockpitBlack)
                                .padding(.trailing, 10)
                        }
                        Text("تسجيل الدخول")
                            .bold()
                    }
                    .frame(maxWidth: .infinity)
                    .frame(height: 56)
                    .background(AppColors.cockpitGreen)
                    .foregroundColor(AppColors.cockpitBlack)
                    .cornerRadius(12)
                    .shadow(color: AppColors.cockpitGreen.opacity(0.3), radius: 10)
                }
                .padding(.horizontal)
                .disabled(viewModel.isLoading)

                Spacer()

                Text("© 2024 إدهام للنقل المبرد")
                    .font(.caption2)
                    .foregroundColor(.gray)
            }
            .padding()
        }
    }
}
