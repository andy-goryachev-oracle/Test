# JUnit5 Migration Notes

Most of the changes are trivial, except for the following:

1. assertEquals() and similar methods: the message can be confused with the expected argument (junit5 moved the message to the last position)
2. parameterized tests: junit5 allows for parameterizing individual tests
3. parameterized `@BeforeEach` and `@AfterEach`: (see discussion below)
4. charts: the test hierarchy for charts mixed parameterized and non-parameterized kinds, necessitating more changes
5. overridden parameterized tests (must be annotated with `@ParameterizedTest, @MethodSource`)

## Parameterized Class-Level Tests

junit5 does not support parameterized class-level tests yet (see https://github.com/junit-team/junit5/issues/878)

The workaround is to setup each test explicitly by calling the method that used to be annotated with `@Before` in each parameterized test method.  There might be another solutions (see, for example, https://stackoverflow.com/questions/62036724/how-to-parameterize-beforeeach-in-junit-5/69265907#69265907) but I thought explicit setup might be simpler to deploy.

To summarize:
- remove `@Before` from the setup method
- call the setup method from each parameterized method (adding parameters and replacing `@Test` with
```
  @ParameterizedTest
  @MethodSource("parameters")
```
where parameters() is a static method which supplies the parameters.  In the case when parameters have more than one element, the following code might be useful:
```
private static Stream<Arguments> parameters() {
    return Stream.of(
            Arguments.of("a", 1),
            Arguments.of("foo", 3)
    );
}
```

## Migration Tricks

Here are the steps that might speed up the process:

1. remove all the junit4 imports
2. paste the following junit5 imports (below)
3. fix the errors
6. optimize imports via IDE (command-shift-O in Eclipse on macOS)
7. after all is done, verify that there is no more junit4 names by running the command mentioned below

junit5 imports (in no particular order):
```
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.params.provider.Arguments;
```

The following command verifies that there is no junit4 imports and inline fully qualified names:

```
grep -lre 'org\.junit\.[^j][^u][^p][^i][^t][^e][^r]' .
```

(A regex provided by @lukostyra `grep -lre 'org\.junit\.(?!jupiter)' .` did not work for some reason.)

## Acceptance Criteria

Aside from the standard review process, I think the following criteria should be sufficient:
- successful GHA (Github Actions) run on all platforms
- the same number of tests executed (number of tests minus number of ignored tests)
- grep shows no hits for junit4 imports
