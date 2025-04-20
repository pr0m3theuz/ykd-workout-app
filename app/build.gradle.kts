plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("kotlin-android")
  //    id("kotlin-kapt")
//  id("io.realm.kotlin")
  id("com.google.devtools.ksp")
  id("org.jetbrains.kotlin.plugin.serialization")
  id("com.google.gms.google-services")
  id("com.google.firebase.crashlytics")
  id("newrelic")
  id("kotlin-parcelize")
  id("org.jetbrains.kotlin.plugin.compose")
  //  id("org.jetbrains.kotlin.plugin.kotlin-parcelize")
//  id("org.pkl-lang")

  //  id("org.jetbrains.compose")
}

tasks.withType<Test> { useJUnitPlatform() }

android {
  namespace = "com.example.myapplication"
  compileSdk = 35

  defaultConfig {
    applicationId = "com.example.myapplication"
    minSdk = 26
    targetSdk = 35
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
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions {
    jvmTarget = "17"

    freeCompilerArgs =
      listOf(
        "-Xjvm-default=all",
        "-Xjsr305=strict", // Strict interpretation of nullability annotations in jqwik API
        "-Xemit-jvm-type-annotations", // Enable annotations on type variables
      )
  }
  kotlin { jvmToolchain(17) }

  tasks.withType<Test> {
    useJUnitPlatform { includeEngines("jqwik") }
    include("**/*Properties.class")
    include("**/*Test.class")
    include("**/*Tests.class")
    include("**/*Examples.class")
  }

//  buildFeatures { compose = true }
//  composeOptions { kotlinCompilerExtensionVersion = "1.5.3" }
  packaging.resources {
    // Multiple dependency bring these files in. Exclude them to enable
    // our test APK to build (has no effect on our AARs)
    excludes += "/META-INF/AL2.0"
    excludes += "/META-INF/LGPL2.1"
    excludes += "META-INF/LICENSE.md"
    excludes += "META-INF/LICENSE-notice.md"
    excludes += "META-INF/native-image/native-image.properties"
    excludes += "META-INF/native-image/reflect-config.json"
  }
}

composeCompiler {
  reportsDestination = layout.buildDirectory.dir("compose_compiler")
  stabilityConfigurationFile = rootProject.layout.projectDirectory.file("stability_config.conf")
//  stabilityConfigurationFiles.addAll(rootProject.layout.projectDirectory.file("stability_config.conf"))
}

dependencies {
  implementation("androidx.core:core-ktx:1.15.0")
  implementation("androidx.activity:activity-compose:1.10.1")

  implementation("androidx.compose.material3.adaptive:adaptive:1.0.0")
  implementation("androidx.compose.material3.adaptive:adaptive-layout:1.0.0")
  implementation("androidx.compose.material3.adaptive:adaptive-navigation:1.0.0")
  implementation("androidx.compose.material3:material3-adaptive-navigation-suite:1.3.1")
  implementation("com.google.firebase:firebase-perf-ktx:21.0.5")
  androidTestImplementation("org.junit.jupiter:junit-jupiter:5.11.3")

  val composeVersion = "1.7.8"
  implementation("androidx.compose.ui:ui:$composeVersion")
  implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
  implementation("androidx.compose.material:material:$composeVersion")
  implementation("androidx.compose.material:material-icons-extended:$composeVersion")
  implementation("androidx.compose.ui:ui-text-google-fonts:$composeVersion")

  implementation("com.google.android.material:material:1.12.0")
  implementation("androidx.compose.material3:material3:1.3.1")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
  implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
  implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
  implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
  implementation("com.google.dagger:hilt-android:2.53.1")
  //    testImplementation "junit:junit:4.13.2"
  //    testImplementation "org.robolectric:robolectric:4.10.3"

  // Core library
  val testing = "1.6.1"
  androidTestImplementation("androidx.test:core:$testing")
  testImplementation("androidx.test:core-ktx:$testing")
  val testingJunit = "1.2.1"
  testImplementation("androidx.test.ext:junit-ktx:$testingJunit")
  testImplementation("org.robolectric:robolectric:4.14.1")

  // AndroidJUnitRunner and JUnit Rules
  androidTestImplementation("androidx.test:runner:1.6.2")
  androidTestImplementation("androidx.test:rules:$testing")

  // Assertions
  androidTestImplementation("androidx.test.ext:junit:$testingJunit")
  androidTestImplementation("androidx.test.ext:truth:$testing")

  // Espresso dependencies
  val espressoVersion = "3.6.1"
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

  testImplementation("io.mockk:mockk:1.13.13")

  testImplementation("junit:junit:4.13.2")

  val junitJupiter = "5.11.3"
  testImplementation(platform("org.junit:junit-bom:$junitJupiter"))
  testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiter")
  testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiter")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiter")
  androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")
  debugImplementation("androidx.compose.ui:ui-tooling:$composeVersion")
  debugImplementation("androidx.compose.ui:ui-test-manifest:$composeVersion")

  testImplementation("org.assertj:assertj-core:3.27.2")

  val jqwik = "1.9.2"
  testImplementation("net.jqwik:jqwik:$jqwik")
  testImplementation("net.jqwik:jqwik-kotlin:$jqwik")
  // Navigation
  implementation("androidx.navigation:navigation-compose:2.8.9")


  // realm
  // implementation ("io.realm.kotlin:library-base:1.11.1") // Add to use local realm (no sync)
//  implementation("io.realm.kotlin:library-sync:2.3.0") // Add to use device sync)
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

//  implementation("org.mongodb:mongodb-driver-kotlin-coroutine:5.3.0")
  implementation("org.mongodb:bson-kotlinx:5.3.0")
//  implementation("live.ditto:ditto:4.8.1")
  // Add to use coroutines with the SDK

  // Serialization
  val serialization = "1.7.3"
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$serialization")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serialization")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:$serialization")

    //    Koin
  val koin = "4.0.0"
    implementation("io.insert-koin:koin-core:$koin")
  implementation("io.insert-koin:koin-android:$koin")
  //    implementation("io.insert-koin:koin-annotations:3.4.3")
  //    ksp("io.insert-koin:koin-ksp-compiler:3.4.3")
  implementation("io.insert-koin:koin-androidx-navigation:$koin")
  implementation("io.insert-koin:koin-androidx-compose:$koin")
  implementation("io.insert-koin:koin-androidx-compose-navigation:$koin")

  testImplementation("io.insert-koin:koin-test:$koin")
  testImplementation("io.insert-koin:koin-test-junit5:$koin")

  // New Relic Crash Analytics
  implementation("com.newrelic.agent.android:android-agent:7.6.5")

  // Apple pkl
//  implementation("org.pkl-lang:pkl-config-kotlin:0.26.0")

  // Firebase
  // Import the Firebase BoM (see: https://firebase.google.com/docs/android/learn-more#bom)
  implementation(platform("com.google.firebase:firebase-bom:33.12.0"))

  // Firebase Crashlytics
  implementation("com.google.firebase:firebase-crashlytics")

  // For an optimal experience using Crashlytics, add the Firebase SDK
  // for Google Analytics. This is recommended, but not required.
  implementation("com.google.firebase:firebase-analytics")

  implementation("io.coil-kt:coil-compose:2.7.0")

  // For Jetpack Compose.
  implementation("com.patrykandpatrick.vico:compose:2.0.0-beta.3")

  // For `compose`. Creates a `ChartStyle` based on an M3 Material Theme.
  implementation("com.patrykandpatrick.vico:compose-m3:2.0.0-beta.3")

  // Houses the core logic for charts and other elements. Included in all other modules.
  implementation("com.patrykandpatrick.vico:core:2.0.0-beta.3")

  // couchbase lite
  implementation("com.couchbase.lite:couchbase-lite-android-ktx:3.2.1")
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
