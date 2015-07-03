FactoryJill
===========

## Usage
```gradle
sourceCompatibility = 1.8
dependencies {
    testCompile('com.github.markymarkmcdonald:FactoryJill:2.0.0')
}
```

## Features
Supported:
- Defining reusable factories
- Overriding fields
- Associations: Defining fields as instances of other factories

Not Supported:
- Aliases
- Sequences
- Building a list of factories

Not Planned to be supported:
- Factory Inheritance
- Lazily setting fields (based off of other properties)
- Callbacks (pre-build, post-build)

## Requirements
- Java 8
- A public getter and setter for each field defined or overridden.

## Examples
Defining a factory:
```java
factory("truck", Car.class, ImmutableMap.of(
                "make", "ford",
                "convertible", false,
                "yearsOwned", 5,
                "releaseDate", new Date()
        ));
```

Building an instance from a factory:
```java
Car pickupTruck = build("truck");
```

Sometimes you need to override specific properties:
```java
Car convertible = build("truck", ImmutableMap.of("convertible", true));
```

Building multiple instances from a factoy:
```java
List<Car> pickupTrucks = buildMultiple("truck", 5);
```

## Full API
For now FactoryJill provides a public interface with three methods, `FactoryJill.factory`, `FactoryJill.build`, and `FactoryJill.buildMultiple`.
Check out the test directory for a complete set of usage examples.
