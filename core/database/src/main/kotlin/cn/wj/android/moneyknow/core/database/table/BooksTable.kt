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

package cn.wj.android.moneyknow.core.database.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 账本数据表格
 *
 * @param id 账本 id 主键自增长
 * @param name 账本名
 * @param description 描述
 * @param bgUri 背景图片 uri
 * @param modifyTime 修改时间
 *
 * > [王杰](mailto:15555650921@163.com) 创建于 2021/5/15
 */
@Entity(tableName = TABLE_BOOKS)
data class BooksTable(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = TABLE_BOOKS_ID)
    val id: Long?,
    @ColumnInfo(name = TABLE_BOOKS_NAME) val name: String,
    @ColumnInfo(name = TABLE_BOOKS_DESCRIPTION) val description: String,
    @ColumnInfo(name = TABLE_BOOKS_BG_URI) val bgUri: String,
    @ColumnInfo(name = TABLE_BOOKS_MODIFY_TIME) val modifyTime: Long,
)
