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
plugins {
    alias(conventionLibs.plugins.moneyknow.android.library.feature)
    alias(conventionLibs.plugins.moneyknow.android.library.compose)
    alias(conventionLibs.plugins.moneyknow.android.library.jacoco)
}

android {
    namespace = "cn.wj.android.moneyknow.feature.books"
}

dependencies {

    // 架构
    implementation(projects.core.model)
    implementation(projects.core.common)
    implementation(projects.core.domain)
    implementation(projects.core.data)

    implementation(libs.androidx.constraintlayout.compose)

    implementation(libs.coil.kt.compose)
}
