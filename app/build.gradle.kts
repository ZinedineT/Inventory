plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.devzine.inventory"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.devzine.inventory"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
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
}

dependencies {
    implementation(libs.itextg)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.gridlayout)
    implementation (libs.glide)
    annotationProcessor (libs.compiler)

    // Room (Librería de persistencia)
    implementation(libs.room.runtime)
    implementation(libs.room.common)
    annotationProcessor(libs.room.compiler)
    implementation (libs.room.rxjava3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}