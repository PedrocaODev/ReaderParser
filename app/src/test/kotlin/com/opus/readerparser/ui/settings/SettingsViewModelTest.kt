package com.opus.readerparser.ui.settings

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.opus.readerparser.domain.model.AppSettings
import com.opus.readerparser.domain.model.AppTheme
import com.opus.readerparser.domain.model.ManhwaLayout
import com.opus.readerparser.domain.model.ManhwaZoom
import com.opus.readerparser.fakes.FakeSettingsRepository
import com.opus.readerparser.testutil.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repo: FakeSettingsRepository
    private lateinit var vm: SettingsViewModel

    @Before
    fun setUp() {
        repo = FakeSettingsRepository()
        vm = SettingsViewModel(repo)
    }

    @Test
    fun `init emits default settings`() = runTest {
        vm.state.test {
            assertThat(awaitItem().settings).isEqualTo(AppSettings())
        }
    }

    @Test
    fun `SetTheme updates state`() = runTest {
        vm.state.test {
            awaitItem()
            vm.onAction(SettingsAction.SetTheme(AppTheme.DARK))
            assertThat(awaitItem().settings.theme).isEqualTo(AppTheme.DARK)
        }
    }

    @Test
    fun `SetTheme WARM_DARK updates state`() = runTest {
        vm.state.test {
            awaitItem()
            vm.onAction(SettingsAction.SetTheme(AppTheme.WARM_DARK))
            assertThat(awaitItem().settings.theme).isEqualTo(AppTheme.WARM_DARK)
        }
    }

    @Test
    fun `SetTheme SEPIA updates state`() = runTest {
        vm.state.test {
            awaitItem()
            vm.onAction(SettingsAction.SetTheme(AppTheme.SEPIA))
            assertThat(awaitItem().settings.theme).isEqualTo(AppTheme.SEPIA)
        }
    }

    @Test
    fun `SetNovelFontSize updates state`() = runTest {
        vm.state.test {
            awaitItem()
            vm.onAction(SettingsAction.SetNovelFontSize(20))
            assertThat(awaitItem().settings.novelFontSize).isEqualTo(20)
        }
    }

    @Test
    fun `SetNovelFontFamily updates state`() = runTest {
        vm.state.test {
            awaitItem()
            vm.onAction(SettingsAction.SetNovelFontFamily("Monospace"))
            assertThat(awaitItem().settings.novelFontFamily).isEqualTo("Monospace")
        }
    }

    @Test
    fun `SetNovelLineHeightMultiplier updates state`() = runTest {
        vm.state.test {
            awaitItem()
            vm.onAction(SettingsAction.SetNovelLineHeightMultiplier(1.8f))
            assertThat(awaitItem().settings.novelLineHeightMultiplier).isEqualTo(1.8f)
        }
    }

    @Test
    fun `SetNovelParagraphSpacingDp updates state`() = runTest {
        vm.state.test {
            awaitItem()
            vm.onAction(SettingsAction.SetNovelParagraphSpacingDp(20))
            assertThat(awaitItem().settings.novelParagraphSpacingDp).isEqualTo(20)
        }
    }

    @Test
    fun `SetNovelHorizontalPaddingDp updates state`() = runTest {
        vm.state.test {
            awaitItem()
            vm.onAction(SettingsAction.SetNovelHorizontalPaddingDp(28))
            assertThat(awaitItem().settings.novelHorizontalPaddingDp).isEqualTo(28)
        }
    }

    @Test
    fun `SetManhwaLayout updates state`() = runTest {
        vm.state.test {
            awaitItem()
            vm.onAction(SettingsAction.SetManhwaLayout(ManhwaLayout.PAGED_LTR))
            assertThat(awaitItem().settings.manhwaLayout).isEqualTo(ManhwaLayout.PAGED_LTR)
        }
    }

    @Test
    fun `SetManhwaZoom updates state`() = runTest {
        vm.state.test {
            awaitItem()
            vm.onAction(SettingsAction.SetManhwaZoom(ManhwaZoom.FIT_HEIGHT))
            assertThat(awaitItem().settings.manhwaZoom).isEqualTo(ManhwaZoom.FIT_HEIGHT)
        }
    }
}
