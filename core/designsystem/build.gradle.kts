plugins {
    alias(libs.plugins.financetracker.android.library)
    alias(libs.plugins.kotlin.compose)
}


android {
    namespace = "ru.malevichrp.core.designsystem"

    buildFeatures {
        compose = true
    }
}
dependencies {
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)

}
