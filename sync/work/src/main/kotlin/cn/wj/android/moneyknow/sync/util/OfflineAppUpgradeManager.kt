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

package cn.wj.android.moneyknow.sync.util

import cn.wj.android.moneyknow.core.common.ext.logger
import cn.wj.android.moneyknow.core.data.uitl.AppUpgradeManager
import cn.wj.android.moneyknow.core.model.entity.UpgradeInfoEntity
import cn.wj.android.moneyknow.core.model.enums.AppUpgradeStateEnum
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OfflineAppUpgradeManager @Inject constructor() : AppUpgradeManager {

    override val upgradeState: Flow<AppUpgradeStateEnum> = MutableStateFlow(AppUpgradeStateEnum.NONE)

    override suspend fun startDownload(info: UpgradeInfoEntity) {
        // empty block
        logger().i("startDownload(info = <$info>)")
    }

    override suspend fun updateDownloadProgress(progress: Int) {
        // empty block
        logger().i("updateDownloadProgress(progress = <$progress>)")
    }

    override suspend fun downloadComplete(apkFile: File) {
        // empty block
        logger().i("downloadComplete(apkFile = <$apkFile>)")
    }

    override suspend fun downloadFailed() {
        // empty block
        logger().i("downloadFailed()")
    }

    override suspend fun downloadStopped() {
        // empty block
        logger().i("downloadStopped()")
    }

    override suspend fun cancelDownload() {
        // empty block
        logger().i("cancelDownload()")
    }

    override suspend fun retry() {
        // empty block
        logger().i("retry()")
    }
}
