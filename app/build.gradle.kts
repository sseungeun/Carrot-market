plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.carrot"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.carrot"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

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
}

dependencies {
    dependencies {
        // Retrofit & Glide
        implementation ("com.squareup.retrofit2:retrofit:2.9.0")
        implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
        implementation("com.github.bumptech.glide:glide:4.12.0")
        annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")

        // Google 위치 서비스
        implementation ("com.google.android.gms:play-services-location:18.0.0")

        // Firebase
        implementation(platform("com.google.firebase:firebase-bom:33.15.0"))
        implementation("com.google.firebase:firebase-analytics")
        implementation("com.google.firebase:firebase-database")   // ✅ 메시지 저장
        implementation("com.google.firebase:firebase-auth")       // ✅ 사용자 ID 구분

        // Jetpack / 테스트
        implementation(libs.appcompat)
        implementation(libs.material)
        implementation(libs.activity)
        implementation(libs.constraintlayout)
        testImplementation(libs.junit)
        androidTestImplementation(libs.ext.junit)
        androidTestImplementation(libs.espresso.core)
    }

}
