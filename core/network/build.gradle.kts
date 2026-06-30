plugins {
    alias(libs.plugins.financetracker.android.library)
    alias(libs.plugins.financetracker.hilt)
    alias(libs.plugins.kotlin.serialization)
}


android {
    namespace = "ru.malevichrp.core.network"
}

dependencies {
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp.logging)

    implementation(project(":core:model"))
    implementation(project(":core:datastore"))
}