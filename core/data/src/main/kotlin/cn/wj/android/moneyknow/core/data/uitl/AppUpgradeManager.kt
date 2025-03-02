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

package cn.wj.android.moneyknow.core.data.uitl

import cn.wj.android.moneyknow.core.model.entity.UpgradeInfoEntity
import cn.wj.android.moneyknow.core.model.enums.AppUpgradeStateEnum
import kotlinx.coroutines.flow.Flow
import java.io.File

/**
 * 应用升级管理
 *
 * > [王杰](mailto:15555650921@163.com) 创建于 2023/10/20
 */
interface AppUpgradeManager {

    val upgradeState: Flow<AppUpgradeStateEnum>

    suspend fun startDownload(info: UpgradeInfoEntity)

    suspend fun updateDownloadProgress(progress: Int)

    suspend fun downloadComplete(apkFile: File)

    suspend fun downloadFailed()

    suspend fun downloadStopped()

    suspend fun cancelDownload()

    suspend fun retry()
}
