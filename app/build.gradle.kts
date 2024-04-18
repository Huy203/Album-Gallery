plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.albumgallery"
    compileSdk = 34

    buildFeatures {
        compose = true
    }

    defaultConfig {
        applicationId = "com.example.albumgallery"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    androidResources {
        generateLocaleConfig = true
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

    buildFeatures {
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.9"
    }
    kotlinOptions {
        jvmTarget = "19"
    }
    sourceSets {
        getByName("main").java.srcDirs("src/main/java")
        getByName("main").res.srcDirs(
            "src/main/res",
            "src/main/res/drawable",
            "src/main/res/drawable-v24",
            "src/main/res/layout/activity/user",
            "src/main/res/layout/activity/albums",
            "src/main/res/layout/activity/album_detail",
            "src/main/res/layout/activity/images",
            "src/main/res/layout/activity/image_detail",
            "src/main/res/layout/activity/images",
            "src/main/res/layout/activity/auth",
            "src/main/res/layout/activity",
            "src/main/res/layout/fragment",
            "src/main/res/menu",
            "src/main/res/mipmap-anydpi-v26",
            "src/main/res/mipmap-hdpi",
            "src/main/res/mipmap-mdpi",
            "src/main/res/mipmap-xhdpi",
            "src/main/res/mipmap-xxhdpi",
            "src/main/res/mipmap-xxxhdpi",
            "src/main/res/values",
            "src/main/res/values-night",
            "src/main/res/xml"
        )
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("com.google.android.flexbox:flexbox:3.0.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.animation:animation-core-android:1.6.4")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.recyclerview:recyclerview")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation(platform("com.google.firebase:firebase-bom:32.7.1"))
    implementation("com.google.firebase:firebase-analytics")
//    don't specify the version when using the BoM
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-storage")
//    Database
    implementation("com.google.firebase:firebase-database")
//    Firestore
    implementation("com.google.firebase:firebase-firestore")
//    Google Play services location APIs
    implementation("com.google.android.gms:play-services-location:21.1.0")
//    Google Play services Places SDK
    implementation("com.google.android.gms:play-services-places:17.0.0")
//    Google Play services Auth SDK
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    implementation("com.google.android.gms:play-services-vision:20.1.3")
//    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
//    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
//    androidTestImplementation(platform("androidx.compose:compose-bom:2023.08.00"))
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation("com.github.bumptech.glide:glide:4.16.0")
    // Image Cropper
    implementation("com.theartofdev.edmodo:android-image-cropper:2.8.0")
    // QR Code Scanner
    implementation("com.google.zxing:core:3.4.1")
    implementation("com.github.denzcoskun:ImageSlideshow:0.1.0")
}
