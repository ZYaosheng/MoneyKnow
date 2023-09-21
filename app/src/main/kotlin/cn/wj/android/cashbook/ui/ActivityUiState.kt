package cn.wj.android.cashbook.ui

import androidx.activity.OnBackPressedDispatcher
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import cn.wj.android.cashbook.core.design.component.LocalDefaultEmptyImagePainter
import cn.wj.android.cashbook.core.design.component.LocalDefaultLoadingHint
import cn.wj.android.cashbook.core.model.enums.DarkModeEnum
import cn.wj.android.cashbook.core.ui.LocalBackPressedDispatcher
import cn.wj.android.cashbook.core.ui.R

sealed interface ActivityUiState {
    data object Loading : ActivityUiState

    data class Success(
        val darkMode: DarkModeEnum,
        val dynamicColor: Boolean,
    ) : ActivityUiState
}

@Composable
internal fun ProvideLocalState(
    onBackPressedDispatcher: OnBackPressedDispatcher,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalBackPressedDispatcher provides onBackPressedDispatcher,
        LocalDefaultEmptyImagePainter provides painterResource(id = R.drawable.vector_no_data_200),
        LocalDefaultLoadingHint provides stringResource(id = R.string.data_in_loading),
        content = content
    )
}

@Composable
internal fun shouldDisableDynamicTheming(
    uiState: ActivityUiState,
): Boolean = when (uiState) {
    ActivityUiState.Loading -> false
    is ActivityUiState.Success -> !uiState.dynamicColor
}

@Composable
internal fun shouldUseDarkTheme(
    uiState: ActivityUiState,
): Boolean = when (uiState) {
    ActivityUiState.Loading -> isSystemInDarkTheme()
    is ActivityUiState.Success -> when (uiState.darkMode) {
        DarkModeEnum.FOLLOW_SYSTEM -> isSystemInDarkTheme()
        DarkModeEnum.LIGHT -> false
        DarkModeEnum.DARK -> true
    }
}