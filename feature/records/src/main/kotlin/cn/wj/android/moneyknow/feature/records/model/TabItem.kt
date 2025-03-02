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

package cn.wj.android.moneyknow.feature.records.model

import cn.wj.android.moneyknow.core.model.enums.RecordTypeCategoryEnum

/**
 * 编辑记录界面标题标签数据
 *
 * @param title 标签文本
 * @param type 标签类型 [RecordTypeCategoryEnum]
 *
 * > [王杰](mailto:15555650921@163.com) 创建于 2023/2/17
 */
internal data class TabItem(
    val title: String,
    val type: RecordTypeCategoryEnum,
)
