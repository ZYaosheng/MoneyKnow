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

package cn.wj.android.moneyknow.feature.records.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ElevatedSuggestionChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import cn.wj.android.moneyknow.core.design.component.CbIconButton
import cn.wj.android.moneyknow.core.design.component.CbModalBottomSheet
import cn.wj.android.moneyknow.core.design.component.CbTextButton
import cn.wj.android.moneyknow.core.design.component.Empty
import cn.wj.android.moneyknow.core.design.component.Footer
import cn.wj.android.moneyknow.core.design.icon.CbIcons
import cn.wj.android.moneyknow.core.model.entity.RecordViewsEntity
import cn.wj.android.moneyknow.core.ui.R
import cn.wj.android.moneyknow.feature.records.view.RecordDetailsSheet
import cn.wj.android.moneyknow.feature.records.viewmodel.SearchViewModel

@Composable
internal fun SearchRoute(
    onRequestNaviToEditRecord: (Long) -> Unit,
    onRequestNaviToAssetInfo: (Long) -> Unit,
    onRequestPopBackStack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val searchHistoryList by viewModel.searchHistoryListData.collectAsStateWithLifecycle()
    val recordList = viewModel.recordListData.collectAsLazyPagingItems()

    SearchScreen(
        viewRecord = viewModel.viewRecordData,
        onRequestShowRecordDetailSheet = viewModel::showRecordDetailSheet,
        onRequestDismissBottomSheet = viewModel::dismissRecordDetailSheet,
        searchHistoryList = searchHistoryList,
        onRequestClearSearchHistory = viewModel::clearSearchHistory,
        recordList = recordList,
        onKeywordChange = viewModel::onKeywordChange,
        onRequestNaviToEditRecord = onRequestNaviToEditRecord,
        onRequestNaviToAssetInfo = onRequestNaviToAssetInfo,
        onRequestPopBackStack = onRequestPopBackStack,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun SearchScreen(
    viewRecord: RecordViewsEntity?,
    onRequestShowRecordDetailSheet: (RecordViewsEntity) -> Unit,
    onRequestDismissBottomSheet: () -> Unit,
    searchHistoryList: List<String>,
    onRequestClearSearchHistory: () -> Unit,
    recordList: LazyPagingItems<RecordViewsEntity>,
    onKeywordChange: (String) -> Unit,
    onRequestNaviToEditRecord: (Long) -> Unit,
    onRequestNaviToAssetInfo: (Long) -> Unit,
    onRequestPopBackStack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (null != viewRecord) {
        CbModalBottomSheet(
            onDismissRequest = onRequestDismissBottomSheet,
            sheetState = rememberModalBottomSheetState(
                confirmValueChange = {
                    if (it == SheetValue.Hidden) {
                        onRequestDismissBottomSheet()
                    }
                    true
                },
            ),
            content = {
                RecordDetailsSheet(
                    recordData = viewRecord,
                    onRequestNaviToEditRecord = onRequestNaviToEditRecord,
                    onRequestNaviToAssetInfo = onRequestNaviToAssetInfo,
                    onRequestDismissSheet = onRequestDismissBottomSheet,
                )
            },
        )
    }

    var query by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = modifier.fillMaxSize()) {
        val onExpandedChange: (Boolean) -> Unit = { expanded = it }
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    modifier = Modifier.fillMaxWidth(),
                    query = query,
                    onQueryChange = { query = it },
                    onSearch = {
                        onKeywordChange(it)
                        expanded = false
                    },
                    expanded = expanded,
                    onExpandedChange = onExpandedChange,
                    placeholder = { Text(text = stringResource(id = R.string.record_search_hint)) },
                    leadingIcon = {
                        CbIconButton(
                            onClick = {
                                if (expanded) {
                                    expanded = false
                                } else {
                                    onRequestPopBackStack()
                                }
                            },
                        ) {
                            Icon(
                                imageVector = CbIcons.ArrowBack,
                                contentDescription = null,
                            )
                        }
                    },
                    trailingIcon = {
                        if (expanded && query.isNotBlank()) {
                            CbIconButton(onClick = { query = "" }) {
                                Icon(imageVector = CbIcons.Cancel, contentDescription = null)
                            }
                        }
                    },
                )
            },
            expanded = expanded,
            onExpandedChange = onExpandedChange,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            content = {
                if (searchHistoryList.isEmpty()) {
                    Empty(
                        hintText = stringResource(id = R.string.record_search_no_history),
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )
                } else {
                    CbTextButton(onClick = onRequestClearSearchHistory) {
                        Text(text = stringResource(id = R.string.clear))
                    }
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        searchHistoryList.forEach {
                            ElevatedSuggestionChip(
                                onClick = { query = it },
                                label = { Text(text = it) },
                            )
                        }
                    }
                }
            },
        )
        LazyColumn(
            modifier = Modifier.padding(top = 8.dp),
            content = {
                if (recordList.itemCount <= 0) {
                    item {
                        Empty(
                            hintText = stringResource(id = R.string.record_search_no_data_hint),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                } else {
                    items(count = recordList.itemCount) { index ->
                        recordList[index]?.let { item ->
                            RecordListItem(
                                item = item,
                                modifier = Modifier.clickable {
                                    onRequestShowRecordDetailSheet(item)
                                },
                            )
                        }
                    }
                    item {
                        Footer(hintText = stringResource(id = R.string.footer_hint_default))
                    }
                }
            },
        )
    }
}
