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

package cn.wj.android.moneyknow.core.network.util

import androidx.annotation.WorkerThread
import cn.wj.android.moneyknow.core.model.model.BackupModel
import java.io.File
import java.io.InputStream

/**
 * 使用 OkHttp 实现的 WebDAV 操作
 *
 * > [王杰](mailto:15555650921@163.com) 创建于 2023/7/24
 */
interface WebDAVHandler {

    fun setCredentials(
        account: String,
        password: String,
    )

    @WorkerThread
    fun exists(url: String): Boolean

    @WorkerThread
    fun createDirectory(url: String): Boolean

    @WorkerThread
    fun put(url: String, dataStream: InputStream, contentType: String): Boolean

    @WorkerThread
    fun put(url: String, file: File, contentType: String): Boolean

    @WorkerThread
    suspend fun list(
        url: String,
        propsList: List<String> = emptyList(),
    ): List<BackupModel>

    @WorkerThread
    suspend fun get(url: String): InputStream?
}
