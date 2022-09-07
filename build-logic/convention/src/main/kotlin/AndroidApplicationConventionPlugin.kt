import cn.wj.android.cashbook.buildlogic.*
import cn.wj.android.cashbook.buildlogic.configureKotlinAndroid
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Kotlin Android Application 插件
 *
 * > [王杰](mailto:15555650921@163.com) 创建于 2022/9/1
 */
@Suppress("unused")
class AndroidApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply(ApplicationSetting.Plugin.PLUGIN_ANDROID_APPLICATION)
                apply(ApplicationSetting.Plugin.PLUGIN_KOTLIN_ANDROID)
            }

            extensions.configure<BaseAppModuleExtension> {
                configureKotlinAndroid(this)
                configureSigningConfigs(this)
                configureFlavors(this)
                configureBuildTypes(this)
            }
        }
    }

}