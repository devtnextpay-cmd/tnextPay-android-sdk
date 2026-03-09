plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
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
    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
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

//afterEvaluate {
//    publishing {
//        publications {
//            create<MavenPublication>("release") {
//                from(components["release"])
//
//                groupId = "com.technonext"
//                artifactId = "tnextpaysdk"
//                version = sdkVersion
//
//                pom {
//                    name.set("TNextPay SDK")
//                    description.set("Android Payment Gateway SDK by Technonext")
////                    url.set("https://github.com/technonext/tnextpaysdk")
//
//                    licenses {
//                        license {
//                            name.set("MIT License")
//                            url.set("https://opensource.org/licenses/MIT")
//                        }
//                    }
//
//                    developers {
//                        developer {
//                            id.set("technonext")
//                            name.set("Technonext Ltd")
//                            email.set("support@technonext.com")
//                        }
//                    }
//
////                    scm {
////                        connection.set("scm:git:git://github.com/technonext/tnextpaysdk.git")
////                        developerConnection.set("scm:git:ssh://github.com/technonext/tnextpaysdk.git")
////                        url.set("https://github.com/technonext/tnextpaysdk")
////                    }
//                }
//            }
//        }
//        repositories {
//            maven {
//                name = "OSSRH"
//                url = uri(
//                    if (sdkVersion.endsWith("SNAPSHOT"))
//                        "https://s01.oss.sonatype.org/content/repositories/snapshots/"
//                    else
//                        "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
//                )
//
//                credentials {
//                    username = project.findProperty("ossrhUsername") as String?
//                    password = project.findProperty("ossrhPassword") as String?
//                }
//            }
//        }
//    }
//}
//signing {
//    sign(publishing.publications)
//}
//tasks.register<Jar>("sourcesJar") {
//    archiveClassifier.set("sources")
//    from(android.sourceSets["main"].java.srcDirs)
//}
//
//tasks.register<Jar>("javadocJar") {
//    archiveClassifier.set("javadoc")
//    from("$buildDir/docs/javadoc")
//}
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                groupId = "com.github.SmueezTN"
                artifactId = "TNestPaySDK"
                version = "1.0.0"
            }
        }
    }
}