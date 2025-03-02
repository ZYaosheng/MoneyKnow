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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import cn.wj.android.moneyknow.core.design.component.Empty
import cn.wj.android.moneyknow.core.design.component.Footer
import cn.wj.android.moneyknow.core.model.entity.RecordViewsEntity
import cn.wj.android.moneyknow.core.ui.R
import cn.wj.android.moneyknow.feature.records.viewmodel.AssetInfoContentViewModel

/**
 * 资产信息界面记录列表
 *
 * @param assetId 资产 id
 * @param topContent 列表头布局
 * @param onRecordItemClick 记录列表 item 点击回调
 */
@Composable
internal fun AssetInfoContentRoute(
    assetId: Long,
    topContent: @Composable () -> Unit,
    onRecordItemClick: (RecordViewsEntity) -> Unit,
    viewModel: AssetInfoContentViewModel = hiltViewModel<AssetInfoContentViewModel>().apply {
        updateAssetId(assetId)
    },
) {
    val recordList = viewModel.recordList.collectAsLazyPagingItems()

    AssetInfoContentScreen(
        topContent = topContent,
        recordList = recordList,
        onRecordItemClick = onRecordItemClick,
    )
}

/**
 * 资产信息界面记录列表
 *
 * @param topContent 列表头布局
 * @param recordList 记录列表数据
 * @param onRecordItemClick 记录列表 item 点击回调
 */
@Composable
internal fun AssetInfoContentScreen(
    topContent: @Composable () -> Unit,
    recordList: LazyPagingItems<RecordViewsEntity>,
    onRecordItemClick: (RecordViewsEntity) -> Unit,
) {
    LazyColumn(
        content = {
            item {
                topContent.invoke()
            }

            if (recordList.itemCount <= 0) {
                item {
                    Empty(
                        hintText = stringResource(id = R.string.asset_no_record_data_hint),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            } else {
                items(count = recordList.itemCount) { index ->
                    recordList[index]?.let { item ->
                        RecordListItem(
                            item = item,
                            modifier = Modifier.clickable {
                                onRecordItemClick(item)
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
