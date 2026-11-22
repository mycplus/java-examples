#!/bin/sh
# Builds the Java patterns and checks them against the C version's output.
set -e
mkdir -p build
javac --release 17 -Xlint:all -Werror -d build src/*.java tests/*.java
java -ea -cp build PatternsTest tests/expected | diff - tests/expected/PatternsTest.txt
java -cp build PatternsDemo list | diff - tests/expected/list.txt
java -cp build PatternsDemo pascal 7 | diff - tests/expected/pascal-7.txt
java -cp build Pyramid < tests/input-5.txt | diff - tests/expected/pyramid-5.txt
if java -cp build Pyramid < tests/input-four.txt > build/rejected.txt; then
  echo "Pyramid accepted 'four'"; exit 1
fi
diff build/rejected.txt tests/expected/pyramid-rejected.txt
if java -cp build PatternsDemo pyramid 51 2>/dev/null; then
  echo "PatternsDemo accepted 51 rows"; exit 1
fi
echo "Java: all checks passed"
