import SwiftUI
import common

struct ComposeView : UIViewControllerRepresentable{
    func makeUIViewController(context: Context) -> some UIViewController {
        Main_iosKt.MainViewController()
    }
    
    func updateUIViewController(_ uiViewController: UIViewControllerType, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea(.all)
    }
}

#Preview {
    Text("Placeholder preview because moko-resources doesn't yet support SwiftUI previews")
}

