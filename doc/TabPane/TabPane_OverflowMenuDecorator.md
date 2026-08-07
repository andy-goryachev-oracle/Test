# Overflow Menu Decorator Property in the TabPaneSkin

Andy Goryachev

<andy.goryachev@oracle.com>


## Summary

Add the `overflowMenuDecorator` property in the `TabPaneSkin` class to allow for customization of
the `TabPane` overflow menu, including support for custom graphic.



## Problem

The original issue described impossibility of supporting custom graphic (such as `Path` or `Canvas`)
in the `TabPane` overflow menu.
This happens because
the overflow menu must duplicate the the graphic, and the current implementation is limited to duplicating
either `ImageView` or `Label` with an `ImageView` graphic.

Going beyond the original complaint, it is also impossible to customize the overflow menu, when, for example,
the application requirements call for text-only overflow menu (with tabs that include the graphic), or when
the overflow menu needs to contain different text/graphic, or apply different rules to individual menu items.



## Solution

The solution is to allow the application to set a "decorator" which would be used to customize the menu items
in the overflow menu (the actual menu item instances are of certain type created by the skin).

The `overflowMenuDecorator` property holds the decorator instance.  The default `null` value makes the `TabPane`
work exactly as it works now.

Example:

```java
public class TabPaneGraphicFactoryExample {
    public void example() {
        Tab tab1 = new Tab("Tab1");
        tab1.setGraphic(...);

        Tab tab2 = new Tab("Tab2");
        tab2.setGraphic(...);

        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(tab1, tab2);

        TabPaneSkin skin = new TabPaneSkin(tabPane);
        // set overflow menu decorator
        skin.setOverflowMenuDecorator(this::decorateMenu);
        tabPane.setSkin(skin);
    }

    // custom overflow menu decorator
    private void decorateMenu(Tab tab, MenuItem menu) {
        switch (tab.getText()) {
        case "Tab1":
            menu.setGraphic(new Circle(10));
            break;
        case "Tab2":
            menu.setText(null);
            menu.setGraphic(new Canvas(10, 10));
        }
    }
}
```



## Specification

In the `TabPaneSkin` class:

```java
    /**
     * This property allows to customize the overflow menu items.  When this property is not {@code null},
     * the {@link MenuItem} text will get initialized to be the same as the {@link Tab} text and {@code null}
     * graphic prior to invocation of the decorator.
     * <p>
     * When this property is {@code null}, the menu item is initialized with the text and the graphic
     * obtained from the corresponding {@link Tab}.  For the graphic, either an {@link ImageView}
     * or a {@link Label} with an {@link ImageView} will be used.
     * <p>
     * Changing this property while the menu is shown has no effect.
     *
     * @since 28
     * @defaultValue null
     */

    public final ObjectProperty<BiConsumer<Tab, MenuItem>> overflowMenuDecoratorProperty()

    public final BiConsumer<Tab, MenuItem> getOverflowMenuDecorator()

    public final void setOverflowMenuDecorator(BiConsumer<Tab, MenuItem> d)
```
