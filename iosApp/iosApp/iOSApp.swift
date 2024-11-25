import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    private let component = IosApplicationComponent.companion.create()
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
