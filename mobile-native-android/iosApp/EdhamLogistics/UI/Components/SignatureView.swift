import SwiftUI
import PencilKit

struct SignatureView: UIViewRepresentable {
    @Binding var canvasView: PKCanvasView

    func makeUIView(context: Context) -> PKCanvasView {
        canvasView.tool = PKInkingTool(.pen, color: .white, width: 3)
        canvasView.backgroundColor = .clear
        #if targetEnvironment(simulator)
        canvasView.drawingPolicy = .anyInput
        #endif
        return canvasView
    }

    func updateUIView(_ uiView: PKCanvasView, context: Context) {}
}
