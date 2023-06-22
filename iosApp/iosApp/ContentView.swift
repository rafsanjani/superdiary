import SwiftUI
import common

struct ComposeView : UIViewControllerRepresentable{
    func makeUIViewController(context: Context) -> some UIViewController {
        Main_iosKt.MainViewController()
    }
    
    func updateUIViewController(_ uiViewController: UIViewControllerType, context: Context) {}
}

struct ContentView: View {
    @StateObject var viewModel: MainViewModel = MainViewModel()
    
    var body: some View {
        ComposeView().ignoresSafeArea(.all)
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
