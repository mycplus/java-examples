#!/bin/sh
set -e
mkdir -p build
javac --release 17 -Xlint:all -Werror -d build src/*.java tests/*.java
java -ea -cp build DigestTest tests/vectors | diff - tests/expected/DigestTest.txt
java -cp build DigestDemo | diff - tests/expected/DigestDemo.txt
echo "Java: all checks passed"
