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

package cn.wj.android.moneyknow.feature.records.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import cn.wj.android.moneyknow.core.common.DEFAULT_PAGE_SIZE
import cn.wj.android.moneyknow.core.common.ext.logger
import cn.wj.android.moneyknow.core.common.model.recordDataVersion
import cn.wj.android.moneyknow.core.model.entity.RecordViewsEntity
import cn.wj.android.moneyknow.domain.usecase.GetAssetRecordViewsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

/**
 * 资产信息页记录数据 ViewModel
 *
 * @param getAssetRecordViewsUseCase 获取对应资产记录数据用例
 *
 * > [王杰](mailto:15555650921@163.com) 创建于 2023/7/3
 */
@HiltViewModel
class AssetInfoContentViewModel @Inject constructor(
    getAssetRecordViewsUseCase: GetAssetRecordViewsUseCase,
) : ViewModel() {

    /** 资产 id 数据 */
    private val _assetIdData = MutableStateFlow(-1L)

    /** 记录列表数据 */
    val recordList = combine(_assetIdData, recordDataVersion) { assetId, _ ->
        assetId
    }
        .flatMapLatest {
            Pager(
                config = PagingConfig(
                    pageSize = DEFAULT_PAGE_SIZE,
                    initialLoadSize = DEFAULT_PAGE_SIZE,
                ),
                pagingSourceFactory = {
                    AssetRecordPagingSource(it, getAssetRecordViewsUseCase)
                },
            ).flow
        }
        .cachedIn(viewModelScope)

    /** 更新资产 id */
    fun updateAssetId(id: Long) {
        _assetIdData.tryEmit(id)
    }
}

/**
 * Paging 数据仓库
 */
private class AssetRecordPagingSource(
    private val assetId: Long,
    private val getAssetRecordViewsUseCase: GetAssetRecordViewsUseCase,
) : PagingSource<Int, RecordViewsEntity>() {
    override fun getRefreshKey(state: PagingState<Int, RecordViewsEntity>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RecordViewsEntity> {
        return runCatching {
            val page = params.key ?: 0
            val pageSize = params.loadSize
            val items = getAssetRecordViewsUseCase(assetId, page, pageSize)
            val prevKey = if (page > 0) page - 1 else null
            val nextKey = if (items.isNotEmpty()) page + 1 else null
            LoadResult.Page(items, prevKey, nextKey)
        }.getOrElse { throwable ->
            logger().e(throwable, "load()")
            LoadResult.Error(throwable)
        }
    }
}
