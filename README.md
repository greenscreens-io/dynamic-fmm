# Dynamic FFM

![GitHub release (latest by date)](https://img.shields.io/github/v/release/greenscreens-io/dynamic-fmm?style=plastic)
![GitHub](https://img.shields.io/github/license/greenscreens-io/dynamic-fmm?style=plastic)

[![Compile](https://github.com/greenscreens-io/dynamic-fmm/actions/workflows/maven.yml/badge.svg)](https://github.com/greenscreens-io/dynamic-fmm/actions/workflows/maven.yml)
![CodeQL](https://github.com/greenscreens-io/dynamic-fmm/workflows/CodeQL/badge.svg)

Java 21 based JEP 454 Foreign Function and Memory API  - Alternative to JNI and JNA

To see how to use it, check Test.java.

### Important!!!
 To use WKHTMLTOX demo, download library from https://wkhtmltopdf.org/downloads.html and extract wkhtmltox.dll or wkhtmltox.so into project libs folder.
 
### Build

1. Clone repository to local drive
2. Use ```maven build clean install```

### Quickstart

Define Java Interface with methods representing foreign functions.
All parameters and return types are of a type MemorySegment (aka pointer) except for primitive types.

Latest change support primitive type arrays also.

Example - C/C++ pseudo code

```
void function process(int percentage)
*byte[] compress(int level, *byte[] data, *process callback) {
 ... do compression and return pointer to compressed data ...
}
```

Example - Java mapping options

```
@External(name="compression.dll")
interface ForeignCompressor (

  // Option 1 - with more low level control
  MemorySegment compress(int level, MemorySegment data, MemorySegment callback)

  // Option 2 - with automatic data conversion; callback must be bind to an instance
  byte[] compress(int level, byte[] data, MethodHandle callback)

  // Option 3 - with automatic data conversion
  byte[] compress(int level, byte[] data, @Callback(name="process") ICallback callback)

)

interface ICallback {

    @Callback(name="process")
    void callback(int percentage)
}

```

Finally, create a callable instance.

```
final ForeignCompressor instance = ExternalFactory.create(ForeignCompressor.class);

byte[] compressed = instance.compress(6, "Quick brown fox jumps over the lazy dog".getBytes(), null);
```

...or use closable instance

```
final Instance<ForeignCompressor> instance = ExternalFactory.createClosable(ForeignCompressor.class);

byte[] compressed = instance.get().compress(6, "Quick brown fox jumps over the lazy dog".getBytes(), null);

instance.get().close();
```

NOTE: @Callback can be named and unnamed. When interface contains only a single Callback, named callbacks are not required.
When iterface contains multiple calllback methods, use named callback to map porper method to foreign function callback argument.

&copy; Green Screens Ltd. 2016 - 2024
