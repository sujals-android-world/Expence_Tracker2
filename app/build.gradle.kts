plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)

}

android {

    namespace = "com.example.expencetracker2"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.expencetracker2"
        minSdk = 29
        targetSdk = 37
        versionCode = 38
        versionName = "37.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.remote.creation.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.datastore.core)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    // --- JETPACK COMPOSE EXTENSIONS ---
    // Architecture integration for Hilt ViewModels in Compose
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    // --- DI & COMPILER (DAGGER HILT) ---
    // Replaces KAPT with KSP for faster compilation in Compose architecture
    implementation(libs.dagger.hilt.android)
    ksp(libs.dagger.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose) // Bridge for scoping ViewModels to Nav Graphs

    // --- LOCAL DATABASE (ROOM) ---
    // Room 2.6+ with Native KSP support for reactive data streams
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.common) // Added to fix ByteArrayWrapper issue

    // --- BACKGROUND PROCESSING (WORKMANAGER) ---
    // WorkManager with Kotlin Coroutines extension for background jobs
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work) // Integrates Hilt Worker injection
    ksp(libs.androidx.hilt.compiler)

    // --- NETWORK & PARSING ---
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.code.gson:gson:2.14.0")

    implementation("androidx.compose.material:material-icons-core:1.7.8")

    implementation("androidx.compose.material:material-icons-extended:1.7.8")

    val navVersion = "2.9.8" // Use the latest stable version

    // Jetpack Compose Navigation integration
    implementation(libs.androidx.navigation.compose)

    //Supabase
    implementation(libs.bundles.supabase)
    implementation(platform("io.github.jan-tennert.supabase:bom:3.7.0"))

    implementation("androidx.datastore:datastore-preferences:1.2.1")

}