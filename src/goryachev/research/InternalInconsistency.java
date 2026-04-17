package goryachev.research;

import java.util.Comparator;
import java.util.List;

// FIX Eclipse reports the following warning which cannot be turned off
// Internal inconsistency: Inappropriate operand stack size encountered during translation InternalInconsistency.java  line 6
public interface InternalInconsistency<E> extends List<E> {

    @SuppressWarnings("unchecked")
    @Override
    public default void sort(Comparator<? super E> comparator) {
        if (size() == 0 || size() == 1) {
            return;
        }
        comparator = comparator != null ? comparator : (Comparator<? super E>) Comparator.naturalOrder();
    }

    void doSort(Comparator<? super E> comparator);
}
