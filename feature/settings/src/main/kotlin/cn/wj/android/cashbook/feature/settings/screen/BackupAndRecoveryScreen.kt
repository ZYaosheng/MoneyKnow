package cn.wj.android.cashbook.feature.settings.screen

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wj.android.cashbook.core.data.uitl.BackupRecoveryState.Companion.FAILED_BACKUP_PATH_UNAUTHORIZED
import cn.wj.android.cashbook.core.data.uitl.BackupRecoveryState.Companion.FAILED_BACKUP_WEBDAV
import cn.wj.android.cashbook.core.data.uitl.BackupRecoveryState.Companion.FAILED_BLANK_BACKUP_PATH
import cn.wj.android.cashbook.core.data.uitl.BackupRecoveryState.Companion.SUCCESS_BACKUP
import cn.wj.android.cashbook.core.design.component.CashbookScaffold
import cn.wj.android.cashbook.core.design.component.CashbookTopAppBar
import cn.wj.android.cashbook.core.design.component.CompatPasswordTextField
import cn.wj.android.cashbook.core.design.component.CompatTextField
import cn.wj.android.cashbook.core.design.component.TextFieldState
import cn.wj.android.cashbook.core.design.component.TransparentListItem
import cn.wj.android.cashbook.core.design.icon.CashbookIcons
import cn.wj.android.cashbook.core.design.theme.PreviewTheme
import cn.wj.android.cashbook.core.model.enums.AutoBackupModeEnum
import cn.wj.android.cashbook.core.ui.DevicePreviews
import cn.wj.android.cashbook.core.ui.R
import cn.wj.android.cashbook.feature.settings.viewmodel.BackupAndRecoveryUiState
import cn.wj.android.cashbook.feature.settings.viewmodel.BackupAndRecoveryViewModel

@Composable
internal fun BackupAndRecoveryRoute(
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> SnackbarResult,
    modifier: Modifier = Modifier,
    viewModel: BackupAndRecoveryViewModel = hiltViewModel(),
) {

    val shouldDisplayBookmark by viewModel.shouldDisplayBookmark.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isConnected by viewModel.isConnected.collectAsStateWithLifecycle()

    BackupAndRecoveryScreen(
        shouldDisplayBookmark,
        dismissBookmark = viewModel::dismissBookmark,
        uiState = uiState,
        isConnected = isConnected,
        onSaveWebDAV = viewModel::saveWebDAV,
        onSaveBackupPath = viewModel::saveBackupPath,
        onBackupClick = viewModel::backup,
        onRecoveryClick = viewModel::recovery,
        onAutoBackupClick = viewModel::showSelectAutoBackupDialog,
        onBackClick = onBackClick,
        onShowSnackbar = onShowSnackbar,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BackupAndRecoveryScreen(
    shouldDisplayBookmark: Int,
    dismissBookmark: () -> Unit,
    uiState: BackupAndRecoveryUiState,
    isConnected: Boolean,
    onSaveWebDAV: (String, String, String) -> Unit,
    onSaveBackupPath: (String) -> Unit,
    onBackupClick: () -> Unit,
    onRecoveryClick: (Boolean) -> Unit,
    onAutoBackupClick: () -> Unit,
    onBackClick: () -> Unit,
    onShowSnackbar: suspend (String, String?) -> SnackbarResult,
    modifier: Modifier = Modifier,
) {

    LaunchedEffect(shouldDisplayBookmark) {
        if (shouldDisplayBookmark != 0) {
            // FIXME
            val tipText = when (shouldDisplayBookmark) {
                FAILED_BLANK_BACKUP_PATH -> "请先选择备份路径"
                FAILED_BACKUP_PATH_UNAUTHORIZED -> "未授权路径"
                FAILED_BACKUP_WEBDAV -> "仅本地备份成功"
                SUCCESS_BACKUP -> "备份成功"
                else -> ""
            }
            val snackbarResult = onShowSnackbar(tipText, null)
            if (snackbarResult == SnackbarResult.Dismissed) {
                dismissBookmark()
            }
        }
    }

    CashbookScaffold(
        modifier = modifier,
        topBar = {
            CashbookTopAppBar(
                onBackClick = onBackClick,
                text = stringResource(id = R.string.backup_and_recovery),
            )
        },
        content = { paddingValues ->
            BackupAndRecoveryScaffoldContent(
                uiState = uiState,
                isConnected = isConnected,
                onSaveWebDAV = onSaveWebDAV,
                onSaveBackupPath = onSaveBackupPath,
                onBackupClick = onBackupClick,
                onRecoveryClick = onRecoveryClick,
                onAutoBackupClick = onAutoBackupClick,
                modifier = Modifier.padding(paddingValues),
            )
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
internal fun BackupAndRecoveryScaffoldContent(
    uiState: BackupAndRecoveryUiState,
    isConnected: Boolean,
    onSaveWebDAV: (String, String, String) -> Unit,
    onSaveBackupPath: (String) -> Unit,
    onBackupClick: () -> Unit,
    onRecoveryClick: (Boolean) -> Unit,
    onAutoBackupClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // 提示文本
    val textBlankErrorText = stringResource(id = R.string.text_must_not_be_blank)
    val webDAVDomainTextFieldState = remember(uiState.webDAVDomain) {
        TextFieldState(
            defaultText = uiState.webDAVDomain,
            validator = { it.isNotBlank() },
            errorFor = { textBlankErrorText },
        )
    }
    val webDAVAccountTextFieldState = remember(uiState.webDAVAccount) {
        TextFieldState(
            defaultText = uiState.webDAVAccount,
            validator = { it.isNotBlank() },
            errorFor = { textBlankErrorText },
        )
    }
    val webDAVPasswordTextFieldState = remember(uiState.webDAVPassword) {
        TextFieldState(
            defaultText = uiState.webDAVPassword,
            validator = { it.isNotBlank() },
            errorFor = { textBlankErrorText },
        )
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState())
            .padding(vertical = 16.dp),
    ) {
        Text(
            text = stringResource(id = R.string.webdav_setting),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 16.dp)
        )

        CompatTextField(
            textFieldState = webDAVDomainTextFieldState,
            label = { Text(text = stringResource(id = R.string.webdav_domain)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .padding(horizontal = 16.dp),
        )
        CompatTextField(
            textFieldState = webDAVAccountTextFieldState,
            label = { Text(text = stringResource(id = R.string.webdav_account)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .padding(horizontal = 16.dp),
        )
        CompatPasswordTextField(
            textFieldState = webDAVPasswordTextFieldState,
            label = { Text(text = stringResource(id = R.string.webdav_password)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .padding(horizontal = 16.dp),
        )

        FilledTonalButton(
            onClick = {
                if (webDAVDomainTextFieldState.isValid && webDAVAccountTextFieldState.isValid && webDAVPasswordTextFieldState.isValid) {
                    // 参数可用
                    onSaveWebDAV(
                        webDAVDomainTextFieldState.text,
                        webDAVAccountTextFieldState.text,
                        webDAVPasswordTextFieldState.text
                    )
                }
            },
            content = {
                Icon(
                    imageVector = if (isConnected) CashbookIcons.CheckCircle else CashbookIcons.Error,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp),
                )
                Text(text = if (isConnected) "已连接" else "未连接")
            },
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 8.dp, end = 16.dp),
        )

        Text(
            text = stringResource(id = R.string.backup_and_recovery),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 32.dp, start = 16.dp),
        )

        val context = LocalContext.current
        val selectDirLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocumentTree(),
            onResult = {
                it?.let { uri ->
                    val path = if (uri.scheme == "content") {
                        // 获取持久化授权
                        context.contentResolver.takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        )
                        uri.toString()
                    } else {
                        uri.path
                    }.orEmpty()
                    onSaveBackupPath(path)
                }
            }
        )

        fun ifPathNotBlank(block: () -> Unit) {
            if (uiState.backupPath.isBlank()) {
                // 未设置备份路径
                selectDirLauncher.launch(null)
            } else {
                block()
            }
        }

        TransparentListItem(
            headlineText = { Text(text = stringResource(id = R.string.backup_path)) },
            supportingText = { Text(text = uiState.backupPath.ifBlank { stringResource(id = R.string.click_to_select_backup_path) }) },
            modifier = Modifier.clickable {
                selectDirLauncher.launch(null)
            },
        )
        TransparentListItem(
            headlineText = { Text(text = stringResource(id = R.string.backup)) },
            supportingText = { Text(text = stringResource(id = R.string.backup_hint)) },
            modifier = Modifier.clickable {
                ifPathNotBlank(onBackupClick)
            },
        )
        TransparentListItem(
            headlineText = { Text(text = stringResource(id = R.string.recovery)) },
            supportingText = { Text(text = stringResource(id = R.string.recovery_hint)) },
            modifier = Modifier.combinedClickable(onClick = {
                ifPathNotBlank {
                    onRecoveryClick(false)
                }
            }, onLongClick = {
                ifPathNotBlank {
                    onRecoveryClick(true)
                }
            }),
        )
        TransparentListItem(
            headlineText = { Text(text = stringResource(id = R.string.auto_backup)) },
            supportingText = {
                Text(
                    text = stringResource(
                        id = when (uiState.autoBackup) {
                            AutoBackupModeEnum.CLOSE -> R.string.close
                            AutoBackupModeEnum.WHEN_LAUNCH -> R.string.each_launch
                            AutoBackupModeEnum.EACH_DAY -> R.string.each_day
                            AutoBackupModeEnum.EACH_WEEK -> R.string.each_week
                        }
                    )
                )
            },
            modifier = Modifier.clickable(onClick = onAutoBackupClick),
        )
    }
}

@DevicePreviews
@Composable
private fun BackupAndRecoveryScreenPreview() {
    PreviewTheme {
        BackupAndRecoveryScreen(
            shouldDisplayBookmark = 0,
            dismissBookmark = {},
            uiState = BackupAndRecoveryUiState(),
            isConnected = false,
            onSaveWebDAV = { _, _, _ -> },
            onSaveBackupPath = {},
            onBackupClick = {},
            onRecoveryClick = {},
            onAutoBackupClick = {},
            onBackClick = {},
            onShowSnackbar = { _, _ -> SnackbarResult.Dismissed },
        )
    }
}