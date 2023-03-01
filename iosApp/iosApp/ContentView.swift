import SwiftUI
import shared

struct ContentView: View {
    @StateObject var viewModel: MainViewModel = MainViewModel()
    
    var body: some View {
        VStack(alignment: .leading){
            if(viewModel.viewState is Result.Loading){
                Text("Loading Diaries")
                    .bold()
                    .font(.headline)
            }
            
    
            if(viewModel.viewState is Result.Success){
                let data = viewModel.viewState as! Result.Success
                
                List(data.data, id: \.id){diary in
                    Text(diary.entry)
                }
                .environmentObject(viewModel)
                    .listStyle(.grouped)
                    .padding(.all, 15)
                    .frame(maxWidth: .infinity)
            }
            
            if(viewModel.viewState is Result.Failure){
                Text("Error Loading Diaries!")
                    .bold()
                    .font(.headline)
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .edgesIgnoringSafeArea(.all)
        .onAppear{
            viewModel.loadDiaries()
            viewModel.clearDiaries()
            viewModel.addRandomDiary()
        }
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
