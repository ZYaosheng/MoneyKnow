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

package cn.wj.android.moneyknow.core.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import cn.wj.android.moneyknow.core.common.ext.logger
import cn.wj.android.moneyknow.core.database.table.TABLE_TAG
import org.intellij.lang.annotations.Language

/**
 * 数据库升级 1 -> 2
 * - 新增 db_tag 表
 *
 * > [王杰](mailto:15555650921@163.com) 创建于 2023/8/1
 */
object Migration1To2 : Migration(1, 2) {

    /** 创建标签表，版本 2 */
    @Language("SQL")
    private const val SQL_CREATE_TABLE_TAG_2 = """
        CREATE TABLE IF NOT EXISTS `$TABLE_TAG` 
        (
            `id` INTEGER PRIMARY KEY AUTOINCREMENT, 
            `name` TEXT NOT NULL
        )
    """

    override fun migrate(db: SupportSQLiteDatabase) {
        logger().i("migrate(db)")
        with(db) {
            // 创建标签表
            execSQL(SQL_CREATE_TABLE_TAG_2)
        }
    }
}
