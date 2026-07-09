# Overflow Menu Decorator Property in the TabPaneSkin

Andy Goryachev

<andy.goryachev@oracle.com>


## Summary

Add the `overflowMenuDecorator` property in the `TabPaneSkin` class for the purpose of eliminating the current
limitation which makes it impossible to customize the menu items' text, as well as graphic, which is currently
limited to either an `ImageView` or a `Label` with an `ImageView` graphic.



## Goals

The goals of this proposal are:

- to allow the application developers to customize the overflow menu item graphic
- to enable wider choices for the graphic in the overflow menu
- retain the backward compatibility with the existing application code
- clarify the behavior of the skin when the property is null (i.e. the current behavior)



## Non-Goals

The following are not the goals of this proposal:

- disable the overflow menu
- configure overflow menu graphic property via CSS
- add this property to the `TabPane` control itself



## Motivation

The existing `TabPaneSkin` does not allow the overflow menu to show graphic other than
an `ImageView` or `Label` with an `ImageView`.

This limitation makes it impossible for the application developer to use other graphic Nodes,
such as `Path` or `Canvas`, or in fact any other types.  The situation becomes even more egregious
when the tabs in the `TabPane` have no text.

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
        // set overflow menu factory with the same method as was used to create the tabs
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


## Description

The proposed solution adds the `overflowMenuDecorator` property in the `TabPaneSkin` class:

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


## Alternatives

Use graphic in tabs based on the `ImageView`, which is currently compatible with the overflow menu.



## Risks and Assumptions

The risk is minimal, as the proposed solution adds a new property and retains the existing behavior when
this property is not set.

There might be a need to update the application code if the `TabPane` uses a custom skin extended from
the `TabPaneSkin` which declares a property or a method (or methods) with the same signature.



## Dependencies

None.



## References

- [JDK-8353599 TabPaneSkin: 'overflowMenuDecorator' property](https://bugs.openjdk.org/browse/JDK-8353599)
- https://mail.openjdk.org/pipermail/openjfx-dev/2025-April/053306.html
- https://mail.openjdk.org/pipermail/openjfx-dev/2025-April/053338.html
- https://github.com/andy-goryachev-oracle/Test/blob/main/doc/TabPane/TabPaneGraphicFactory.md

