/*
 * Copyright 2021 The Cashbook Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.wj.android.moneyknow.feature.types.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wj.android.moneyknow.core.common.RECORD_TYPE_COLUMNS
import cn.wj.android.moneyknow.core.design.component.CbAlertDialog
import cn.wj.android.moneyknow.core.design.component.CbElevatedCard
import cn.wj.android.moneyknow.core.design.component.CbFloatingActionButton
import cn.wj.android.moneyknow.core.design.component.CbIconButton
import cn.wj.android.moneyknow.core.design.component.CbListItem
import cn.wj.android.moneyknow.core.design.component.CbModalBottomSheet
import cn.wj.android.moneyknow.core.design.component.CbOutlinedTextField
import cn.wj.android.moneyknow.core.design.component.CbScaffold
import cn.wj.android.moneyknow.core.design.component.CbTab
import cn.wj.android.moneyknow.core.design.component.CbTabRow
import cn.wj.android.moneyknow.core.design.component.CbTextButton
import cn.wj.android.moneyknow.core.design.component.CbTopAppBar
import cn.wj.android.moneyknow.core.design.component.Empty
import cn.wj.android.moneyknow.core.design.component.Footer
import cn.wj.android.moneyknow.core.design.component.Loading
import cn.wj.android.moneyknow.core.design.component.TextFieldState
import cn.wj.android.moneyknow.core.design.component.painterDrawableResource
import cn.wj.android.moneyknow.core.design.icon.CbIcons
import cn.wj.android.moneyknow.core.design.theme.LocalExtendedColors
import cn.wj.android.moneyknow.core.model.enums.RecordTypeCategoryEnum
import cn.wj.android.moneyknow.core.ui.DialogState
import cn.wj.android.moneyknow.core.ui.R
import cn.wj.android.moneyknow.core.ui.component.TypeIcon
import cn.wj.android.moneyknow.feature.types.enums.MyCategoriesBookmarkEnum
import cn.wj.android.moneyknow.feature.types.model.ExpandableRecordTypeModel
import cn.wj.android.moneyknow.feature.types.view.TypeIconGroupList
import cn.wj.android.moneyknow.feature.types.viewmodel.MyCategoriesDialogData
import cn.wj.android.moneyknow.feature.types.viewmodel.MyCategoriesUiState
import cn.wj.android.moneyknow.feature.types.viewmodel.MyCategoriesViewModel

@Composable
internal fun MyCategoriesRoute(
    onRequestNaviToTypeStatistics: (Long) -> Unit,
    onRequestPopBackStack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyCategoriesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MyCategoriesScreen(
        shouldDisplayBookmark = viewModel.shouldDisplayBookmark,
        onRequestDismissBookmark = viewModel::dismissBookmark,
        dialogState = viewModel.dialogState,
        onRequestDismissDialog = viewModel::dismissDialog,
        uiState = uiState,
        onRequestSelectTypeCategory = viewModel::selectTypeCategory,
        onRequestEditType = { viewModel.requestEditType(it, -1L) },
        onRequestChangeFirstTypeToSecond = viewModel::requestChangeFirstTypeToSecond,
        onRequestAddFirstType = { viewModel.requestEditType(-1L, -1L) },
        onRequestAddSecondType = { viewModel.requestEditType(-1L, it) },
        changeFirstTypeToSecond = viewModel::changeTypeToSecond,
        onRequestChangeSecondTypeToFirst = viewModel::changeSecondTypeToFirst,
        onRequestMoveSecondTypeToAnother = viewModel::requestMoveSecondTypeToAnother,
        onRequestSetRefundType = viewModel::setRefundType,
        onRequestSetReimburseType = viewModel::setReimburseType,
        onRequestSetCreditCardPaymentType = viewModel::setCreditCardPaymentType,
        onRequestNaviToTypeStatistics = onRequestNaviToTypeStatistics,
        onRequestDeleteType = viewModel::requestDeleteType,
        changeRecordTypeBeforeDelete = viewModel::changeRecordTypeBeforeDeleteType,
        onRequestSaveRecordType = viewModel::saveRecordType,
        onRequestPopBackStack = onRequestPopBackStack,
        modifier = modifier,
    )
}

@Composable
internal fun MyCategoriesScreen(
    shouldDisplayBookmark: MyCategoriesBookmarkEnum,
    onRequestDismissBookmark: () -> Unit,
    dialogState: DialogState,
    onRequestDismissDialog: () -> Unit,
    uiState: MyCategoriesUiState,
    onRequestSelectTypeCategory: (RecordTypeCategoryEnum) -> Unit,
    onRequestEditType: (Long) -> Unit,
    onRequestChangeFirstTypeToSecond: (Long) -> Unit,
    onRequestAddFirstType: () -> Unit,
    onRequestAddSecondType: (Long) -> Unit,
    changeFirstTypeToSecond: (Long, Long) -> Unit,
    onRequestChangeSecondTypeToFirst: (Long) -> Unit,
    onRequestMoveSecondTypeToAnother: (Long, Long) -> Unit,
    onRequestSetRefundType: (Long) -> Unit,
    onRequestSetReimburseType: (Long) -> Unit,
    onRequestSetCreditCardPaymentType: (Long) -> Unit,
    onRequestNaviToTypeStatistics: (Long) -> Unit,
    onRequestDeleteType: (Long) -> Unit,
    changeRecordTypeBeforeDelete: (Long, Long) -> Unit,
    onRequestSaveRecordType: (Long, Long, String, String) -> Unit,
    onRequestPopBackStack: () -> Unit,
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    // 提示文本
    val changeFirstTypeHasChildHintText =
        stringResource(id = R.string.change_first_type_has_child_hint)
    val deleteFirstTypeHasChildHintText =
        stringResource(id = R.string.delete_first_type_has_child_hint)
    val protectedTypeHintText = stringResource(id = R.string.protected_type_hint)
    val deleteSuccessHintText = stringResource(id = R.string.delete_success)
    val duplicateTypeNameHintText = stringResource(id = R.string.duplicate_type_name_hint)
    LaunchedEffect(shouldDisplayBookmark) {
        if (shouldDisplayBookmark != MyCategoriesBookmarkEnum.DISMISS) {
            val hintText = when (shouldDisplayBookmark) {
                MyCategoriesBookmarkEnum.CHANGE_FIRST_TYPE_HAS_CHILD -> changeFirstTypeHasChildHintText
                MyCategoriesBookmarkEnum.DELETE_FIRST_TYPE_HAS_CHILD -> deleteFirstTypeHasChildHintText
                MyCategoriesBookmarkEnum.PROTECTED_TYPE -> protectedTypeHintText
                MyCategoriesBookmarkEnum.DELETE_SUCCESS -> deleteSuccessHintText
                MyCategoriesBookmarkEnum.DUPLICATE_TYPE_NAME -> duplicateTypeNameHintText
                else -> ""
            }
            if (hintText.isNotBlank()) {
                val result = snackbarHostState.showSnackbar(hintText)
                if (result == SnackbarResult.Dismissed) {
                    onRequestDismissBookmark()
                }
            } else {
                onRequestDismissBookmark()
            }
        }
    }

    CbScaffold(
        modifier = modifier,
        topBar = {
            MyCategoriesTopBar(
                uiState = uiState,
                onTabSelected = onRequestSelectTypeCategory,
                onBackClick = onRequestPopBackStack,
            )
        },
        floatingActionButton = {
            CbFloatingActionButton(onClick = onRequestAddFirstType) {
                Icon(imageVector = CbIcons.Add, contentDescription = null)
            }
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            (dialogState as? DialogState.Shown<*>)?.data?.let { data ->
                if (data is MyCategoriesDialogData) {
                    when (data) {
                        is MyCategoriesDialogData.SelectFirstType -> {
                            SelectFirstTypeDialog(
                                onRequestDismissDialog = onRequestDismissDialog,
                                data = data,
                                changeFirstTypeToSecond = changeFirstTypeToSecond,
                            )
                        }

                        is MyCategoriesDialogData.DeleteType -> {
                            DeleteTypeDialog(
                                onRequestDismissDialog = onRequestDismissDialog,
                                data = data,
                                changeRecordTypeBeforeDelete = changeRecordTypeBeforeDelete,
                            )
                        }

                        is MyCategoriesDialogData.EditType -> {
                            EditTypeSheet(
                                onRequestDismissDialog = onRequestDismissDialog,
                                data = data,
                                onRequestSaveRecordType = onRequestSaveRecordType,
                            )
                        }
                    }
                }
            }

            when (uiState) {
                MyCategoriesUiState.Loading -> {
                    Loading(modifier = Modifier.align(Alignment.Center))
                }

                is MyCategoriesUiState.Success -> {
                    if (uiState.typeList.isEmpty()) {
                        Empty(hintText = stringResource(id = R.string.type_no_data))
                    } else {
                        Column {
                            Text(
                                text = stringResource(id = R.string.click_arrow_expand_more),
                                color = LocalContentColor.current.copy(alpha = 0.5f),
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            )
                            ExpandableTypeList(
                                typeList = uiState.typeList,
                                onRequestEditType = onRequestEditType,
                                onRequestDeleteType = onRequestDeleteType,
                                onRequestChangeFirstTypeToSecond = onRequestChangeFirstTypeToSecond,
                                onRequestAddSecondType = onRequestAddSecondType,
                                onRequestNaviToTypeStatistics = onRequestNaviToTypeStatistics,
                                onRequestChangeSecondTypeToFirst = onRequestChangeSecondTypeToFirst,
                                onRequestMoveSecondTypeToAnother = onRequestMoveSecondTypeToAnother,
                                onRequestSetRefundType = onRequestSetRefundType,
                                onRequestSetReimburseType = onRequestSetReimburseType,
                                onRequestSetCreditCardPaymentType = onRequestSetCreditCardPaymentType,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun EditTypeSheet(
    onRequestDismissDialog: () -> Unit,
    data: MyCategoriesDialogData.EditType,
    onRequestSaveRecordType: (Long, Long, String, String) -> Unit,
) {
    CbModalBottomSheet(
        onDismissRequest = onRequestDismissDialog,
        sheetState = rememberModalBottomSheetState(
            confirmValueChange = {
                if (it == SheetValue.Hidden) {
                    onRequestDismissDialog()
                }
                true
            },
        ),
        content = {
            var typeNameEdit by remember {
                mutableStateOf(data.type != null)
            }
            val typeNameBlackHintText =
                stringResource(id = R.string.type_name_must_not_blank)
            val editTypeName = remember {
                TextFieldState(
                    defaultText = data.type?.name.orEmpty(),
                    filter = {
                        typeNameEdit = true
                        true
                    },
                    validator = { it.isNotBlank() },
                    errorFor = { typeNameBlackHintText },
                )
            }
            var editTypeIcon by remember {
                mutableStateOf(
                    data.type?.iconName ?: "vector_type_three_meals_24",
                )
            }
            Row(
                modifier = Modifier.padding(
                    horizontal = 16.dp,
                    vertical = 8.dp,
                ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(id = R.string.save_type_hint),
                    color = LocalContentColor.current.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.weight(1f),
                )
                CbTextButton(
                    onClick = {
                        if (editTypeName.isValid) {
                            onRequestSaveRecordType(
                                data.type?.id ?: -1L,
                                data.parentType?.id ?: -1L,
                                editTypeName.text,
                                editTypeIcon,
                            )
                        }
                    },
                ) {
                    Text(text = stringResource(id = R.string.save))
                }
            }
            val color = LocalExtendedColors.current.quaternaryContainer
            if (null != data.parentType) {
                Row(
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 8.dp,
                    ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(text = stringResource(id = R.string.first_type))
                    Text(
                        text = data.parentType.name,
                        textAlign = TextAlign.End,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                    )
                    TypeIcon(
                        painter = painterDrawableResource(idStr = data.parentType.iconName),
                        containerColor = color,
                    )
                }
            }
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (null != data.parentType) {
                        stringResource(id = R.string.second_type)
                    } else {
                        stringResource(id = R.string.first_type)
                    },
                    modifier = Modifier
                        .padding(bottom = 8.dp),
                )
                CbOutlinedTextField(
                    textFieldState = editTypeName,
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        disabledBorderColor = Color.Transparent,
                        errorBorderColor = Color.Transparent,
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                )
                TypeIcon(
                    painter = painterDrawableResource(idStr = editTypeIcon),
                    containerColor = color,
                )
            }
            TypeIconGroupList(
                onTypeIconSelect = { name, iconName ->
                    if (!typeNameEdit) {
                        editTypeName.text = name
                    }
                    editTypeIcon = iconName
                },
            )
        },
    )
}

@Composable
private fun DeleteTypeDialog(
    onRequestDismissDialog: () -> Unit,
    data: MyCategoriesDialogData.DeleteType,
    changeRecordTypeBeforeDelete: (Long, Long) -> Unit,
) {
    CbAlertDialog(
        onDismissRequest = onRequestDismissDialog,
        title = {
            Text(
                text = stringResource(id = R.string.select_type_to_move_before_delete_format).format(
                    data.recordSize,
                ),
                style = MaterialTheme.typography.labelLarge,
            )
        },
        text = {
            DialogExpandableTypeList(
                typeList = data.expandableTypeList,
                onTypeItemClick = {
                    changeRecordTypeBeforeDelete(data.id, it)
                },
            )
        },
        confirmButton = {
            CbTextButton(onClick = onRequestDismissDialog) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
    )
}

@Composable
private fun SelectFirstTypeDialog(
    onRequestDismissDialog: () -> Unit,
    data: MyCategoriesDialogData.SelectFirstType,
    changeFirstTypeToSecond: (Long, Long) -> Unit,
) {
    CbAlertDialog(
        onDismissRequest = onRequestDismissDialog,
        title = { Text(text = stringResource(id = R.string.select_first_type_to_move)) },
        text = {
            LazyColumn(
                content = {
                    items(items = data.typeList) { first ->
                        CbListItem(
                            leadingContent = {
                                Icon(
                                    painter = painterDrawableResource(idStr = first.iconName),
                                    contentDescription = null,
                                )
                            },
                            headlineContent = { Text(text = first.name) },
                            modifier = Modifier.clickable {
                                changeFirstTypeToSecond(data.id, first.id)
                            },
                        )
                    }
                },
            )
        },
        confirmButton = {
            CbTextButton(onClick = onRequestDismissDialog) {
                Text(text = stringResource(id = R.string.cancel))
            }
        },
    )
}

@Composable
private fun ExpandableTypeList(
    typeList: List<ExpandableRecordTypeModel>,
    onRequestEditType: (Long) -> Unit,
    onRequestDeleteType: (Long) -> Unit,
    onRequestChangeFirstTypeToSecond: (Long) -> Unit,
    onRequestAddSecondType: (Long) -> Unit,
    onRequestNaviToTypeStatistics: (Long) -> Unit,
    onRequestChangeSecondTypeToFirst: (Long) -> Unit,
    onRequestMoveSecondTypeToAnother: (Long, Long) -> Unit,
    onRequestSetRefundType: (Long) -> Unit,
    onRequestSetReimburseType: (Long) -> Unit,
    onRequestSetCreditCardPaymentType: (Long) -> Unit,
) {
    LazyColumn(
        content = {
            typeList.forEach { first ->
                val hasChild = first.list.isNotEmpty()
                item {
                    FirstTypeItem(
                        first = first,
                        hasChild = hasChild,
                        onRequestEditType = onRequestEditType,
                        onRequestDeleteType = onRequestDeleteType,
                        onRequestChangeFirstTypeToSecond = onRequestChangeFirstTypeToSecond,
                        onRequestAddSecondType = onRequestAddSecondType,
                        onRequestNaviToTypeStatistics = onRequestNaviToTypeStatistics,
                        onRequestSetRefundType = onRequestSetRefundType,
                        onRequestSetReimburseType = onRequestSetReimburseType,
                        onRequestSetCreditCardPaymentType = onRequestSetCreditCardPaymentType,
                    )
                    if (first.expanded && hasChild) {
                        SecondTypeList(
                            first = first,
                            onRequestEditType = onRequestEditType,
                            onRequestDeleteType = onRequestDeleteType,
                            onRequestChangeSecondTypeToFirst = onRequestChangeSecondTypeToFirst,
                            onRequestMoveSecondTypeToAnother = onRequestMoveSecondTypeToAnother,
                            onRequestNaviToTypeStatistics = onRequestNaviToTypeStatistics,
                            onRequestSetRefundType = onRequestSetRefundType,
                            onRequestSetReimburseType = onRequestSetReimburseType,
                            onRequestSetCreditCardPaymentType = onRequestSetCreditCardPaymentType,
                        )
                    }
                }
            }
            item {
                Footer(hintText = stringResource(id = R.string.footer_hint_default))
            }
        },
    )
}

@Composable
private fun DialogExpandableTypeList(
    typeList: List<ExpandableRecordTypeModel>,
    onTypeItemClick: (Long) -> Unit,
) {
    LazyColumn(
        content = {
            typeList.forEach { first ->
                val hasChild = first.list.isNotEmpty()
                item {
                    CbListItem(
                        leadingContent = {
                            Icon(
                                painter = painterDrawableResource(idStr = first.data.iconName),
                                contentDescription = null,
                            )
                        },
                        headlineContent = { Text(text = first.data.name) },
                        trailingContent = {
                            if (hasChild) {
                                CbIconButton(onClick = { first.expanded = !first.expanded }) {
                                    Icon(
                                        imageVector = if (first.expanded) {
                                            CbIcons.KeyboardArrowDown
                                        } else {
                                            CbIcons.KeyboardArrowRight
                                        },
                                        contentDescription = null,
                                    )
                                }
                            }
                        },
                        modifier = Modifier.clickable { onTypeItemClick(first.data.id) },
                    )
                    if (first.expanded && hasChild) {
                        CbElevatedCard(Modifier.padding(horizontal = 8.dp)) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                val col = if (first.list.size % RECORD_TYPE_COLUMNS == 0) {
                                    first.list.size / RECORD_TYPE_COLUMNS
                                } else {
                                    first.list.size / RECORD_TYPE_COLUMNS + 1
                                }
                                for (c in 0 until col) {
                                    Row(modifier = Modifier.fillMaxWidth()) {
                                        for (r in 0 until RECORD_TYPE_COLUMNS) {
                                            val index = c * RECORD_TYPE_COLUMNS + r
                                            if (index >= first.list.size) {
                                                Spacer(modifier = Modifier.weight(1f))
                                            } else {
                                                val second = first.list[index]
                                                Column(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .padding(8.dp)
                                                        .clickable {
                                                            onTypeItemClick(second.data.id)
                                                        },
                                                    horizontalAlignment = Alignment.CenterHorizontally,
                                                ) {
                                                    Icon(
                                                        painter = painterDrawableResource(idStr = second.data.iconName),
                                                        contentDescription = null,
                                                        tint = LocalExtendedColors.current.unselected,
                                                        modifier = Modifier
                                                            .size(32.dp)
                                                            .background(
                                                                color = Color.Unspecified,
                                                                shape = CircleShape,
                                                            )
                                                            .clip(CircleShape)
                                                            .padding(4.dp),
                                                    )
                                                    Text(
                                                        text = second.data.name,
                                                        color = LocalExtendedColors.current.unselected,
                                                        style = MaterialTheme.typography.labelMedium,
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            item {
                Footer(hintText = stringResource(id = R.string.footer_hint_default))
            }
        },
    )
}

@Composable
private fun FirstTypeItem(
    first: ExpandableRecordTypeModel,
    hasChild: Boolean,
    onRequestEditType: (Long) -> Unit,
    onRequestDeleteType: (Long) -> Unit,
    onRequestChangeFirstTypeToSecond: (Long) -> Unit,
    onRequestAddSecondType: (Long) -> Unit,
    onRequestNaviToTypeStatistics: (Long) -> Unit,
    onRequestSetRefundType: (Long) -> Unit,
    onRequestSetReimburseType: (Long) -> Unit,
    onRequestSetCreditCardPaymentType: (Long) -> Unit,
) {
    Box {
        var expandedMenu by remember {
            mutableStateOf(false)
        }
        CbListItem(
            colors = ListItemDefaults.colors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
            ),
            leadingContent = {
                Icon(
                    painter = painterDrawableResource(idStr = first.data.iconName),
                    contentDescription = null,
                )
            },
            headlineContent = { Text(text = first.data.name) },
            trailingContent = {
                if (hasChild) {
                    CbIconButton(onClick = { first.expanded = !first.expanded }) {
                        Icon(
                            imageVector = if (first.expanded) {
                                CbIcons.KeyboardArrowDown
                            } else {
                                CbIcons.KeyboardArrowRight
                            },
                            contentDescription = null,
                        )
                    }
                }
            },

            modifier = Modifier.clickable { expandedMenu = true },
        )
        DropdownMenu(
            expanded = expandedMenu,
            onDismissRequest = { expandedMenu = false },
        ) {
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.edit)) },
                onClick = {
                    expandedMenu = false
                    onRequestEditType(first.data.id)
                },
            )
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.delete)) },
                onClick = {
                    expandedMenu = false
                    onRequestDeleteType(first.data.id)
                },
            )
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.change_to_second_type)) },
                onClick = {
                    expandedMenu = false
                    onRequestChangeFirstTypeToSecond(first.data.id)
                },
            )
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.add_second_type)) },
                onClick = {
                    expandedMenu = false
                    onRequestAddSecondType(first.data.id)
                },
            )
            if (first.data.typeCategory == RecordTypeCategoryEnum.INCOME) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(
                                id = if (first.refundType) {
                                    R.string.refund_type
                                } else {
                                    R.string.set_refund_type
                                },
                            ),
                        )
                    },
                    enabled = !first.refundType,
                    onClick = {
                        expandedMenu = false
                        onRequestSetRefundType(first.data.id)
                    },
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(
                                id = if (first.reimburseType) {
                                    R.string.reimburse_type
                                } else {
                                    R.string.set_reimburse_type
                                },
                            ),
                        )
                    },
                    enabled = !first.reimburseType,
                    onClick = {
                        expandedMenu = false
                        onRequestSetReimburseType(first.data.id)
                    },
                )
            }
            if (first.data.typeCategory == RecordTypeCategoryEnum.TRANSFER) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(
                                id = if (first.creditCardPaymentType) {
                                    R.string.credit_card_payment_type
                                } else {
                                    R.string.set_credit_card_payment_type
                                },
                            ),
                        )
                    },
                    enabled = !first.creditCardPaymentType,
                    onClick = {
                        expandedMenu = false
                        onRequestSetCreditCardPaymentType(first.data.id)
                    },
                )
            }
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.statistic_data)) },
                onClick = {
                    expandedMenu = false
                    onRequestNaviToTypeStatistics(first.data.id)
                },
            )
        }
    }
}

@Composable
private fun SecondTypeList(
    first: ExpandableRecordTypeModel,
    onRequestEditType: (Long) -> Unit,
    onRequestDeleteType: (Long) -> Unit,
    onRequestChangeSecondTypeToFirst: (Long) -> Unit,
    onRequestMoveSecondTypeToAnother: (Long, Long) -> Unit,
    onRequestNaviToTypeStatistics: (Long) -> Unit,
    onRequestSetRefundType: (Long) -> Unit,
    onRequestSetReimburseType: (Long) -> Unit,
    onRequestSetCreditCardPaymentType: (Long) -> Unit,
) {
    CbElevatedCard(Modifier.padding(horizontal = 8.dp)) {
        Column(modifier = Modifier.fillMaxWidth()) {
            val col = if (first.list.size % RECORD_TYPE_COLUMNS == 0) {
                first.list.size / RECORD_TYPE_COLUMNS
            } else {
                first.list.size / RECORD_TYPE_COLUMNS + 1
            }
            for (c in 0 until col) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (r in 0 until RECORD_TYPE_COLUMNS) {
                        val index = c * RECORD_TYPE_COLUMNS + r
                        if (index >= first.list.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        } else {
                            val second = first.list[index]
                            SecondTypeItem(
                                second = second,
                                onRequestEditType = onRequestEditType,
                                onRequestDeleteType = onRequestDeleteType,
                                onRequestChangeSecondTypeToFirst = onRequestChangeSecondTypeToFirst,
                                onRequestMoveSecondTypeToAnother = onRequestMoveSecondTypeToAnother,
                                onRequestNaviToTypeStatistics = onRequestNaviToTypeStatistics,
                                onRequestSetRefundType = onRequestSetRefundType,
                                onRequestSetReimburseType = onRequestSetReimburseType,
                                onRequestSetCreditCardPaymentType = onRequestSetCreditCardPaymentType,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SecondTypeItem(
    second: ExpandableRecordTypeModel,
    onRequestEditType: (Long) -> Unit,
    onRequestDeleteType: (Long) -> Unit,
    onRequestChangeSecondTypeToFirst: (Long) -> Unit,
    onRequestMoveSecondTypeToAnother: (Long, Long) -> Unit,
    onRequestNaviToTypeStatistics: (Long) -> Unit,
    onRequestSetRefundType: (Long) -> Unit,
    onRequestSetReimburseType: (Long) -> Unit,
    onRequestSetCreditCardPaymentType: (Long) -> Unit,
    modifier: Modifier,
) {
    Box(
        modifier = modifier,
    ) {
        var expandedMenu by remember {
            mutableStateOf(false)
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable {
                    expandedMenu = true
                },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterDrawableResource(idStr = second.data.iconName),
                contentDescription = null,
                tint = LocalExtendedColors.current.unselected,
                modifier = Modifier
                    .size(32.dp)
                    .background(color = Color.Unspecified, shape = CircleShape)
                    .clip(CircleShape)
                    .padding(4.dp),
            )
            Text(
                text = second.data.name,
                color = LocalExtendedColors.current.unselected,
                style = MaterialTheme.typography.labelMedium,
            )
        }
        DropdownMenu(
            expanded = expandedMenu,
            onDismissRequest = {
                expandedMenu = false
            },
        ) {
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(id = R.string.edit))
                },
                onClick = {
                    expandedMenu = false
                    onRequestEditType(second.data.id)
                },
            )
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(id = R.string.delete))
                },
                onClick = {
                    expandedMenu = false
                    onRequestDeleteType(second.data.id)
                },
            )
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(id = R.string.change_to_first_type))
                },
                onClick = {
                    expandedMenu = false
                    onRequestChangeSecondTypeToFirst(second.data.id)
                },
            )
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(id = R.string.move_to_another_first_type))
                },
                onClick = {
                    expandedMenu = false
                    onRequestMoveSecondTypeToAnother(second.data.id, second.data.parentId)
                },
            )
            if (second.data.typeCategory == RecordTypeCategoryEnum.INCOME) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(
                                id = if (second.refundType) {
                                    R.string.refund_type
                                } else {
                                    R.string.set_refund_type
                                },
                            ),
                        )
                    },
                    enabled = !second.refundType,
                    onClick = {
                        expandedMenu = false
                        onRequestSetRefundType(second.data.id)
                    },
                )
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(
                                id = if (second.reimburseType) {
                                    R.string.reimburse_type
                                } else {
                                    R.string.set_reimburse_type
                                },
                            ),
                        )
                    },
                    enabled = !second.reimburseType,
                    onClick = {
                        expandedMenu = false
                        onRequestSetReimburseType(second.data.id)
                    },
                )
            }
            if (second.data.typeCategory == RecordTypeCategoryEnum.TRANSFER) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = stringResource(
                                id = if (second.creditCardPaymentType) {
                                    R.string.credit_card_payment_type
                                } else {
                                    R.string.set_credit_card_payment_type
                                },
                            ),
                        )
                    },
                    enabled = !second.creditCardPaymentType,
                    onClick = {
                        expandedMenu = false
                        onRequestSetCreditCardPaymentType(second.data.id)
                    },
                )
            }
            DropdownMenuItem(
                text = {
                    Text(text = stringResource(id = R.string.statistic_data))
                },
                onClick = {
                    expandedMenu = false
                    onRequestNaviToTypeStatistics(second.data.id)
                },
            )
        }
    }
}

private val RecordTypeCategoryEnum.text: String
    @Composable get() = stringResource(
        id = when (this) {
            RecordTypeCategoryEnum.EXPENDITURE -> R.string.expend
            RecordTypeCategoryEnum.INCOME -> R.string.income
            RecordTypeCategoryEnum.TRANSFER -> R.string.transfer
        },
    )

/**
 * 编辑记录标题栏
 *
 * @param uiState 界面 UI 状态
 * @param onTabSelected 大类变化回调
 * @param onBackClick 返回点击回调
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MyCategoriesTopBar(
    uiState: MyCategoriesUiState,
    onTabSelected: (RecordTypeCategoryEnum) -> Unit,
    onBackClick: () -> Unit,
) {
    CbTopAppBar(
        onBackClick = onBackClick,
        title = {
            if (uiState is MyCategoriesUiState.Success) {
                val selectedTab = uiState.selectedTab
                CbTabRow(
                    modifier = Modifier.fillMaxSize(),
                    selectedTabIndex = selectedTab.ordinal,
                    containerColor = Color.Unspecified,
                    contentColor = Color.Unspecified,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                        )
                    },
                    divider = {},
                ) {
                    RecordTypeCategoryEnum.entries.forEach { enum ->
                        CbTab(
                            selected = selectedTab == enum,
                            onClick = { onTabSelected(enum) },
                            text = { Text(text = enum.text) },
                            selectedContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                            unselectedContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        )
                    }
                }
            }
        },
    )
}
