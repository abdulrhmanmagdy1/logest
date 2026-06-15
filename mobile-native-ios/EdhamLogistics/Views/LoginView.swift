// ============================================
// 🔐 Login View - SwiftUI
// ============================================

import SwiftUI

struct LoginView: View {
    @StateObject private var viewModel = AuthViewModel()
    @State private var email = ""
    @State private var password = ""
    @State private var showPassword = false
    
    var body: some View {
        ZStack {
            // Background
            LinearGradient(
                colors: [Color(hex: "0A1128"), Color(hex: "111C3A")],
                startPoint: .top,
                endPoint: .bottom
            )
            .ignoresSafeArea()
            
            ScrollView {
                VStack(spacing: 32) {
                    // Logo
                    VStack(spacing: 8) {
                        Image(systemName: "shippingbox.fill")
                            .font(.system(size: 60))
                            .foregroundColor(.white)
                            .frame(width: 120, height: 120)
                            .background(
                                LinearGradient(
                                    colors: [Color(hex: "2563EB"), Color(hex: "1D4ED8")],
                                    startPoint: .topLeading,
                                    endPoint: .bottomTrailing
                                )
                            )
                            .cornerRadius(24)
                        
                        Text("Edham")
                            .font(.system(size: 48, weight: .bold))
                            .foregroundColor(.white)
                        
                        Text("LOGISTICS")
                            .font(.system(size: 16, weight: .medium))
                            .tracking(8)
                            .foregroundColor(Color(hex: "64748B"))
                    }
                    .padding(.top, 60)
                    
                    // Form
                    VStack(spacing: 20) {
                        // Email Field
                        TextField("", text: $email)
                            .placeholder(when: email.isEmpty) {
                                Text("البريد الإلكتروني").foregroundColor(Color(hex: "64748B"))
                            }
                            .foregroundColor(.white)
                            .padding()
                            .background(Color(hex: "1A2744"))
                            .cornerRadius(12)
                            .keyboardType(.emailAddress)
                            .autocapitalization(.none)
                        
                        // Password Field
                        ZStack {
                            if showPassword {
                                TextField("", text: $password)
                                    .placeholder(when: password.isEmpty) {
                                        Text("كلمة المرور").foregroundColor(Color(hex: "64748B"))
                                    }
                                    .foregroundColor(.white)
                            } else {
                                SecureField("", text: $password)
                                    .placeholder(when: password.isEmpty) {
                                        Text("كلمة المرور").foregroundColor(Color(hex: "64748B"))
                                    }
                                    .foregroundColor(.white)
                            }
                            
                            HStack {
                                Spacer()
                                Button(action: { showPassword.toggle() }) {
                                    Image(systemName: showPassword ? "eye.slash" : "eye")
                                        .foregroundColor(Color(hex: "64748B"))
                                }
                                .padding(.trailing, 16)
                            }
                        }
                        .padding()
                        .background(Color(hex: "1A2744"))
                        .cornerRadius(12)
                        
                        // Remember Me & Forgot Password
                        HStack {
                            Toggle("تذكرني", isOn: .constant(false))
                                .toggleStyle(CheckboxToggleStyle())
                                .foregroundColor(Color(hex: "AAB4C8"))
                            
                            Spacer()
                            
                            Button("نسيت كلمة المرور؟") {}
                                .foregroundColor(Color(hex: "2563EB"))
                                .font(.system(size: 14, weight: .medium))
                        }
                    }
                    .padding(.horizontal, 24)
                    
                    // Login Button
                    Button(action: {
                        Task {
                            await viewModel.login(email: email, password: password)
                        }
                    }) {
                        if viewModel.isLoading {
                            ProgressView()
                                .progressViewStyle(CircularProgressViewStyle(tint: .white))
                        } else {
                            Text("تسجيل الدخول")
                                .font(.system(size: 16, weight: .semibold))
                                .foregroundColor(.white)
                        }
                    }
                    .frame(maxWidth: .infinity)
                    .frame(height: 56)
                    .background(
                        LinearGradient(
                            colors: [Color(hex: "2563EB"), Color(hex: "1D4ED8")],
                            startPoint: .leading,
                            endPoint: .trailing
                        )
                    )
                    .cornerRadius(12)
                    .shadow(color: Color(hex: "2563EB").opacity(0.3), radius: 12, x: 0, y: 4)
                    .padding(.horizontal, 24)
                    .disabled(viewModel.isLoading)
                    
                    // Demo Login Options
                    VStack(spacing: 16) {
                        Text("تسجيل دخول سريع (للتجربة)")
                            .foregroundColor(Color(hex: "AAB4C8"))
                            .font(.system(size: 14))
                        
                        HStack(spacing: 12) {
                            Button(action: {
                                Task {
                                    await viewModel.demoLogin(role: .client)
                                }
                            }) {
                                HStack {
                                    Image(systemName: "person")
                                    Text("عميل")
                                }
                                .foregroundColor(.white)
                                .padding(.vertical, 12)
                                .padding(.horizontal, 24)
                                .overlay(
                                    RoundedRectangle(cornerRadius: 12)
                                        .stroke(Color(hex: "2563EB"), lineWidth: 2)
                                )
                            }
                            
                            Button(action: {
                                Task {
                                    await viewModel.demoLogin(role: .driver)
                                }
                            }) {
                                HStack {
                                    Image(systemName: "truck")
                                    Text("سائق")
                                }
                                .foregroundColor(.white)
                                .padding(.vertical, 12)
                                .padding(.horizontal, 24)
                                .overlay(
                                    RoundedRectangle(cornerRadius: 12)
                                        .stroke(Color(hex: "2563EB"), lineWidth: 2)
                                )
                            }
                        }
                    }
                    .padding(.top, 16)
                    
                    // Error Message
                    if let error = viewModel.errorMessage {
                        Text(error)
                            .foregroundColor(.red)
                            .font(.system(size: 14))
                            .padding(.top, 8)
                    }
                    
                    Spacer()
                    
                    // Register Link
                    HStack {
                        Text("ليس لديك حساب؟")
                            .foregroundColor(Color(hex: "AAB4C8"))
                        
                        Button("سجل الآن") {}
                            .foregroundColor(Color(hex: "F5C542"))
                            .font(.system(size: 14, weight: .semibold))
                    }
                    .padding(.bottom, 32)
                }
            }
        }
    }
}

// MARK: - Helper Views
struct CheckboxToggleStyle: ToggleStyle {
    func makeBody(configuration: Configuration) -> some View {
        Button(action: { configuration.isOn.toggle() }) {
            HStack {
                Image(systemName: configuration.isOn ? "checkmark.square.fill" : "square")
                    .foregroundColor(configuration.isOn ? Color(hex: "2563EB") : Color(hex: "64748B"))
                configuration.label
            }
        }
    }
}

extension View {
    func placeholder<Content: View>(
        when shouldShow: Bool,
        alignment: Alignment = .leading,
        @ViewBuilder placeholder: () -> Content
    ) -> some View {
        ZStack(alignment: alignment) {
            placeholder().opacity(shouldShow ? 1 : 0)
            self
        }
    }
}

// MARK: - Color Extension
extension Color {
    init(hex: String) {
        let hex = hex.trimmingCharacters(in: CharacterSet.alphanumerics.inverted)
        var int: UInt64 = 0
        Scanner(string: hex).scanHexInt64(&int)
        let a, r, g, b: UInt64
        switch hex.count {
        case 3:
            (a, r, g, b) = (255, (int >> 8) * 17, (int >> 4 & 0xF) * 17, (int & 0xF) * 17)
        case 6:
            (a, r, g, b) = (255, int >> 16, int >> 8 & 0xFF, int & 0xFF)
        case 8:
            (a, r, g, b) = (int >> 24, int >> 16 & 0xFF, int >> 8 & 0xFF, int & 0xFF)
        default:
            (a, r, g, b) = (255, 0, 0, 0)
        }
        self.init(
            .sRGB,
            red: Double(r) / 255,
            green: Double(g) / 255,
            blue: Double(b) / 255,
            opacity: Double(a) / 255
        )
    }
}

// MARK: - Preview
struct LoginView_Previews: PreviewProvider {
    static var previews: some View {
        LoginView()
    }
}
