# Selection Sort in Java

[![selection-sort](https://github.com/mycplus/java-examples/actions/workflows/selection-sort.yml/badge.svg)](https://github.com/mycplus/java-examples/actions/workflows/selection-sort.yml)

Companion code for [Selection Sort in C, C++, Java, Python and C#](https://www.mycplus.com/computer-science/algorithms/selection-sort/) on MYCPLUS.

| File | What it is |
| --- | --- |
| `src/SelectionSort.java` | The article's listing: `selectionSort(int[])` and a `main` that sorts the article's array |
| `tests/SelectionSortTest.java` | Dependency-free tests against `Arrays.sort`, and the program's expected output |

Requires Java 17 or later. The class is in the default package so the article's listing compiles as shown.

## Build and test

```sh
javac --release 17 -Xlint:all -Werror -d build src/*.java tests/*.java
java -ea -cp build SelectionSortTest
java -cp build SelectionSort
```

## What the build checks

- Compiles on Temurin 17, 21 and 25, on Linux and Windows, with `-Xlint:all -Werror`.
- `selectionSort` agrees with `Arrays.sort` on 20,000 random arrays of 0 to 64 elements, including `Integer.MIN_VALUE`, `Integer.MAX_VALUE` and heavy duplication, and on 20,000 random ints, 2,000 reversed values, an empty array and one element.
- `SelectionSort` prints exactly the output shown in the article.
