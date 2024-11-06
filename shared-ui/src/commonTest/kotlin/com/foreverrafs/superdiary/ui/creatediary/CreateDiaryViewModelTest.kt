package com.foreverrafs.superdiary.ui.creatediary

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.foreverrafs.superdiary.TestAppDispatchers
import com.foreverrafs.superdiary.core.location.Location
import com.foreverrafs.superdiary.core.location.LocationManager
import com.foreverrafs.superdiary.core.location.permission.LocationPermissionManager
import com.foreverrafs.superdiary.core.location.permission.PermissionState
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.data.datasource.DataSource
import com.foreverrafs.superdiary.data.diaryai.DiaryAI
import com.foreverrafs.superdiary.data.model.Diary
import com.foreverrafs.superdiary.data.usecase.AddDiaryUseCase
import com.foreverrafs.superdiary.data.utils.DiaryPreference
import com.foreverrafs.superdiary.data.utils.DiarySettings
import com.foreverrafs.superdiary.ui.creatediary.FakePermissionsControllerWrapper.ActionPerformed.ProvidePermission
import com.foreverrafs.superdiary.ui.feature.creatediary.screen.CreateDiaryViewModel
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify
import dev.mokkery.verifySuspend
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.yield

@OptIn(ExperimentalCoroutinesApi::class)
class CreateDiaryViewModelTest {

    private val diaryAI: DiaryAI = mock()

    private val dataSource: DataSource = mock()

    private val locationManager: LocationManager = object : LocationManager {
        override fun requestLocation(onError: (Exception) -> Unit, onLocation: (Location) -> Unit) {
            onLocation(
                Location(
                    latitude = 1.0,
                    longitude = 1.0,
                ),
            )
        }

        override fun stopRequestingLocation() {
        }
    }

    private val preference: DiaryPreference = mock()

    private lateinit var createDiaryViewModel: CreateDiaryViewModel

    private val permissionsController: FakePermissionsControllerWrapper =
        FakePermissionsControllerWrapper()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())

        every { preference.snapshot }.returns(DiarySettings.Empty)
        every { preference.settings }.returns(emptyFlow())
        everySuspend { preference.getSnapshot() }.returns(DiarySettings.Empty)

        createDiaryViewModel = CreateDiaryViewModel(
            addDiaryUseCase = AddDiaryUseCase(
                dataSource = dataSource,
                dispatchers = TestAppDispatchers,
                validator = {},
            ),
            diaryAI = diaryAI,
            logger = AggregateLogger(loggers = emptyList()),
            locationManager = locationManager,
            locationPermissionManager = LocationPermissionManager(
                permissionsController = permissionsController,
                logger = AggregateLogger(
                    emptyList(),
                ),
                dispatchers = TestAppDispatchers,
            ),
            preference = preference,
        )
    }

    @AfterTest
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Should grab users location when they open create screen`() = runTest {
        permissionsController.permissionStateResult = PermissionState.Granted

        createDiaryViewModel.screenState.test {
            skipItems(1)
            val state = awaitItem()
            assertThat(state.location).isNotNull()
        }
    }

    @Test
    fun `Should save diary when user clicks on save button`() = runTest {
        val diary = Diary("Hello World!!")

        permissionsController.permissionStateResult = PermissionState.Granted

        everySuspend { dataSource.add(diary) }.returns(100L)

        createDiaryViewModel.saveDiary(diary)
        delay(100)

        verifySuspend { dataSource.add(diary) }
    }

    @Test
    fun `Should generate AI diary when generate AI button is clicked`() = runTest {
        every { diaryAI.generateDiary("hello", 100) }.returns(emptyFlow())
        permissionsController.permissionStateResult = PermissionState.Granted

        createDiaryViewModel.generateAIDiary("hello", 100)

        verify { diaryAI.generateDiary(any(), any()) }
    }

    @Test
    fun `Requesting location permission should initiate the permission request`() = runTest {
        permissionsController.permissionStateResult = PermissionState.NotDetermined

        createDiaryViewModel.onRequestLocationPermission()

        yield()

        assertThat(
            permissionsController.actionPerformed,
        )
            .isNotNull()
            .isInstanceOf(ProvidePermission::class)
    }

    @Test
    fun `Should save user selection when location dialog is permanently dismissed`() = runTest {
        everySuspend { preference.save(any()) }.returns(Unit)

        permissionsController.permissionStateResult = PermissionState.Denied

        createDiaryViewModel.onPermanentlyDismissLocationPermissionDialog()

        yield()
        verifySuspend { preference.save(any()) }
    }

    @Test
    fun `Should fetch location when user has granted permission`() = runTest {
        permissionsController.permissionStateResult = PermissionState.Granted

        createDiaryViewModel.screenState.test {
            skipItems(1)
            val state = awaitItem()

            assertThat(state.location).isNotNull()
        }
    }

    @Test
    fun `Should NOT fetch location when location permission is DENIED`() = runTest {
        permissionsController.permissionStateResult = PermissionState.Denied

        createDiaryViewModel.screenState.test {
            val state = awaitItem()
            expectNoEvents()

            assertThat(state.location).isNull()
        }
    }
}
