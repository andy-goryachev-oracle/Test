package generics;

// https://github.com/openjdk/jfx/blob/master/modules/javafx.graphics/src/main/java/javafx/animation/Interpolatable.java
@FunctionalInterface
public interface Interpolatable<T> {
    T interpolate(T endValue, double t);
}