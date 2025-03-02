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

package cn.wj.android.moneyknow.sync.workers

import android.content.Context
import android.os.Environment
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import cn.wj.android.moneyknow.core.common.annotation.MoneyknowDispatchers
import cn.wj.android.moneyknow.core.common.annotation.Dispatcher
import cn.wj.android.moneyknow.core.common.ext.logger
import cn.wj.android.moneyknow.core.data.uitl.AppUpgradeManager
import cn.wj.android.moneyknow.sync.initializers.ApkDownloadWorkName
import cn.wj.android.moneyknow.sync.initializers.syncForegroundInfo
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.time.Duration
import kotlin.coroutines.CoroutineContext

/**
 * 初始化 [CoroutineWorker]，按照用户配置执行不同的任务
 *
 * > [王杰](mailto:15555650921@163.com) 创建于 2023/8/8
 */
@HiltWorker
class ApkDownloadWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val appUpgradeManager: AppUpgradeManager,
    @Dispatcher(MoneyknowDispatchers.IO) private val ioDispatcher: CoroutineContext,
) : CoroutineWorker(appContext, workerParams) {

    private val okhttpClient: OkHttpClient by lazy {
        val timeoutDuration = Duration.ofMinutes(5L)
        OkHttpClient.Builder()
            .callTimeout(timeoutDuration)
            .connectTimeout(timeoutDuration)
            .readTimeout(timeoutDuration)
            .writeTimeout(timeoutDuration)
            .build()
    }

    override suspend fun getForegroundInfo(): ForegroundInfo =
        appContext.syncForegroundInfo()

    override suspend fun doWork(): Result = withContext(ioDispatcher) {
        val apkName =
            inputData.getString(cn.wj.android.moneyknow.core.common.ACTION_APK_NAME).orEmpty()
        val downloadUrl =
            inputData.getString(cn.wj.android.moneyknow.core.common.ACTION_DOWNLOAD_URL).orEmpty()
        logger().d("doWork apkName: $apkName downloadUrl: $downloadUrl")

        val request = Request.Builder().url(downloadUrl).build()

        try {
            okhttpClient.newCall(request).execute().use { response ->
                val downloads = appContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                val saveFile = File(downloads, apkName)
                logger().d("doWork save path: ${saveFile.path}")

                // 文件总大小
                val total = response.body!!.contentLength()

                if (saveFile.exists()) {
                    // 文件已存在
                    FileInputStream(saveFile).use {
                        if (it.available().toLong() == total) {
                            // 且大小相同，已下载
                            logger().d("doWork downloaded")
                            appUpgradeManager.downloadComplete(saveFile)
                            return@withContext Result.success()
                        }
                    }
                }

                val buf = ByteArray(2048)
                var len: Int
                var sum = 0L

                var fos: FileOutputStream? = FileOutputStream(saveFile)
                response.body!!.byteStream().use { inputStream ->
                    while (inputStream.read(buf).also { len = it } != -1) {
                        if (!isActive) {
                            // 已停止
                            logger().d("doWork stopped")
                            appUpgradeManager.downloadStopped()
                            return@withContext Result.failure()
                        }
                        fos?.write(buf, 0, len)
                        sum += len.toLong()
                        val progress = (sum * 1f / total * 100).toInt()
                        appUpgradeManager.updateDownloadProgress(progress)
                    }
                    fos?.flush()
                    // 下载完成
                    logger().d("doWork download finish")
                    appUpgradeManager.downloadComplete(saveFile)
                }
                try {
                    fos?.close()
                    fos = null
                } catch (throwable: Throwable) {
                    logger().e(throwable, "doWork fos close")
                }
            }
            return@withContext Result.success()
        } catch (throwable: Throwable) {
            logger().e(throwable, "doWork")
            appUpgradeManager.downloadFailed()
            return@withContext Result.failure()
        }
    }

    companion object {

        /** 下载地址 */
        private const val ACTION_DOWNLOAD_URL = "action_download_url"

        /** APK 名称 */
        private const val ACTION_APK_NAME = "action_apk_name"

        /** 使用代理任务启动APK下载任务，以支持依赖注入 */
        fun startUpApkDownloadWork(apkName: String, downloadUrl: String) =
            OneTimeWorkRequestBuilder<DelegatingWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setInputData(
                    ApkDownloadWorker::class.delegatedData(
                        Data.Builder()
                            .putString(ACTION_APK_NAME, apkName)
                            .putString(ACTION_DOWNLOAD_URL, downloadUrl)
                            .build(),
                    ),
                )
                .addTag(ApkDownloadWorkName)
                .build()
    }
}
