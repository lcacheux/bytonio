# Bytonio

Bytonio (said Byte on IO) is a library and a KSP processor intended to ease the serialization and
deserialization of Kotlin objects into ByteArray, to be used in protocols that use binary data.
The goal is to be purely compatible with Kotlin Multiplatform, without using any platform specific
library.

## How to use

### Add KSP processor to your Kotlin module

Those examples are for a Kotlin Multiplatform module.

Add the KSP plugin:
```kotlin
plugins {
    id("com.google.devtools.ksp")
}
```

Add the core library as a dependency:
```kotlin
    sourceSets {
        commonMain {
            dependencies {
                api("net.cacheux.bytonio:bytonio-core:<version>")
                ...
            }
        }
        ...
    }
```

Add the KSP processor for platforms you want to have their own code generated:
```kotlin
dependencies {
    add("kspCommonMainMetadata", "net.cacheux.bytonio:bytonio-processor:<version>")
    add("kspJvm", "net.cacheux.bytonio:bytonio-processor:<version>")
    add("kspAndroid", "net.cacheux.bytonio:bytonio-processor:<version>")
}
```

For a JVM or Android library, you can add both dependencies like this:
```kotlin
dependencies {
    api("net.cacheux.bytonio:bytonio-core:<version>")
    ksp("net.cacheux.bytonio:bytonio-processor:<version>")
}
```

### Processor options

* bytonio.packageName : Change the package where the processor generate code (default is `bytonio`) 
* bytonio.dataSizeFormat : Change the default format used to store size of arrays and objects. Allowed
  values are `int`, `short` and `byte`. Default is `int`.
* bytonio.byteOrder : Change the byte order for int and short serialization. Allowed values are `littleEndian`
  and `bigEndian`. Default is `bigEndian`.

Example:
```kotlin
ksp {
    arg("bytonio.packageName", "net.cacheux.generated")
    arg("bytonio.dataSizeFormat", "short")
    arg("bytonio.byteOrder", "littleEndian")
}
```

### Use annotations in the code

Classes annotated with ```@DataObject``` will have a serializer and a deserializer class created, as
well as extension methods ```getBinarySize()``` and ```toByteArray()```, if these are not already
existing for the class.

These classes must have a constructor with one or more arguments with type supported by the
processor : Int, Short, Byte, ByteArray, or another data type which have the ```@DataObject```
annotation.

You can have ignored arguments of any type, annotated with ```@IgnoreEncoding```, but they must have
a default value that will be used during deserialization.

Numeric arguments can be encoded using a different type than the one used in the class, by using
annotations ```@EncodeAsByte```, ```@EncodeAsShort``` and ```@EncodeAsInt```. 

### Custom serializers and deserializers

A class annotated with ```@DataObject``` can also have custom serializers and deserializers that will
be used instead of the one generated. This is also a way to have different types for constructor
arguments. Use ```@Serializer``` and ```@Deserializer``` annotations to specify them.
