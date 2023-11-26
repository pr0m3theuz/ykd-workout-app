plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  //    id("kotlin-kapt")
  id("io.realm.kotlin")
  id("com.google.devtools.ksp")
  id("org.jetbrains.kotlin.plugin.serialization")
//  id("com.google.gms.google-services")
//  id("com.google.firebase.crashlytics")
  id("newrelic")
}

tasks.withType<Test> { useJUnitPlatform() }

android {
  namespace = "com.example.myapplication"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.example.myapplication"
    minSdk = 26
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    vectorDrawables { useSupportLibrary = true }

    /*javaCompileOptions {
        annotationProcessorOptions {
            compilerArgumentProviders(
                    new RoomSchemaArgProvider(new File(projectDir, "schemas"))
            )
        }
    }*/
  }

  buildTypes {
    getByName("release") {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions {
    jvmTarget = "1.8"

    freeCompilerArgs = listOf("-Xjvm-default=all")
  }
  kotlin { jvmToolchain(17) }

  buildFeatures { compose = true }
  composeOptions { kotlinCompilerExtensionVersion = "1.5.3" }
  packaging.resources {
    // Multiple dependency bring these files in. Exclude them to enable
    // our test APK to build (has no effect on our AARs)
    excludes += "/META-INF/AL2.0"
    excludes += "/META-INF/LGPL2.1"
  }
}

dependencies {
  implementation("androidx.core:core-ktx:1.12.0")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
  implementation("androidx.activity:activity-compose:1.8.1")
  implementation("androidx.compose.ui:ui:1.6.0-beta01")
  implementation("androidx.compose.ui:ui-tooling-preview:1.5.4")
  implementation("androidx.compose.material3:material3:1.2.0-alpha11")
  implementation("androidx.compose.material:material:1.6.0-beta01")
  implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
  implementation("com.google.android.material:material:1.10.0")
  implementation("androidx.compose.material:material-icons-extended:1.6.0-beta01")
  implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
  implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
  implementation("com.google.dagger:hilt-android:2.48.1")
  implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")
  //    testImplementation "junit:junit:4.13.2"
  //    testImplementation "org.robolectric:robolectric:4.10.3"

  // Core library
  androidTestImplementation("androidx.test:core:1.5.0")
  testImplementation("androidx.test.ext:junit-ktx:1.1.5")
  testImplementation("androidx.test:core-ktx:1.5.0")
  testImplementation("org.robolectric:robolectric:4.11.1")

  // AndroidJUnitRunner and JUnit Rules
  androidTestImplementation("androidx.test:runner:1.5.2")
  androidTestImplementation("androidx.test:rules:1.5.0")

  // Assertions
  androidTestImplementation("androidx.test.ext:junit:1.1.5")
  androidTestImplementation("androidx.test.ext:truth:1.5.0")

  // Espresso dependencies
  val espressoVersion = "3.5.1"
  androidTestImplementation("androidx.test.espresso:espresso-core:$espressoVersion")
  androidTestImplementation("androidx.test.espresso:espresso-contrib:$espressoVersion")
  androidTestImplementation("androidx.test.espresso:espresso-intents:$espressoVersion")
  androidTestImplementation("androidx.test.espresso:espresso-accessibility:$espressoVersion")
  androidTestImplementation("androidx.test.espresso:espresso-web:$espressoVersion")
  androidTestImplementation("androidx.test.espresso.idling:idling-concurrent:$espressoVersion")

  // The following Espresso dependency can be either "implementation",
  // or "androidTestImplementation", depending on whether you want the
  // dependency to appear on your APK’"s compile classpath or the test APK
  // classpath.
  androidTestImplementation("androidx.test.espresso:espresso-idling-resource:$espressoVersion")

  testImplementation("io.mockk:mockk:1.13.8")

  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.1.5")
  testImplementation("org.junit.jupiter:junit-jupiter")
  testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.1")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.1")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
  androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.4")
  debugImplementation("androidx.compose.ui:ui-tooling:1.5.4")
  debugImplementation("androidx.compose.ui:ui-test-manifest:1.5.4")

  // Navigation
  implementation("androidx.navigation:navigation-compose:2.7.5")

  // realm
  //    implementation ("io.realm.kotlin:library-base:1.11.1") // Add to use local realm (no sync)
  implementation("io.realm.kotlin:library-sync:1.12.0") // Add to use device sync)
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
  // Add to use coroutines with the SDK

  // Serialization
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.1")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:1.6.1")

  //    Koin
  implementation("io.insert-koin:koin-core:3.5.0")
  implementation("io.insert-koin:koin-android:3.5.0")
  //    implementation("io.insert-koin:koin-annotations:3.4.3")
  //    ksp("io.insert-koin:koin-ksp-compiler:3.4.3")
  implementation("io.insert-koin:koin-androidx-navigation:3.5.0")
  implementation("io.insert-koin:koin-androidx-compose:3.5.0")

  testImplementation("io.insert-koin:koin-test:3.5.0")
  testImplementation("io.insert-koin:koin-test-junit5:3.5.0")

  // Import the Firebase BoM
//  implementation(platform("com.google.firebase:firebase-bom:32.6.0"))

  // When using the BoM, you don't specify versions in Firebase library dependencies

  // Add the dependency for the Firebase SDK for Google Analytics
//  implementation("com.google.firebase:firebase-analytics")

  // TODO: Add the dependencies for any other Firebase products you want to use
  // See https://firebase.google.com/docs/android/setup#available-libraries
  // For example, add the dependencies for Firebase Authentication and Cloud Firestore
//  implementation("com.google.firebase:firebase-auth")
//  implementation("com.google.firebase:firebase-firestore")
//  implementation("com.google.firebase:firebase-crashlytics")
//  implementation("com.google.firebase:firebase-crashlytics-ndk")

  implementation("com.newrelic.agent.android:android-agent:6.10.0")
}

/*kapt {
     correctErrorTypes true
 }*/

/*
class RoomSchemaArgProvider implements CommandLineArgumentProvider {

    @InputDirectory
    @PathSensitive(PathSensitivity.RELATIVE)
    File schemaDir

    RoomSchemaArgProvider(File schemaDir) {
        this.schemaDir = schemaDir
    }

    @Override
    Iterable<String> asArguments() {
        // Note: If you"re using KSP, change the line below to return
        // ["room.schemaLocation=${schemaDir.path}".toString()].
        return ["-Aroom.schemaLocation=${schemaDir.path}".toString()]
    }
}*/
