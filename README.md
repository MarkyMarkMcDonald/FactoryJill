FactoryJill
===========

## Usage
```gradle
sourceCompatibility = 1.8
dependencies {
    testCompile('com.github.markymarkmcdonald:FactoryJill:1.0.3')
}
```

## Features
Supported:
- Defining reusable factories
- Overriding fields
- Lazily setting fields
- Associations: Defining fields as instances of other factories

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
                "releaseDate", new Date()
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

## Full API
For now FactoryJill provides a public interface with two methods, `FactoryJill.factory` and `FactoryJill.build`.
Check out the test directory for a complete set of usage examples.
