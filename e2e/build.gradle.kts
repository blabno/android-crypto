plugins {
    id("com.android.library")
}

android {
    namespace = "com.labnoratory.android_crypto.e2e"
    compileSdk = 34

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}


dependencies {
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.hamcrest:hamcrest:2.2")
    testImplementation("com.github.appium:java-client:v8.6.0")
    testImplementation("commons-codec:commons-codec:1.16.0")
}

afterEvaluate {
    tasks.named("testReleaseUnitTest") {
        dependsOn(":sample-app:build")
    }
    tasks.named("testDebugUnitTest") {
        dependsOn(":sample-app:build")
    }
}
