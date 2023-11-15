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
    testImplementation("org.testng:testng:7.8.0")
    testImplementation("org.hamcrest:hamcrest:2.2")
    testImplementation("com.github.appium:java-client:v8.6.0")
    testImplementation("com.github.javafaker:javafaker:1.0.2")
    testImplementation("commons-codec:commons-codec:1.16.0")
}

afterEvaluate {
    tasks.named<Test>("testReleaseUnitTest") {
        useTestNG {
            suites("src/test/resources/testng.xml")
        }
        dependsOn(":sample-app:build")
    }
    tasks.named<Test>("testDebugUnitTest") {
        useTestNG {
            suites("src/test/resources/testng.xml")
        }
        dependsOn(":sample-app:build")
    }
}
