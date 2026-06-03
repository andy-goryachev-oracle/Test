package goryachev.bugs;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.ModifiableObservableListBase;
import javafx.collections.transformation.FilteredList;

/** 
 * @author daniel 
 */
public class FilteredList_AIOOBE_8195750 {

    public static void main(String ... args) {
        try {
            test1();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        try {
            test2();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static class MyList<Model> extends ModifiableObservableListBase<Model> {

        private final List<Model> children = new ArrayList<>();

        @Override
        public Model get(int index) {
            return children.get(index);
        }

        @Override
        public int size() {
            return children.size();
        }

        @Override
        protected void doAdd(int index, Model element) {

            children.add(index, element);
        }

        @Override
        protected Model doSet(int index, Model element) {
            if (children.get(index) == element) {
                return element;
            } else {
                Model old = children.set(index, element);
                return old;
            }
        }

        @Override
        protected Model doRemove(int index) {
            Model child = children.remove(index);
            return child;
        }

        public void removeAndAdd(Model o, Model after) {
            beginChange();
            remove(o);
            int index = after == null ? 0 : indexOf(after) + 1;
            add(index, o);
            endChange();
        }

        public void permute(Model o, Model after) {
            beginChange();
            int idxToRemove = indexOf(o);
            int indexToAdd = after == null ? 0 : indexOf(after) + 1;
            idxToRemove = idxToRemove > indexToAdd ? idxToRemove + 1 : idxToRemove;
            children.add(indexToAdd, o);
            children.remove(idxToRemove);
            // nextAdd(indexToAdd, indexToAdd + 1); 
            // nextRemove(idxToRemove, o); 
            nextPermutation(idxToRemove, idxToRemove + 1, new int[] { indexToAdd });
            endChange();
        }
    }

    private static void test1() {
        for (int j = 3; j < 20; j++) {
            System.err.println("trying to move index 2 after 0 in a list of " + j + " elements");
            MyList<String> observableList = new MyList<>();
            FilteredList<String> filteredList = new FilteredList<>(observableList);

            for (int i = 0; i < j; i++) {
                final String a = "a" + i;
                observableList.add(a);
            }

            System.out.println("elements = " + filteredList.stream().collect(Collectors.joining(",")));
            observableList.removeAndAdd(observableList.get(2), observableList.get(0));
            System.out.println("elements = " + filteredList.stream().collect(Collectors.joining(",")));
        }
    }

    private static void test2() {
        for (int j = 100; j > 0; j--) {
            MyList<String> observableList = new MyList<>();
            System.err.println("observable list with " + j + " elements");
            for (int i = 0; i < j; i++) {
                final String a = "a" + i;
                observableList.add(a);
            }

            FilteredList<String> filteredList = new FilteredList<>(observableList);
            final String b = "b";
            observableList.add(b);
            // System.out.println("elements = " + filteredList.stream().collect(Collectors.joining(","))); 
            observableList.removeAndAdd(b, null);
            // System.out.println("elements = " + filteredList.stream().collect(Collectors.joining(","))); 
        }
    }
}