# Binary Search Tree in Java

[![binary-tree](https://github.com/mycplus/java-examples/actions/workflows/binary-tree.yml/badge.svg)](https://github.com/mycplus/java-examples/actions/workflows/binary-tree.yml)

Companion code for [Binary Trees in C, C++, Java, Python and C#](https://www.mycplus.com/computer-science/data-structures/binary-trees/) on MYCPLUS.

| File | What it is |
| --- | --- |
| `src/BinarySearchTree.java` | A generic binary search tree with unique keys: insert, search and remove with loops, the four traversals with an explicit stack or queue |
| `src/BstDemo.java` | The article's demo tree; prints the same output as the C, C++, Python and C# versions |
| `pitfalls/RecursiveHeight.java` | The C version's recursive height on chains of 1,000 to 1,000,000 nodes, showing where the thread's stack runs out |
| `tests/` | Dependency-free tests and the demo's expected output |

## Build and test

```sh
javac --release 17 -Xlint:all -Werror -d build src/*.java tests/*.java pitfalls/*.java
java -ea -cp build BstTest
java -cp build BstDemo
```

## What the build checks

- Compiles for Java 17 with every lint warning as an error, and runs on Java 17, 21 and 25 on Linux and Windows.
- The demo tree's four traversal orders, height and size, and every removal case.
- An empty tree, a reverse-ordered `String` tree, and a `null` key rejected with `NullPointerException`.
- 200,000 random inserts, removes and searches agree with `java.util.TreeSet`, including `Integer.MIN_VALUE` and `Integer.MAX_VALUE`.
- A 20,000-node chain (sorted insertion) is walked in preorder, inorder and postorder on a thread with a 64 KiB stack, where a recursive walk throws `StackOverflowError`.
- `BstDemo` prints exactly the output shown in the article.
- `RecursiveHeight` succeeds at 1,000 nodes and throws `StackOverflowError` at 100,000 with the default thread stack.
