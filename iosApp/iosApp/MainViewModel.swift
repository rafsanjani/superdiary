//
//  MainViewModel.swift
//  iosApp
//
//  Created by Rafsanjani Aziz on 27/12/2022.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import shared

@MainActor class MainViewModel: ObservableObject {
    private let dataSource: DataSource = LocalDataSource(database: Database(databaseDriver: DarwinDatabaseDriver()))

    private lazy var fetchAllDiariesUseCase = GetAllDiariesUseCase(dataSource: dataSource)
    private lazy var deleteDiariesUseCase = DeleteAllDiariesUseCase(dataSource: dataSource)
    private lazy var addDiaryUseCase = AddDiaryUseCase(dataSource: dataSource)

    @Published var viewState: Result = Result.Loading()

    func loadDiaries() {
        print("Loading diaries")
        Task.init {
            do {
                let data = try await fetchAllDiariesUseCase.invoke()
                viewState = data
            } catch {
                print("Error loading diaries")
            }
        }

    }

    func addRandomDiary() {
        addDiaryUseCase.invoke(diary: Diary(id: nil, entry: "Hello from Hell", date: "Tomorrow's date")) { (outcome, error) in
            print("Successfully added random entry")
        }
    }

    func clearDiaries() {
        deleteDiariesUseCase.invoke { result, error in
            if (error != nil) {
                print("All diaries deleted successfully")
            } else {
                print("Error deleting diary \(error)")
            }
        }
    }
}
