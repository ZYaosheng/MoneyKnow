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
    alias(conventionLibs.plugins.cashbook.android.library)
    alias(conventionLibs.plugins.cashbook.android.library.jacoco)
    alias(conventionLibs.plugins.cashbook.android.hilt)
}

android {
    namespace = "cn.wj.android.cashbook.core.data"

    libraryVariants.all {
        preBuildProvider.get().doFirst {
            val intoDir = File(projectDir, "/src/main/assets")
            println("> Task :${project.name}:beforePreBuild copy .md files from $rootDir into $intoDir")
            delete(intoDir)
            copy {
                from(rootDir)
                into(intoDir)
                include("PRIVACY_POLICY.md", "CHANGELOG.md")
            }
        }
    }
}

dependencies {

    implementation(projects.core.common)
    implementation(projects.core.model)
    implementation(projects.core.database)
    implementation(projects.core.datastore)
    implementation(projects.core.network)
    implementation(projects.core.ui)

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.documentfile)
    implementation(libs.androidx.room.ktx)
}