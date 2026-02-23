plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}
val baseUrl = "https://default-payment-gateway.com/pay/"
// SDK Version
val sdkVersion = "0.0.1"
android {
    namespace = "com.technonext.tnextpaysdk"
    compileSdk = 36

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
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
        buildConfig = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation("androidx.webkit:webkit:1.15.0")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.10.0")

    implementation("com.google.code.gson:gson:2.12.0")
    implementation(libs.billing)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(android.sourceSets["main"].java.srcDirs)
}

tasks.register<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    from("$buildDir/docs/javadoc")
}