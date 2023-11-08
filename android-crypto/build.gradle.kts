plugins {
    id("com.android.library")
    id("maven-publish")
}

android {
    namespace = "com.labnoratory.andorid_crypto"
    compileSdk = 33

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    publishing {
        singleVariant("release") {
        }
    }
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.labnoratory.andorid-crypto"
            artifactId = project.name
            version = project.properties["artifactVersion"] as? String ?: "unknown-version"
            afterEvaluate {
                from(components["release"])
            }
        }
    }
}

dependencies {

    api("androidx.biometric:biometric:1.1.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("org.bouncycastle:bcpkix-jdk15to18:1.76")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("com.github.javafaker:javafaker:1.0.2")
    androidTestImplementation("org.mockito:mockito-android:5.7.0")
}
