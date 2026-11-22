# Pattern Programs in Java

The Java versions of the patterns in the article
[Pattern Programs in C: Pyramids, Diamonds and More](https://www.mycplus.com/programming/c/pattern-programs-in-c/).
Every program prints exactly what the C version in
[c-examples/loops/patterns](https://github.com/mycplus/c-examples/tree/main/loops/patterns) prints,
and the tests compare against the same expected files.

| File | What it does |
| --- | --- |
| `src/Pyramid.java` | Asks for a number of rows (1-50) and prints a centered pyramid |
| `src/Patterns.java` | 16 patterns: squares, triangles, pyramids, diamonds, hourglass, number, Floyd, Pascal and alphabet triangles |
| `src/PatternsDemo.java` | `java PatternsDemo NAME ROWS` or `java PatternsDemo list` |
| `tests/PatternsTest.java` | Compares with the C output, checks the shape rules at every size and Pascal's entries up to row 66 |

## Build and run

Java 17 or later:

```sh
javac --release 17 -d build src/*.java
java -cp build Pyramid
java -cp build PatternsDemo diamond 5
```

## Tests

```sh
sh run_all.sh          # Linux, macOS, WSL
.\run_all.ps1          # Windows PowerShell
```

`pascalEntry` divides before it multiplies, so it is exact up to row 66 (the last row
whose middle entry fits in a signed 64-bit `long`) and returns -1 instead of a wrapped value after that.
