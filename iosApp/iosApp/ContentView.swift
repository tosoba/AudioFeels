import UIKit
import SwiftUI
import ComposeApp

private struct ComposeView: UIViewControllerRepresentable {
    let applicationComponent: ApplicationComponent

    init(applicationComponent: ApplicationComponent) {
        self.applicationComponent = applicationComponent
    }

    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController(applicationComponent: applicationComponent)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    let applicationComponent: ApplicationComponent

    init(applicationComponent: ApplicationComponent) {
        self.applicationComponent = applicationComponent
    }

    var body: some View {
        ComposeView(applicationComponent: applicationComponent).ignoresSafeArea(.all)
    }
}
