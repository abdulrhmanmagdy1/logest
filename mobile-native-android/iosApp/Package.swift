// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "EdhamLogistics",
    platforms: [
        .iOS(.v16)
    ],
    products: [
        .library(
            name: "EdhamLogistics",
            targets: ["EdhamLogistics"]),
    ],
    dependencies: [
        .package(url: "https://github.com/Alamofire/Alamofire.git", from: "5.8.1"),
        .package(url: "https://github.com/onevcat/Kingfisher.git", from: "7.10.0")
    ],
    targets: [
        .target(
            name: "EdhamLogistics",
            dependencies: ["Alamofire", "Kingfisher"],
            path: "EdhamLogistics")
    ]
)
