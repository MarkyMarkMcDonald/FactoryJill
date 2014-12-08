FactoryJill
===========

## Documentation
Check out the test directory.

## Installation
You can always check this repo out, run `gradle build` and include the jar on your classpath for tests.
For now, the releases folder contains a maven repo that you can link to.

Gradle setup:
```gradle
repositories {
    maven { url "https://github.com/MarkyMarkMcDonald/FactoryJill/raw/master/releases" }
}

dependencies {
    testCompile('org.MarkyMarkMcDonald:FactoryJill:1.0.0')
}
```
