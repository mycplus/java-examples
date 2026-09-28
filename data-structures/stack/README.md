# Stack in Java

[![stack](https://github.com/mycplus/java-examples/actions/workflows/stack.yml/badge.svg)](https://github.com/mycplus/java-examples/actions/workflows/stack.yml)

Companion code for [Stack Implementation in C, C++, Java, Python and C#](https://www.mycplus.com/computer-science/data-structures/stack-implementation/) on MYCPLUS.

| File | What it is |
| --- | --- |
| `src/ArrayStack.java` | A generic growable array-backed stack that clears popped slots |
| `src/LinkedStack.java` | A generic linked-list stack |
| `src/StackDemo.java` | The article's demo, with a bracket checker on `ArrayDeque` |
| `pitfalls/Loitering.java` | Shows a popped object staying reachable when the slot is not cleared. **Do not copy `LoiteringStack`.** |
| `tests/StackTest.java` | Dependency-free tests, and the demo's expected output |

Requires Java 17 or later. The classes are in the default package so the article's listings compile as shown.

## Build and test

```sh
javac --release 17 -Xlint:all -Werror -d build src/*.java tests/*.java pitfalls/*.java
java -ea -cp build StackTest
java -cp build StackDemo
```

## What the build checks

- Compiles on Temurin 17, 21 and 25, on Linux and Windows, with `-Xlint:all -Werror`.
- Both stacks agree with `java.util.ArrayDeque` over 100,000 random operations.
- `pop()` and `peek()` on an empty stack throw `NoSuchElementException`.
- `ArrayStack` stores `null` as an ordinary value and grows to 1,000,000 elements.
- The bracket checker handles crossed pairs, a lone closer and 100,000 levels of nesting.
- `StackDemo` prints exactly the output shown in the article.
- After `System.gc()`, an object popped from `ArrayStack` has been reclaimed and one popped from `LoiteringStack` has not. `System.gc()` is a request, not a guarantee, so this step depends on the JVM honouring it; it did in all ten runs made while writing the article, across four collectors.
