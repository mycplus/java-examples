# Towers of Hanoi in Java

[![towers-of-hanoi](https://github.com/mycplus/java-examples/actions/workflows/towers-of-hanoi.yml/badge.svg)](https://github.com/mycplus/java-examples/actions/workflows/towers-of-hanoi.yml)

Companion code for [Towers of Hanoi: Recursive Algorithm With Code in C, C++, Java, Python and C#](https://www.mycplus.com/computer-science/algorithms/towers-of-hanoi/) on MYCPLUS.

| File | What it is |
| --- | --- |
| `src/TowersOfHanoi.java` | The article's listing: prints the moves for 3 disks |
| `src/Hanoi.java` | `solve` and `solveIterative` with a `Consumer<Move>`, `moveAt` for any single move (k read as unsigned 64-bit), and `moveCount` as a `BigInteger` |
| `src/HanoiAnimation.java` | A Swing animation (`JPanel` + `javax.swing.Timer`); `--snapshot FILE [K]` writes a PNG without a display |
| `pitfalls/ShiftOverflow.java` | `(1 << n) - 1`: 0 moves for 32 disks |
| `pitfalls/BaseCaseOne.java` | A base case of `n == 1`: `StackOverflowError` for 0 disks |
| `tests/HanoiTest.java` | Dependency-free tests: simulation, solver agreement, 64-disk `moveAt` against a reference, the listing's output, and the animation model |

Requires Java 17 or later. The classes are in the default package so the article's listing compiles as shown.

## Build and test

```sh
javac --release 17 -Xlint:all -Werror -d build src/*.java tests/*.java pitfalls/*.java
java -ea -cp build HanoiTest
java -cp build TowersOfHanoi
java -cp build HanoiAnimation 5
```

## What the build checks

- Compiles on Temurin 17, 21 and 25, on Linux and Windows, with `-Xlint:all -Werror`.
- The recursive and iterative solvers produce identical, legal, complete sequences for 0 to 18 disks, and `moveAt` agrees with an independent reference on 2,000 moves of the 64-disk puzzle.
- `TowersOfHanoi` prints exactly the output shown in the article.
- The animation's model solves 6 disks and renders a frame headlessly.
- The two pitfall programs behave the way the article describes.
