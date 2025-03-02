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

@file:Suppress("unused")
@file:JvmName("IntentTools")

package cn.wj.android.moneyknow.core.common.tools

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import cn.wj.android.moneyknow.core.common.ext.ifCondition

/**
 * Intent 跳转相关工具
 *
 * > [王杰](mailto:15555650921@163.com) 创建于 2021/6/17
 */

fun jumpSendEmail(
    email: String,
    subject: String = "",
    text: String = "",
    chooserTitle: CharSequence? = null,
    context: Context,
) {
    val emailStart = "mailto:"
    val fixedEmail =
        Uri.parse(email.ifCondition(!email.startsWith(emailStart)) { "$emailStart$email" })

    val intent = Intent(Intent.ACTION_SENDTO).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        data = fixedEmail
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, chooserTitle))
}

/** 通过 [context] 唤起浏览器应用，打开指定路径 [url] */
fun jumpBrowser(
    url: String,
    chooserTitle: CharSequence? = null,
    context: Context,
) {
    context.startActivity(
        Intent.createChooser(
            Intent().apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                action = Intent.ACTION_VIEW
                data = Uri.parse(url)
            },
            chooserTitle,
        ),
    )
}

/** 通过 [context] 跳转应用详情界面 */
fun jumpAppDetails(context: Context) {
    context.startActivity(
        Intent().apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", context.packageName, null)
        },
    )
}
