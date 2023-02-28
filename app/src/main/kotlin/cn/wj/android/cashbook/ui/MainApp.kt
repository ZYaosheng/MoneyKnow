@file:OptIn(ExperimentalAnimationApi::class)

package cn.wj.android.cashbook.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumedWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cn.wj.android.cashbook.core.design.component.CashbookBackground
import cn.wj.android.cashbook.core.model.enums.LauncherMenuAction
import cn.wj.android.cashbook.feature.record.navigation.LauncherCollapsedTitleContent
import cn.wj.android.cashbook.feature.record.navigation.LauncherContent
import cn.wj.android.cashbook.feature.record.navigation.LauncherPinnedTitleContent
import cn.wj.android.cashbook.feature.record.navigation.editRecordScreen
import cn.wj.android.cashbook.feature.record.navigation.naviToEditRecord
import cn.wj.android.cashbook.feature.settings.navigation.ROUTE_SETTINGS_LAUNCHER
import cn.wj.android.cashbook.feature.settings.navigation.settingsLauncherScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController

/** 开始默认显示路径 */
private const val START_DESTINATION = ROUTE_SETTINGS_LAUNCHER

/** 应用入口 */
@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalLayoutApi::class
)
@Composable
fun MainApp() {
    CashbookBackground {
        val navController = rememberAnimatedNavController()
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
        ) { paddingValues ->
            AnimatedNavHost(
                navController = navController,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .consumedWindowInsets(paddingValues),
                startDestination = START_DESTINATION
            ) {
                settingsLauncherScreen(
                    onMenuClick = { action ->
                        when (action) {
                            LauncherMenuAction.ADD -> {
                                navController.naviToEditRecord()
                            }

                            LauncherMenuAction.SEARCH -> {

                            }

                            LauncherMenuAction.CALENDAR -> {

                            }

                            LauncherMenuAction.MY_ASSET -> {

                            }

                            LauncherMenuAction.MY_BOOK -> {

                            }

                            LauncherMenuAction.MY_CATEGORY -> {

                            }

                            LauncherMenuAction.MY_TAG -> {

                            }

                            LauncherMenuAction.SETTING -> {

                            }

                            LauncherMenuAction.ABOUT_US -> {

                            }

                            else -> {}
                        }
                    },
                    pinnedTitle = { LauncherPinnedTitleContent() },
                    collapsedTitle = { LauncherCollapsedTitleContent() },
                    content = { modifier ->
                        LauncherContent(modifier = modifier)
                    },
                )
                editRecordScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onTypeSettingClick = {
                        // TODO
                    },
                    onAddAssetClick = {
                        // TODO
                    },
                )
            }
        }
    }
}