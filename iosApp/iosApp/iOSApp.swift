import ComposeApp
import SwiftUI

@main
struct iOSApp: App {
    private let applicationComponent = IosApplicationComponent.companion.create()

    var body: some Scene {
        WindowGroup {
            ContentView(applicationComponent: applicationComponent)
        }
    }
}
