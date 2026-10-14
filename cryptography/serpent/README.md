# Serpent Block Cipher in Java

[![serpent](https://github.com/mycplus/java-examples/actions/workflows/serpent.yml/badge.svg)](https://github.com/mycplus/java-examples/actions/workflows/serpent.yml)

Companion code for [Serpent Cipher Explained](https://www.mycplus.com/computer-science/algorithms/serpent-cipher/) on MYCPLUS: a dependency-free Java implementation of the Serpent block cipher (128-bit blocks, keys up to 256 bits), checked against 1,043 published test vectors.

> For learning and testing. This code has not been reviewed for side channels. To protect real data, use a maintained library and an authenticated mode such as AES-GCM (built into the JDK) or ChaCha20-Poly1305.

| File | What it is |
| --- | --- |
| `src/Serpent.java` | Key schedule, `encrypt` and `decrypt` of one 16-byte block; the inverse S-boxes are computed from the forward ones |
| `src/SerpentDemo.java` | Encrypts one block with a 256-bit key and checks two known vectors |
| `tests/SerpentTest.java` | Runs every vector in `tests/vectors/serpent_nessie.txt` in both directions and checks key and block lengths |
| `bench/SerpentSpeed.java` | Throughput in ECB mode over 16 MiB |

The test vectors are the NESSIE set as distributed with [Botan](https://github.com/randombit/botan) (`src/tests/data/block/serpent.vec`).

## Build and run

Java 17 or later; no build tool needed.

```sh
javac -d build src/*.java tests/*.java bench/*.java
java -ea -cp build SerpentTest tests/vectors/serpent_nessie.txt
java -cp build SerpentDemo
java -cp build SerpentSpeed
```

## What the build checks

- Compiles with `javac --release 17 -Xlint:all -Werror` on Java 17, 21 and 25, on Linux and Windows.
- All 1,043 vectors (128-, 192- and 256-bit keys) encrypt and decrypt correctly; keys of 0 and 33 bytes and a 15-byte block throw `IllegalArgumentException`.
- The demo prints exactly `tests/expected/SerpentDemo.txt`.
