package cn.wj.android.cashbook.feature.records.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.BackdropScaffoldState
import androidx.compose.material.BackdropValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WebAsset
import androidx.compose.material.rememberBackdropScaffoldState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BackdropScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheetLayout
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wj.android.cashbook.core.common.Symbol
import cn.wj.android.cashbook.core.common.ext.decimalFormat
import cn.wj.android.cashbook.core.common.ext.string
import cn.wj.android.cashbook.core.common.ext.toBigDecimalOrZero
import cn.wj.android.cashbook.core.common.ext.toDoubleOrZero
import cn.wj.android.cashbook.core.design.component.CommonDivider
import cn.wj.android.cashbook.core.design.component.Empty
import cn.wj.android.cashbook.core.design.component.TopAppBar
import cn.wj.android.cashbook.core.design.component.TopAppBarDefaults
import cn.wj.android.cashbook.core.design.theme.LocalExtendedColors
import cn.wj.android.cashbook.core.model.entity.RecordViewsEntity
import cn.wj.android.cashbook.core.model.enums.LauncherMenuAction
import cn.wj.android.cashbook.core.model.enums.RecordTypeCategoryEnum
import cn.wj.android.cashbook.core.model.model.ResultModel
import cn.wj.android.cashbook.core.ui.BackPressHandler
import cn.wj.android.cashbook.feature.records.R
import cn.wj.android.cashbook.feature.records.model.RecordDialogState
import cn.wj.android.cashbook.feature.records.viewmodel.LauncherContentViewModel
import java.math.BigDecimal
import java.util.Calendar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/** 首页内容部分 */
@OptIn(
    ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
internal fun LauncherContentScreen(
    onRecordItemEditClick: (Long) -> Unit,
    onMenuClick: (LauncherMenuAction) -> Unit,
    sheetState: ModalBottomSheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden),
    scaffoldState: BackdropScaffoldState = rememberBackdropScaffoldState(BackdropValue.Revealed),
    viewModel: LauncherContentViewModel = hiltViewModel(),
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
) {

    if (sheetState.isVisible) {
        BackPressHandler {
            coroutineScope.launch {
                sheetState.hide()
            }
        }
    }

    // 账本名称
    val booksName by viewModel.bookName.collectAsStateWithLifecycle()
    // 月收入
    val monthIncome by viewModel.monthIncome.collectAsStateWithLifecycle()
    // 月支出
    val monthExpand by viewModel.monthExpand.collectAsStateWithLifecycle()
    // 月结余
    val monthBalance by viewModel.monthBalance.collectAsStateWithLifecycle()
    // 记录数据
    val recordMap by viewModel.currentMonthRecordListMapData.collectAsStateWithLifecycle()

    /** 弹窗状态 */
    val dialogState: RecordDialogState by viewModel.dialogState.collectAsStateWithLifecycle()

    val selectedRecord by viewModel.selectedRecordData.collectAsStateWithLifecycle()

    val todayInt = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            if (null == selectedRecord) {
                Spacer(modifier = Modifier.height(1.dp))
            } else {
                RecordDetailsSheet(
                    recordEntity = selectedRecord!!,
                    onRecordItemEditClick = {
                        coroutineScope.launch {
                            sheetState.hide()
                        }
                        onRecordItemEditClick(it)
                    },
                    onRecordItemDeleteClick = viewModel::onRecordDeleteClick,
                )
            }
        },
    ) {
        Scaffold(
            topBar = {
                LauncherTopBar(
                    booksName = booksName,
                    backdropScaffoldState = scaffoldState,
                    onMenuClick = onMenuClick,
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                FloatingActionButton(onClick = { onMenuClick(LauncherMenuAction.ADD) }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                }
            },
        ) { paddingValues ->
            BackdropScaffold(
                scaffoldState = scaffoldState,
                appBar = { /* 使用上层 topBar 处理 */ },
                peekHeight = paddingValues.calculateTopPadding(),
                frontLayerScrimColor = Color.Unspecified,
                backLayerContent = {
                    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onPrimary) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(paddingValues)
                                .padding(16.dp),
                        ) {
                            Column(
                                modifier = Modifier.weight(1f),
                            ) {
                                Text(
                                    text = "${stringResource(id = R.string.current_month_income)} ${Symbol.rmb}$monthIncome",
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Text(
                                    text = "${stringResource(id = R.string.current_month_balance)} ${Symbol.rmb}$monthBalance",
                                )
                            }
                            Text(
                                modifier = Modifier.weight(1f),
                                text = "${stringResource(id = R.string.current_month_expend)} ${Symbol.rmb}$monthExpand",
                            )
                        }
                    }
                },
                frontLayerContent = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        (dialogState as? RecordDialogState.Show)?.let { state ->
                            // 显示删除确认弹窗
                            coroutineScope.launch {
                                sheetState.hide()
                            }
                            AlertDialog(
                                onDismissRequest = viewModel::onDismiss,
                                text = {
                                    Text(text = stringResource(id = R.string.record_delete_hint))
                                },
                                confirmButton = {
                                    TextButton(onClick = {
                                        coroutineScope.launch {
                                            val result = viewModel.tryDeleteRecord(state.recordId)
                                            if (result is ResultModel.Failure<*>) {
                                                // 删除失败
                                                snackbarHostState.showSnackbar(
                                                    R.string.delete_failed_format.string.format(
                                                        result.code
                                                    )
                                                )
                                            } else {
                                                // 删除成功
                                                viewModel.onDismiss()
                                            }
                                        }
                                    }) {
                                        Text(text = stringResource(id = R.string.confirm))
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = viewModel::onDismiss) {
                                        Text(text = stringResource(id = R.string.cancel))
                                    }
                                },
                            )
                        }

                        if (recordMap.isEmpty()) {
                            Empty(
                                imageResId = R.drawable.vector_no_data_200,
                                hintResId = R.string.launcher_no_data_hint,
                                buttonResId = R.string.launcher_no_data_button,
                                onButtonClick = { onMenuClick(LauncherMenuAction.CALENDAR) },
                            )
                            return@Box
                        }
                        LazyColumn {
                            recordMap.keys.reversed().forEach { key ->
                                val recordList = recordMap[key] ?: listOf()
                                stickyHeader {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(MaterialTheme.colorScheme.surface)
                                            .padding(horizontal = 16.dp, vertical = 8.dp),
                                    ) {
                                        Text(
                                            modifier = Modifier
                                                .weight(1f),
                                            text = when (key.toInt()) {
                                                todayInt -> stringResource(id = R.string.today)
                                                todayInt - 1 -> stringResource(id = R.string.yesterday)
                                                todayInt - 2 -> stringResource(id = R.string.before_yesterday)
                                                else -> key
                                            }
                                        )
                                        var totalExpenditure = BigDecimal.ZERO
                                        var totalIncome = BigDecimal.ZERO
                                        recordList.forEach { record ->
                                            when (record.type.typeCategory) {
                                                RecordTypeCategoryEnum.EXPENDITURE -> {
                                                    // 支出
                                                    totalExpenditure += (record.amount.toBigDecimalOrZero() + record.charges.toBigDecimalOrZero() - record.concessions.toBigDecimalOrZero())
                                                }

                                                RecordTypeCategoryEnum.INCOME -> {
                                                    // 收入
                                                    totalIncome += (record.amount.toBigDecimalOrZero() - record.charges.toBigDecimalOrZero())
                                                }

                                                RecordTypeCategoryEnum.TRANSFER -> {
                                                    // 转账
                                                    totalExpenditure += record.charges.toBigDecimalOrZero()
                                                    totalIncome += record.concessions.toBigDecimalOrZero()
                                                }
                                            }
                                        }
                                        Text(text = buildString {
                                            val hasIncome = totalIncome.toDouble() != 0.0
                                            if (hasIncome) {
                                                append(stringResource(id = R.string.income_with_colon) + Symbol.rmb + totalIncome.decimalFormat())
                                            }
                                            if (totalExpenditure.toDouble() != 0.0) {
                                                if (hasIncome) {
                                                    append(", ")
                                                }
                                                append(stringResource(id = R.string.expend_with_colon) + Symbol.rmb + totalExpenditure.decimalFormat())
                                            }
                                        })
                                    }
                                }
                                items(recordList, key = { it.id }) {
                                    RecordListItem(
                                        recordViewsEntity = it,
                                        onRecordItemClick = {
                                            viewModel.onRecordItemClick(it)
                                            coroutineScope.launch {
                                                sheetState.show()
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
internal fun LauncherTopBar(
    booksName: String,
    backdropScaffoldState: BackdropScaffoldState,
    onMenuClick: (LauncherMenuAction) -> Unit,
) {
    TopAppBar(
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = Color.Transparent,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        title = {
            if (backdropScaffoldState.isConcealed) {
                Text(text = booksName)
            }
        },
        navigationIcon = {
            IconButton(onClick = { onMenuClick(LauncherMenuAction.MENU) }) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = null,
                )
            }
        },
        actions = {
            IconButton(onClick = { onMenuClick(LauncherMenuAction.SEARCH) }) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                )
            }
            IconButton(onClick = { onMenuClick(LauncherMenuAction.CALENDAR) }) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                )
            }
            IconButton(onClick = { onMenuClick(LauncherMenuAction.MY_ASSET) }) {
                Icon(
                    imageVector = Icons.Default.WebAsset,
                    contentDescription = null,
                )
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RecordListItem(
    recordViewsEntity: RecordViewsEntity,
    onRecordItemClick: () -> Unit
) {
    ListItem(
        modifier = Modifier.clickable {
            onRecordItemClick()
        },
        leadingContent = {
            Icon(
                painter = painterResource(id = recordViewsEntity.type.iconResId),
                contentDescription = null
            )
        },
        headlineText = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = recordViewsEntity.type.name)
                val tags = recordViewsEntity.relatedTags
                if (tags.isNotEmpty()) {
                    val tagsText = with(StringBuilder()) {
                        tags.forEach { tag ->
                            if (!isBlank()) {
                                append(",")
                            }
                            append(tag.name)
                        }
                        var result = toString()
                        if (result.length > 12) {
                            result = result.substring(0, 12) + "…"
                        }
                        result
                    }
                    Text(
                        text = tagsText,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .background(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = MaterialTheme.shapes.small
                            )
                            .padding(horizontal = 4.dp),
                    )
                }
            }
        },
        supportingText = {
            Text(
                text = "${
                    recordViewsEntity.recordTime.split(" ").first()
                } ${recordViewsEntity.remark}"
            )
        },
        trailingContent = {
            Column(
                horizontalAlignment = Alignment.End
            ) {
                // TODO 关联记录
                Text(
                    text = buildAnnotatedString {
                        append("${Symbol.rmb}${recordViewsEntity.amount}")
                    },
                    color = when (recordViewsEntity.type.typeCategory) {
                        RecordTypeCategoryEnum.EXPENDITURE -> LocalExtendedColors.current.expenditure
                        RecordTypeCategoryEnum.INCOME -> LocalExtendedColors.current.income
                        RecordTypeCategoryEnum.TRANSFER -> LocalExtendedColors.current.transfer
                    },
                    style = MaterialTheme.typography.labelLarge,
                )
                recordViewsEntity.asset?.let { asset ->
                    Text(text = buildAnnotatedString {
                        val hasCharges = recordViewsEntity.charges.toDoubleOrZero() > 0.0
                        val hasConcessions = recordViewsEntity.concessions.toDoubleOrZero() > 0.0
                        if (hasCharges || hasConcessions) {
                            // 有手续费、优惠信息
                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                                append("(")
                            }
                            if (hasCharges) {
                                withStyle(style = SpanStyle(color = LocalExtendedColors.current.expenditure)) {
                                    append("-${Symbol.rmb}${recordViewsEntity.charges}")
                                }
                            }
                            if (hasConcessions) {
                                if (hasCharges) {
                                    append(" ")
                                }
                                withStyle(style = SpanStyle(color = LocalExtendedColors.current.income)) {
                                    append("+${Symbol.rmb}${recordViewsEntity.concessions}")
                                }
                            }
                            withStyle(style = SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                                append(") ")
                            }
                        }
                        append(asset.name)
                        if (recordViewsEntity.type.typeCategory == RecordTypeCategoryEnum.TRANSFER) {
                            append(" -> ${recordViewsEntity.relatedAsset?.name}")
                        }
                    })
                }
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun RecordDetailsSheet(
    recordEntity: RecordViewsEntity,
    onRecordItemEditClick: (Long) -> Unit,
    onRecordItemDeleteClick: (Long) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(id = R.string.record_details),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f),
            )
            TextButton(
                onClick = { onRecordItemEditClick(recordEntity.id) },
            ) {
                Text(
                    text = stringResource(id = R.string.edit),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            TextButton(
                onClick = { onRecordItemDeleteClick(recordEntity.id) },
            ) {
                Text(
                    text = stringResource(id = R.string.delete),
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
        CommonDivider()

        // 金额
        ListItem(
            headlineText = { Text(text = stringResource(id = R.string.amount)) },
            trailingContent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (recordEntity.type.typeCategory == RecordTypeCategoryEnum.EXPENDITURE && recordEntity.reimbursable) {
                        // 支出类型，并且可报销
                        val text = if (recordEntity.relatedRecord.isEmpty()) {
                            // 未报销
                            stringResource(id = R.string.reimbursable)
                        } else {
                            // 已报销
                            stringResource(id = R.string.reimbursed)
                        }
                        Text(
                            text = text,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(end = 8.dp),
                        )
                    }
                    Text(
                        text = "${Symbol.rmb}${recordEntity.amount}",
                        color = when (recordEntity.type.typeCategory) {
                            RecordTypeCategoryEnum.EXPENDITURE -> LocalExtendedColors.current.expenditure
                            RecordTypeCategoryEnum.INCOME -> LocalExtendedColors.current.income
                            RecordTypeCategoryEnum.TRANSFER -> LocalExtendedColors.current.transfer
                        },
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            },
        )

        if (recordEntity.charges.toDoubleOrZero() > 0.0) {
            // 手续费
            ListItem(
                headlineText = { Text(text = stringResource(id = R.string.charges)) },
                trailingContent = {
                    Text(
                        text = "-${Symbol.rmb}${recordEntity.charges}",
                        color = LocalExtendedColors.current.expenditure,
                        style = MaterialTheme.typography.labelLarge,
                    )
                },
            )
        }

        if (recordEntity.type.typeCategory != RecordTypeCategoryEnum.INCOME && recordEntity.concessions.toDoubleOrZero() > 0.0) {
            // 优惠
            ListItem(
                headlineText = { Text(text = stringResource(id = R.string.concessions)) },
                trailingContent = {
                    Text(
                        text = "+${Symbol.rmb}${recordEntity.concessions}",
                        color = LocalExtendedColors.current.income,
                        style = MaterialTheme.typography.labelLarge,
                    )
                },
            )
        }

        // 类型
        ListItem(
            headlineText = { Text(text = stringResource(id = R.string.type)) },
            trailingContent = {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = recordEntity.type.iconResId),
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp),
                    )
                    Text(
                        text = recordEntity.type.name,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            },
        )

        // TODO 关联的记录

        recordEntity.asset?.let { asset ->
            // 资产
            ListItem(
                headlineText = { Text(text = stringResource(id = R.string.asset)) },
                trailingContent = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = asset.iconResId),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.padding(end = 8.dp),
                        )
                        Text(
                            text = asset.name,
                            style = MaterialTheme.typography.labelLarge,
                        )
                        // 关联资产
                        recordEntity.relatedAsset?.let { related ->
                            Text(
                                text = "->",
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(horizontal = 8.dp),
                            )
                            Icon(
                                painter = painterResource(id = related.iconResId),
                                contentDescription = null,
                                tint = Color.Unspecified,
                                modifier = Modifier.padding(end = 8.dp),
                            )
                            Text(
                                text = related.name,
                                style = MaterialTheme.typography.labelLarge,
                            )
                        }
                    }
                },
            )
        }

        if (recordEntity.relatedTags.isNotEmpty()) {
            // 标签
            ListItem(
                headlineText = { Text(text = stringResource(id = R.string.tags)) },
                trailingContent = {
                    val tagsText = with(StringBuilder()) {
                        recordEntity.relatedTags.forEach { tag ->
                            if (!isBlank()) {
                                append(",")
                            }
                            append(tag.name)
                        }
                        var result = toString()
                        if (result.length > 12) {
                            result = result.substring(0, 12) + "…"
                        }
                        result
                    }
                    Text(
                        text = tagsText,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.secondaryContainer,
                                shape = MaterialTheme.shapes.small
                            )
                            .padding(horizontal = 4.dp),
                    )
                },
            )
        }

        if (recordEntity.remark.isNotBlank()) {
            // 备注
            ListItem(
                headlineText = { Text(text = stringResource(id = R.string.remark)) },
                trailingContent = {
                    Text(
                        text = recordEntity.remark,
                        style = MaterialTheme.typography.labelLarge,
                    )
                },
            )
        }

        // 时间
        ListItem(
            headlineText = { Text(text = stringResource(id = R.string.time)) },
            trailingContent = {
                Text(
                    text = recordEntity.recordTime,
                    style = MaterialTheme.typography.labelLarge,
                )
            },
        )
    }
}
