package generics;

@FunctionalInterface
public interface Interpolatable<T> {
    T interpolate(T endValue, double t);
}