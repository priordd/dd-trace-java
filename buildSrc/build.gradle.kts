plugins {
  groovy
  `java-gradle-plugin`
  `kotlin-dsl`
  id("com.diffplug.spotless") version "6.13.0"
}

gradlePlugin {
  plugins {
    create("instrument-plugin") {
      id = "instrument"
      implementationClass = "InstrumentPlugin"
    }
    create("muzzle-plugin") {
      id = "muzzle"
      implementationClass = "MuzzlePlugin"
    }
    create("call-site-instrumentation-plugin") {
      id = "call-site-instrumentation"
      implementationClass = "datadog.gradle.plugin.CallSiteInstrumentationPlugin"
    }
  }
}

apply {
  from("$rootDir/../gradle/repositories.gradle")
}

dependencies {
  implementation(gradleApi())
  implementation(localGroovy())

  implementation("net.bytebuddy", "byte-buddy-gradle-plugin", "1.15.11")

  implementation("org.eclipse.aether", "aether-connector-basic", "1.1.0")
  implementation("org.eclipse.aether", "aether-transport-http", "1.1.0")
  implementation("org.apache.maven", "maven-aether-provider", "3.3.9")

  implementation("com.google.guava", "guava", "20.0")
  implementation("org.ow2.asm", "asm", "9.8")
  implementation("org.ow2.asm", "asm-tree", "9.8")

  testImplementation("org.spockframework", "spock-core", "2.2-groovy-3.0")
  testImplementation("org.codehaus.groovy", "groovy-all", "3.0.17")
}

tasks.compileKotlin {
  dependsOn(":call-site-instrumentation-plugin:build")
}

tasks.test {
  useJUnitPlatform()
  enabled = project.hasProperty("runBuildSrcTests")
}
