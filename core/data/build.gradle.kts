plugins {
    alias(libs.plugins.financetracker.android.library)
    alias(libs.plugins.financetracker.hilt)
}
android {
    namespace = "ru.malevichrp.core.data"
}
dependencies {
    implementation(libs.kotlinx.coroutines.core)

    implementation(project(":core:network"))
    implementation(project(":core:model"))
    implementation(project(":core:datastore"))
}