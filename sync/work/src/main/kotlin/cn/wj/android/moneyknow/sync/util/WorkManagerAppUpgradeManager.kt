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

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.FileProvider
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import cn.wj.android.moneyknow.core.common.ApplicationInfo
import cn.wj.android.moneyknow.core.common.SERVICE_ACTION_RETRY_DOWNLOAD
import cn.wj.android.moneyknow.core.common.ext.logger
import cn.wj.android.moneyknow.core.common.ext.string
import cn.wj.android.moneyknow.core.data.uitl.AppUpgradeManager
import cn.wj.android.moneyknow.core.model.entity.UpgradeInfoEntity
import cn.wj.android.moneyknow.core.model.enums.AppUpgradeStateEnum
import cn.wj.android.moneyknow.sync.R
import cn.wj.android.moneyknow.sync.initializers.ApkDownloadWorkName
import cn.wj.android.moneyknow.sync.initializers.NoticeNotificationId
import cn.wj.android.moneyknow.sync.initializers.UpgradeNotificationId
import cn.wj.android.moneyknow.sync.initializers.noticeNotificationBuilder
import cn.wj.android.moneyknow.sync.initializers.upgradeNotificationBuilder
import cn.wj.android.moneyknow.sync.workers.ApkDownloadWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkManagerAppUpgradeManager @Inject constructor(
    @ApplicationContext private val context: Context,
) : AppUpgradeManager {

    private var upgradeInfo: UpgradeInfoEntity? = null

    private val _upgradeState = MutableStateFlow(AppUpgradeStateEnum.NONE)

    override val upgradeState: Flow<AppUpgradeStateEnum> = _upgradeState

    override suspend fun startDownload(info: UpgradeInfoEntity) {
        logger().i("startDownload(info = <$info>)")
        upgradeInfo = info
        _upgradeState.tryEmit(AppUpgradeStateEnum.DOWNLOADING)
        showNotification()
        WorkManager.getInstance(context).apply {
            enqueueUniqueWork(
                ApkDownloadWorkName,
                ExistingWorkPolicy.KEEP,
                ApkDownloadWorker.startUpApkDownloadWork(
                    apkName = info.apkName,
                    downloadUrl = info.downloadUrl,
                ),
            )
        }
    }

    override suspend fun updateDownloadProgress(progress: Int) {
        logger().i("updateDownloadProgress(progress = <$progress>)")
        updateProgress(progress)
    }

    override suspend fun downloadComplete(apkFile: File) {
        logger().i("downloadComplete(apkFile = <$apkFile>)")
        _upgradeState.tryEmit(AppUpgradeStateEnum.DOWNLOAD_SUCCESS)
        hideNotification()
        install(apkFile)
    }

    override suspend fun downloadFailed() {
        logger().i("downloadFailed()")
        _upgradeState.tryEmit(AppUpgradeStateEnum.DOWNLOAD_FAILED)
        hideNotification()
        val retryIntent = Intent(SERVICE_ACTION_RETRY_DOWNLOAD).apply {
            `package` = ApplicationInfo.applicationId
        }
        val retryPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PendingIntent.getForegroundService(
                context,
                0,
                retryIntent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        } else {
            PendingIntent.getService(
                context,
                0,
                retryIntent,
                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
        }

        nm.notify(
            NoticeNotificationId,
            context.noticeNotificationBuilder()
                .setContentTitle(R.string.update_error_title.string(context))
                .addAction(
                    0,
                    R.string.retry.string(context),
                    retryPendingIntent,
                )
                .build(),
        )
        delay(200L)
        _upgradeState.tryEmit(AppUpgradeStateEnum.NONE)
    }

    override suspend fun downloadStopped() {
        logger().i("downloadStopped()")
        _upgradeState.tryEmit(AppUpgradeStateEnum.NONE)
        hideNotification()
    }

    override suspend fun cancelDownload() {
        logger().i("cancelDownload()")
        _upgradeState.tryEmit(AppUpgradeStateEnum.NONE)
        hideNotification()
        WorkManager.getInstance(context).cancelAllWorkByTag(ApkDownloadWorkName)
    }

    override suspend fun retry() {
        logger().i("retry()")
        upgradeInfo?.let { startDownload(it) }
    }

    private val nm: NotificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private fun hideNotification() {
        progressTemp = -1
        nm.cancel(NoticeNotificationId)
        nm.cancel(UpgradeNotificationId)
    }

    private fun showNotification() {
        hideNotification()
        updateProgress(0)
    }

    private var progressTemp = -1

    private fun updateProgress(progress: Int) {
        if (progress == progressTemp) {
            return
        }
        logger().d("updateProgress $progress")
        progressTemp = progress
        nm.notify(
            UpgradeNotificationId,
            context.upgradeNotificationBuilder()
                .setContentText(R.string.update_progress_format.string(context).format(progress))
                .setProgress(100, progress, false)
                .build(),
        )
    }

    private suspend fun install(file: File) {
        this@WorkManagerAppUpgradeManager.logger().d("install file: ${file.path}")
        try {
            context.startActivity(
                Intent(Intent.ACTION_VIEW).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${ApplicationInfo.applicationId}.FileProvider",
                        file,
                    )
                    setDataAndType(uri, "application/vnd.android.package-archive")
                },
            )
        } catch (throwable: Throwable) {
            this@WorkManagerAppUpgradeManager.logger().e(throwable, "install")
            _upgradeState.tryEmit(AppUpgradeStateEnum.INSTALL_FAILED)
        } finally {
            delay(200L)
            _upgradeState.tryEmit(AppUpgradeStateEnum.NONE)
        }
    }
}
