import SwiftUI
import shared

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> some UIViewController {
        ViewController().mainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewControllerType, context: Context) {
    }
}

struct ContentView: View {

	var body: some View {
		ComposeView()
            .ignoresSafeArea(.all)
	}
}

#Preview {
    ContentView()
}
