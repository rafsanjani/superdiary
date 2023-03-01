//
//  MainViewModel.swift
//  iosApp
//
//  Created by Rafsanjani Aziz on 27/12/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import shared
import RxSwift
import KMPNativeCoroutinesRxSwift

@MainActor class MainViewModel: ObservableObject {
    private let dataSource: DataSource = LocalDataSource(database: Database(databaseDriver: DarwinDatabaseDriver()))

    private lazy var fetchAllDiariesUseCase = GetAllDiariesUseCase(dataSource: dataSource)
    private lazy var deleteDiariesUseCase = DeleteDiaryUseCase(dataSource: dataSource)
    private lazy var addDiaryUseCase = AddDiaryUseCase(dataSource: dataSource)

    @Published private(set) var isMonitoring: Bool = false
    
    private var disposable: Disposable? = nil {
        didSet { isMonitoring = disposable != nil }
    }
    
    
    @Published var viewState: Result = Result.Loading()

    func loadDiaries() {
        startMonitoring()
    }
    
    func startMonitoring(){
        disposable = createObservable(for: fetchAllDiariesUseCase.diariesNative)
            // Update the UI on the main thread
            .observe(on: MainScheduler.instance)
            .subscribe(onNext: { [weak self] time in
               print("got diary")
            }, onError: { [weak self] _ in
                // Replace any errors with a text message :)
                print("erro getting diary")
            })
    }

    func addRandomDiary() {
        addDiaryUseCase.invoke(diary: Diary(id: nil, entry: "Hello from Hell", date: "Tomorrow's date")) { (outcome, error) in
            print("Successfully added random entry")
        }
        
        addDiaryUseCase.invoke(diary: Diary(id: nil, entry: "Hello from Hell's kitchen", date: "Tomorrow's date")) { (outcome, error) in
            print("Successfully added random entry")
        }
    }
    
    func stopMonitoring(){
        disposable?.dispose()
        disposable = nil
    }

    func clearDiaries() {
        deleteDiariesUseCase.deleteAll { result, error in
            if (error != nil) {
                print("All diaries deleted successfully")
            } else {
               
            }
        }
    }
}
