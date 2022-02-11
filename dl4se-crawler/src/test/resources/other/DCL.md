## Explain the concept of a class loader. How could one use a class loader to load and execute bytecode that is generated on the fly.

The class loader is itself a class used for dynamically loading classes from compiled class files into a Java program.
<!-- TODO -->

---

## Explain when class loading happens

Class loading is lazily executed at runtime by virtue of a class loader.

---

## Explain how to load multiple identically named classes

Due to the fact that each class type is a tuple of the class name and the defining class loader, it is possible to have multiple namespaces in a Java program (each corresponding to its own dedicated class loader).

---

## Explain how to unload a class

To unload a class in a Java program, the variables containing:

- The `ClassLoader` instance
- The loaded `Class` metadata instance
- Instance of the class itself

Need to be set to `null`.
After that, the garbage collector can safely unload the class.