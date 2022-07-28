package goryachev.test;

// https://github.com/openjdk/jfx/pull/824
public class SomeClass {
    private double meth(double x) {
        return Math.sqrt(x);
    }

    static class NestedClass {
        double meth(SomeClass cls, double x) {
            return cls.meth(x);
        }
    }
}
