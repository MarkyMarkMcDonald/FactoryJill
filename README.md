FactoryJill
===========

## Features
Supported:
- Defining reusable factories
- Overriding fields
- Lazily setting fields
- Defining fields as instances of other factories (associations)

Not Supported:
- Inheritance
- Aliases
- Sequences
- Building a list of factories
- Callbacks (pre-build, post-build)

## Requirements
- Java 8 (for lazily setting attributes)
- A public getter and setter for each field defined or overridden.

## Examples
Defining a factory

```java
factory("truck", Car.class, ImmutableMap.of(
                "make", "ford",
                "convertible", false,
                "yearsOwned", 5,
                "year", new Date()
        ));
```

Building an instance from a factory
```java
Car pickupTruck = build("truck");
assert pickupTruck.getMake().equals("ford");
```

Sometimes you need to override specific properties
```java
Car convertible = build("truck", ImmutableMap.of("convertible", true));
assert convertible.getConvertible().equals(true);
```

## Full Documentation
For now the test directory is a good place to start

## Usage
Here's a sample build.gradle file:
```gradle
apply plugin: 'java'

repositories {
    mavenCentral()
}

sourceCompatibility = 1.8

dependencies {
    testCompile('com.github.markymarkmcdonald:FactoryJill:1.0.3')
}
```
