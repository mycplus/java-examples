# MD5, SHA-1 and SHA-256 in Java

[![hash-functions](https://github.com/mycplus/java-examples/actions/workflows/hash-functions.yml/badge.svg)](https://github.com/mycplus/java-examples/actions/workflows/hash-functions.yml)

Companion code for [MD5, SHA-1 and SHA-256: Hash Functions in C, C++, Java, Python and C#](https://www.mycplus.com/computer-science/algorithms/md5-sha1-sha256-hash-functions/) on MYCPLUS: the three hashes and HMAC through `java.security.MessageDigest` and `javax.crypto.Mac`, tested against the same vectors as the C and C++ implementations.

> For learning and testing. Never store passwords with a plain hash, and never build your own cryptography for real data: use a maintained library (OpenSSL, the platform's own crypto API) and, for passwords, a slow password hash such as Argon2id, scrypt or bcrypt.

| File | What it is |
| --- | --- |
| `src/DigestDemo.java` | Hashes three short messages; prints exactly what the C version prints |
| `tests/DigestTest.java` | Known answers, 306 message lengths, every split offset up to 130 bytes, HMAC-MD5/SHA-1/SHA-256, constant-time comparison |

The vectors are in `tests/vectors/`: the RFC 1321 and FIPS 180-4 examples, 306 message lengths generated with Python's `hashlib` (an independent implementation), and the RFC 4231 and RFC 2202 HMAC cases. `tests/vectors/` is identical in all five languages.

## Build and run

```sh
javac --release 17 -Xlint:all -Werror -d build src/*.java tests/*.java
java -ea -cp build DigestTest tests/vectors
java -cp build DigestDemo
```

## What the build checks

Compiles with lint warnings as errors on Java 17, 21 and 25; every vector passes; the demo prints exactly `tests/expected/DigestDemo.txt`; and the summary printed by `DigestTest` equals `tests/expected/DigestTest.txt`.
