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

import cn.wj.android.moneyknow.buildlogic.ProjectSetting
import cn.wj.android.moneyknow.buildlogic.libs
import com.android.build.gradle.api.AndroidBasePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class HiltConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(ProjectSetting.Plugin.PLUGIN_GOOGLE_KSP)
            dependencies {
                "ksp"(libs.findLibrary("google-hilt-compiler").get())
                "implementation"(libs.findLibrary("google-hilt-core").get())
            }

            /** Add support for Android modules, based on [AndroidBasePlugin] */
            pluginManager.withPlugin(ProjectSetting.Plugin.PLUGIN_ANDROID_BASE) {
                pluginManager.apply(ProjectSetting.Plugin.PLUGIN_GOOGLE_HILT)
                dependencies {
                    "implementation"(libs.findLibrary("google-hilt-android").get())
                }
            }
        }
    }
}
