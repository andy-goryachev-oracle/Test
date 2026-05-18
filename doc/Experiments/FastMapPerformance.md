# FastMap Performance Experiment

Andy Goryachev

<andy.goryachev@oracle.com>


## Summary

Measured performance of a map-like structure using linear search through small array
vs. `HashMap`, as part of [Node Properties](NodeProperties.md) experiment [0].

TL;DR The `HashMap` wins.


## Description

This measurement is a response to the following feedback to my original proposal [0], see

https://mail.openjdk.org/pipermail/openjfx-dev/2026-February/059591.html

The basic premise is that a linear search within an array small enough to fit into a single cache line might outperform
the standard `HashMap` when the number of stored items is low.


## Measurements

The measurement test code can be found here [1].  The test runs several invocations of the measurement function in
sequence in order to prime the just-in-time compiler.  The results listed below are taken from the very last run, to avoid 
initial slow path.

The results are in nanoseconds per access cycle, including computation of the next key index.

Columns:

- Size: the map size
- FastMap: FastMap implementation (two arrays, one for keys, one for values)
- FasterMap: FasterMap (a single array for keys and values)
- HashMap: standard JDK `HashMap`


### Apple M1 Pro

| Size | FastMap | FasterMap | HashMap |
| ---: | ------: | --------: | ------: |
| 1 | 3.70725 | 5.160666 | 3.920084
| 2 | 3.594332 | 5.234916 | 4.096332
| 4 | 3.667 | 5.407166 | 4.01025
| 8 | 3.651334 | 5.3415 | 3.944832
| 16 | 6.169834 | 6.52725 | 4.063082
| 32 | 11.565 | 12.45225 | 3.96
| 64 | 24.988916 | 25.59025 | 4.005
| 128 | 49.86975 | 50.689834 | 3.960584

### 12th Gen Intel(R) Core(TM) i7-1270P (2.20 GHz)

| Size | FastMap | FasterMap | HashMap |
| ---: | ------: | --------: | ------: |
| 1 | 5.2082 | 5.2178 | 5.5474
| 2 | 5.81 | 5.191 | 5.5976
| 4 | 7.1652 | 5.2352 | 5.5894
| 8 | 9.5912 | 5.4334 | 5.5568
| 16 | 14.537 | 8.1466 | 5.5414
| 32 | 23.6632 | 14.9688 | 5.4012
| 64 | 41.9554 | 30.0622 | 5.2708
| 128 | 76.7284 | 52.4314 | 5.0396


## Discussion

The experimental results show that a small array is indeed faster than a `HashMap` for small containers:
8 for Apple silicon, 4 for Intel.  Then, the `HashMap` provides consistent and fast access time.
`FastMap` seems to outperform the `FasterMap`.

Even when the alternatives perform better than the `HashMap`, the difference is minute, making the `HashMap` a better choice.



## References

[0] https://github.com/andy-goryachev-oracle/Test/blob/main/doc/Experiments/NodeProperties.md

[1] https://github.com/andy-goryachev-oracle/jfx/blob/node.props/modules/javafx.graphics/src/test/java/test/com/sun/javafx/util/FastMapTest.java