plugins {
    alias(libs.plugins.financetracker.android.library)
    alias(libs.plugins.financetracker.hilt)
}


android {
    namespace = "ru.malevichrp.core.datastore"
}
dependencies {
    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.datastore.preferences)

    implementation(project(":core:model"))
}
