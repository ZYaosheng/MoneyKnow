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
import cn.wj.android.moneyknow.buildlogic.MoneyknowFlavor

plugins {
    alias(conventionLibs.plugins.moneyknow.android.library)
    alias(conventionLibs.plugins.moneyknow.android.library.jacoco)
    alias(conventionLibs.plugins.moneyknow.android.hilt)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "cn.wj.android.moneyknow.core.network"
}

dependencies {

    implementation(projects.core.common)
    implementation(projects.core.model)

    // kotlin 协程
    implementation(libs.kotlinx.coroutines.android)
    // kotlin 序列化
    implementation(libs.kotlinx.serialization.json)

    // OkHttp
    implementation(libs.squareup.okhttp3)

    // Retrofit
    implementation(libs.squareup.retrofit2)
    implementation(libs.jakewharton.retrofit2.converter.kotlin)

    // HTML 解析
    implementation(libs.jsoup)
}