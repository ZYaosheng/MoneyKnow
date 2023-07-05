package cn.wj.android.cashbook.feature.records.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.SaveAs
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheetState
import androidx.compose.material3.ModalBottomSheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wj.android.cashbook.core.common.Symbol
import cn.wj.android.cashbook.core.common.ext.completeZero
import cn.wj.android.cashbook.core.common.ext.toIntOrZero
import cn.wj.android.cashbook.core.common.ext.withCNY
import cn.wj.android.cashbook.core.common.tools.DATE_FORMAT_DATE
import cn.wj.android.cashbook.core.common.tools.DATE_FORMAT_TIME
import cn.wj.android.cashbook.core.common.tools.dateFormat
import cn.wj.android.cashbook.core.common.tools.parseDateLong
import cn.wj.android.cashbook.core.design.component.Calculator
import cn.wj.android.cashbook.core.design.component.CashbookBottomSheetScaffold
import cn.wj.android.cashbook.core.design.component.CashbookFloatingActionButton
import cn.wj.android.cashbook.core.design.component.CashbookGradientBackground
import cn.wj.android.cashbook.core.design.component.CompatTextField
import cn.wj.android.cashbook.core.design.component.Empty
import cn.wj.android.cashbook.core.design.component.TextFieldState
import cn.wj.android.cashbook.core.design.component.rememberSnackbarHostState
import cn.wj.android.cashbook.core.design.theme.CashbookTheme
import cn.wj.android.cashbook.core.design.theme.LocalExtendedColors
import cn.wj.android.cashbook.core.model.enums.RecordTypeCategoryEnum
import cn.wj.android.cashbook.core.ui.BackPressHandler
import cn.wj.android.cashbook.core.ui.DevicePreviews
import cn.wj.android.cashbook.core.ui.R
import cn.wj.android.cashbook.core.ui.UiState
import cn.wj.android.cashbook.feature.records.enums.EditRecordBookmarkEnum
import cn.wj.android.cashbook.feature.records.enums.EditRecordBottomSheetEnum
import cn.wj.android.cashbook.feature.records.model.EditRecordUiData
import cn.wj.android.cashbook.feature.records.model.TabItem
import cn.wj.android.cashbook.feature.records.viewmodel.EditRecordViewModel
import com.google.accompanist.flowlayout.FlowRow
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat.CLOCK_24H

@Composable
internal fun EditRecordRoute(
    recordId: Long,
    typeId: Long,
    typeListContent: @Composable (
        modifier: Modifier,
        typeCategory: RecordTypeCategoryEnum,
        selectedTypeId: Long,
        onTypeSelect: (Long) -> Unit,
        headerContent: @Composable (modifier: Modifier) -> Unit,
        footerContent: @Composable (modifier: Modifier) -> Unit,
    ) -> Unit,
    selectAssetBottomSheetContent: @Composable (
        currentTypeId: Long,
        isRelated: Boolean,
        onAssetChange: (Long) -> Unit,
    ) -> Unit,
    selectTagBottomSheetContent: @Composable (
        selectedTagIdList: List<Long>,
        onTagIdListChange: (List<Long>) -> Unit,
    ) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditRecordViewModel = hiltViewModel<EditRecordViewModel>().apply {
        updateRecordId(recordId)
        onTypeSelect(typeId)
    },
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedTypeCategory by viewModel.selectedTypeCategoryData.collectAsStateWithLifecycle()
    val tagText by viewModel.tagTextData.collectAsStateWithLifecycle()
    val selectedTagIdList by viewModel.displayTagIdListData.collectAsStateWithLifecycle()

    val selectedTypeId =
        ((uiState as? UiState.Success<*>)?.data as? EditRecordUiData)?.selectedTypeId ?: -1L

    EditRecordScreen(
        uiState = uiState,
        shouldDisplayBookmark = viewModel.shouldDisplayBookmark,
        dismissBookmark = viewModel::dismissBookmark,
        onBackClick = onBackClick,
        selectedTypeCategory = selectedTypeCategory,
        onTypeCategorySelect = viewModel::onTypeCategorySelect,
        bottomSheetType = viewModel.bottomSheetType,
        dismissBottomSheet = viewModel::dismissBottomSheet,
        onAmountClick = viewModel::onAmountClick,
        onAmountChange = viewModel::onAmountChange,
        onChargesClick = viewModel::onChargesClick,
        onChargesChange = viewModel::onChargeChange,
        onConcessionsClick = viewModel::onConcessionsClick,
        onConcessionsChange = viewModel::onConcessionsChange,
        typeListContent = typeListContent,
        onTypeSelect = viewModel::onTypeSelect,
        onRemarkChange = viewModel::onRemarkChange,
        onAssetClick = viewModel::onAssetClick,
        onRelatedAssetClick = viewModel::onRelatedAssetClick,
        selectAssetBottomSheetContent = {
            selectAssetBottomSheetContent(
                currentTypeId = selectedTypeId,
                isRelated = false,
                onAssetChange = viewModel::onAssetChange,
            )
        },
        selectRelatedAssetBottomSheetContent = {
            selectAssetBottomSheetContent(
                currentTypeId = selectedTypeId,
                isRelated = true,
                onAssetChange = viewModel::onRelatedAssetChange,
            )
        },
        onDateTimeChange = viewModel::onDateTimeChange,
        tagText = tagText,
        onTagClick = viewModel::onTagClick,
        selectTagBottomSheetContent = {
            selectTagBottomSheetContent(
                selectedTagIdList = selectedTagIdList,
                onTagIdListChange = viewModel::onTagChange,
            )
        },
        onReimbursableClick = viewModel::onReimbursableClick,
        onSaveClick = { viewModel.onSaveClick(onSuccess = onBackClick) },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditRecordScreen(
    uiState: UiState,
    // Snackbar 提示
    shouldDisplayBookmark: EditRecordBookmarkEnum,
    dismissBookmark: () -> Unit,
    // 标题栏
    onBackClick: () -> Unit,
    selectedTypeCategory: RecordTypeCategoryEnum,
    onTypeCategorySelect: (RecordTypeCategoryEnum) -> Unit,
    // bottom sheet
    bottomSheetType: EditRecordBottomSheetEnum,
    dismissBottomSheet: () -> Unit,
    // 金额
    onAmountClick: () -> Unit,
    onAmountChange: (String) -> Unit,
    // 手续费
    onChargesClick: () -> Unit,
    onChargesChange: (String) -> Unit,
    // 优惠
    onConcessionsClick: () -> Unit,
    onConcessionsChange: (String) -> Unit,
    // 类型列表
    typeListContent: @Composable (
        modifier: Modifier,
        typeCategory: RecordTypeCategoryEnum,
        selectedTypeId: Long,
        onTypeSelect: (Long) -> Unit,
        headerContent: @Composable (modifier: Modifier) -> Unit,
        footerContent: @Composable (modifier: Modifier) -> Unit,
    ) -> Unit,
    onTypeSelect: (Long) -> Unit,
    // 备注
    onRemarkChange: (String) -> Unit,
    // 资产
    onAssetClick: () -> Unit,
    // 关联资产
    onRelatedAssetClick: () -> Unit,
    selectAssetBottomSheetContent: @Composable () -> Unit,
    selectRelatedAssetBottomSheetContent: @Composable () -> Unit,
    // 时间
    onDateTimeChange: (String) -> Unit,
    // 标签
    tagText: String,
    onTagClick: () -> Unit,
    selectTagBottomSheetContent: @Composable () -> Unit,
    // 报销
    onReimbursableClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: ModalBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { value ->
            if (value == ModalBottomSheetValue.Hidden) {
                dismissBottomSheet()
            }
            true
        }),
    snackbarHostState: SnackbarHostState = rememberSnackbarHostState(),
) {

    // 提示文本
    val amountMustNotBeNullText = stringResource(id = R.string.amount_must_not_be_zero)
    val typeErrorText = stringResource(id = R.string.please_select_type)
    val saveFailedText = stringResource(id = R.string.save_failure)
    LaunchedEffect(shouldDisplayBookmark) {
        if (shouldDisplayBookmark != EditRecordBookmarkEnum.NONE) {
            val tipText = when (shouldDisplayBookmark) {
                EditRecordBookmarkEnum.AMOUNT_MUST_NOT_BE_ZERO -> amountMustNotBeNullText
                EditRecordBookmarkEnum.TYPE_NOT_MATCH_CATEGORY, EditRecordBookmarkEnum.TYPE_MUST_NOT_BE_NULL -> typeErrorText
                EditRecordBookmarkEnum.SAVE_FAILED -> saveFailedText
                else -> ""
            }
            val showSnackbarResult = snackbarHostState.showSnackbar(tipText)
            if (SnackbarResult.Dismissed == showSnackbarResult) {
                dismissBookmark()
            }
        }
    }

    // sheet 状态
    if (sheetState.isVisible) {
        // sheet 显示时，返回隐藏 sheet
        BackPressHandler {
            dismissBottomSheet()
        }
    }
    LaunchedEffect(bottomSheetType) {
        if (bottomSheetType == EditRecordBottomSheetEnum.NONE) {
            sheetState.hide()
        } else {
            sheetState.show()
        }
    }

    // 主色调
    val primaryColor = when (selectedTypeCategory) {
        RecordTypeCategoryEnum.EXPENDITURE -> LocalExtendedColors.current.expenditure
        RecordTypeCategoryEnum.INCOME -> LocalExtendedColors.current.income
        RecordTypeCategoryEnum.TRANSFER -> LocalExtendedColors.current.transfer
    }

    CashbookBottomSheetScaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            EditRecordTopBarNew(
                selectedTab = selectedTypeCategory,
                onTabSelected = onTypeCategorySelect,
                onBackClick = onBackClick,
            )
        },
        floatingActionButton = {
            CashbookFloatingActionButton(onClick = {
                onSaveClick()
            }) {
                Icon(imageVector = Icons.Default.SaveAs, contentDescription = null)
            }
        },
        sheetState = sheetState,
        sheetContent = {
            when (bottomSheetType) {

                EditRecordBottomSheetEnum.AMOUNT -> {
                    ((uiState as? UiState.Success<*>)?.data as? EditRecordUiData)?.let { data ->
                        Calculator(
                            defaultText = data.amountText,
                            primaryColor = primaryColor,
                            onConfirmClick = onAmountChange,
                        )
                    }
                }

                EditRecordBottomSheetEnum.CHARGES -> {
                    ((uiState as? UiState.Success<*>)?.data as? EditRecordUiData)?.let { data ->
                        Calculator(
                            defaultText = data.chargesText,
                            primaryColor = primaryColor,
                            onConfirmClick = onChargesChange,
                        )
                    }
                }

                EditRecordBottomSheetEnum.CONCESSIONS -> {
                    ((uiState as? UiState.Success<*>)?.data as? EditRecordUiData)?.let { data ->
                        Calculator(
                            defaultText = data.concessionsText,
                            primaryColor = primaryColor,
                            onConfirmClick = onConcessionsChange,
                        )
                    }
                }

                EditRecordBottomSheetEnum.ASSETS -> {
                    // 显示选择资产弹窗
                    selectAssetBottomSheetContent()
                }

                EditRecordBottomSheetEnum.RELATED_ASSETS -> {
                    // 显示选择关联资产弹窗
                    selectRelatedAssetBottomSheetContent()
                }

                EditRecordBottomSheetEnum.TAGS -> {
                    // TODO 显示选择标签弹窗
                    selectTagBottomSheetContent()
                }

                EditRecordBottomSheetEnum.NONE -> {
                    // empty block
                }
            }
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier.padding(paddingValues),
            ) {
                if (uiState == UiState.Loading) {
                    Empty(
                        imagePainter = painterResource(id = R.drawable.vector_no_data_200),
                        hintText = stringResource(id = R.string.data_in_loading),
                        modifier = Modifier.align(Alignment.Center),
                    )
                } else {
                    ((uiState as? UiState.Success<*>)?.data as? EditRecordUiData)?.let { data ->
                        typeListContent(
                            modifier = Modifier.fillMaxSize(),
                            typeCategory = selectedTypeCategory,
                            selectedTypeId = data.selectedTypeId,
                            onTypeSelect = onTypeSelect,
                            headerContent = { modifier ->
                                Column(
                                    modifier = modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp)
                                ) {
                                    // 金额显示
                                    Amount(
                                        amount = data.amountText,
                                        primaryColor = primaryColor,
                                        onAmountClick = onAmountClick,
                                    )
                                    Divider()
                                    Text(
                                        text = stringResource(id = R.string.record_type),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.labelSmall,
                                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                                    )
                                }
                            },
                            footerContent = { modifier ->
                                Column(
                                    modifier = modifier
                                        .fillMaxWidth()
                                        .padding(top = 8.dp)
                                ) {
                                    Divider()

                                    // 备注文本
                                    val remarkTextState = remember {
                                        TextFieldState(
                                            defaultText = data.remarkText,
                                            filter = { text ->
                                                onRemarkChange(text)
                                                true
                                            },
                                        )
                                    }

                                    // 备注信息
                                    CompatTextField(
                                        textFieldState = remarkTextState,
                                        label = { Text(text = stringResource(id = R.string.remark)) },
                                        colors = TextFieldDefaults.outlinedTextFieldColors(),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp),
                                    )

                                    // 其他选项
                                    FlowRow(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp),
                                        mainAxisSpacing = 8.dp,
                                        crossAxisSpacing = 4.dp,
                                    ) {
                                        // 目标资产
                                        val hasAsset = data.assetText.isNotBlank()
                                        FilterChip(
                                            selected = hasAsset,
                                            onClick = onAssetClick,
                                            label = { Text(text = stringResource(id = R.string.target_asset) + if (hasAsset) ":${data.assetText}" else "") },
                                        )

                                        if (selectedTypeCategory == RecordTypeCategoryEnum.TRANSFER) {
                                            // 只有转账类型显示关联资产
                                            val hasRelatedAsset =
                                                data.relatedAssetText.isNotBlank()
                                            FilterChip(
                                                selected = hasRelatedAsset,
                                                onClick = onRelatedAssetClick,
                                                label = { Text(text = stringResource(id = R.string.related_asset) + if (hasRelatedAsset) ":${data.relatedAssetText}" else "") },
                                            )
                                        }

                                        // 记录时间
                                        (LocalContext.current as? FragmentActivity)?.supportFragmentManager?.let { fm ->
                                            val dateTime = data.dateTimeText
                                            FilterChip(
                                                selected = true,
                                                onClick = {
                                                    var date =
                                                        dateTime.parseDateLong().dateFormat(
                                                            DATE_FORMAT_DATE
                                                        )
                                                    var time =
                                                        dateTime.parseDateLong().dateFormat(
                                                            DATE_FORMAT_TIME
                                                        )
                                                    val datePicker =
                                                        MaterialDatePicker.Builder.datePicker()
                                                            .setSelection(dateTime.parseDateLong())
                                                            .build()
                                                    val timePicker =
                                                        MaterialTimePicker.Builder()
                                                            .setTimeFormat(CLOCK_24H)
                                                            .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                                                            .setHour(
                                                                time.split(":").first()
                                                                    .toIntOrZero()
                                                            )
                                                            .setMinute(
                                                                time.split(":").last()
                                                                    .toIntOrZero()
                                                            )
                                                            .build()
                                                    timePicker.addOnPositiveButtonClickListener {
                                                        time =
                                                            (timePicker.hour.completeZero() + ":" + timePicker.minute.completeZero())
                                                        onDateTimeChange("$date $time")
                                                    }
                                                    datePicker.addOnPositiveButtonClickListener { timeMs ->
                                                        date =
                                                            timeMs.dateFormat(DATE_FORMAT_DATE)
                                                        timePicker.show(fm, "timePicker")
                                                    }
                                                    datePicker.show(fm, "datePicker")
                                                },
                                                label = { Text(text = dateTime) },
                                            )
                                        }


                                        // 标签
                                        val hasTag = tagText.isNotBlank()
                                        FilterChip(
                                            selected = hasTag,
                                            onClick = onTagClick,
                                            label = { Text(text = stringResource(id = R.string.tags) + if (hasTag) ":$tagText" else "") },
                                        )

                                        if (selectedTypeCategory == RecordTypeCategoryEnum.EXPENDITURE) {
                                            // 只有支出类型显示是否可报销
                                            val reimbursable = data.reimbursable
                                            FilterChip(
                                                selected = reimbursable,
                                                onClick = onReimbursableClick,
                                                leadingIcon = {
                                                    if (reimbursable) {
                                                        Icon(
                                                            imageVector = Icons.Default.Check,
                                                            contentDescription = null,
                                                        )
                                                    }
                                                },
                                                label = { Text(text = stringResource(id = R.string.reimbursable)) },
                                            )
                                        }

                                        // 手续费
                                        val hasCharges = data.chargesText.isNotBlank()
                                        FilterChip(
                                            selected = hasCharges,
                                            onClick = onChargesClick,
                                            label = { Text(text = stringResource(id = R.string.charges) + if (hasCharges) ":${data.chargesText.withCNY()}" else "") },
                                        )

                                        if (selectedTypeCategory != RecordTypeCategoryEnum.INCOME) {
                                            // 非收入类型才有优惠
                                            val hasConcessions =
                                                data.concessionsText.isNotBlank()
                                            FilterChip(
                                                selected = hasConcessions,
                                                onClick = onConcessionsClick,
                                                label = { Text(text = stringResource(id = R.string.concessions) + if (hasConcessions) ":${data.concessionsText.withCNY()}" else "") },
                                            )
                                        }

                                        // TODO 关联的支出记录
                                        if (selectedTypeCategory == RecordTypeCategoryEnum.INCOME/* FIXME && selectedType?.needRelated == true*/) {
//                                if (relatedRecordList.isEmpty()) {
//                                    FilterChip(
//                                        selected = false,
//                                        onClick = onSelectRelatedRecordClick,
//                                        label = { Text(text = stringResource(id = R.string.related_record)) },
//                                    )
//                                } else {
//                                    relatedRecordList.forEach {
//                                        FilterChip(
//                                            selected = true,
//                                            onClick = { /*TODO*/ },
//                                            label = { Text(text = it.value) },
//                                        )
//                                    }
//                                }
                                        }
                                    }
                                }
                            },
                        )
                    }
                }
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditRecordTopBarNew(
    onBackClick: () -> Unit,
    selectedTab: RecordTypeCategoryEnum,
    onTabSelected: (RecordTypeCategoryEnum) -> Unit,
) {
    // 顶部标签列表
    val tabs = arrayListOf(
        TabItem(
            title = stringResource(id = R.string.expend),
            type = RecordTypeCategoryEnum.EXPENDITURE,
        ),
        TabItem(title = stringResource(id = R.string.income), type = RecordTypeCategoryEnum.INCOME),
        TabItem(
            title = stringResource(id = R.string.transfer), type = RecordTypeCategoryEnum.TRANSFER
        ),
    )
    TopAppBar(
        colors = TopAppBarDefaults.smallTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
            navigationIconContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
        ),
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
            }
        },
        title = {
            TabRow(
                modifier = Modifier.fillMaxSize(),
                selectedTabIndex = selectedTab.position,
                containerColor = Color.Unspecified,
                contentColor = Color.Unspecified,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.position]),
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                },
                divider = {},
            ) {
                tabs.forEach { tabItem ->
                    Tab(
                        selected = selectedTab == tabItem.type,
                        onClick = { onTabSelected(tabItem.type) },
                        text = { Text(text = tabItem.title) },
                        selectedContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        unselectedContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                }
            }
        },
    )
}

/**
 * 金额显示框
 *
 * @param amount 金额
 * @param primaryColor 显示颜色
 * @param onAmountClick 点击回调
 */
@Composable
internal fun Amount(
    amount: String,
    primaryColor: Color,
    onAmountClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onAmountClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = Symbol.CNY,
            color = primaryColor,
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = amount,
            color = primaryColor,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp),
        )
    }
}

@DevicePreviews
@Composable
private fun EditRecordScreenPreview() {
    CashbookTheme {
        CashbookGradientBackground {
            EditRecordScreen(
                uiState = UiState.Success(
                    EditRecordUiData(
                        amountText = "100",
                        chargesText = "10",
                        concessionsText = "",
                        remarkText = "备注",
                        assetText = "微信(￥1000)",
                        relatedAssetText = "",
                        dateTimeText = "2023-07-01 11:30",
                        reimbursable = false,
                        selectedTypeId = -1L,
                    )
                ),
                shouldDisplayBookmark = EditRecordBookmarkEnum.NONE,
                dismissBookmark = {},
                selectedTypeCategory = RecordTypeCategoryEnum.EXPENDITURE,
                onTypeCategorySelect = {},
                onBackClick = {},
                bottomSheetType = EditRecordBottomSheetEnum.NONE,
                dismissBottomSheet = {},
                onAmountClick = {},
                onAmountChange = {},
                onChargesClick = {},
                onChargesChange = {},
                onConcessionsClick = {},
                onConcessionsChange = {},
                typeListContent = { modifier, _, _, _, headerContent, footerContent ->
                    Column(
                        modifier = modifier.padding(horizontal = 16.dp),
                    ) {
                        headerContent(Modifier)
                        Text(
                            text = "分类列表",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 50.dp),
                        )
                        footerContent(Modifier)
                    }
                },
                onTypeSelect = {},
                onAssetClick = {},
                onRelatedAssetClick = {},
                selectAssetBottomSheetContent = {},
                selectRelatedAssetBottomSheetContent = {},
                onDateTimeChange = {},
                tagText = "",
                onTagClick = {},
                selectTagBottomSheetContent = { },
                onRemarkChange = {},
                onReimbursableClick = {},
                onSaveClick = {},
            )
        }
    }
}