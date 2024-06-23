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
  id("kotlin-parcelize")
  //  id("org.jetbrains.kotlin.plugin.kotlin-parcelize")
  id("org.pkl-lang")
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
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  kotlinOptions {
    jvmTarget = "11"

    freeCompilerArgs =
      listOf(
        "-Xjvm-default=all",
        "-Xjsr305=strict", // Strict interpretation of nullability annotations in jqwik API
        "-Xemit-jvm-type-annotations", // Enable annotations on type variables
      )
  }
  kotlin { jvmToolchain(18) }

  tasks.withType<Test> {
    useJUnitPlatform { includeEngines("jqwik") }
    include("**/*Properties.class")
    include("**/*Test.class")
    include("**/*Tests.class")
    include("**/*Examples.class")
  }

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
  implementation("androidx.core:core-ktx:1.13.1")
  implementation("androidx.activity:activity-compose:1.9.0")

  implementation("androidx.compose.material3.adaptive:adaptive:1.0.0-beta03")
  implementation("androidx.compose.material3.adaptive:adaptive-layout:1.0.0-beta03")
  implementation("androidx.compose.material3.adaptive:adaptive-navigation:1.0.0-beta03")
  implementation("androidx.compose.material3:material3-adaptive-navigation-suite:1.0.0-alpha07")


  val composeVersion = "1.7.0-beta02"
  implementation("androidx.compose.ui:ui:$composeVersion")
  implementation("androidx.compose.ui:ui-tooling-preview:$composeVersion")
  implementation("androidx.compose.material:material:$composeVersion")
  implementation("androidx.compose.material:material-icons-extended:$composeVersion")

  implementation("com.google.android.material:material:1.12.0")
  implementation("androidx.compose.material3:material3:1.2.1")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.1")
  implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.1")
  implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.1")
  implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
  implementation("com.google.dagger:hilt-android:2.51.1")
  //    testImplementation "junit:junit:4.13.2"
  //    testImplementation "org.robolectric:robolectric:4.10.3"

  // Core library
  val testing = "1.5.0"
  androidTestImplementation("androidx.test:core:$testing")
  testImplementation("androidx.test:core-ktx:$testing")
  val testingJunit = "1.1.5"
  testImplementation("androidx.test.ext:junit-ktx:$testingJunit")
  testImplementation("org.robolectric:robolectric:4.12.2")

  // AndroidJUnitRunner and JUnit Rules
  androidTestImplementation("androidx.test:runner:1.5.2")
  androidTestImplementation("androidx.test:rules:$testing")

  // Assertions
  androidTestImplementation("androidx.test.ext:junit:$testingJunit")
  androidTestImplementation("androidx.test.ext:truth:$testing")

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

  testImplementation("io.mockk:mockk:1.13.11")

  testImplementation("junit:junit:4.13.2")

  val junitJupiter = "5.10.2"
  testImplementation(platform("org.junit:junit-bom:$junitJupiter"))
  testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiter")
  testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiter")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiter")
  androidTestImplementation("androidx.compose.ui:ui-test-junit4:$composeVersion")
  debugImplementation("androidx.compose.ui:ui-tooling:$composeVersion")
  debugImplementation("androidx.compose.ui:ui-test-manifest:$composeVersion")

  val jqwik = "1.8.5"
  testImplementation("net.jqwik:jqwik:$jqwik")
  testImplementation("net.jqwik:jqwik-kotlin:$jqwik")
  // Navigation
  implementation("androidx.navigation:navigation-compose:2.7.7")

  // realm
  //    implementation ("io.realm.kotlin:library-base:1.11.1") // Add to use local realm (no sync)
  implementation("io.realm.kotlin:library-sync:1.16.0") // Add to use device sync)
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
  // Add to use coroutines with the SDK

  // Serialization
  val serialization = "1.6.3"
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serialization")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-protobuf:$serialization")

  //    Koin
  val koin = "3.5.6"
  implementation("io.insert-koin:koin-core:$koin")
  implementation("io.insert-koin:koin-android:$koin")
  //    implementation("io.insert-koin:koin-annotations:3.4.3")
  //    ksp("io.insert-koin:koin-ksp-compiler:3.4.3")
  implementation("io.insert-koin:koin-androidx-navigation:$koin")
  implementation("io.insert-koin:koin-androidx-compose:$koin")
  implementation("io.insert-koin:koin-androidx-compose-navigation:$koin")

  testImplementation("io.insert-koin:koin-test:$koin")
  testImplementation("io.insert-koin:koin-test-junit5:$koin")

  implementation("com.newrelic.agent.android:android-agent:7.3.1")
  implementation("org.pkl-lang:pkl-config-kotlin:0.25.3")
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
