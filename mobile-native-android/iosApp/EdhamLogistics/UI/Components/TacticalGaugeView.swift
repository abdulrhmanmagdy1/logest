import SwiftUI

struct TacticalGaugeView: View {
    var value: Double
    var minValue: Double = -30
    var maxValue: Double = 30
    var title: String
    var unit: String = "°C"

    var body: some View {
        VStack(spacing: 8) {
            Text(title)
                .font(.system(size: 10, weight: .bold))
                .foregroundColor(.gray)
                .kerning(1)

            ZStack {
                // Background Track
                Circle()
                    .trim(from: 0.1, to: 0.9)
                    .stroke(Color.gray.opacity(0.1), style: StrokeStyle(lineWidth: 10, lineCap: .round))
                    .rotationEffect(.degrees(90))

                // Value Track (Glow effect)
                Circle()
                    .trim(from: 0.1, to: normalizedValue())
                    .stroke(
                        AngularGradient(
                            gradient: Gradient(colors: [AppColors.cockpitGreen.opacity(0.5), AppColors.cockpitGreen]),
                            center: .center,
                            startAngle: .degrees(0),
                            endAngle: .degrees(360)
                        ),
                        style: StrokeStyle(lineWidth: 10, lineCap: .round)
                    )
                    .rotationEffect(.degrees(90))
                    .shadow(color: AppColors.cockpitGreen.opacity(0.3), radius: 5)

                VStack(spacing: -2) {
                    Text("\(String(format: "%.1f", value))")
                        .font(.system(size: 24, weight: .black, design: .monospaced))
                        .foregroundColor(.white)
                    Text(unit)
                        .font(.caption2)
                        .foregroundColor(AppColors.cockpitGreen)
                        .bold()
                }
            }
            .frame(width: 110, height: 110)
        }
        .padding()
        .background(AppColors.cockpitBlack2)
        .cornerRadius(20)
        .overlay(RoundedRectangle(cornerRadius: 20).stroke(AppColors.border, lineWidth: 1))
    }

    private func normalizedValue() -> Double {
        let range = maxValue - minValue
        let progress = (value - minValue) / range
        // Map 0...1 to 0.1...0.9
        return 0.1 + (progress * 0.8)
    }
}
