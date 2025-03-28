# Homework Specification

## Overview

In this assignment, you will implement a `LinkedList` and one of two maps: a `HashMap` with closed addressing or
an `AvlTreeMap` (for advanced students, gives bonus points).

## Part 1. `LinkedList` (4 points)

Implement a two-way linked list that conforms to the `MutableList<T>` interface.

### Tasks:

- **L1 Basic accessors and insertion (1 point):** Implement `size`, `get`, `set`, `isEmpty`, `add`, and `addAll`.
- **L2 Removal (2 points):** Implement `remove`, `removeAt`, `removeAll`, and `clear`.
- **L3 Lookup operations (1 point):** Implement `indexOf`, `lastIndexOf`, `contains` and `containsAll`.

### Complexity requirements:

- `size`, `isEmpty`, `clear`: O(1)
- `addAll`, `containsAll`: O(n + k) where n is the number of elements in the list and k is the number of elements to add
- All other operations: O(n)
- Space in memory for the list itself: O(n)
- Additional space in memory for `addAll`, `containsAll`: O(k)
- Additional space in memory for other operations: O(1)

### Running tests:

#### L1

```bash
./gradlew :testList -PignoreTags=L2,L3
```

#### L1 + L2

```bash
./gradlew :testList -PignoreTags=L3
```

#### L1 + L3

```bash
./gradlew :testList -PignoreTags=L2
```

#### All tests

```bash
./gradlew :testList
```

## Part 2a. `HashMap` (6 points)

Implement a hash map with closed addressing that conforms to the `MutableMap<K, V>` interface.

### Tasks:

- **H1 Basic accessors and insertion (2 points):**
  Implement `size`, `get`, `put`, `putAll`, `isEmpty`, `keys`, `values`, and `entries`.
- **H2 Removal (2 points) :** Implement `remove`, `clear`, `containsKey`, and `containsValue`.
- **H3 Rehashing (2 points):** Implement `rehash`. This function recalculates the optimal number of buckets and, if
  necessary, redistributes all entries into the new buckets.

### Complexity requirements:

- `get`, `put`, `remove`, `contains`: O(1) amortized
- `clear`, `size`: O(1)
- `putAll`: O(k) amortized where k is the number of elements to put
- `keys`, `values`, `entries`, `rehash`: O(n)
- Space in memory: O(n)

### Running tests:

#### H1

```bash
./gradlew :testHashMap -PignoreTags=H2,H3
```

#### H1 + H2

```bash
./gradlew :testHashMap -PignoreTags=H3
```

#### All tests

```bash
./gradlew :testHashMap
```

## Part 2b. `AvlTreeMap<K, V>` (8 points)

Implement a self-balancing AVL tree that conforms to the `MutableMap<K, V>` interface.

### Tasks:

- **A1 Basic accessors and insertion (4 points):**
  Implement `size`, `get`, `put`, `putAll`, `isEmpty`, `keys`, `values`, and `entries`.
- **A2 Removal (4 points):** Implement `remove`, `clear`, `containsKey`, and `containsValue`.

### Complexity requirements:

- `get`, `put`, `remove`, `contains`: O(log(n))
- `clear`, `size`, `isEmpty`: O(1)
- `putAll`: O(k*log(n)) where k is the number of elements to put
- `keys`, `values`, `entries`: O(n)
- Space in memory: O(n)

### Running tests:

#### A1

```bash
./gradlew :testAvlMap -PignoreTags=A2
```

#### All tests

```bash
./gradlew :testAvlMap
```

## Submission Guidelines

1. Implement the `LinkedList<T>` first.
2. Choose and implement **one** of `HashMap<K, V>` or `AvlTreeMap<K, V>`.
3. Test your solution locally.
4. If you are going to implement an AVL-tree instead of a hash table,
   replace the command for running Map tests in `.github/test.yml:34` with `./gradlew :testAvlMap`
5. If you are not going to implement some of the tasks, exclude them from testing by adding option
   `-PignoreTags=<tasks-to-ignore>` to the corresponding commands in `.github/test.yml:31`
   and `.github/test.yml:34`
6. You can see the number of points you are going to get when running the tests.
   Note that these points are the correctness points (60% of the grade).
   The rest of the points are awarded during code review for writing clean and concise code.

Good luck!
