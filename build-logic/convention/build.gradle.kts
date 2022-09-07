plugins {
    `kotlin-dsl`
}

group = "cn.wj.android.cashbook.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.squareup.javapoet)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "cashbook.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "cashbook.android.library"
            implementationClass = "AndroidLibraryConventionPlugin"
        }
    }
}
