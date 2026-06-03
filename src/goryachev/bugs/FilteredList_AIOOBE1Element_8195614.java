package goryachev.bugs;

import java.util.ArrayList;
import java.util.List;
import javafx.collections.ModifiableObservableListBase;
import javafx.collections.transformation.FilteredList;

/** 
 * 
 * @author daniel 
 */
public class FilteredList_AIOOBE1Element_8195614 {

    public static void main(String[] args) {
        for (int j = 100; j > 0; j--) {
            MyList<String> observableList = new MyList<>();
            System.out.println("observable list with " + j + " elements");
            for (int i = 0; i < j; i++) {
                final String a = "a" + i;
                observableList.add(a);
            }

            FilteredList<String> filteredList = new FilteredList<>(observableList);
            final String b = "b";
            observableList.add(b);
            // System.out.println("elements = " + filteredList.stream().collect(Collectors.joining(","))); 
            observableList.moveToBegin(b);
            // System.out.println("elements = " + filteredList.stream().collect(Collectors.joining(","))); 
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

        public void moveToBegin(Model o) {
            beginChange();
            remove(o);
            add(0, o);
            endChange();
        }
    }
}