import SwiftUI
import GoogleSignIn
import shared

struct ComposeView: UIViewControllerRepresentable {
    
    func makeUIViewController(context: Context) -> some UIViewController {
        
        let viewController = ViewController { latitude, longitude in
            
            UIHostingController(
                rootView: GoogleMap(latitude: latitude.doubleValue, longitude: longitude.doubleValue)
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
