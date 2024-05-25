// buildscript {
//    ext["compose_version"] = "1.5.3"
// }

buildscript {

  // The following section is needed only if pluginMangement is not used in settings.gradle
  repositories { mavenCentral() }

  dependencies { classpath("com.newrelic.agent.android:agent-gradle-plugin:7.0.0") }
}

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  id("com.android.application") version "8.4.0" apply false
  id("com.android.library") version "8.4.0" apply false
  id("org.jetbrains.kotlin.android") version "1.9.10" apply false
  id("io.realm.kotlin") version "1.11.1" apply false
  id("com.google.devtools.ksp") version "1.9.10-1.0.13" apply false
  //    id("com.google.devtools.ksp") version "1.9.0-1.0.13" apply false
  id("org.jetbrains.kotlin.jvm") version "1.9.10" apply false
  id("org.jetbrains.kotlin.plugin.serialization") version "1.9.10"
  //  id("org.jetbrains.kotlin.plugin.parcelize") version "1.9.10" apply true
  //  id("com.google.gms.google-services") version "4.4.0" apply false
  //  id("com.google.firebase.crashlytics") version "2.9.9" apply false
}
