buildscript {

//   The following section is needed only if pluginManagement is not used in settings.gradle
  repositories {
//    maven(url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev"))
    mavenCentral()
    google()
  }

  dependencies { classpath("com.newrelic.agent.android:agent-gradle-plugin:7.6.8") }
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  id("com.android.application") version "8.13.0" apply false
  id("com.android.library") version "8.13.0" apply false
  id("org.jetbrains.kotlin.android") version "2.2.10" apply false
//  id("io.realm.kotlin") version "2.3.0" apply false
  id("com.google.devtools.ksp") version "2.1.21-2.0.1" apply false
  //    id("com.google.devtools.ksp") version "1.9.0-1.0.13" apply false
  id("org.jetbrains.kotlin.jvm") version "2.2.10" apply false
  id("org.jetbrains.kotlin.plugin.serialization") version "2.2.10" apply false
  //  id("org.jetbrains.kotlin.plugin.parcelize") version "1.9.10" apply true
  id("com.google.gms.google-services") version "4.4.3" apply false
  id("com.google.firebase.crashlytics") version "3.0.6" apply false
//  id("org.pkl-lang") version ("0.26.0")
//  id("org.jetbrains.compose").apply(false)
  id("org.jetbrains.kotlin.plugin.compose") version "2.2.10"
}
