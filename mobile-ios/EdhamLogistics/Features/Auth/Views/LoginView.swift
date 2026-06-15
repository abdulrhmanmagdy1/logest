//
//  LoginView.swift
//  Edham Logistics
//

import SwiftUI

struct LoginView: View {
    @EnvironmentObject var authManager: AuthManager
    @State private var email = ""
    @State private var password = ""
    @State private var isLoading = false
    @State private var showError = false
    @State private var errorMessage = ""
    
    var body: some View {
        ZStack {
            // Background
            ColorTheme.background
                .ignoresSafeArea()
            
            VStack(spacing: 30) {
                // Logo
                VStack(spacing: 16) {
                    Image(systemName: "truck.box.fill")
                        .resizable()
                        .scaledToFit()
                        .frame(width: 100, height: 100)
                        .foregroundColor(ColorTheme.primary)
                    
                    Text("إدهام لوجستيكس")
                        .font(.largeTitle)
                        .fontWeight(.bold)
                        .foregroundColor(.white)
                    
                    Text("نظام النقل المبرد الذكي")
                        .font(.subheadline)
                        .foregroundColor(ColorTheme.textSecondary)
                }
                .padding(.top, 60)
                
                // Form
                VStack(spacing: 20) {
                    // Email Field
                    VStack(alignment: .leading, spacing: 8) {
                        Text("البريد الإلكتروني")
                            .font(.caption)
                            .foregroundColor(ColorTheme.textSecondary)
                        
                        HStack {
                            Image(systemName: "envelope")
                                .foregroundColor(ColorTheme.textSecondary)
                            TextField("", text: $email)
                                .textContentType(.emailAddress)
                                .keyboardType(.emailAddress)
                                .autocapitalization(.none)
                                .foregroundColor(.white)
                        }
                        .padding()
                        .background(
                            RoundedRectangle(cornerRadius: 12)
                                .fill(ColorTheme.cardBackground)
                        )
                        .overlay(
                            RoundedRectangle(cornerRadius: 12)
                                .stroke(ColorTheme.border, lineWidth: 1)
                        )
                    }
                    
                    // Password Field
                    VStack(alignment: .leading, spacing: 8) {
                        Text("كلمة المرور")
                            .font(.caption)
                            .foregroundColor(ColorTheme.textSecondary)
                        
                        HStack {
                            Image(systemName: "lock")
                                .foregroundColor(ColorTheme.textSecondary)
                            SecureField("", text: $password)
                                .textContentType(.password)
                                .foregroundColor(.white)
                        }
                        .padding()
                        .background(
                            RoundedRectangle(cornerRadius: 12)
                                .fill(ColorTheme.cardBackground)
                        )
                        .overlay(
                            RoundedRectangle(cornerRadius: 12)
                                .stroke(ColorTheme.border, lineWidth: 1)
                        )
                    }
                    
                    // Forgot Password
                    Button("نسيت كلمة المرور؟") {
                        // Handle forgot password
                    }
                    .font(.caption)
                    .foregroundColor(ColorTheme.primary)
                    .frame(maxWidth: .infinity, alignment: .trailing)
                }
                .padding(.horizontal, 24)
                
                // Login Button
                Button(action: login) {
                    HStack {
                        if isLoading {
                            ProgressView()
                                .progressViewStyle(CircularProgressViewStyle(tint: .white))
                        } else {
                            Text("تسجيل الدخول")
                                .fontWeight(.bold)
                        }
                    }
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .padding()
                    .background(
                        LinearGradient(
                            colors: [ColorTheme.primary, ColorTheme.primaryDark],
                            startPoint: .leading,
                            endPoint: .trailing
                        )
                    )
                    .cornerRadius(12)
                }
                .disabled(isLoading || email.isEmpty || password.isEmpty)
                .opacity(isLoading || email.isEmpty || password.isEmpty ? 0.6 : 1)
                .padding(.horizontal, 24)
                
                // Register Link
                HStack {
                    Text("ليس لديك حساب؟")
                        .foregroundColor(ColorTheme.textSecondary)
                    Button("سجل الآن") {
                        // Navigate to register
                    }
                    .foregroundColor(ColorTheme.primary)
                }
                .font(.callout)
                
                Spacer()
                
                // Version
                Text("الإصدار 1.0.0")
                    .font(.caption2)
                    .foregroundColor(ColorTheme.textSecondary)
                    .padding(.bottom, 20)
            }
        }
        .alert("خطأ", isPresented: $showError) {
            Button("حسناً", role: .cancel) {}
        } message: {
            Text(errorMessage)
        }
        .onChange(of: authManager.error) { error in
            if let error = error {
                errorMessage = error.localizedDescription
                showError = true
                isLoading = false
            }
        }
    }
    
    private func login() {
        isLoading = true
        Task {
            await authManager.login(email: email, password: password)
            isLoading = false
        }
    }
}

// MARK: - Preview
struct LoginView_Previews: PreviewProvider {
    static var previews: some View {
        LoginView()
            .environmentObject(AuthManager.shared)
            .preferredColorScheme(.dark)
    }
}
