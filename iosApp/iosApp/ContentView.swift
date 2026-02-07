import ComposeApp
import SwiftUI
import UIKit

private struct ComposeView: UIViewControllerRepresentable {
    let applicationComponent: ApplicationComponent

    func makeUIViewController(context _: Context) -> UIViewController {
        MainViewControllerKt.MainViewController(applicationComponent: applicationComponent)
    }

    func updateUIViewController(_: UIViewController, context _: Context) {}
}

struct ContentView: View {
    let applicationComponent: ApplicationComponent

    var body: some View {
        ComposeView(applicationComponent: applicationComponent).ignoresSafeArea(.all)
    }
}
