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

package cn.wj.android.moneyknow.core.model.entity

import cn.wj.android.moneyknow.core.model.enums.RecordTypeCategoryEnum

/**
 * 记录类型数据实体
 *
 * @param id 类型 id
 * @param parentId 二级分类父类型 id，一级类型默认为 -1L
 * @param name 类型名称
 * @param iconResName 类型图标资源 id
 * @param sort 排序关键字
 * @param child 一级分类的二级分类列表
 * @param selected 是否是选中状态
 * @param shapeType 形状类型，二级分类使用 -1：第一条 0：中间部分 1：最后一条
 *
 * > [王杰](mailto:15555650921@163.com) 创建于 2023/2/23
 */
data class RecordTypeEntity(
    val id: Long,
    val parentId: Long,
    val name: String,
    val iconResName: String,
    val typeCategory: RecordTypeCategoryEnum,
    val sort: Int,
    val child: List<RecordTypeEntity>,
    val selected: Boolean,
    val shapeType: Int,
    val needRelated: Boolean,
)

/** 固定类型 - 设置 */
val RECORD_TYPE_SETTINGS: RecordTypeEntity
    get() = RecordTypeEntity(
        id = -1001L,
        parentId = -1L,
        name = "",
        iconResName = "",
        sort = 0,
        child = emptyList(),
        selected = true,
        typeCategory = RecordTypeCategoryEnum.EXPENDITURE,
        shapeType = 0,
        needRelated = false,
    )
