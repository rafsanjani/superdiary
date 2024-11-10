import SwiftUI
import GoogleSignIn
import shared
import GoogleSignInSwift


struct ComposeView: UIViewControllerRepresentable {
    
    func makeUIViewController(context: Context) -> some UIViewController {
        
        let viewController = ViewController { location in
            
            UIHostingController(
                rootView: GoogleMap(location: location)
            )
        }
        
        return viewController.mainViewController()
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
