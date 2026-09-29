# Prime numbers in Java

[![prime-numbers](https://github.com/mycplus/java-examples/actions/workflows/prime-numbers.yml/badge.svg)](https://github.com/mycplus/java-examples/actions/workflows/prime-numbers.yml)

Companion code for [Prime Number Programs in C, C++, Java, Python, C#, PHP and JavaScript](https://www.mycplus.com/computer-science/algorithms/prime-number-program/) on MYCPLUS: a primality test by trial division up to the square root, and the Sieve of Eratosthenes. The same program exists in seven languages and every version prints the same output.

| File | What it is |
| --- | --- |
| `src/Primes.java` | `isPrime(long)`, `sieve(int)` and the demo |
| `tests/PrimesTest.java` | Dependency-free tests |

Requires Java 17 or later.

## Build and test

```sh
javac --release 17 -Xlint:all -Werror -d build src/*.java tests/*.java
java -ea -cp build PrimesTest
java -cp build Primes
```

## What the build checks

- Compiles on Temurin 17, 21 and 25, on Linux and Windows, with `-Xlint:all -Werror`.
- Trial division agrees with the sieve on every number below 200,000.
- Known primes (including 2147483647, 1000000007 and 999999999989) and composites (including negatives, 0, 1, squares of primes and Carmichael numbers 561 and 1105) are classified correctly.
- The sieve reproduces the published prime counts: 168 below 1,000, 78,498 below 1,000,000 and 664,579 below 10,000,000.
- The demo prints exactly `tests/expected/primes.txt`, the same file in all seven repositories.
