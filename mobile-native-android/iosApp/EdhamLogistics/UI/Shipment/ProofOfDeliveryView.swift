import SwiftUI
import PencilKit

struct ProofOfDeliveryView: View {
    let shipmentId: Int
    @Environment(\.dismiss) var dismiss
    @State private var canvasView = PKCanvasView()
    @State private var showImagePicker = false
    @State private var selectedImage: UIImage?
    @State private var isSubmitting = false

    var body: some View {
        ZStack {
            AppColors.cockpitBlack.ignoresSafeArea()

            VStack(spacing: 20) {
                headerSection

                ScrollView {
                    VStack(spacing: 25) {
                        // Image Upload Section
                        uploadSection

                        // Signature Section
                        signatureSection

                        Spacer()
                    }
                    .padding()
                }

                submitButton
            }
        }
        .navigationBarHidden(true)
        .sheet(isPresented: $showImagePicker) {
            ImagePicker(image: $selectedImage)
        }
    }

    var headerSection: some View {
        HStack {
            Button(action: { dismiss() }) {
                Image(systemName: "xmark")
                    .foregroundColor(.white)
                    .padding()
                    .background(AppColors.cockpitBlack2)
                    .clipShape(Circle())
            }
            Spacer()
            Text("إثبات التسليم #\(shipmentId)")
                .font(.headline)
                .foregroundColor(.white)
            Spacer()
            Rectangle().fill(Color.clear).frame(width: 44, height: 44)
        }
        .padding()
    }

    var uploadSection: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("صورة الفاتورة / المستند")
                .font(.subheadline)
                .foregroundColor(.gray)

            Button(action: { showImagePicker = true }) {
                ZStack {
                    if let image = selectedImage {
                        Image(uiImage: image)
                            .resizable()
                            .aspectRatio(contentMode: .fill)
                            .frame(height: 180)
                            .cornerRadius(12)
                    } else {
                        VStack(spacing: 8) {
                            Image(systemName: "camera.fill")
                                .font(.largeTitle)
                            Text("التقط صورة")
                                .font(.caption)
                        }
                        .foregroundColor(AppColors.cockpitBlue)
                        .frame(maxWidth: .infinity)
                        .frame(height: 180)
                        .background(AppColors.cockpitBlack2)
                        .cornerRadius(12)
                        .overlay(RoundedRectangle(cornerRadius: 12).stroke(AppColors.border, lineWidth: 1, style: StrokeStyle(dash: [5])))
                    }
                }
            }
        }
    }

    var signatureSection: some View {
        VStack(alignment: .leading, spacing: 12) {
            HStack {
                Text("توقيع المستلم")
                    .font(.subheadline)
                    .foregroundColor(.gray)
                Spacer()
                Button("مسح") {
                    canvasView.drawing = PKDrawing()
                }
                .font(.caption)
                .foregroundColor(.red)
            }

            SignatureView(canvasView: $canvasView)
                .frame(height: 200)
                .background(AppColors.cockpitBlack2)
                .cornerRadius(12)
                .overlay(RoundedRectangle(cornerRadius: 12).stroke(AppColors.border, lineWidth: 1))
        }
    }

    var submitButton: some View {
        Button(action: { submitPOD() }) {
            HStack {
                if isSubmitting { ProgressView().tint(.black).padding(.trailing, 8) }
                Text(isSubmitting ? "جاري الإرسال..." : "إرسال إثبات التسليم")
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

    func submitPOD() {
        isSubmitting = true
        // Logic to export signature image and call API
        DispatchQueue.main.asyncAfter(deadline: .now() + 2) {
            isSubmitting = false
            dismiss()
        }
    }
}

// Simple ImagePicker wrapper
struct ImagePicker: UIViewControllerRepresentable {
    @Binding var image: UIImage?
    @Environment(\.dismiss) var dismiss

    func makeUIViewController(context: Context) -> UIImagePickerController {
        let picker = UIImagePickerController()
        picker.delegate = context.coordinator
        return picker
    }

    func updateUIViewController(_ uiViewController: UIImagePickerController, context: Context) {}

    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }

    class Coordinator: NSObject, UINavigationControllerDelegate, UIImagePickerControllerDelegate {
        let parent: ImagePicker
        init(_ parent: ImagePicker) { self.parent = parent }
        func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
            if let uiImage = info[.originalImage] as? UIImage { parent.image = uiImage }
            parent.dismiss()
        }
    }
}
