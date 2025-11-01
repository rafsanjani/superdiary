package com.foreverrafs.superdiary.creatediary

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isInstanceOf
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import com.foreverrafs.preferences.DiaryPreference
import com.foreverrafs.superdiary.ai.api.DiaryAI
import com.foreverrafs.superdiary.common.coroutines.TestAppDispatchers
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.core.permission.LocationManager
import com.foreverrafs.superdiary.core.permission.LocationPermissionManager
import com.foreverrafs.superdiary.core.permission.PermissionState
import com.foreverrafs.superdiary.creatediary.FakePermissionsControllerWrapper.ActionPerformed
import com.foreverrafs.superdiary.creatediary.screen.CreateDiaryViewModel
import com.foreverrafs.superdiary.domain.Synchronizer
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.repository.DataSource
import com.foreverrafs.superdiary.utils.DiarySettings
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
class CreateDiaryViewModelTest {

    private val diaryAI: DiaryAI = mock()

    private val dataSource: DataSource = mock()

    private val locationManager: LocationManager = object : LocationManager {
        override fun requestLocation(
            onError: (Exception) -> Unit,
            onLocation: (Double, Double) -> Unit,
        ) {
            onLocation(
                1.0,
                1.0,
            )
        }

        override fun stopRequestingLocation() {
        }
    }

    private val preference: DiaryPreference = mock()

    private val synchronizer: Synchronizer = mock {
        everySuspend { sync(any()) } returns true
    }

    private lateinit var createDiaryViewModel: CreateDiaryViewModel

    private val permissionsController: FakePermissionsControllerWrapper =
        FakePermissionsControllerWrapper()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(TestAppDispatchers.main)

        every { preference.settings }.returns(emptyFlow())
        everySuspend { preference.getSnapshot() }.returns(DiarySettings.Empty)

        createDiaryViewModel = CreateDiaryViewModel(
            addDiaryUseCase = AddDiaryUseCase(
                dispatchers = TestAppDispatchers,
                validator = {}, // lenient validator
                dataSource = dataSource,
            ),
            diaryAI = diaryAI,
            logger = AggregateLogger(loggers = emptyList()),
            locationManager = locationManager,
            locationPermissionManager = LocationPermissionManager(
                permissionsController = permissionsController,
                logger = AggregateLogger(
                    emptyList(),
                ),
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

        everySuspend { dataSource.save(diary) }.returns(100L)

        createDiaryViewModel.saveDiary(diary)
        delay(100)

        verifySuspend { dataSource.save(diary) }
    }

    @Test
    @Ignore
    fun `Should generate AI diary when generate AI button is clicked`() = runTest {
        every { diaryAI.generateDiary("hello", 100) }.returns(emptyFlow())
        permissionsController.permissionStateResult = PermissionState.Granted

        createDiaryViewModel.generateAIDiary("hello", 100)
            .test {
                val item = awaitItem()
                assertThat(item).isNotNull()
                cancelAndConsumeRemainingEvents()
            }
    }

    @Test
    fun `Requesting location permission should initiate the permission request`() = runTest {
        permissionsController.permissionStateResult = PermissionState.NotDetermined

        createDiaryViewModel.onRequestLocationPermission()

        advanceUntilIdle()

        assertThat(
            permissionsController.actionPerformed,
        )
            .isNotNull()
            .isInstanceOf(ActionPerformed.ProvidePermission::class)
    }

    @Test
    fun `Should save user selection when location dialog is permanently dismissed`() = runTest {
        everySuspend { preference.save(any()) }.returns(Unit)

        permissionsController.permissionStateResult = PermissionState.Denied

        createDiaryViewModel.onPermanentlyDismissLocationPermissionDialog()

        advanceUntilIdle()
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

    @Test
    @Ignore
    fun `Should perform data sync after successfully saving an entry`() = runTest {
        val diary = Diary(id = 12L, entry = "test diary")
        everySuspend { dataSource.save(diary) } returns diary.id!!

        createDiaryViewModel.saveDiary(diary)
        advanceUntilIdle()

        verifySuspend(
            mode = VerifyMode.exactly(1),
        ) {
            synchronizer.sync(
                operation = Synchronizer.SyncOperation.Save(diary),
            )
        }
    }

    @Test
    fun `Should NOT perform data sync WHEN entry is not saved`() = runTest {
        val diary = Diary(id = 12L, entry = "test diary")
        everySuspend { dataSource.save(diary) } returns 0

        createDiaryViewModel.saveDiary(diary)
        advanceUntilIdle()

        verifySuspend(mode = VerifyMode.not) {
            synchronizer.sync(
                operation = Synchronizer.SyncOperation.Save(diary),
            )
        }
    }
}
